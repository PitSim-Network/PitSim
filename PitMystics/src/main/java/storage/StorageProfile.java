package storage;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.log.DupeManager;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.exceptions.DataNotLoadedException;
import dev.kyro.pitsim.exceptions.NoCommonEnchantException;
import dev.kyro.pitsim.misc.Base64;
import dev.kyro.pitsim.misc.Misc;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

public class StorageProfile {

	private Inventory[]enderChest;
	private ItemStack[] cachedInventory;
	private ItemStack[] armor;

	private final UUID uuid;

	private PluginMessage enderchestSave;
	private PluginMessage inventorySave;


	private BukkitTask enderchestSaveTask;
	private BukkitTask inventorySaveTask;

	private boolean saving = false;

	public StorageProfile(Player player) {
		this.uuid = player.getUniqueId();
	}

	public StorageProfile(UUID player, int enderChestPages) {
		System.out.println("New profile");

		enderChest = new Inventory[enderChestPages];
		for(int i = 0; i < enderChestPages; i++) {
			enderChest[i] = PitSim.INSTANCE.getServer().createInventory(null, 27, "Enderchest - Page " + (i + 1));
		}

		System.out.println("1");

		this.uuid = player;
		JSONParser jsonParser = new JSONParser();

		System.out.println("1.5");

		try(FileReader reader = new FileReader("mstore/galacticvaults_players/" + player.toString() + ".json")) {
			JSONObject data = (JSONObject) jsonParser.parse(reader);
			JSONObject vaults = (JSONObject) data.get("vaultContents");
			for(int i = 1; i < 18; i++) {
				Inventory inventory = enderChest[i - 1];
				JSONObject vault = (JSONObject) vaults.get(i + "");
				if(vault == null) continue;
				for(int j = 8; j < 35; j++) {
					String base64String = (String) vault.get(j + "");
					if(base64String == null) continue;
					ItemStack itemStack = Base64.itemFrom64(base64String);

					inventory.setItem(j - 8, itemStack);
				}
			}
		} catch(IOException | ParseException e) {
			if(!(e instanceof FileNotFoundException)) e.printStackTrace();
		}

		System.out.println("2");


		try {
			File inventoryFile = new File("world/playerdata/" + player + ".dat");
			NBTTagCompound nbt = NBTCompressedStreamTools.a(Files.newInputStream(inventoryFile.toPath()));
			NBTTagList playerInventory = (NBTTagList) nbt.get("Inventory");
			for(int i = 0; i < playerInventory.size(); i++) {
				NBTTagCompound compound = playerInventory.get(i);
				if(!compound.isEmpty()) {
					ItemStack itemStack = CraftItemStack.asBukkitCopy(net.minecraft.server.v1_8_R3.ItemStack.createStack(compound));
					cachedInventory = new ItemStack[27];
					if(i < 27) cachedInventory[i] = itemStack;
					else armor[i] = itemStack;
				}
			}

		} catch(IOException e) {
			if(!(e instanceof FileNotFoundException)) e.printStackTrace();
		}

		System.out.println("3");

		saveEnderchest();
		saveInventory();

		System.out.println("4");
	}

	public void setEnderchest(PluginMessage message) {
		List<String> strings = message.getStrings();
		UUID playerUUID = UUID.fromString(strings.get(0));
		strings.remove(0);

		int pages = message.getIntegers().get(0);
		enderChest = new Inventory[pages];
		for(int i = 0; i < pages; i++) {
			enderChest[i] = PitSim.INSTANCE.getServer().createInventory(null, 27, "Enderchest - Page " + (i + 1));
		}

		for(int i = 0; i < strings.size(); i++) {
			int page = i / 18;

			Inventory inventory = enderChest[page];
			inventory.setItem(i % 18, deserialize(strings.get(i)));
		}
	}

	public void setInventory(PluginMessage message) {
		List<String> strings = message.getStrings();
		UUID playerUUID = UUID.fromString(strings.get(0));
		strings.remove(0);

		cachedInventory = new ItemStack[36];
		armor = new ItemStack[4];

		for(int i = 0; i < 36; i++) {
			cachedInventory[i] = deserialize(strings.get(i));
		}

		for(int i = 0; i < 4; i++) {
			armor[i] = deserialize(strings.get(36 + i));
		}
	}

	public void saveEnderchest() {
		PluginMessage message = new PluginMessage().writeString("ENDERCHEST").writeString(uuid.toString());
		message.writeString(PitSim.serverName);
//		for(Inventory items : enderChest) {
//			for(ItemStack item : items) {
//				message.writeString(serialize(item));
//			}
//		}

		enderchestSave = message;
		saving = true;

		enderchestSaveTask = new BukkitRunnable() {
			@Override
			public void run() {
				OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
				if(!player.isOnline()) return;
				player.getPlayer().kickPlayer("§cYour playerdata failed to save. Please report this issue");
			}
		}.runTaskLater(PitSim.INSTANCE, 40);

		System.out.println(message.getStrings().get(0));
		message.writeInt(87654);
		message.send();
	}

	public void saveInventory() {
		OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
		if(!player.isOnline()) return;
		PluginMessage message = new PluginMessage().writeString("INVENTORY").writeString(player.getUniqueId().toString());
		message.writeString(PitSim.serverName);
		if(player.isOnline()) {
			for(ItemStack itemStack : player.getPlayer().getInventory()) {
				message.writeString(serialize(itemStack));
			}

			for(ItemStack itemStack : player.getPlayer().getInventory().getArmorContents()) {
				message.writeString(serialize(itemStack));
			}
		} else {
			for(ItemStack itemStack : cachedInventory) {
				message.writeString(serialize(itemStack));
			}

			for(ItemStack itemStack : armor) {
				message.writeString(serialize(itemStack));
			}
		}

		inventorySave = message;
		saving = true;

		inventorySaveTask = new BukkitRunnable() {
			@Override
			public void run() {
				OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
				if(!player.isOnline()) return;
				player.getPlayer().kickPlayer("§cYour playerdata failed to save. Please report this issue");
			}
		}.runTaskLater(PitSim.INSTANCE, 40);

		message.send();
	}

	public UUID getUUID() {
		return uuid;
	}

	public Inventory getEnderchest(int page) {
		if(enderChest == null) {
			try {
				throw new DataNotLoadedException();
			} catch(DataNotLoadedException ignored) { }
		}

		return enderChest[page - 1];
	}

	public ItemStack[] getCachedInventory() {
		if(cachedInventory == null) {
			try {
				throw new DataNotLoadedException();
			} catch(DataNotLoadedException ignored) { }
		}

		return cachedInventory;
	}

	public ItemStack[] getArmor() {
		if(armor == null) {
			try {
				throw new DataNotLoadedException();
			} catch(DataNotLoadedException ignored) { }
		}

		return armor;
	}


	public static ItemStack deserialize(String string) {
		try {
			return Base64.itemFrom64(string);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String serialize(ItemStack item) {
		return Base64.itemTo64(item);
	}

	public boolean hasData() {
		return enderChest != null && cachedInventory != null && armor != null;
	}

	public boolean isSaving() {
		return saving;
	}

	protected void receiveSaveConfirmation(PluginMessage message) {
		if(message.getStrings().get(0).equals("ENDERCHEST SAVE")) {
			enderchestSaveTask.cancel();
		} else if(message.getStrings().get(0).equals("INVENTORY SAVE")) {
			inventorySaveTask.cancel();
		}

		saving = false;
	}

	public PluginMessage getEnderchestSave() {
		return enderchestSave;
	}

	public PluginMessage getInventorySave() {
		return inventorySave;
	}

}
