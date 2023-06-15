package net.pitsim.pitsim.storage;

import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.controllers.EnchantManager;
import net.pitsim.pitsim.controllers.ItemFactory;
import net.pitsim.pitsim.controllers.objects.PluginMessage;
import net.pitsim.pitsim.exceptions.DataNotLoadedException;
import net.pitsim.pitsim.logging.LogManager;
import net.pitsim.pitsim.misc.CustomSerializer;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.misc.PlayerItemLocation;
import net.pitsim.pitsim.misc.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StorageProfile implements Cloneable {
	private ItemStack[] inventory = new ItemStack[StorageManager.ENDERCHEST_ITEM_SLOTS];
	private ItemStack[] armor = new ItemStack[4];
	private final EnderchestPage[] enderchestPages = new EnderchestPage[StorageManager.MAX_ENDERCHEST_PAGES];

	private int defaultOverworldSet = -1;
	private int defaultDarkzoneSet = -1;
	private final Outfit[] outfits = new Outfit[9];

	private final UUID uuid;
	private BukkitTask saveTask;
	private BukkitRunnable saveRunnable;
	private boolean isLoaded;
	private boolean saving;

	public StorageProfile(UUID uuid) {
		this.uuid = uuid;
	}

	public void saveData(BukkitRunnable runnable, boolean logout) {
		saveRunnable = runnable;
		if(saving) return;
		saveData(logout);
	}

	@Override
	public StorageProfile clone() throws CloneNotSupportedException {
		return (StorageProfile) super.clone();
	}

	public void loadData(PluginMessage message, boolean view) {
		List<String> strings = message.getStrings();
		List<Integer> integers = message.getIntegers();

		defaultOverworldSet = integers.remove(0);
		defaultDarkzoneSet = integers.remove(0);
		for(int i = 0; i < 36; i++) inventory[i] = deserialize(strings.remove(0), uuid);
		for(int i = 0; i < 4; i++) armor[i] = deserialize(strings.remove(0), uuid);
		for(int i = 0; i < enderchestPages.length; i++) enderchestPages[i] = new EnderchestPage(this, message);
		for(int i = 0; i < StorageManager.OUTFITS; i++) outfits[i] = new Outfit(this, message);
		isLoaded = true;

		if(view) return;

		Player player = getOnlinePlayer();
		if(player == null || !player.isOnline()) return;
		AOutput.log("Loading online data for " + player.getName());
		AOutput.send(player, "&9&lRELOAD!&7 The server you were on was reloaded and your data has been restored!");
		Sounds.BOOSTER_REMIND.play(player);

		initializePlayerInventory(player);
	}

	public void saveData(boolean isLogout) {
		if(!isLoaded) throw new DataNotLoadedException();

		PluginMessage message = new PluginMessage()
				.writeString("ITEM DATA SAVE")
				.writeString(PitSim.serverName)
				.writeString(uuid.toString())
				.writeBoolean(isLogout)
				.writeInt(defaultOverworldSet)
				.writeInt(defaultDarkzoneSet);

		if(StorageManager.isEditing(uuid) && Bukkit.getPlayer(uuid) != null) return;
		if(StorageManager.isBeingEdited(uuid) && Bukkit.getPlayer(uuid) != null) return;

		if(getOnlinePlayer() != null) {
			for(ItemStack itemStack : getOnlinePlayer().getInventory()) message.writeString(serialize(getOfflinePlayer(), itemStack, isLogout));
			for(ItemStack itemStack : getOnlinePlayer().getInventory().getArmorContents()) message.writeString(serialize(getOfflinePlayer(), itemStack, isLogout));
		} else if(inventory != null) {
			for(ItemStack itemStack : inventory) message.writeString(serialize(getOfflinePlayer(), itemStack, isLogout));
			for(ItemStack itemStack : armor) message.writeString(serialize(getOfflinePlayer(), itemStack, isLogout));
		}
		for(EnderchestPage enderchestPage : enderchestPages) enderchestPage.writeData(message, isLogout);

		for(Outfit outfit : outfits) outfit.writeData(message);

		saving = true;
		saveTask = isLogout ? null : new BukkitRunnable() {
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
		if(string.isEmpty()) return new ItemStack(Material.AIR);
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

	public boolean isSaving() {
		return saving;
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	protected void receiveSaveConfirmation(PluginMessage message) {
		saving = false;

		if(message.getStrings().get(0).equals("SAVE CONFIRMATION")) {
			if(saveRunnable != null) saveRunnable.run();
			saveRunnable = null;
			if(saveTask != null) saveTask.cancel();
		}
	}

	public void initializePlayerInventory(Player player) {
		player.setItemOnCursor(null);
		player.getInventory().setContents(getInventory());
		player.getInventory().setArmorContents(getArmor());
		player.updateInventory();
	}

}
