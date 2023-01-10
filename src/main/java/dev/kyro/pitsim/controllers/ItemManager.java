package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.aitems.misc.VeryYummyBread;
import dev.kyro.pitsim.aitems.misc.YummyBread;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ItemManager implements Listener {

	@EventHandler
	public static void onInventoryClick(InventoryClickEvent event) {
		if(event.getAction() != InventoryAction.DROP_ALL_CURSOR && event.getAction() != InventoryAction.DROP_ALL_SLOT &&
				event.getAction() != InventoryAction.DROP_ONE_CURSOR && event.getAction() != InventoryAction.DROP_ONE_SLOT)
			return;

		ItemStack itemStack = !Misc.isAirOrNull(event.getCursor()) ? event.getCursor() : event.getCurrentItem();
		Player player = (Player) event.getWhoClicked();

		PitItem pitItem = ItemFactory.getItem(itemStack);
		if(pitItem == null || !pitItem.hasDropConfirm) return;

		event.setCancelled(true);
		player.updateInventory();
		AOutput.error(player, "This item can only be dropped when your inventory is closed");
		Sounds.WARNING_LOUD.play(player);
	}

	@EventHandler(ignoreCancelled = true)
	public static void onItemDrop(PlayerDropItemEvent event) {
		ItemStack itemStack = event.getItemDrop().getItemStack();
		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(Misc.isAirOrNull(itemStack)) return;

		if(ShutdownManager.isShuttingDown) {
			event.setCancelled(true);
			AOutput.send(player, "&c&lERROR!&7 You cannot drop items while the server is shutting down");
			return;
		}

		if(player.getWorld() == MapManager.getDarkzone()) {
			Location darkAuction = AuctionDisplays.pedestalLocations[0];
			double distance = darkAuction.distance(event.getPlayer().getLocation());

			if(distance < 50) {
				event.setCancelled(true);
				AOutput.error(player, "&cYou cannot drop items in this area!");
				Sounds.WARNING_LOUD.play(player);
				return;
			}
		}

		PitItem pitItem = ItemFactory.getItem(itemStack);
		if(pitItem != null && pitPlayer.megastreak.isOnMega()) {
			if(pitItem.getClass() == YummyBread.class || pitItem.getClass() == VeryYummyBread.class) {
				AOutput.error(player, "&c&lERROR!&7 You cannot drop bread while on a megastreak");
				return;
			}
		}

		if(pitItem == null || !pitItem.hasDropConfirm) {
			if(itemStack.getType() != Material.ENDER_CHEST && itemStack.getType() != Material.TRIPWIRE_HOOK) return;
		}

		if(pitPlayer.confirmedDrop == null || !pitPlayer.confirmedDrop.equals(itemStack)) {
			event.setCancelled(true);
			new BukkitRunnable() {
				@Override
				public void run() {
					if(pitPlayer.confirmedDrop != null && pitPlayer.confirmedDrop.equals(itemStack))
						pitPlayer.confirmedDrop = null;
				}
			}.runTaskLater(PitSim.INSTANCE, 60L);
			pitPlayer.confirmedDrop = itemStack;
			AOutput.error(player, "&e&lWARNING!&7 You are about to drop an item. Click the drop button again to drop the item.");
			Sounds.WARNING_LOUD.play(player);
		} else {
			pitPlayer.confirmedDrop = null;
		}
	}
}
