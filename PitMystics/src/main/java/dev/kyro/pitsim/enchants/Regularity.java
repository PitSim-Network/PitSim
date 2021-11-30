package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.pitevents.Juggernaut;
import org.bukkit.entity.Player;
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
		if(!fakeHits && attackEvent.fakeHit) return;

		if(toReg.contains(attackEvent.defender.getUniqueId())) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		double finalDamage = attackEvent.event.getFinalDamage();
		if(finalDamage >= maxFinalDamage(enchantLvl)) return;
		if(attackEvent.defender == Juggernaut.juggernaut) return;

		toReg.add(attackEvent.defender.getUniqueId());
		regCooldown.add(attackEvent.defender.getUniqueId());
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!toReg.contains(attackEvent.defender.getUniqueId())) return;

				double damage = attackEvent.event.getOriginalDamage(EntityDamageEvent.DamageModifier.BASE);
				attackEvent.defender.setNoDamageTicks(0);
				attackEvent.defender.damage(damage * secondHitDamage(enchantLvl) / 100, attackEvent.attacker);
			}
		}.runTaskLater(PitSim.INSTANCE, 3L);

		new BukkitRunnable() {
			@Override
			public void run() {
				toReg.remove(attackEvent.defender.getUniqueId());
			}
		}.runTaskLater(PitSim.INSTANCE, 4L);
		new BukkitRunnable() {
			@Override
			public void run() {
				regCooldown.remove(attackEvent.defender.getUniqueId());
			}
		}.runTaskLater(PitSim.INSTANCE, 11L);
	}

	public static boolean isRegHit(Player defender) {

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
		return enchantLvl * 15 + 15;
	}

	public static double maxFinalDamage(int enchantLvl) {
		return enchantLvl * 0.4 + 1.2;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7If your strike deals less than &c" + Misc.getHearts(maxFinalDamage(enchantLvl)),
				"&7final damage, &astrike again &7for &c" + secondHitDamage(enchantLvl) + "%",
				"&7damage. &7(Combo enchants have a", "&e" + secondComboChance(enchantLvl) + "% &7of incrementing the combo",
				"&7on the second hit)").getLore();
	}
}
