package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DiamondAllergy extends PitEnchant {

	public DiamondAllergy() {
		super("Diamond Allergy", false, ApplyType.PANTS,
				"da", "dallergy", "diamondallergy", "diamond-allergy");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(enchantLvl == 0) return;

		ItemStack weapon = attackEvent.getAttacker().getEquipment().getItemInHand();
		if(weapon == null) return;
		if(weapon.getType() != Material.DIAMOND_SWORD && weapon.getType() != Material.DIAMOND_SPADE) return;

		attackEvent.multipliers.add(getDamageMultiplier(enchantLvl));
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {

		return new ALoreBuilder("&7Receive &9-" + getDamageReduction(enchantLvl) + "% &7damage from", "&7diamond weapons").getLore();
	}

	public double getDamageMultiplier(int enchantLvl) {

		return Math.max(1 - ((double) enchantLvl / 10), 0);
	}

	public int getDamageReduction(int enchantLvl) {

		return (int) (100 - getDamageMultiplier(enchantLvl) * 100);
	}
}
