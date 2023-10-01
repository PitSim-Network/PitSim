package net.pitsim.spigot.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import net.md_5.bungee.api.chat.TextComponent;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.controllers.EnchantManager;
import net.pitsim.spigot.controllers.ItemFactory;
import net.pitsim.spigot.controllers.objects.PluginMessage;
import net.pitsim.spigot.exceptions.DataNotLoadedException;
import net.pitsim.spigot.logging.LogManager;
import net.pitsim.spigot.misc.CustomSerializer;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.PlayerItemLocation;
import net.pitsim.spigot.misc.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StorageProfile implements Cloneable {
	private transient ItemStack[] inventory = new ItemStack[StorageManager.ENDERCHEST_ITEM_SLOTS];
	private transient ItemStack[] armor = new ItemStack[4];
	private final String[] inventoryData = new String[StorageManager.ENDERCHEST_ITEM_SLOTS];
	private final String[] armorData = new String[4];
	private final EnderchestPage[] enderchestPages = new EnderchestPage[StorageManager.MAX_ENDERCHEST_PAGES];

	private int defaultOverworldSet = -1;
	private int defaultDarkzoneSet = -1;
	private final Outfit[] outfits = new Outfit[9];

	private transient UUID uuid;
	private transient boolean isLoaded;
	private transient File saveFile;

	public StorageProfile(UUID uuid) {
		this.uuid = uuid;
		this.saveFile = StorageManager.getStorageFile(uuid);
		this.isLoaded = true;

		Arrays.fill(inventoryData, "");
		Arrays.fill(armorData, "");

		for(int i = 0; i < outfits.length; i++) {
			outfits[i] = new Outfit(i);
		}

		for(int i = 0; i < enderchestPages.length; i++) {
			enderchestPages[i] = new EnderchestPage(i);
		}

		loadData(false, uuid);
	}

	public void saveData(BukkitRunnable runnable, boolean logout) {
		saveData();
	}

	@Override
	public StorageProfile clone() throws CloneNotSupportedException {
		return (StorageProfile) super.clone();
	}

	public void loadData(boolean view, UUID uuid) {
		inventory = new ItemStack[StorageManager.ENDERCHEST_ITEM_SLOTS];
		armor = new ItemStack[4];

		this.uuid = uuid;
		this.saveFile = StorageManager.getStorageFile(uuid);
		this.isLoaded = true;

		for(int i = 0; i < inventoryData.length; i++) {
			ItemStack itemStack = deserialize(inventoryData[i], uuid);
			inventory[i] = itemStack;
		}

		for(int i = 0; i < armorData.length; i++) {
			armor[i] = deserialize(armorData[i], uuid);
		}

		for(EnderchestPage enderchestPage : enderchestPages) {
			enderchestPage.init(this);
		}
		for(Outfit outfit : outfits) {
			outfit.init(this);
		}

		if(view) return;

		Player player = getOnlinePlayer();
		if(player == null || !player.isOnline()) return;
		AOutput.log("Loading online data for " + player.getName());
		AOutput.send(player, "&9&lRELOAD!&7 The server you were on was reloaded and your data has been restored!");
		Sounds.BOOSTER_REMIND.play(player);

		initializePlayerInventory(player);
	}

	public void saveData() {
		if(!isLoaded) throw new DataNotLoadedException();

		if(StorageManager.isEditing(uuid) && Bukkit.getPlayer(uuid) != null) return;
		if(StorageManager.isBeingEdited(uuid) && Bukkit.getPlayer(uuid) != null) return;

		for(int i = 0; i < inventory.length; i++) {
			inventoryData[i] = serialize(getOfflinePlayer(), inventory[i], false);
		}

		for(int i = 0; i < armor.length; i++) {
			armorData[i] = serialize(getOfflinePlayer(), armor[i], false);
		}

		for(EnderchestPage enderchestPage : enderchestPages) {
			enderchestPage.save();
		}

		for(Outfit outfit : outfits) {
			outfit.saveData();
		}

		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String json = gson.toJson(this);
			FileWriter writer = new FileWriter(saveFile.toPath().toString());
			writer.write(json);
			writer.close();

		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public boolean storeInvAndArmor(Map<PlayerItemLocation, ItemStack> proposedChanges, List<PlayerItemLocation> emptySlots,
									boolean useOnlinePlayer) {
		Player player = getOnlinePlayer();
		loop:
		for(PlayerItemLocation itemLocation : PlayerItemLocation
				.getLocations(PlayerItemLocation.Location.INVENTORY, PlayerItemLocation.Location.ARMOR)) {
			ItemStack itemStack = proposedChanges.containsKey(itemLocation) ? proposedChanges.get(itemLocation) :
					itemLocation.getItem(getUniqueID(), useOnlinePlayer).clone();
			proposedChanges.put(itemLocation, new ItemStack(Material.AIR));
			if(Misc.isAirOrNull(itemStack)) continue;
			System.out.println("checking to store: " + itemLocation.getIdentifier());
			if(emptySlots.contains(itemLocation)) continue;
			System.out.println("trying to store: " + itemLocation.getIdentifier());

			int maxStackSize = itemStack.getMaxStackSize();

			boolean foundAnyEnabled = false;
			for(EnderchestPage enderchestPage : getEnderchestPages()) {
				if(!enderchestPage.isWardrobeEnabled()) continue;
				foundAnyEnabled = true;
				for(PlayerItemLocation testLocation : PlayerItemLocation.enderchest(enderchestPage.getIndex())) {
					ItemStack testStack = proposedChanges.containsKey(testLocation) ? proposedChanges.get(testLocation) :
							testLocation.getItem(getUniqueID(), useOnlinePlayer).clone();
					if(!testStack.isSimilar(itemStack)) continue;
					int amountToAdd = Math.min(itemStack.getAmount(), maxStackSize - testStack.getAmount());
					testStack.setAmount(testStack.getAmount() + amountToAdd);
					itemStack.setAmount(Math.max(itemStack.getAmount() - amountToAdd, 0));
					proposedChanges.put(testLocation, testStack);
					System.out.println("found similar stack: " + testLocation.getIdentifier());
					if(itemStack.getAmount() == 0) continue loop;
				}
			}

			for(EnderchestPage enderchestPage : getEnderchestPages()) {
				if(!enderchestPage.isWardrobeEnabled()) continue;
				for(PlayerItemLocation testLocation : PlayerItemLocation.enderchest(enderchestPage.getIndex())) {
					ItemStack testStack = proposedChanges.containsKey(testLocation) ? proposedChanges.get(testLocation) :
							testLocation.getItem(getUniqueID(), useOnlinePlayer).clone();
					if(!Misc.isAirOrNull(testStack)) continue;
					proposedChanges.put(testLocation, itemStack);
					System.out.println("found empty slot: " + testLocation.getIdentifier());
					continue loop;
				}
			}

			if(player != null) {
				if(foundAnyEnabled) {
					AOutput.error(player, "&c&lERROR!&7 Not enough space for your current inventory and armor");
				} else {
					AOutput.error(player, "&c&lERROR!&7 You do not have any enderchests with wardrobe enabled");
				}
				Sounds.NO.play(player);
			}
			return false;
		}
		return true;
	}

	public UUID getUniqueID() {
		return uuid;
	}

	public Player getOnlinePlayer() {
		return Bukkit.getPlayer(uuid);
	}

	public OfflinePlayer getOfflinePlayer() {
		return Bukkit.getOfflinePlayer(uuid);
	}

	public ItemStack[] getInventory() {
		if(!isLoaded) throw new DataNotLoadedException();
		return inventory;
	}

	public void setInventory(ItemStack[] inventory) {
		if(!isLoaded) throw new DataNotLoadedException();
		this.inventory = inventory;
	}

	public ItemStack[] getArmor() {
		if(!isLoaded) throw new DataNotLoadedException();
		return armor;
	}

	public void setArmor(ItemStack[] armor) {
		if(!isLoaded) throw new DataNotLoadedException();
		this.armor = armor;
	}

	public EnderchestPage[] getEnderchestPages() {
		if(!isLoaded) throw new DataNotLoadedException();
		return enderchestPages;
	}

	public EnderchestPage getEnderchestPage(int index) {
		if(!isLoaded) throw new DataNotLoadedException();
		return enderchestPages[index];
	}

	public int getDefaultOverworldSet() {
		return defaultOverworldSet;
	}

	public void setDefaultOverworldSet(int defaultOverworldSet) {
		this.defaultOverworldSet = defaultOverworldSet;
	}

	public int getDefaultDarkzoneSet() {
		return defaultDarkzoneSet;
	}

	public void setDefaultDarkzoneSet(int defaultDarkzoneSet) {
		this.defaultDarkzoneSet = defaultDarkzoneSet;
	}

	public Outfit[] getOutfits() {
		return outfits;
	}

	public static ItemStack deserialize(String string, UUID informUUID) {
		if(string == null || string.isEmpty()) return new ItemStack(Material.AIR);
		return CustomSerializer.deserializeFromPlayerData(string, informUUID);
	}

	public static String serialize(OfflinePlayer player, ItemStack itemStack, boolean isLogout) {
		if(Misc.isAirOrNull(itemStack)) return "";
//		return Base64.itemTo64(itemStack);

		if(isLogout && ItemFactory.isTutorialItem(itemStack)) {
			AOutput.log("Did not save tutorial item: " + Misc.stringifyItem(itemStack));
			return "";
		}

		if(isLogout && EnchantManager.isIllegalItem(itemStack) && !player.isOp()) {
			AOutput.log("Did not save illegal item: " + Misc.stringifyItem(itemStack));
			LogManager.onIllegalItemRemoved(player, itemStack);
			return "";
		}
		return CustomSerializer.serialize(itemStack);
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	public void initializePlayerInventory(Player player) {
		player.setItemOnCursor(null);
		player.getInventory().setContents(getInventory());
		player.getInventory().setArmorContents(getArmor());
		player.updateInventory();
	}

}
