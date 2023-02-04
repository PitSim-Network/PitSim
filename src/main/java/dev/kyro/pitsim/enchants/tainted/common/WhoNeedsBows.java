package dev.kyro.pitsim.enchants.tainted.common;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;

import java.util.List;

public class WhoNeedsBows extends PitEnchant {
	public static WhoNeedsBows INSTANCE;

	public WhoNeedsBows() {
		super("Who Needs Bows?", false, ApplyType.SCYTHES,
				"whoneedsbows", "whatsabow");
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
