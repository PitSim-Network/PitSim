package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.DamageEvent;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Regularity extends PitEnchant {

	public static List<UUID> toReg = new ArrayList<>();

	public Regularity() {
		super("Regularity", true, ApplyType.PANTS,
				"regularity", "reg");

		meleOnly = true;
	}

	@EventHandler
	public void onDamage(AttackEvent.Apply attackEvent) {

		if(toReg.contains(attackEvent.attacker.getUniqueId())) return;

		int enchantLvl = attackEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return;

		double finalDamage = attackEvent.event.getFinalDamage();
		if(finalDamage >= 3) return;

		toReg.add(attackEvent.attacker.getUniqueId());

		new BukkitRunnable() {
			@Override
			public void run() {

				double damage = attackEvent.event.getOriginalDamage(EntityDamageEvent.DamageModifier.BASE);
				attackEvent.defender.setNoDamageTicks(0);
				attackEvent.defender.damage(damage, attackEvent.attacker);
			}
		}.runTaskLater(PitSim.INSTANCE, 3L);

		new BukkitRunnable() {
			@Override
			public void run() {

				toReg.remove(attackEvent.attacker.getUniqueId());
			}
		}.runTaskLater(PitSim.INSTANCE, 4L);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7If the final damage of your strike", "&7deals less than &c" + Misc.getHearts(3) + " &7damage,",
				"&7strike again in &a0.1s &7for &c75%", "&7damage").getLore();
	}
}
