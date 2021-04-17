package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitremake.controllers.DamageEvent;
import dev.kyro.pitremake.controllers.EnchantManager;
import dev.kyro.pitremake.controllers.PitEnchant;
import dev.kyro.pitremake.enums.ApplyType;
import org.bukkit.entity.Player;

import java.util.List;

public class PainFocus extends PitEnchant {

	public PainFocus() {
		super("Pain Focus", false, ApplyType.SWORDS,
				"painfocus", "pf", "pain-focus");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = EnchantManager.getEnchantLevel(damageEvent.attacker, this);
		if(enchantLvl == 0) return damageEvent;

		damageEvent.increasePercent += getDamage(damageEvent.attacker, enchantLvl) / 100;

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Deal &c+" + getDamage(enchantLvl) + "% &7damage per &c\u2764", "&7you're missing").getLore();
	}

	public int getDamage(int enchantLvl) {

		return (int) Math.floor(Math.pow(enchantLvl, 1.5));
	}

	public double getDamage(Player player, int enchantLvl) {

		int missingHearts = (int) ((player.getMaxHealth() - player.getHealth()) / 2);
		return missingHearts * getDamage(enchantLvl);
	}
}
