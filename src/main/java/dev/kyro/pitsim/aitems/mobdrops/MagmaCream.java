package dev.kyro.pitsim.aitems.mobdrops;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.StaticPitItem;
import dev.kyro.pitsim.enums.AuctionCategory;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MagmaCream extends StaticPitItem {

	public MagmaCream() {
		hasDropConfirm = true;
		auctionCategory = AuctionCategory.DARKZONE_DROPS;
	}

	@Override
	public String getNBTID() {
		return "magma-cream";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("magma", "magmacream"));
	}

	@Override
	public Material getMaterial() {
		return Material.MAGMA_CREAM;
	}

	@Override
	public String getName() {
		return "&aMagma Cream";
	}

	@Override
	public List<String> getLore() {
		return new ALoreBuilder(
				"&7Cream gathered from the Cubes",
				"&7of the Magma Caves",
				"",
				"&5Tainted Item"
		).getLore();
	}
}
