package dev.kyro.pitsim.aitems.mobdrops;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.StaticPitItem;
import dev.kyro.pitsim.enums.AuctionCategory;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlazeRod extends StaticPitItem {

	public BlazeRod() {
		hasDropConfirm = true;
		auctionCategory = AuctionCategory.DARKZONE_DROPS;
	}

	@Override
	public String getNBTID() {
		return "blaze-powder";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("powder", "blazepowder"));
	}

	@Override
	public Material getMaterial() {
		return Material.BLAZE_POWDER;
	}

	@Override
	public String getName() {
		return "&aRaw Pork";
	}

	@Override
	public List<String> getLore() {
		return new ALoreBuilder(
				"&7Pork gathered from the Blazes",
				"&7of the Blaze Caves",
				"",
				"&5Tainted Item"
		).getLore();
	}
}
