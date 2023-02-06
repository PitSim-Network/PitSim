package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.events.OofEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RetroGravityMicrocosm extends PitEnchant {
	public static Map<LivingEntity, RGMInfo> rgmGlobalMap = new HashMap<>();

	public RetroGravityMicrocosm() {
		super("Retro-Gravity Microcosm", true, ApplyType.PANTS,
				"rgm", "retro", "retrogravitymicrocosm", "retro-gravitymicrocosm", "retro-gravity-microcosm");
	}

	@EventHandler
	public void onOof(OofEvent event) {
		Player player = event.getPlayer();
		clearAttacker(player);
		clearDefender(player);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		clearAttacker(player);
		clearDefender(player);
	}

	@EventHandler
	public void onChangeWorld(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		clearAttacker(player);
		clearDefender(player);
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		clearAttacker(killEvent.getDead());
		clearDefender(killEvent.getDead());
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int attackerEnchantLvl = attackEvent.getAttackerEnchantLevel(this);
		int defenderEnchantLvl = attackEvent.getDefenderEnchantLevel(this);

		if(attackerEnchantLvl != 0) {

			int charge = getProcs(attackEvent.getDefender(), attackEvent.getAttacker());
			attackEvent.increase += getDamagePerStack(attackerEnchantLvl) * Math.min(charge, getMaxStacks(attackerEnchantLvl));
		}
		if(attackEvent.isDefenderPlayer() && defenderEnchantLvl != 0) {
			if(attackEvent.getAttacker().getLocation().add(0, -0.1, 0).getBlock().getType() != Material.AIR) return;

			HitCounter.incrementCounter(attackEvent.getDefenderPlayer(), this);
			if(!HitCounter.hasReachedThreshold(attackEvent.getDefenderPlayer(), this, getStrikes())) return;
			add(attackEvent.getAttacker(), attackEvent.getDefender());

			PitPlayer pitDefender = attackEvent.getDefenderPitPlayer();
			pitDefender.heal(getHealing(defenderEnchantLvl));

			int charge = getProcs(attackEvent.getAttacker(), attackEvent.getDefender());
			AOutput.send(attackEvent.getDefender(), "&d&lRGM!&7 Procced against " +
					attackEvent.getAttacker().getName() + " &8(" + Math.min(charge, getMaxStacks(defenderEnchantLvl)) + "x)");
			Sounds.RGM.play(attackEvent.getDefender());
			Sounds.RGM.play(attackEvent.getAttacker());

			if(pitDefender.stats != null) pitDefender.stats.rgm++;
		}
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {

		return new ALoreBuilder(
				"&7When a player hits you from",
				"&7above ground &e3 times&7:",
				"&7You heal &c" + Misc.getHearts(getHealing(enchantLvl)),
				"&7Gain &c" + Misc.getHearts(getDamagePerStack(enchantLvl)) + " &7damage vs them for 30s"
//				"&7Can have up to &6" + getMaxStacks(enchantLvl) + " &7stacks at a time"
		).getLore();
	}

	public double getDamagePerStack(int enchantLvl) {
		return enchantLvl * 0.4 - 0.2;
	}

	public int getMaxStacks(int enchantLvl) {
		return Integer.MAX_VALUE;
	}

	public double getHealing(int enchantLvl) {
		return enchantLvl * 0.4 + 1.2;
	}

	public int getStrikes() {
		return 3;
	}

	public static int getRGMStackTime() {
		return 30 * 20;
	}

	//	Attacker is the player that will take more damage, defender is the player that will deal more damage
	public static int getProcs(LivingEntity attacker, LivingEntity defender) {
		if(!rgmGlobalMap.containsKey(defender)) return 0;
		RGMInfo rgmInfo = rgmGlobalMap.get(defender);
		if(!rgmInfo.rgmPlayerProcMap.containsKey(attacker)) return 0;
		return rgmInfo.rgmPlayerProcMap.get(attacker).size();
	}

	public static void add(LivingEntity attacker, LivingEntity defender) {
		if(rgmGlobalMap.containsKey(defender)) {
			rgmGlobalMap.get(defender).add(attacker);
		} else {
			RGMInfo rgmInfo = new RGMInfo();
			rgmInfo.add(attacker);
			rgmGlobalMap.putIfAbsent(defender, rgmInfo);
		}
	}

	public static void clearDefender(LivingEntity defender) {
		rgmGlobalMap.remove(defender);
	}

	public static void clearAttacker(LivingEntity attacker) {
		for(Map.Entry<LivingEntity, RGMInfo> entry : rgmGlobalMap.entrySet()) entry.getValue().clear(attacker);
	}

	public static class RGMInfo {
		private final Map<LivingEntity, List<BukkitTask>> rgmPlayerProcMap = new HashMap<>();

		private void add(LivingEntity entity) {
			rgmPlayerProcMap.putIfAbsent(entity, new ArrayList<>());
			rgmPlayerProcMap.get(entity).add(new BukkitRunnable() {
				@Override
				public void run() {
					if(rgmPlayerProcMap.get(entity) == null) return;
					for(BukkitTask bukkitTask : rgmPlayerProcMap.get(entity)) {
						if(bukkitTask.getTaskId() != getTaskId()) continue;
						rgmPlayerProcMap.get(entity).remove(bukkitTask);
						break;
					}
				}
			}.runTaskLater(PitSim.INSTANCE, getRGMStackTime()));
		}

		public void clear(LivingEntity player) {
			rgmPlayerProcMap.remove(player);
		}
	}
}
