package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.DamageEvent;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BottomlessQuiver extends PitEnchant {

	public BottomlessQuiver() {
		super("Bottomless Quiver", false, ApplyType.BOWS,
				"bq", "bottomless-quiver", "bottom", "quiver");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {

		int enchantLvl = attackEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(attackEvent.attacker.equals(attackEvent.defender)) return;

		ItemStack arrows = new ItemStack(Material.ARROW);
		arrows.setAmount(getArrowAmount(enchantLvl));
		attackEvent.attacker.getInventory().addItem(arrows);
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