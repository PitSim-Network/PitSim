package dev.kyro.pitsim.enchants;

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
	public static Map<Player, RGMInfo> rgmGlobalMap = new HashMap<>();

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
		clearAttacker(killEvent.dead);
		clearDefender(killEvent.dead);
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int attackerEnchantLvl = attackEvent.getAttackerEnchantLevel(this);
		int defenderEnchantLvl = attackEvent.getDefenderEnchantLevel(this);

		if(attackerEnchantLvl != 0) {

			int charge = getProcs(attackEvent.defender, attackEvent.attacker);
			attackEvent.increase += getDamagePerStack(attackerEnchantLvl) * Math.min(charge, getMaxStacks(attackerEnchantLvl));
		}
		if(defenderEnchantLvl != 0) {
			if(attackEvent.attacker.getLocation().add(0, -0.1, 0).getBlock().getType() != Material.AIR) return;

			HitCounter.incrementCounter(attackEvent.defender, this);
			if(!HitCounter.hasReachedThreshold(attackEvent.defender, this, getStrikes())) return;
			add(attackEvent.attacker, attackEvent.defender);

			PitPlayer pitDefender = PitPlayer.getPitPlayer(attackEvent.defender);
			pitDefender.heal(getHealing(defenderEnchantLvl));

			int charge = getProcs(attackEvent.attacker, attackEvent.defender);
			AOutput.send(attackEvent.defender, "&d&lRGM!&7 Procced against " +
					attackEvent.attacker.getName() + " &8(" + Math.min(charge, getMaxStacks(defenderEnchantLvl)) + "x)");
			Sounds.RGM.play(attackEvent.defender);
			Sounds.RGM.play(attackEvent.attacker);

			if(pitDefender.stats != null) pitDefender.stats.rgm++;
		}
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7When a player hits you from", "&7above ground &e3 times&7:",
				"&7You heal &c" + Misc.getHearts(getHealing(enchantLvl)), "&7Gain &c" + Misc.getHearts(getDamagePerStack(enchantLvl)) + " &7damage vs them for 30s",
				"&7Can have up to &6" + getMaxStacks(enchantLvl) + " &7stacks at a time").getLore();
	}

	public double getDamagePerStack(int enchantLvl) {
		return enchantLvl * 0.4;
	}

	public int getMaxStacks(int enchantLvl) {
		return 5;
	}

	public double getHealing(int enchantLvl) {
		return enchantLvl;
	}

	public int getStrikes() {
		return 3;
	}

	//	Attacker is the player that will take more damage, defender is the player that will deal more damage
	public static int getProcs(Player attacker, Player defender) {
		if(!rgmGlobalMap.containsKey(defender)) return 0;
		RGMInfo rgmInfo = rgmGlobalMap.get(defender);
		if(!rgmInfo.rgmPlayerProcMap.containsKey(attacker)) return 0;
		return rgmInfo.rgmPlayerProcMap.get(attacker).size();
	}

	public static void add(Player attacker, Player defender) {
		if(rgmGlobalMap.containsKey(defender)) {
			rgmGlobalMap.get(defender).add(attacker);
		} else {
			RGMInfo rgmInfo = new RGMInfo();
			rgmInfo.add(attacker);
			rgmGlobalMap.putIfAbsent(defender, rgmInfo);
		}
	}

	public static void clearDefender(Player defender) {
		rgmGlobalMap.remove(defender);
	}

	public static void clearAttacker(Player attacker) {
		for(Map.Entry<Player, RGMInfo> entry : rgmGlobalMap.entrySet()) entry.getValue().clear(attacker);
	}

	public static class RGMInfo {
		private final Map<Player, List<BukkitTask>> rgmPlayerProcMap = new HashMap<>();

		private void add(Player player) {
			rgmPlayerProcMap.putIfAbsent(player, new ArrayList<>());
			rgmPlayerProcMap.get(player).add(new BukkitRunnable() {
				@Override
				public void run() {
					if(rgmPlayerProcMap.get(player) == null) return;
					for(BukkitTask bukkitTask : rgmPlayerProcMap.get(player)) {
						if(bukkitTask.getTaskId() != getTaskId()) continue;
						rgmPlayerProcMap.get(player).remove(bukkitTask);
						break;
					}
				}
			}.runTaskLater(PitSim.INSTANCE, 30 * 20));
		}

		public void clear(Player player) {
			rgmPlayerProcMap.remove(player);
		}
	}
}
