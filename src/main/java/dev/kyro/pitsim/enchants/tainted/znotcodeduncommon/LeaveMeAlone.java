package dev.kyro.pitsim.enchants.tainted.znotcodeduncommon;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;

import java.util.List;

public class LeaveMeAlone extends PitEnchant {
	public static LeaveMeAlone INSTANCE;

	public LeaveMeAlone() {
		super("Leave Me Alone", false, ApplyType.CHESTPLATES,
				"leavemealone", "leaveme", "leave", "alone");
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
