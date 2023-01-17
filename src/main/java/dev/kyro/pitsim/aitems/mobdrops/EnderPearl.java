package dev.kyro.pitsim.aitems.mobdrops;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.StaticPitItem;
import dev.kyro.pitsim.enums.AuctionCategory;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnderPearl extends StaticPitItem {

	public EnderPearl() {
		hasDropConfirm = true;
		auctionCategory = AuctionCategory.DARKZONE_DROPS;
	}

	@Override
	public String getNBTID() {
		return "ender-pearl";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("pearl", "enderpearl"));
	}

	@Override
	public Material getMaterial() {
		return Material.ENDER_PEARL;
	}

	@Override
	public String getName() {
		return "&aEnder Pearl";
	}

	@Override
	public List<String> getLore() {
		return new ALoreBuilder(
				"&7Pearl gathered from the endermen",
				"&7of the End Caves",
				"",
				"&5Tainted Item"
		).getLore();
	}
}
