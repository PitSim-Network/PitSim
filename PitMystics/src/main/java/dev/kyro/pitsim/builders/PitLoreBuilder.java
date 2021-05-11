package dev.kyro.pitsim.builders;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.PitEnchant;

public class PitLoreBuilder extends ALoreBuilder {

	public PitEnchant pitEnchant;

	public PitLoreBuilder(PitEnchant pitEnchant) {
		this.pitEnchant = pitEnchant;
	}
}
