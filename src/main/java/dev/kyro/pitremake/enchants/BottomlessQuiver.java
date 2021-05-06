package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitremake.controllers.DamageEvent;
import dev.kyro.pitremake.controllers.PitEnchant;
import dev.kyro.pitremake.enums.ApplyType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BottomlessQuiver extends PitEnchant {

	public BottomlessQuiver() {
		super("Bottomless Quiver", false, ApplyType.BOWS,
				"bq", "bottomless-quiver", "bottom", "quiver");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = damageEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return damageEvent;

		if(damageEvent.attacker.equals(damageEvent.defender)) return damageEvent;

		ItemStack arrows = new ItemStack(Material.ARROW);
		arrows.setAmount(getArrowAmount(enchantLvl));
		damageEvent.attacker.getInventory().addItem(arrows);

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		if(getArrowAmount(enchantLvl) == 1)
			return new ALoreBuilder("&7Get &f" + getArrowAmount(enchantLvl) + " arrow &7on arrow hit").getLore();

		else return new ALoreBuilder("&7Get &f" + getArrowAmount(enchantLvl) + " arrows &7on arrow hit").getLore();
	}

//	TODO: Bottomless quiver arrow equation
	public int getArrowAmount(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 1;
			case 2:
				return 3;
			case 3:
				return 8;

		}

		return 0;
	}
}
