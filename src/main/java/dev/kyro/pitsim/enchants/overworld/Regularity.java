package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Regularity extends PitEnchant {
	public static Regularity INSTANCE;

	public static List<UUID> toReg = new ArrayList<>();
	public static List<UUID> regCooldown = new ArrayList<>();

	public Regularity() {
		super("Regularity", true, ApplyType.PANTS,
				"regularity", "reg");

		meleOnly = true;
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Post attackEvent) {
		if(!canApply(attackEvent)) return;
		if(!fakeHits && attackEvent.isFakeHit()) return;

		if(toReg.contains(attackEvent.getDefender().getUniqueId())) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		double finalDamage = attackEvent.getEvent().getFinalDamage();

		double random = Math.random() * (upperBoundFinalDamage(enchantLvl) - lowerBoundFinalDamage(enchantLvl)) + lowerBoundFinalDamage(enchantLvl);
		if(finalDamage > random) return;

		toReg.add(attackEvent.getDefender().getUniqueId());
		regCooldown.add(attackEvent.getDefender().getUniqueId());
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!toReg.contains(attackEvent.getDefender().getUniqueId())) return;

				double damage = attackEvent.getEvent().getOriginalDamage(EntityDamageEvent.DamageModifier.BASE);
				attackEvent.getDefender().setNoDamageTicks(0);
				attackEvent.getDefender().damage(damage * secondHitDamage(enchantLvl) / 100, attackEvent.getAttacker());
			}
		}.runTaskLater(PitSim.INSTANCE, 3L);

		new BukkitRunnable() {
			@Override
			public void run() {
				toReg.remove(attackEvent.getDefender().getUniqueId());
			}
		}.runTaskLater(PitSim.INSTANCE, 4L);
		new BukkitRunnable() {
			@Override
			public void run() {
				regCooldown.remove(attackEvent.getDefender().getUniqueId());
			}
		}.runTaskLater(PitSim.INSTANCE, 11L);
	}

	public static boolean isRegHit(LivingEntity defender) {
		return toReg.contains(defender.getUniqueId());
	}

	//	Only really used for gamble is a bit weird and not exactly correct to call it this
	public static boolean reduceDamage(int enchantLvl) {
		if(enchantLvl == 0) return true;
		return Math.random() * 100 > secondHitDamage(enchantLvl);
	}

	public static boolean skipIncrement(int enchantLvl) {
		if(enchantLvl == 0) return true;
		return Math.random() * 100 > secondComboChance(enchantLvl);
	}

	public static int secondHitDamage(int enchantLvl) {
		return enchantLvl * 15 + 30;
	}

	public static int secondComboChance(int enchantLvl) {
		return 100;
	}

	public static double lowerBoundFinalDamage(int enchantLvl) {
		return enchantLvl * 0.4 + 0.6;
	}

	public static double upperBoundFinalDamage(int enchantLvl) {
		return enchantLvl * 0.4 + 1.4;
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {

//		return new ALoreBuilder("&7If your strike does a low amount of",
//				"&7final damage, &astrike again &7for &c" + secondHitDamage(enchantLvl) + "%",
//				"&7damage. &7(Combo enchants have a", "&e" + secondComboChance(enchantLvl) + "% &7of incrementing the combo",
//				"&7on the second hit)").getLore();

//		return new ALoreBuilder("&7If your strike does a low amount of",
//				"&7final damage, &astrike again &7for &c" + secondHitDamage(enchantLvl) + "%",
//				"&7damage.").getLore();

		return new ALoreBuilder("&7Your hits have a chance to &astrike",
				"&aagain &7for &c" + secondHitDamage(enchantLvl) + "% &7damage if the final",
				"&7damage of your strike is low enough",
				"&7(&e100% &7below &c" + Misc.getHearts(lowerBoundFinalDamage(enchantLvl)) + "&7, &e0% &7above &c" +
						Misc.getHearts(upperBoundFinalDamage(enchantLvl)) + "&7)").getLore();
	}
}
