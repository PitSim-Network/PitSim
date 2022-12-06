package storage;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.exceptions.DataNotLoadedException;
import dev.kyro.pitsim.misc.Base64;
import dev.kyro.pitsim.misc.Misc;
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class StorageProfile {
	public static final int ENDERCHEST_PAGES = 18;

	private Inventory[] enderChest;
	private ItemStack[] cachedInventory;
	private ItemStack[] armor;

	private final UUID uuid;

	private PluginMessage enderchestSave;
	private PluginMessage inventorySave;

	private BukkitTask enderchestSaveTask;
	private BukkitTask inventorySaveTask;

	private boolean saving = false;

	public StorageProfile(UUID uuid) {
		this.uuid = uuid;
	}

	public StorageProfile(UUID player, int enderChestPages) {
		System.out.println("New profile");

		enderChest = new Inventory[enderChestPages];
		for(int i = 0; i < enderChestPages; i++) {
			enderChest[i] = PitSim.INSTANCE.getServer().createInventory(null, 27, "Enderchest - Page " + (i + 1));
		}

		this.uuid = player;
		JSONParser jsonParser = new JSONParser();

		try(FileReader reader = new FileReader("mstore/galacticvaults_players/" + player.toString() + ".json")) {
			JSONObject data = (JSONObject) jsonParser.parse(reader);
			JSONObject vaults = (JSONObject) data.get("vaultContents");
			for(int i = 1; i < ENDERCHEST_PAGES + 1; i++) {
				Inventory inventory = enderChest[i - 1];
				JSONObject vault = (JSONObject) vaults.get(i + "");
				if(vault == null) continue;
				for(int j = 9; j < 36; j++) {
					String base64String = (String) vault.get(j + "");
					if(base64String == null) continue;
					ItemStack itemStack = Base64.itemFrom64(base64String);

					inventory.setItem(j - 9, itemStack);
				}
			}
		} catch(IOException | ParseException e) {
			if(!(e instanceof FileNotFoundException)) e.printStackTrace();
		}

		cachedInventory = new ItemStack[36];
		armor = new ItemStack[4];
		int armorIndex = 0;

		try {
			File inventoryFile = new File("world/playerdata/" + player + ".dat");
			NBTTagCompound nbt = NBTCompressedStreamTools.a(Files.newInputStream(inventoryFile.toPath()));
			NBTTagList playerInventory = (NBTTagList) nbt.get("Inventory");
			for(int i = 0; i < playerInventory.size(); i++) {
				NBTTagCompound compound = playerInventory.get(i);
				int slot = compound.getByte("Slot") & 0xFF;
				Bukkit.broadcastMessage(slot + "");
				if(!compound.isEmpty()) {
					ItemStack itemStack = CraftItemStack.asBukkitCopy(net.minecraft.server.v1_8_R3.ItemStack.createStack(compound));
					if(slot < 36) cachedInventory[slot] = itemStack;
					else {
						armor[armorIndex] = itemStack;
						armorIndex++;
					}
				}
			}

			Bukkit.broadcastMessage(Arrays.toString(armor) + "");


		} catch(IOException e) {
			if(!(e instanceof FileNotFoundException)) e.printStackTrace();
		}

		saveEnderchest();
		saveInventory();

	}

	public void setEnderchest(PluginMessage message) {
		List<String> strings = message.getStrings();

		System.out.println();

		int pages = message.getIntegers().get(0);
		enderChest = new Inventory[pages];
		for(int i = 0; i < pages; i++) {
			enderChest[i] = PitSim.INSTANCE.getServer().createInventory(null, 27, "Enderchest - Page " + (i + 1));
		}

		for(int i = 0; i < strings.size(); i++) {
			int page = i / 27;

			Inventory inventory = enderChest[page];
			inventory.setItem(i % 27, deserialize(strings.get(i)));
		}
	}

	public void setInventory(PluginMessage message) {
		List<String> strings = message.getStrings();

		cachedInventory = new ItemStack[36];
		armor = new ItemStack[4];

		for(int i = 0; i < 36; i++) {
			cachedInventory[i] = strings.get(i).isEmpty() ? new ItemStack(Material.AIR) : deserialize(strings.get(i));
		}

		for(int i = 0; i < 4; i++) {
			System.out.println(strings.size());
			armor[i] = strings.get(i + 36).isEmpty() ? new ItemStack(Material.AIR) : deserialize(strings.get(i + 36));
		}
	}

	public void saveEnderchest() {
		PluginMessage message = new PluginMessage().writeString("ENDERCHEST").writeString(uuid.toString());
		message.writeString(PitSim.serverName);
		for(Inventory items : enderChest) {
			for(ItemStack item : items) {
				message.writeString(serialize(item));
			}
		}

		enderchestSave = message;
		saving = true;

		enderchestSaveTask = new BukkitRunnable() {
			@Override
			public void run() {
				//TODO: Discord alert

				OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
				if(!player.isOnline()) return;
				player.getPlayer().kickPlayer(ChatColor.RED + "Your playerdata failed to save. Please report this issue");
			}
		}.runTaskLater(PitSim.INSTANCE, 40);

		message.writeInt(87654);
		message.send();
	}

	public void saveInventory() {
		OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
		PluginMessage message = new PluginMessage().writeString("INVENTORY").writeString(player.getUniqueId().toString());
		message.writeString(PitSim.serverName);
		if(player.isOnline()) {
			System.out.println("Online!");
			for(ItemStack itemStack : player.getPlayer().getInventory()) {
				message.writeString(serialize(itemStack));
			}

			for(ItemStack itemStack : player.getPlayer().getInventory().getArmorContents()) {
				message.writeString(serialize(itemStack));
			}
		} else {
			System.out.println("Offline!");
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
				//TODO: Discord alert

				OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
				if(!player.isOnline()) return;
				player.getPlayer().kickPlayer(ChatColor.RED + "Your playerdata failed to save. Please report this issue");
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
		if(string.isEmpty()) return new ItemStack(Material.AIR);
		try {
			return Base64.itemFrom64(string);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String serialize(ItemStack item) {
		if(Misc.isAirOrNull(item)) return "";
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
			if(enderchestSaveTask != null) enderchestSaveTask.cancel();
		} else if(message.getStrings().get(0).equals("INVENTORY SAVE")) {
			if(inventorySaveTask != null) inventorySaveTask.cancel();
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
