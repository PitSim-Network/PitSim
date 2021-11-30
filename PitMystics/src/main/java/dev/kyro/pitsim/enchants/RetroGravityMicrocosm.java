package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RetroGravityMicrocosm extends PitEnchant {

	public static Map<UUID, Map<UUID, Integer>> rgmMap = new HashMap<>();

	public RetroGravityMicrocosm() {
		super("Retro-Gravity Microcosm", true, ApplyType.PANTS,
				"rgm", "retro", "retrogravitymicrocosm", "retro-gravitymicrocosm", "retro-gravity-microcosm");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int attackerEnchantLvl = attackEvent.getAttackerEnchantLevel(this);
		int defenderEnchantLvl = attackEvent.getDefenderEnchantLevel(this);

		if(attackerEnchantLvl != 0) {

			int charge = getCharge(attackEvent.attacker, attackEvent.defender);
			attackEvent.increase += getDamagePerStack(attackerEnchantLvl) * Math.min(charge, getMaxStacks(attackerEnchantLvl));
		}
		if(defenderEnchantLvl != 0) {
			if(attackEvent.attacker.getLocation().add(0, -0.1, 0).getBlock().getType() != Material.AIR) return;

			HitCounter.incrementCounter(attackEvent.defender, this);
			if(!HitCounter.hasReachedThreshold(attackEvent.defender, this, getStrikes())) return;

			int charge = getCharge(attackEvent.defender, attackEvent.attacker);
			setCharge(attackEvent.defender, attackEvent.attacker, ++charge);

			PitPlayer pitDefender = PitPlayer.getPitPlayer(attackEvent.defender);
			pitDefender.heal(getHealing(defenderEnchantLvl));
			new BukkitRunnable() {
				@Override
				public void run() {
					int charge = getCharge(attackEvent.defender, attackEvent.attacker);
					setCharge(attackEvent.defender, attackEvent.attacker, --charge);
				}
			}.runTaskLater(PitSim.INSTANCE, 30 * 20);

			AOutput.send(attackEvent.defender, "&d&lRGM!&7 Procced against " +
					attackEvent.attacker.getName() + " &8(" + Math.min(charge, getMaxStacks(defenderEnchantLvl)) + "x)");
			Sounds.RGM.play(attackEvent.defender);
			Sounds.RGM.play(attackEvent.attacker);

			if(pitDefender.stats != null) pitDefender.stats.rgm++;
		}
	}

	public static Map<UUID, Integer> getPlayerRGMMap(Player player) {

		rgmMap.putIfAbsent(player.getUniqueId(), new HashMap<>());
		return rgmMap.get(player.getUniqueId());
	}

	public static int getCharge(Player rgmPlayer, Player player) {

		Map<UUID, Integer> playerRGMMap = getPlayerRGMMap(rgmPlayer);
		playerRGMMap.putIfAbsent(player.getUniqueId(), 0);
		return playerRGMMap.get(player.getUniqueId());
	}

	public static void setCharge(Player rgmPlayer, Player player, int amount) {

		Map<UUID, Integer> playerRGMMap = getPlayerRGMMap(rgmPlayer);
		playerRGMMap.put(player.getUniqueId(), amount);
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
}
