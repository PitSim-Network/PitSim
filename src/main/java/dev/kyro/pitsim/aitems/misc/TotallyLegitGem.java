package dev.kyro.pitsim.aitems.misc;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.enums.AuctionCategory;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TotallyLegitGem extends PitItem {

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
	public Material getMaterial(Player player) {
		return Material.EMERALD;
	}

	@Override
	public String getName(Player player) {
		return "&aTotally Legit Gem";
	}

	@Override
	public List<String> getLore(Player player) {
		return new ALoreBuilder(
				"&7Kept on death",
				"&7Adds &d1 tier &7to a mystic enchant.",
				"&8Once per item!",
				"",
				"&eHold and right-click to use!"
		).getLore();
	}
}
