package dev.kyro.pitsim.misc;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.inventories.GemGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class TotallyLegitGem implements Listener {
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if(Misc.isAirOrNull(event.getPlayer().getItemInHand())) return;
		if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) return;
		NBTItem nbtItem = new NBTItem(event.getPlayer().getItemInHand());

		if(nbtItem.hasKey(NBTTag.IS_GEM.getRef())) {
			GemGUI gemGUI = new GemGUI(event.getPlayer());
			gemGUI.open();
		}
	}
}
