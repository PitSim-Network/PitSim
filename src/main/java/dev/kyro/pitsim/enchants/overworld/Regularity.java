package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Sound;
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
	public void onAttack(AttackEvent.Pre attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;

		if(!attackEvent.getAttackerEnchantMap().containsKey(this) ||
				!attackEvent.getAttackerEnchantMap().containsKey(Billionaire.INSTANCE)) return;
		attackEvent.getAttackerEnchantMap().remove(this);

		Sounds.NO.play(attackEvent.getAttackerPlayer());
		AOutput.error(attackEvent.getAttackerPlayer(), "&c&lERROR!&7 " + getDisplayName() + " &7is incompatible " +
				"with " + Billionaire.INSTANCE.getDisplayName());
	}

	@EventHandler
	public void onAttack(AttackEvent.Post attackEvent) {
		HitCounter.setCharge(attackEvent.getDefenderPlayer(), this, 0);

		if(!PlayerManager.isRealPlayer(attackEvent.getDefenderPlayer())) return;
		if(!canApply(attackEvent)) return;
		if(!fakeHits && attackEvent.isFakeHit()) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		float pitch = (float) Math.min(1.5 + HitCounter.getCharge(attackEvent.getAttackerPlayer(), this) * 0.1, 2);
		ASound.play(attackEvent.getAttackerPlayer(), Sound.NOTE_BASS, 1, pitch);

		if(toReg.contains(attackEvent.getDefender().getUniqueId())) return;

		HitCounter.incrementCharge(attackEvent.getAttackerPlayer(), this);

//		double finalDamage = attackEvent.getEvent().getFinalDamage();
//
//		double random = Math.random() * (upperBoundFinalDamage(enchantLvl) - lowerBoundFinalDamage(enchantLvl)) + lowerBoundFinalDamage(enchantLvl);
//		if(finalDamage > random) return;

		toReg.add(attackEvent.getDefender().getUniqueId());
		regCooldown.add(attackEvent.getDefender().getUniqueId());
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!toReg.contains(attackEvent.getDefender().getUniqueId())) return;

				double damage = attackEvent.getWrapperEvent().getSpigotEvent().getOriginalDamage(EntityDamageEvent.DamageModifier.BASE);
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
		return enchantLvl * 15 + 100;
	}

	public static int secondComboChance(int enchantLvl) {
		return 100;
	}

//	public static double lowerBoundFinalDamage(int enchantLvl) {
//		return enchantLvl * 0.4 + 0.6;
//	}

//	public static double upperBoundFinalDamage(int enchantLvl) {
//		return enchantLvl * 0.4 + 1.4;
//	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {

//		return new PitLoreBuilder("&7If your strike does a low amount of",
//				"&7final damage, &astrike again &7for &c" + secondHitDamage(enchantLvl) + "%",
//				"&7damage. &7(Combo enchants have a", "&e" + secondComboChance(enchantLvl) + "% &7of incrementing the combo",
//				"&7on the second hit)").getLore();

		return new PitLoreBuilder(
				"&7When you strike a player, &astrike again &7for &c" + secondHitDamage(enchantLvl) +
				"% &7damage. Does not work &7with " + Billionaire.INSTANCE.getDisplayName()
		).getLore();
	}
}
