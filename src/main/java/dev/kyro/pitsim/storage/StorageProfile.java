package dev.kyro.pitsim.storage;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.exceptions.DataNotLoadedException;
import dev.kyro.pitsim.logging.LogManager;
import dev.kyro.pitsim.misc.CustomSerializer;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.UUID;

public class StorageProfile {
	protected ItemStack[] inventory = new ItemStack[StorageManager.ENDERCHEST_ITEM_SLOTS];
	protected ItemStack[] armor = new ItemStack[4];
	protected EnderchestPage[] enderchestPages = new EnderchestPage[StorageManager.MAX_ENDERCHEST_PAGES];

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

	public void loadData(PluginMessage message) {
		List<String> strings = message.getStrings();
		for(int i = 0; i < 36; i++) inventory[i] = deserialize(strings.remove(0), uuid);
		for(int i = 0; i < 4; i++) armor[i] = deserialize(strings.remove(0), uuid);
		for(int i = 0; i < enderchestPages.length; i++) enderchestPages[i] = new EnderchestPage(this, message);
		isLoaded = true;
	}

	public void saveData(boolean isLogout) {
		if(!isLoaded) throw new DataNotLoadedException();

		PluginMessage message = new PluginMessage()
				.writeString("ITEM DATA SAVE")
				.writeString(PitSim.serverName)
				.writeString(uuid.toString())
				.writeBoolean(isLogout);

		if(StorageManager.isEditing(uuid) && Bukkit.getPlayer(uuid) != null) return;
		if(StorageManager.isBeingEdited(uuid) && Bukkit.getPlayer(uuid) != null) return;

		for(EnderchestPage enderchestPage : enderchestPages) enderchestPage.writeData(message, isLogout);
		if(getOnlinePlayer() != null) {
			for(ItemStack itemStack : getOnlinePlayer().getInventory()) message.writeString(serialize(getOfflinePlayer(), itemStack, isLogout));
			for(ItemStack itemStack : getOnlinePlayer().getInventory().getArmorContents()) message.writeString(serialize(getOfflinePlayer(), itemStack, isLogout));
		} else if(inventory != null) {
			for(ItemStack itemStack : inventory) message.writeString(serialize(getOfflinePlayer(), itemStack, isLogout));
			for(ItemStack itemStack : armor) message.writeString(serialize(getOfflinePlayer(), itemStack, isLogout));
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

	public ItemStack[] getArmor() {
		if(!isLoaded) throw new DataNotLoadedException();
		return armor;
	}

	public EnderchestPage[] getEnderchestPages() {
		if(!isLoaded) throw new DataNotLoadedException();
		return enderchestPages;
	}

	public EnderchestPage getEnderchestPage(int index) {
		if(!isLoaded) throw new DataNotLoadedException();
		return enderchestPages[index];
	}

	public static ItemStack deserialize(String string, UUID informUUID) {
		if(string.isEmpty()) return new ItemStack(Material.AIR);
		return CustomSerializer.deserializeFromPlayerData(string, informUUID);
	}

	public static String serialize(OfflinePlayer player, ItemStack itemStack, boolean isLogout) {
		if(Misc.isAirOrNull(itemStack)) return "";
//		return Base64.itemTo64(itemStack);
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
}
