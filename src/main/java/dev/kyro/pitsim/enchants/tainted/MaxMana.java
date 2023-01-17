package dev.kyro.pitsim.enchants.tainted;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.Misc;

import java.util.List;
import java.util.Map;

public class MaxMana extends PitEnchant {
	public static MaxMana INSTANCE;

	public MaxMana() {
		super("Max Mana", true, ApplyType.CHESTPLATES,
				"maxmana");
		isTainted = true;
		INSTANCE = this;
	}

	public int getExtraMana(Map<PitEnchant, Integer> enchantMap) {
		if(!enchantMap.containsKey(this)) return 0;
		int enchantLvl = enchantMap.get(this);

		return getExtraMana(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {

		return new ALoreBuilder("&7Increase your max mana by &d" + Misc.getHearts(getExtraMana(enchantLvl)), "&d&o-" + reduction(enchantLvl) + "% Mana Regen").getLore();
	}

	public int getExtraMana(int enchantLvl) {
		return 100;
	}

	public static int reduction(int enchantLvl) {
		return 80 - (20 * enchantLvl);
	}
}
