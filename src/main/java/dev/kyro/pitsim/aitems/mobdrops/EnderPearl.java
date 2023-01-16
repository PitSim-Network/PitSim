package dev.kyro.pitsim.aitems.mobdrops;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.enums.AuctionCategory;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnderPearl extends PitItem {

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
	public Material getMaterial(Player player) {
		return Material.ENDER_PEARL;
	}

	@Override
	public String getName(Player player) {
		return "&aEnder Pearl";
	}

	@Override
	public List<String> getLore(Player player) {
		return new ALoreBuilder(
				"&7Pearl gathered from the endermen",
				"&7of the End Caves",
				"",
				"&5Tainted Item"
		).getLore();
	}
}
