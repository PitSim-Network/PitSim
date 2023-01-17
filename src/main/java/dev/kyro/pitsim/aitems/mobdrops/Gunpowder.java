package dev.kyro.pitsim.aitems.mobdrops;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.StaticPitItem;
import dev.kyro.pitsim.enums.AuctionCategory;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Gunpowder extends StaticPitItem {

	public Gunpowder() {
		hasDropConfirm = true;
		auctionCategory = AuctionCategory.DARKZONE_DROPS;
	}

	@Override
	public String getNBTID() {
		return "gunpowder";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("gunpowder"));
	}

	@Override
	public Material getMaterial() {
		return Material.SULPHUR;
	}

	@Override
	public String getName() {
		return "&aGunpowder";
	}

	@Override
	public List<String> getLore() {
		return new ALoreBuilder(
				"&7Gunpowder gathered from the Creepers",
				"&7of the Creeper Caves",
				"",
				"&5Tainted Item"
		).getLore();
	}
}
