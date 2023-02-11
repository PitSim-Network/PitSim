package dev.kyro.pitsim.enchants.tainted.znotcodeduncommon;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;

import java.util.List;

public class ComboSlow extends PitEnchant {
	public static ComboSlow INSTANCE;

	public ComboSlow() {
		super("Combo: Slow", false, ApplyType.SWORDS,
				"comboeslow", "slow");
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {

		return new ALoreBuilder(
				"&7I can't be asked to code this"
		).getLore();
	}
}
