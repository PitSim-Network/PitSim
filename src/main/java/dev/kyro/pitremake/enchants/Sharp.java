package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitremake.controllers.DamageEvent;
import dev.kyro.pitremake.controllers.PitEnchant;
import dev.kyro.pitremake.enums.ApplyType;
import org.bukkit.Material;

import java.util.List;

public class Sharp extends PitEnchant {

	public Sharp() {
		super("Sharp", false, ApplyType.SWORDS,
				"sharp", "s");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = damageEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return damageEvent;

		damageEvent.increasePercent += getDamage(enchantLvl) / 100D;

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Deal &c+" + getDamage(enchantLvl) + "% &7melee damage").getLore();
	}

	//	TODO: Sharp damage calculation
	
	public int getDamage(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 4;
			case 2:
				return 7;
			case 3:
				return 12;

		}

		return 0;
	}
}
