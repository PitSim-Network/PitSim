package dev.kyro.pitsim.controllers;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.GoldenHelmet;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.inventories.HelmetGUI;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
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


			ASound.play(event.getPlayer(), Sound.ANVIL_USE, 1, 2);
			HelmetGUI helmetGUI = new HelmetGUI(event.getPlayer());
			helmetGUI.open();
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		if(HelmetGUI.depositPlayers.containsKey(event.getPlayer())){
			event.setCancelled(true);
			ItemStack helmet = HelmetGUI.depositPlayers.get(event.getPlayer());

			if(Misc.isAirOrNull(helmet)) {
				HelmetGUI.depositPlayers.remove(event.getPlayer());
				return;
			}

			int gold = 0;

			try {
				gold = Integer.parseInt(event.getMessage());
			} catch(Exception e) {
				AOutput.send(event.getPlayer(), "&cThat is not a valid number!");
				HelmetGUI.depositPlayers.remove(event.getPlayer());
				event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.VILLAGER_NO, 1F, 1F);
				return;
			}

			double finalBalance = PitSim.VAULT.getBalance((Player) event.getPlayer()) - gold;
			if(finalBalance < 0) {
				AOutput.send(event.getPlayer(), "&cYou do not have enough gold!");
				HelmetGUI.depositPlayers.remove(event.getPlayer());
				event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.VILLAGER_NO, 1F, 1F);
				return;
			}
			PitSim.VAULT.withdrawPlayer((Player) event.getPlayer(), gold);

			GoldenHelmet goldenHelmet = GoldenHelmet.getHelmet(helmet, event.getPlayer());
			if(goldenHelmet.getInventorySlot() == -1) {
				AOutput.send(event.getPlayer(), "&cUnable to find helmet!");
				HelmetGUI.depositPlayers.remove(event.getPlayer());
				event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.VILLAGER_NO, 1F, 1F);
				return;
			}
			goldenHelmet.depositGold(gold);

			AOutput.send(event.getPlayer(), "&aSuccessfully deposited gold!");
			HelmetGUI.depositPlayers.remove(event.getPlayer());
			ASound.play(event.getPlayer(), Sound.ZOMBIE_METAL, 1, 2);
		}

	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		GoldenHelmet attackerHelmet = getHelmet(attackEvent.attacker);
		GoldenHelmet defenderHelmet = getHelmet(attackEvent.defender);

		int attackLevel = 0;
		if(attackerHelmet != null) attackLevel = HelmetSystem.getLevel(attackerHelmet.gold);
		if(attackerHelmet != null) attackEvent.increasePercent += HelmetSystem.getTotalStacks(HelmetSystem.Passive.DAMAGE, attackLevel) / 100D;

		int defenderLevel = 0;
		if(defenderHelmet != null) defenderLevel = HelmetSystem.getLevel(defenderHelmet.gold);
		if(defenderHelmet != null) attackEvent.multiplier.add(Misc.getReductionMultiplier(HelmetSystem.getTotalStacks(HelmetSystem.Passive.DAMAGE_REDUCTION, attackLevel)));

	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		GoldenHelmet helmet = getHelmet(killEvent.killer);
		if(helmet == null) return;

		int level = HelmetSystem.getLevel(helmet.gold);

		killEvent.goldMultipliers.add(1 + HelmetSystem.getTotalStacks(HelmetSystem.Passive.GOLD_BOOST, level) / 100D);

		killEvent.xpMultipliers.add(1 + HelmetSystem.getTotalStacks(HelmetSystem.Passive.XP_BOOST, level) / 100D);


	}

	public static GoldenHelmet getHelmet(Player player) {
		if(Misc.isAirOrNull(player.getInventory().getHelmet())) return null;
		return GoldenHelmet.getHelmet(player.getInventory().getHelmet(), player);

	}
}
