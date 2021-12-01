package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.events.OofEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class aRetroGravityMinikloon extends PitEnchant {

	public static Map<UUID, Map<UUID, Integer>> rgmMap = new HashMap<>();

	public aRetroGravityMinikloon() {
		super("Retro-Gravity Minkloon", true, ApplyType.NONE,
				"minikloon");
	}

	public void clear(Player player) {
		rgmMap.remove(player.getUniqueId());
		for(Map.Entry<UUID, Map<UUID, Integer>> entry : rgmMap.entrySet()) entry.getValue().remove(player.getUniqueId());
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		clear(killEvent.dead);
	}

	@EventHandler
	public void onOof(OofEvent event) {
		clear(event.getPlayer());
	}

	@EventHandler
	public void onOof(PlayerQuitEvent event) {
		clear(event.getPlayer());
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent) || attackEvent.fakeHit) return;

		int attackerEnchantLvl = attackEvent.getAttackerEnchantLevel(this);
		int defenderEnchantLvl = attackEvent.getDefenderEnchantLevel(this);

		int attackingCharge = getCharge(attackEvent.attacker, attackEvent.defender) - 1;
		if(attackingCharge >= 0) {
			if(attackerEnchantLvl >= 1) {
				PitPlayer pitAttacker = PitPlayer.getPitPlayer(attackEvent.attacker);
				pitAttacker.heal(getHealing(attackingCharge));
			}
			if(attackerEnchantLvl >= 2) {
				Misc.applyPotionEffect(attackEvent.attacker, PotionEffectType.DAMAGE_RESISTANCE, 20 * attackingCharge, 0, true, false);
			}
			if(attackerEnchantLvl >= 3) {
				attackEvent.increase += getDamage(attackingCharge);
//				attackEvent.veryTrueDamage += getDamage(attackingCharge);
//				AOutput.broadcast(getDamage(attackingCharge) + "");
			}
//			setCharge(attackEvent.attacker, attackEvent.defender, 0);
			setCharge(attackEvent.attacker, attackEvent.defender, Math.max(0, attackingCharge - 2));
		}

		if(defenderEnchantLvl != 0 && !attackEvent.defender.isBlocking()) {
//			if(attackEvent.attacker.getLocation().add(0, -0.1, 0).getBlock().getType() != Material.AIR) return;

//			HitCounter.incrementCounter(attackEvent.defender, this);
//			if(!HitCounter.hasReachedThreshold(attackEvent.defender, this, getStrikes())) return;

			int charge = getCharge(attackEvent.defender, attackEvent.attacker);
			setCharge(attackEvent.defender, attackEvent.attacker, Math.min(++charge, getMaxStacks(defenderEnchantLvl)));

//			PitPlayer pitDefender = PitPlayer.getPitPlayer(attackEvent.defender);
//			pitDefender.heal(getHealing(defenderEnchantLvl));
//			new BukkitRunnable() {
//				@Override
//				public void run() {
//					int charge = getCharge(attackEvent.defender, attackEvent.attacker);
//					setCharge(attackEvent.defender, attackEvent.attacker, --charge);
//				}
//			}.runTaskLater(PitSim.INSTANCE, 30 * 20);

			if(charge > 1) {
				AOutput.send(attackEvent.defender, "&d&lRGM!&7 Procced against " +
						attackEvent.attacker.getName() + " &8(" + Math.min(--charge, getMaxStacks(defenderEnchantLvl) - 1) + "x)");
				Sounds.RGM.play(attackEvent.defender);
				Sounds.RGM.play(attackEvent.attacker);
			}
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

		ALoreBuilder loreBuilder = new ALoreBuilder("&7Each combo hit on you increases", "&echarge &7(max 4) against that player by 1.",
				"&7When you hit that player:");
		if(enchantLvl >= 1) loreBuilder.addLore("&7Heal &c" + Misc.getHearts(1) + " &7x &echarge");
		if(enchantLvl >= 2) loreBuilder.addLore("&7Gain Resistance I for &91 &7x &echarge &7seconds");
		if(enchantLvl >= 3) loreBuilder.addLore("&7Deal &c+" + Misc.getHearts(5) + " &7x &echarge &7damage");
		loreBuilder.addLore("&7Remove 3 charges on a player when you hit them");
		return loreBuilder.getLore();
	}

	public int getMaxStacks(int enchantLvl) {
		return 5;
	}

	public double getHealing(int charge) {
		return charge;
	}

	public double getDamage(int charge) {
		return charge * 5;
	}

//	public int getStrikes() {
//
//		return 3;
//	}
}
