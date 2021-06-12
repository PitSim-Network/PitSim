package dev.kyro.pitsim.controllers;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class ItemManager implements Listener {

	public static Map<Player, ItemStack> dropConfirmMap = new HashMap<>();

	public static ItemStack enableUndroppable(ItemStack itemStack) {

		if(Misc.isAirOrNull(itemStack)) return itemStack;
		NBTItem nbtItem = new NBTItem(itemStack);

		nbtItem.setBoolean(NBTTag.UNDROPPABLE.getRef(), true);
		return nbtItem.getItem();
	}

	public static ItemStack enableDropConfirm(ItemStack itemStack) {

		if(Misc.isAirOrNull(itemStack)) return itemStack;
		NBTItem nbtItem = new NBTItem(itemStack);

		nbtItem.setBoolean(NBTTag.DROP_CONFIRM.getRef(), true);
		return nbtItem.getItem();
	}

	@EventHandler
	public static void onItemDrop(PlayerDropItemEvent event) {
		ItemStack itemStack = event.getItemDrop().getItemStack();
		Player player = event.getPlayer();
		if(Misc.isAirOrNull(itemStack)) return;
		NBTItem nbtItem = new NBTItem(itemStack);

		if(nbtItem.hasKey(NBTTag.UNDROPPABLE.getRef())) {

			AOutput.error(player, "You are not able to drop that item");
			event.setCancelled(true);
		}

		if(nbtItem.hasKey(NBTTag.DROP_CONFIRM.getRef())) {

			if(!dropConfirmMap.containsKey(player)) {

				dropConfirmMap.put(player, itemStack);
				new BukkitRunnable() {
					@Override
					public void run() {
						dropConfirmMap.remove(player);
					}
				}.runTaskLater(PitSim.INSTANCE, 60L);

				AOutput.error(player, "&e&lWARNING! &7You are about to drop an item. Click the drop button again to drop the item.");
				event.setCancelled(true);
				return;
			}

			dropConfirmMap.remove(player);
		}
	}
}
