package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.Material;
import org.bukkit.Sound;
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

		if(attackerEnchantLvl >= 2) {

			int charge = getCharge(attackEvent.attacker, attackEvent.defender);
			attackEvent.increase += Math.min(charge, 5);
		}
		if(defenderEnchantLvl != 0) {
			if(attackEvent.attacker.getLocation().add(0, -0.1, 0).getBlock().getType() != Material.AIR) return;

			HitCounter.incrementCounter(attackEvent.defender, this);
			if(!HitCounter.hasReachedThreshold(attackEvent.defender, this, 3)) return;

			int charge = getCharge(attackEvent.defender, attackEvent.attacker);
			setCharge(attackEvent.defender, attackEvent.attacker, ++charge);

			if(defenderEnchantLvl >= 1) {
				attackEvent.defender.setHealth(Math.min(attackEvent.defender.getHealth() + 2.5, attackEvent.defender.getMaxHealth()));
			}
			if(defenderEnchantLvl >= 3) {
				attackEvent.selfTrueDamage += 0.5;
			}

			new BukkitRunnable() {
				@Override
				public void run() {
					int charge = getCharge(attackEvent.defender, attackEvent.attacker);
					setCharge(attackEvent.defender, attackEvent.attacker, --charge);
				}
			}.runTaskLater(PitSim.INSTANCE, 30 * 20);

			AOutput.send(attackEvent.defender, "&d&lRGM!&7 Procced against " + attackEvent.attacker.getName() + " &8(" + Math.min(charge, 5) + "x)");
			ASound.play(attackEvent.defender, Sound.ENDERMAN_HIT, 1F, 1F);
			ASound.play(attackEvent.attacker, Sound.ENDERMAN_HIT, 1F, 1F);
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

		if(enchantLvl == 1) {
			return new ALoreBuilder("&7When a player hits you from", "&7above ground &e3 times &7in a row:",
					"&7You heal &c1.25\u2764").getLore();
		}
		if(enchantLvl == 2) {
			return new ALoreBuilder("&7When a player hits you from", "&7above ground &e3 times &7in a row:",
					"&7You heal &c1.25\u2764", "&7Gain &c+1.5\u2764 &7damage vs them for 30s").getLore();
		}
		if(enchantLvl == 3) {
			return new ALoreBuilder("&7When a player hits you from", "&7above ground &e3 times &7in a row:",
					"&7You heal &c1.25\u2764", "&7Gain &c+1.5\u2764 &7damage vs them for 30s",
					"&7They take &c0.5\u2764 &7true damage").getLore();
		} else {
			return null;
		}

	}

	public float getHealing(int enchantLvl) {


		return 0.2F;
	}

	public double getDamageReduction(int enchantLvl) {

		return (int) Math.floor(Math.pow(enchantLvl, 1.3) * 2) + 2;
	}
}
