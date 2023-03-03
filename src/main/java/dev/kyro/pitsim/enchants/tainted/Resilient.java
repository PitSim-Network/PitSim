package dev.kyro.pitsim.enchants.tainted;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.PitLoreBuilder;

import java.util.List;

public class Resilient extends PitEnchant {

	public Resilient() {
		super("Resilient", false, ApplyType.CHESTPLATES,
				"resilient", "resilent", "resileint");
		isUncommonEnchant = true;
		isTainted = true;
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Can't be asked to code this"
		).getLore();
	}

	public double getDamageReduction(int enchantLvl) {
		return enchantLvl * 5;
	}
}
