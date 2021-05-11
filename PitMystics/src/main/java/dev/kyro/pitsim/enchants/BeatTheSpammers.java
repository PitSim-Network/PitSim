package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.DamageEvent;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import org.bukkit.Material;

import java.util.List;

public class BeatTheSpammers extends PitEnchant {

	public BeatTheSpammers() {
		super("Beat the Spammers", false, ApplyType.SWORDS,
				"bts", "spammers", "beat", "beat-the-spammers", "beatthespammers");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = damageEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return damageEvent;

		if(!damageEvent.defender.getItemInHand().getType().equals(Material.BOW)) return damageEvent;
		damageEvent.increasePercent += getDamage(enchantLvl) / 100D;

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Deal &c+" + getDamage(enchantLvl) + "% &7damage vs. players", "&7holding a bow").getLore();
	}

	//	TODO: Beat The Spammers damage calculation

	public int getDamage(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 10;
			case 2:
				return 25;
			case 3:
				return 40;

		}

		return 0;
	}
}
