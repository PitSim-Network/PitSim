package dev.kyro.pitsim.enchants.tainted.znotcodeduncommon;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.PitLoreBuilder;

import java.util.List;

public class StartingHand extends PitEnchant {
	public static StartingHand INSTANCE;

	public StartingHand() {
		super("Starting Hand", false, ApplyType.SCYTHES,
				"startinghand", "starting", "start");
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {

		return new PitLoreBuilder(
				"&7I can't be asked to code this"
		).getLore();
	}
}
