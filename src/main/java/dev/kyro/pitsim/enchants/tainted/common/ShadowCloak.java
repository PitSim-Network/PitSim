package dev.kyro.pitsim.enchants.tainted.common;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.PitLoreBuilder;

import java.util.List;

public class ShadowCloak extends PitEnchant {
	public static ShadowCloak INSTANCE;

	public ShadowCloak() {
		super("Shadow Cloak", false, ApplyType.CHESTPLATES,
				"shadowcloak", "cloak");
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
