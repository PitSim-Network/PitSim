package dev.kyro.pitsim.storage;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.UUID;

public class EnderchestPanel extends AGUIPanel {

	public StorageProfile profile;
	public EnderchestPanel(AGUI gui, UUID storagePlayer) {
		super(gui);

		profile = StorageManager.getProfile(storagePlayer);
	}

	@Override
	public String getName() {
		return "Enderchest";
	}

	@Override
	public int getRows() {
		return 4;
	}

	@Override
	public void onClick(InventoryClickEvent event) {

		EnderchestGUI.EnderchestPages rank = EnderchestGUI.EnderchestPages.getRank(player);

		if(event.getClickedInventory().getHolder() != this) return;
		int slot = event.getSlot();

		if(slot == 8 && !player.getUniqueId().equals(profile.getUUID())) {
			new BukkitRunnable() {
				@Override
				public void run() {
					player.openInventory(Objects.requireNonNull(StorageManager.getSession(player)).inventory.getInventory());
				}
			}.runTaskLater(PitSim.INSTANCE, 2);
		}

		if(slot < 9 || slot >= 27) return;

		if((slot - 9) + 1 > rank.pages) {
			event.setCancelled(true);
			AOutput.error(player, "&5&lRANK REQUIRED!&7 Browse ranks at &d&nhttps://store.pitsim.net");
			return;
		}

		if(!profile.hasData() || profile.isSaving()) return;

		Inventory inventory = profile.getEnderchest(slot - 8);
		player.openInventory(inventory);
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		EnderchestGUI.EnderchestPages rank = EnderchestGUI.EnderchestPages.getRank(player);

		for(int i = 0; i < 9; i++) {
			getInventory().setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
		}

		for(int i = 27; i < 36; i++) {
			getInventory().setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
		}

		for(int i = 9; i < 27; i++) {
			int page = (i - 9) + 1;

			AItemStackBuilder stackBuilder;
			ALoreBuilder lore = new ALoreBuilder();
			String enderchestName = "&5&lENDERCHEST &7Page " + (i - 8);

			if(!(page > rank.pages)) {
				stackBuilder = new AItemStackBuilder(Material.ENDER_CHEST);
				stackBuilder.setName(enderchestName);
				lore.addLore(
						"&7Status: &aUnlocked",
						"&7Items: &d" + profile.getEnderchestItemCount(page) + "&7/&d27"
				);
				Misc.addEnchantGlint(stackBuilder.getItemStack());
			}
			else {
				stackBuilder = new AItemStackBuilder(Material.BARRIER);
				stackBuilder.setName(enderchestName);
				lore.addLore(
						"&7Status: &cLocked",
						"&7Required Rank: " + EnderchestGUI.EnderchestPages.getMinimumRequiredRank(page).rankName,
						"&7Store: &d&nstore.pitsim.net"
				);
			}

			stackBuilder.setLore(lore);
			getInventory().setItem(i, stackBuilder.getItemStack());

			if(player.getUniqueId().equals(profile.getUUID())) return;

			AItemStackBuilder builder = new AItemStackBuilder(Material.CHEST);
			builder.setName("&6View Inventory");
			getInventory().setItem(8, builder.getItemStack());
		}
	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
