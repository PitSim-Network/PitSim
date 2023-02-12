package dev.kyro.pitsim.enchants.tainted.common;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.PitLoreBuilder;

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

		return new PitLoreBuilder(
				"&7A basic tainted enchant"
		).getLore();
	}
}
