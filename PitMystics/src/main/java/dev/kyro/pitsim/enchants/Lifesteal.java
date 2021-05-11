package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.DamageEvent;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;

import java.util.List;

public class Lifesteal extends PitEnchant {

	public Lifesteal() {
		super("Lifesteal", false, ApplyType.SWORDS,
				"ls", "lifesteal", "life");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = damageEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return damageEvent;

		double damage = damageEvent.getFinalDamageIncrease();

//		Bukkit.broadcastMessage(String.valueOf(damage));
//		Bukkit.broadcastMessage(String.valueOf(damage * getHealing(enchantLvl)));

		if(damageEvent.attacker.getHealth() > damageEvent.attacker.getMaxHealth() - damage * getHealing(enchantLvl)) {
			damageEvent.attacker.setHealth(damageEvent.attacker.getMaxHealth());
		} else {
			damageEvent.attacker.setHealth(damageEvent.attacker.getHealth() + damage * getHealing(enchantLvl));
		}


		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Deal &c+" + getHealing(enchantLvl) + "% &7damage vs. players", "&7above 50% HP").getLore();
	}

	public double getHealing(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 0.04;
			case 2:
				return 0.08;
			case 3:
				return 0.13;

		}

		return 0;
	}
}
