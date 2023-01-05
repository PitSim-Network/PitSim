package dev.kyro.pitsim.storage;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.logging.LogManager;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.exceptions.DataNotLoadedException;
import dev.kyro.pitsim.misc.Base64;
import dev.kyro.pitsim.misc.CustomSerializer;
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
import org.bukkit.inventory.meta.ItemMeta;
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
import java.nio.file.NoSuchFileException;
import java.util.List;
import java.util.UUID;

public class StorageProfile {
	public static final int ENDERCHEST_MAX_PAGES = 18;

	protected Inventory[] enderChest;
	protected ItemStack[] cachedInventory;
	protected ItemStack[] armor;

	private final UUID uuid;

	private BukkitTask saveTask;

	private BukkitRunnable saveRunnable;

	public boolean playerHasBeenOnline = false;

	private boolean saving = false;

	public StorageProfile(UUID uuid) {
		this.uuid = uuid;
	}

	public StorageProfile(UUID player, int enderChestPages) {
		enderChest = new Inventory[enderChestPages];
		for(int i = 0; i < enderChestPages; i++) {
			enderChest[i] = PitSim.INSTANCE.getServer().createInventory(null, 45, "Enderchest - Page " + (i + 1));
		}

		this.uuid = player;
		JSONParser jsonParser = new JSONParser();

		try(FileReader reader = new FileReader("mstore/galacticvaults_players/" + player.toString() + ".json")) {
			JSONObject data = (JSONObject) jsonParser.parse(reader);
			JSONObject vaults = (JSONObject) data.get("vaultContents");
			for(int i = 1; i < ENDERCHEST_MAX_PAGES + 1; i++) {
				Inventory inventory = enderChest[i - 1];
				JSONObject vault = (JSONObject) vaults.get(i + "");
				if(vault == null) continue;
				for(int j = 9; j < 36; j++) {
					String base64String = (String) vault.get(j + "");
					if(base64String == null) continue;
					ItemStack itemStack = Base64.itemFrom64(base64String);
//					ItemStack itemStack = CustomSerializer.deserialize(base64String);

					inventory.setItem(j, itemStack);
				}

				for(int j = 0; j < 9; j++) {
					ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
					ItemMeta meta = pane.getItemMeta();
					meta.setDisplayName(" ");
					pane.setItemMeta(meta);
					inventory.setItem(j, pane);
				}

				for(int j = 36; j < inventory.getSize(); j++) {
					ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
					ItemMeta meta = pane.getItemMeta();
					meta.setDisplayName(" ");
					pane.setItemMeta(meta);
					inventory.setItem(j, pane);
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

		} catch(IOException exception) {
			if(!(exception instanceof FileNotFoundException) && !(exception instanceof NoSuchFileException))
				exception.printStackTrace();
		}

		saveData(false);

	}

	public void saveData(BukkitRunnable runnable, boolean logout) {
		saveRunnable = runnable;
		if(saving) return;
		saveData(logout);
	}

	public void setData(PluginMessage message) {
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
		List<String> strings = message.getStrings();
		List<Integer> ints = message.getIntegers();
		int invCount = ints.get(0);

		cachedInventory = new ItemStack[36];
		armor = new ItemStack[4];

		for(int i = 0; i < 36; i++) {
			cachedInventory[i] = strings.get(i).isEmpty() ? new ItemStack(Material.AIR) : deserialize(strings.get(i));
		}

		for(int i = 0; i < 4; i++) {
			armor[i] = strings.get(i + 36).isEmpty() ? new ItemStack(Material.AIR) : deserialize(strings.get(i + 36));
		}

		enderChest = new Inventory[ENDERCHEST_MAX_PAGES];
		for(int i = 0; i < ENDERCHEST_MAX_PAGES; i++) {
			enderChest[i] = PitSim.INSTANCE.getServer().createInventory(null, 45, "Enderchest - Page " + (i + 1));
		}

		for(int i = 0; i < (strings.size() - invCount); i++) {
			int page = i / 27;

			Inventory inventory = enderChest[page];
			inventory.setItem((i % 27) + 9, deserialize(strings.get(i + invCount)));
		}

		for(int i = 0; i < enderChest.length; i++) {
			Inventory inventory = enderChest[i];

			for(int j = 0; j < 9; j++) {
				ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
				ItemMeta meta = pane.getItemMeta();
				meta.setDisplayName(" ");
				pane.setItemMeta(meta);
				inventory.setItem(j, pane);
			}

			for(int j = 36; j < inventory.getSize(); j++) {
				ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
				ItemMeta meta = pane.getItemMeta();
				meta.setDisplayName(" ");
				pane.setItemMeta(meta);
				inventory.setItem(j, pane);
			}

			ItemStack back = new ItemStack(Material.PAPER);
			ItemMeta backMeta = back.getItemMeta();
			if(i == 0) backMeta.setDisplayName(ChatColor.RED + "Previous Page");
			else backMeta.setDisplayName(ChatColor.GREEN + "Previous Page");
			back.setItemMeta(backMeta);
			back.setAmount(i == 0 ? 1 : i);
			inventory.setItem(36, back);

			ItemStack next = new ItemStack(Material.PAPER);
			ItemMeta nextMeta = next.getItemMeta();
			if(i == ENDERCHEST_MAX_PAGES - 1) nextMeta.setDisplayName(ChatColor.RED + "Next Page");
			else nextMeta.setDisplayName(ChatColor.GREEN + "Next Page");
			next.setItemMeta(nextMeta);
			next.setAmount(i == (ENDERCHEST_MAX_PAGES - 1) ? 1 : i + 2);
			inventory.setItem(44, next);

			ItemStack menu = new ItemStack(Material.COMPASS);
			ItemMeta menuMeta = menu.getItemMeta();
			menuMeta.setDisplayName(ChatColor.DARK_PURPLE + "Back to Menu");
			menu.setItemMeta(menuMeta);
			menu.setAmount(i + 1);
			inventory.setItem(40, menu);
		}
	}

	public void saveData(boolean logout) {
		PluginMessage message = new PluginMessage().writeString("ITEM DATA SAVE").writeString(uuid.toString());

		message.writeBoolean(logout);
//		System.out.println(StorageManager.isEditing(uuid) + " | " + StorageManager.editSessions);
//		System.out.println(Bukkit.getPlayer(uuid) != null);
		if(StorageManager.isEditing(uuid) && Bukkit.getPlayer(uuid) != null) return;
		if(StorageManager.isBeingEdited(uuid) && Bukkit.getPlayer(uuid) != null) return;

		OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
		message.writeString(PitSim.serverName);
		for(Inventory items : enderChest) {
			for(int i = 9; i < items.getSize() - 9; i++) {
				message.writeString(serialize(player, items.getItem(i)));
			}
		}

		if(player.getPlayer() != null) {
			for(ItemStack itemStack : player.getPlayer().getInventory()) {
				message.writeString(serialize(player, itemStack));
			}

			for(ItemStack itemStack : player.getPlayer().getInventory().getArmorContents()) {
				message.writeString(serialize(player, itemStack));
			}
		} else if(cachedInventory != null) {
			for(ItemStack itemStack : cachedInventory) {
				message.writeString(serialize(player, itemStack));
			}

			for(ItemStack itemStack : armor) {
				message.writeString(serialize(player, itemStack));
			}
		}

		saving = true;
		saveTask = new BukkitRunnable() {
			@Override
			public void run() {
				OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
				if(!player.isOnline()) return;
				player.getPlayer().kickPlayer(ChatColor.RED + "Your playerdata failed to save. Please report this issue");
				Misc.alertDiscord("@everyone Save Confirmation failed for player: " + uuid + " on server: " + PitSim.serverName);
			}
		}.runTaskLater(PitSim.INSTANCE, 40);

		message.send();
	}

	public UUID getUUID() {
		return uuid;
	}

	public Inventory getEnderchest(int page) {
		if(enderChest == null) throw new DataNotLoadedException();

		return enderChest[page - 1];
	}

	public int getEnderchestItemCount(int page) {
		if(enderChest == null) throw new DataNotLoadedException();

		int total = 0;
		for(int i = 9; i < 36; i++) if(!Misc.isAirOrNull(enderChest[page - 1].getItem(i))) total++;
		return total;
	}

	public ItemStack[] getCachedInventory() {
		if(cachedInventory == null) {
			throw new DataNotLoadedException();
		}

		return cachedInventory;
	}

	public ItemStack[] getArmor() {
		if(armor == null) {
			throw new DataNotLoadedException();
		}

		return armor;
	}

	public static ItemStack deserialize(String string) {
		if(string.isEmpty()) return new ItemStack(Material.AIR);
		try {
//			return Base64.itemFrom64(string);
			return CustomSerializer.deserialize(string);
//		} catch(IOException e) {
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String serialize(OfflinePlayer player, ItemStack itemStack) {
		if(Misc.isAirOrNull(itemStack)) return "";
//		return Base64.itemTo64(itemStack);
		if(EnchantManager.isIllegalItem(itemStack) && !player.isOp()) {
			System.out.println("Did not save illegal item: " + Misc.stringifyItem(itemStack));
			LogManager.onIllegalItemRemoved(player, itemStack);
			return "";
		}
		return CustomSerializer.serialize(itemStack);
	}

	public boolean hasData() {
		return enderChest != null && cachedInventory != null && armor != null;
	}

	public boolean isSaving() {
		return saving;
	}

	protected void receiveSaveConfirmation(PluginMessage message) {

		saving = false;

		if(message.getStrings().get(0).equals("SAVE CONFIRMATION")) {

			if(saveRunnable != null) {
				saveRunnable.run();
			}
			saveRunnable = null;
			if(saveTask != null) saveTask.cancel();
		}
	}
}
