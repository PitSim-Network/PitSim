package dev.kyro.pitremake.builders;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitremake.controllers.PitEnchant;

public class PitLoreBuilder extends ALoreBuilder {

	public PitEnchant pitEnchant;

	public PitLoreBuilder(PitEnchant pitEnchant) {
		this.pitEnchant = pitEnchant;
	}
}
