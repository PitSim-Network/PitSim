package dev.kyro.pitsim.enchants.tainted.common;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.PitLoreBuilder;

import java.util.List;

public class Nimble extends PitEnchant {
	public static Nimble INSTANCE;

	public Nimble() {
		super("Nimble", false, ApplyType.SCYTHES,
				"nimble");
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
