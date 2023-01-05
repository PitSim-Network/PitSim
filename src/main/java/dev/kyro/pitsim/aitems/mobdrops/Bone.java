package dev.kyro.pitsim.aitems.mobdrops;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.PitItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Bone extends PitItem {

	public Bone() {
		hasDropConfirm = true;
	}

	@Override
	public String getNBTID() {
		return "rotten-flesh";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("bone"));
	}

	@Override
	public Material getMaterial(Player player) {
		return Material.BONE;
	}

	@Override
	public String getName(Player player) {
		return "&aBone";
	}

	@Override
	public List<String> getLore(Player player) {
		return new ALoreBuilder(
				"&7Flesh gathered from the skeletons",
				"&7of the Skeleton Caves",
				"",
				"&5Tainted Item"
		).getLore();
	}
}
