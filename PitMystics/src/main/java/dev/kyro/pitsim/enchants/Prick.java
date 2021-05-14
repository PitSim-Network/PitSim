package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.DamageEvent;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Prick extends PitEnchant {

	public Prick() {
		super("Prick", false, ApplyType.PANTS,
				"prick", "thorns");
	}

	@EventHandler
	public void onDamage(AttackEvent.Apply attackEvent) {

		int enchantLvl = EnchantManager.getEnchantLevel(attackEvent.defender, this);
		if(enchantLvl == 0) return;

		attackEvent.selfTrueDamage += getDamage(enchantLvl);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Enemies hitting you receive", "&c" + Misc.getHearts(getDamage(enchantLvl)) + " &7true damage").getLore();
	}

	public double getDamage(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 0.5;
			case 2:
				return 0.75;
			case 3:
				return 1.0;

		}

		return 0;
	}
}
