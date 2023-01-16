package dev.kyro.pitsim.aitems.mobdrops;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.enums.AuctionCategory;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Charcoal extends PitItem {

	public Charcoal() {
		hasDropConfirm = true;
		itemData = 1;
		auctionCategory = AuctionCategory.DARKZONE_DROPS;
	}

	@Override
	public String getNBTID() {
		return "charcoal";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("charcoal"));
	}

	@Override
	public Material getMaterial(Player player) {
		return Material.COAL;
	}

	@Override
	public String getName(Player player) {
		return "&aCharcoal";
	}

	@Override
	public List<String> getLore(Player player) {
		return new ALoreBuilder(
				"&7Eye gathered from the Spiders",
				"&7of the Spider Caves",
				"",
				"&5Tainted Item"
		).getLore();
	}
}
