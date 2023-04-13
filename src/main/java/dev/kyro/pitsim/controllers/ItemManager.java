package dev.kyro.pitsim.controllers;

import de.myzelyam.api.vanish.VanishAPI;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.aitems.MysticFactory;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.aitems.misc.SoulPickup;
import dev.kyro.pitsim.aitems.misc.VeryYummyBread;
import dev.kyro.pitsim.aitems.misc.YummyBread;
import dev.kyro.pitsim.controllers.objects.FakeItem;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.PitEntityType;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ItemManager implements Listener {
	public static Map<UUID, List<ItemStack>> updatedItems = new HashMap<>();
	public static List<FakeItem> fakeItems = new ArrayList<>();
	public static Map<Item, Player> soulPickupMap = new HashMap<>();
	public static Map<Player, Long> soulNotificationCooldownMap = new HashMap<>();

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(FakeItem fakeItem : new ArrayList<>(fakeItems)) {
					if(fakeItem.hasBeenRemoved()) {
						fakeItem.remove();
						continue;
					}

					for(Entity entity : fakeItem.getLocation().getWorld().getNearbyEntities(fakeItem.getLocation(), 1, 1, 1)) {
						if(!Misc.isEntity(entity, PitEntityType.REAL_PLAYER)) continue;
						Player player = (Player) entity;
						if(!fakeItem.getViewers().contains(player) || VanishAPI.isInvisible(player) ||
								!Misc.hasSpaceForItem(player, fakeItem.getItemStack())) continue;

						fakeItem.pickup(player);
						break;
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0, 3);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!updatedItems.containsKey(player.getUniqueId()) || !player.isOnline()) return;
				List<ItemStack> itemList = updatedItems.remove(player.getUniqueId());
				for(ItemStack itemStack : itemList) {
					String itemName = itemStack.getItemMeta().getDisplayName();
					String haveHas = itemName.endsWith("s") ? "have" : "has";
					AOutput.send(player, "&a&lITEM UPDATED!&7 Your " + itemName + "&7 " + haveHas + " been updated");
				}
			}
		}.runTaskLater(PitSim.INSTANCE, 10L);
	}

	@EventHandler
	public static void onInventoryClick(InventoryClickEvent event) {
		if(event.getAction() != InventoryAction.DROP_ALL_CURSOR && event.getAction() != InventoryAction.DROP_ALL_SLOT &&
				event.getAction() != InventoryAction.DROP_ONE_CURSOR && event.getAction() != InventoryAction.DROP_ONE_SLOT)
			return;

		ItemStack itemStack = !Misc.isAirOrNull(event.getCursor()) ? event.getCursor() : event.getCurrentItem();
		Player player = (Player) event.getWhoClicked();

		PitItem pitItem = ItemFactory.getItem(itemStack);
		if(pitItem == null || !pitItem.hasDropConfirm) return;
		if(pitItem.isMystic && !MysticFactory.isImportant(itemStack)) return;

		event.setCancelled(true);
		player.updateInventory();
		AOutput.error(player, "This item can only be dropped when your inventory is closed");
		Sounds.WARNING_LOUD.play(player);
	}

	@EventHandler
	public static void onUnload(ChunkUnloadEvent event) {
		PitItem pitItem = ItemFactory.getItem(SoulPickup.class);
		for(Entity entity : new ArrayList<>(Arrays.asList(event.getChunk().getEntities()))) {
			if(!(entity instanceof Item)) continue;
			Item item = (Item) entity;
			ItemStack itemStack = item.getItemStack();
			if(!pitItem.isThisItem(itemStack)) continue;
			item.remove();
		}
	}

	@EventHandler
	public static void onPickup(PlayerPickupItemEvent event) {
		Item droppedItem = event.getItem();
		ItemStack itemStack = droppedItem.getItemStack();
		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		SoulPickup pitItem = ItemFactory.getItem(SoulPickup.class);
		if(pitItem.isThisItem(itemStack)) {
			int souls = pitItem.getSouls(itemStack);
			event.setCancelled(true);

			if(soulPickupMap.containsKey(droppedItem)) {
				Player designatedPlayer = soulPickupMap.get(droppedItem);
				if(designatedPlayer != player) {
					long lastNotifyTick = soulNotificationCooldownMap.getOrDefault(player, 0L);
					if(lastNotifyTick + 40 > PitSim.currentTick) return;
					AOutput.error(player, "&c&lERROR!&7 You cannot pick up this soul");
					soulNotificationCooldownMap.put(player, PitSim.currentTick);
					return;
				}
			}

			soulPickupMap.remove(droppedItem);
			droppedItem.remove();
			pitPlayer.taintedSouls += souls;

			Sounds.SOUL_PICKUP.play(player);
			AOutput.send(player, "&5&lHARVEST!&7 You harvested &f" + souls + " soul" + (souls == 1 ? "" : "s"));
		}
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
			if(pitItem instanceof YummyBread || pitItem instanceof VeryYummyBread) {
				event.setCancelled(true);
				AOutput.error(player, "&c&lERROR!&7 You cannot drop bread while on a megastreak");
				return;
			}
		}

		if(!MysticFactory.isImportant(itemStack)) return;
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
