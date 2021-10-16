package dev.kyro.pitsim.controllers;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.GoldenHelmet;
import dev.kyro.pitsim.controllers.objects.HelmetAbility;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
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

			GoldenHelmet goldenHelmet = GoldenHelmet.getHelmetItem(helmet, event.getPlayer());
			if(goldenHelmet.getInventorySlot(event.getPlayer()) == -1) {
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
		if(NonManager.getNon(attackEvent.attacker) != null || NonManager.getNon(attackEvent.defender) != null) return;
		GoldenHelmet attackerHelmet = getHelmetInstance(attackEvent.attacker);
		GoldenHelmet defenderHelmet = getHelmetInstance(attackEvent.defender);

		int attackLevel = 0;
		if(attackerHelmet != null) attackLevel = HelmetSystem.getLevel(attackerHelmet.gold);
		if(attackerHelmet != null && attackerHelmet.getInventorySlot(attackEvent.attacker) == -2) attackEvent.increasePercent += HelmetSystem.getTotalStacks(HelmetSystem.Passive.DAMAGE, attackLevel - 1) / 100D;

		int defenderLevel = 0;
		if(defenderHelmet != null) defenderLevel = HelmetSystem.getLevel(defenderHelmet.gold);
		if(defenderHelmet != null && attackerHelmet.getInventorySlot(attackEvent.attacker) == -2) attackEvent.multiplier.add(Misc.getReductionMultiplier(HelmetSystem.getTotalStacks(HelmetSystem.Passive.DAMAGE_REDUCTION, defenderLevel - 1)));

	}

	@EventHandler
	public void onKill(KillEvent killEvent) {

		if(NonManager.getNon(killEvent.killer) != null) return;
		if(killEvent.killer.getInventory().getHelmet().getType() != Material.GOLD_HELMET) return;

		GoldenHelmet helmet = getHelmetInstance(killEvent.killer);
		if(helmet == null) return;

		int level = HelmetSystem.getLevel(helmet.gold);

		killEvent.goldMultipliers.add(1 + HelmetSystem.getTotalStacks(HelmetSystem.Passive.GOLD_BOOST,  level  - 1) / 100D);

		killEvent.xpMultipliers.add(1 + HelmetSystem.getTotalStacks(HelmetSystem.Passive.XP_BOOST, level - 1) / 100D);


	}

	public static GoldenHelmet getHelmetInstance(Player player) {
		if(Misc.isAirOrNull(player.getInventory().getHelmet())) return null;
		return GoldenHelmet.getHelmetItem(player.getInventory().getHelmet(), player);

	}

	public List<Player> crouchPlayers = new ArrayList<>();

	@EventHandler
	public void onCrouch(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();
		if(!event.isSneaking()) return;

		if(!crouchPlayers.contains(player)) {
			crouchPlayers.add(player);
			new BukkitRunnable() {
				@Override
				public void run() {

					crouchPlayers.remove(player);
				}
			}.runTaskLater(PitSim.INSTANCE, 7L);
			return;
		}
		crouchPlayers.remove(player);

		int helmSlot = getInventorySlot(player);
		if(helmSlot == -1) return;

		ItemStack helm = null;
		if(helmSlot == -2) helm = player.getInventory().getHelmet();
		else return;


		GoldenHelmet goldenHelmet = GoldenHelmet.getHelmetItem(helm, player);
		assert goldenHelmet != null;

		if(goldenHelmet.ability == null) {
			AOutput.error(player, "&6&lGOLDEN HELMET! &cNo ability selected!");
			event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.VILLAGER_NO, 1F, 1F);
			return;
		}

		if(goldenHelmet.ability.isTogglable) {
			if(HelmetAbility.toggledHelmets.contains(goldenHelmet)) {
				goldenHelmet.deactivate();
			} else {
				for(GoldenHelmet helmet : GoldenHelmet.INSTANCE.getHelmetsFromPlayer(player)) {
					if(helmet.ability == null) continue;
					if(helmet.ability.refName.equals(goldenHelmet.ability.refName) && HelmetAbility.toggledHelmets.contains(helmet)) {
						AOutput.error(player, "&cAbility already activated!");
						return;
					}
				}
				HelmetAbility.toggledHelmets.add(goldenHelmet);
				goldenHelmet.ability.onActivate();
			}
		} else {
			goldenHelmet.ability.onProc();
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		crouchPlayers.remove(event.getPlayer());
		for(GoldenHelmet goldenHelmet : GoldenHelmet.INSTANCE.getHelmetsFromPlayer(event.getPlayer())) {
			HelmetAbility.toggledHelmets.remove(goldenHelmet);
			if(goldenHelmet.ability != null) goldenHelmet.deactivate();
		}

	}

	@EventHandler
	public void onRemove(InventoryClickEvent event) {

		Player player = (Player) event.getWhoClicked();

		if(event.getClickedInventory().getType() != InventoryType.PLAYER) return;
		if(Misc.isAirOrNull(player.getInventory().getHelmet())) return;
		if(event.getSlot() == 39 && player.getInventory().getHelmet().getType() == Material.GOLD_HELMET) {
			for(GoldenHelmet goldenHelmet : GoldenHelmet.INSTANCE.getHelmetsFromPlayer(player)) {
				if(goldenHelmet.ability != null) goldenHelmet.deactivate();
			}

		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if(event.getItemDrop().getItemStack().getType() != Material.GOLD_HELMET) return;
		for(GoldenHelmet goldenHelmet : GoldenHelmet.INSTANCE.getHelmetsFromPlayer(event.getPlayer())) {
			if(goldenHelmet.ability != null) goldenHelmet.deactivate();
		}
	}

	public static int getInventorySlot(Player owner) {
		if(Misc.isAirOrNull(owner.getInventory().getHelmet())) return -1;
		if(owner.getInventory().getHelmet().getType() == Material.GOLD_HELMET) {

			NBTItem playerItem = new NBTItem(owner.getInventory().getHelmet());

			if(playerItem.hasKey(NBTTag.GHELMET_UUID.getRef())) return -2;
		}
		for(int i = 0; i < owner.getInventory().getSize(); i++) {
			if(Misc.isAirOrNull(owner.getInventory().getItem(i))) continue;
			if(owner.getInventory().getItem(i).getType() == Material.GOLD_HELMET) {
				NBTItem playerItem = new NBTItem(owner.getInventory().getItem(i));

				if(!playerItem.hasKey(NBTTag.GHELMET_UUID.getRef())) continue;
				return i;
			}
		}
		return -1;
	}
}
