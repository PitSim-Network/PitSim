package dev.kyro.pitsim.enchants.tainted.znotcodedrare;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.PitLoreBuilder;

import java.util.List;

public class Medic extends PitEnchant {
	public static Medic INSTANCE;

	public Medic() {
		super("Medic", true, ApplyType.SCYTHES,
				"medic");
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
