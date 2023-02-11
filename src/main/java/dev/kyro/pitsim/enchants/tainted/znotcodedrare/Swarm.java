package dev.kyro.pitsim.enchants.tainted.znotcodedrare;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;

import java.util.List;

public class Swarm extends PitEnchant {
	public static Swarm INSTANCE;

	public Swarm() {
		super("Swarm", true, ApplyType.CHESTPLATES,
				"swarm");
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
