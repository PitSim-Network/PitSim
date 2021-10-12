package dev.kyro.pitsim.controllers;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.objects.GoldenHelmet;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.inventories.HelmetGUI;
import dev.kyro.pitsim.misc.ItemRename;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class HelmetListeners implements Listener {



	private final List<Material> armorMaterials = Collections.singletonList(Material.GOLD_HELMET);

//	@EventHandler
//	public void onPlayerInteract(PlayerInteractEvent event) {
//		Player player = event.getPlayer();
//		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR))
//		{
//			// Check if the item in hand is contained in the armor list
//			if (armorMaterials.contains(player.getItemInHand().getType()))
//			{
//				event.setCancelled(true);
//			}
//		}
//	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {


		if(Misc.isAirOrNull(event.getPlayer().getItemInHand()) || event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) return;
		NBTItem nbtItem = new NBTItem(event.getPlayer().getItemInHand());

		if(nbtItem.hasKey(NBTTag.IS_GHELMET.getRef())) {


			if(!UpgradeManager.hasUpgrade(event.getPlayer(), "HELMETRY")) {
				AOutput.error(event.getPlayer(), "&cYou must first unlock &6Helmetry &cfrom the renown shop before using this item!");
				event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.VILLAGER_NO, 1F, 1F);
				return;
			}


			HelmetGUI helmetGUI = new HelmetGUI(event.getPlayer());
			helmetGUI.open();
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		if(HelmetGUI.depositPlayers.containsKey(event.getPlayer())){
			event.setCancelled(true);
			ItemStack helmet = ItemRename.renamePlayers.get(event.getPlayer());

			if(Misc.isAirOrNull(helmet)) {
				ItemRename.renamePlayers.remove(event.getPlayer());
				return;
			}

			try {
				Integer.parseInt(event.getMessage());
			} catch(Exception e) {
				AOutput.send(event.getPlayer(), "&cThat is not a valid number!");
				return;
			}

			GoldenHelmet goldenHelmet = GoldenHelmet.getHelmet(helmet, event.getPlayer());
			goldenHelmet.depositGold();

			AOutput.send(event.getPlayer(), "&aSuccessfully renamed item!");
			ItemRename.renamePlayers.remove(event.getPlayer());
		}
	}
}
