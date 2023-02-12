package dev.kyro.pitsim.enchants.tainted.common;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.PitLoreBuilder;

import java.util.List;

public class Pyrotechnic extends PitEnchant {
	public static Pyrotechnic INSTANCE;

	public Pyrotechnic() {
		super("Pyrotechnic", false, ApplyType.CHESTPLATES,
				"pyrotechnic", "pyro");
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {

		return new PitLoreBuilder(
				"&7A basic tainted enchant"
		).getLore();
	}
}
