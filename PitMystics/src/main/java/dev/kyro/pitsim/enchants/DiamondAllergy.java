package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.DamageEvent;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DiamondAllergy extends PitEnchant {

	public DiamondAllergy() {
		super("Diamond Allergy", false, ApplyType.PANTS,
				"da", "dallergy", "diamondallergy", "diamond-allergy");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = EnchantManager.getEnchantLevel(damageEvent.defender, this);
		if(enchantLvl == 0) return damageEvent;

		ItemStack weapon = damageEvent.attacker.getItemInHand();
		if(weapon == null) return damageEvent;
		if(weapon.getType() != Material.DIAMOND_SWORD && weapon.getType() != Material.DIAMOND_SPADE) return damageEvent;

		damageEvent.multiplier.add(getDamageMultiplier(enchantLvl));

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Receive &9-" + getDamageReduction(enchantLvl) + "% &7damage from", "&7diamond weapons").getLore();
	}

	public double getDamageMultiplier(int enchantLvl) {

		return Math.max(1 - ((double) enchantLvl / 10), 0);
	}

	public int getDamageReduction(int enchantLvl) {

		return (int) (100 - getDamageMultiplier(enchantLvl) * 100);
	}
}
