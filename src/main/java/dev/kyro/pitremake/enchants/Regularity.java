package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitremake.PitRemake;
import dev.kyro.pitremake.controllers.DamageEvent;
import dev.kyro.pitremake.controllers.PitEnchant;
import dev.kyro.pitremake.enums.ApplyType;
import dev.kyro.pitremake.misc.Misc;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Regularity extends PitEnchant {

	public static List<UUID> toReg = new ArrayList<>();

	public Regularity() {
		super("Regularity", true, ApplyType.SWORDS,
				"regularity", "reg");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		if(toReg.contains(damageEvent.attacker.getUniqueId())) return damageEvent;

		int enchantLvl = damageEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return damageEvent;

		double finalDamage = damageEvent.event.getFinalDamage();
		if(finalDamage >= 3) return damageEvent;

		toReg.add(damageEvent.attacker.getUniqueId());

		new BukkitRunnable() {
			@Override
			public void run() {

				double damage = damageEvent.event.getOriginalDamage(EntityDamageEvent.DamageModifier.BASE);
				damageEvent.defender.setNoDamageTicks(0);
				damageEvent.defender.damage(damage, damageEvent.attacker);
			}
		}.runTaskLater(PitRemake.INSTANCE, 3L);

		new BukkitRunnable() {
			@Override
			public void run() {

				toReg.remove(damageEvent.attacker.getUniqueId());
			}
		}.runTaskLater(PitRemake.INSTANCE, 4L);

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7If the final damage of your strike", "&7deals less than &c" + Misc.getHearts(3) + " &7damage,",
				"&7strike again in &a0.1s &7for &c75%", "&7damage").getLore();
	}
}
