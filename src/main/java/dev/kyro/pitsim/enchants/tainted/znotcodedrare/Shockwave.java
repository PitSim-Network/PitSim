package dev.kyro.pitsim.enchants.tainted.znotcodedrare;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;

import java.util.List;

public class Shockwave extends PitEnchant {
	public static Shockwave INSTANCE;

	public Shockwave() {
		super("Shockwave", true, ApplyType.SCYTHES,
				"shockwave");
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
