package dev.kyro.pitsim.enchants.tainted.common;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;

import java.util.List;

public class Huggable extends PitEnchant {
	public static Huggable INSTANCE;

	public Huggable() {
		super("Huggable", false, ApplyType.CHESTPLATES,
				"huggable", "hug");
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {

		return new ALoreBuilder(
				"&7A basic tainted enchant"
		).getLore();
	}
}
