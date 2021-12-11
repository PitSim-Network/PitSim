package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;

import java.util.List;

public class aBowPlaceholder extends PitEnchant {

	public aBowPlaceholder() {
		super("Bow Enchant Placeholder", false, ApplyType.BOWS,
				"common");
	}

	@Override
	public List<String> getDescription(int enchantLvl) {
		return new ALoreBuilder("&7Just a placeholder for now").getLore();
	}
}
