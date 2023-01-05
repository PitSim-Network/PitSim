package dev.kyro.pitsim.aitems.mobdrops;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.PitItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlazeRod extends PitItem {

	public BlazeRod() {
		hasDropConfirm = true;
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
	public Material getMaterial(Player player) {
		return Material.BLAZE_POWDER;
	}

	@Override
	public String getName(Player player) {
		return "&aRaw Pork";
	}

	@Override
	public List<String> getLore(Player player) {
		return new ALoreBuilder(
				"&7Pork gathered from the Blazes",
				"&7of the Blaze Caves",
				"",
				"&5Tainted Item"
		).getLore();
	}
}
