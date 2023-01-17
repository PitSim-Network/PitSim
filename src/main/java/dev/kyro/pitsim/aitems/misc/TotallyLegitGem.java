package dev.kyro.pitsim.aitems.misc;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.StaticPitItem;
import dev.kyro.pitsim.enums.AuctionCategory;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TotallyLegitGem extends StaticPitItem {

	public TotallyLegitGem() {
		hasDropConfirm = true;
		auctionCategory = AuctionCategory.PURE_RELATED;
	}

	@Override
	public String getNBTID() {
		return "gem";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("gem", "totallylegitgem"));
	}

	@Override
	public Material getMaterial() {
		return Material.EMERALD;
	}

	@Override
	public String getName() {
		return "&aTotally Legit Gem";
	}

	@Override
	public List<String> getLore() {
		return new ALoreBuilder(
				"&7Kept on death",
				"&7Adds &d1 tier &7to a mystic enchant.",
				"&8Once per item!",
				"",
				"&eHold and right-click to use!"
		).getLore();
	}
}
