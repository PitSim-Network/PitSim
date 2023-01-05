package dev.kyro.pitsim.aitems.mobdrops;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.PitItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MagmaCream extends PitItem {

	public MagmaCream() {
		hasDropConfirm = true;
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
	public Material getMaterial(Player player) {
		return Material.MAGMA_CREAM;
	}

	@Override
	public String getName(Player player) {
		return "&aMagma Cream";
	}

	@Override
	public List<String> getLore(Player player) {
		return new ALoreBuilder(
				"&7Cream gathered from the Cubes",
				"&7of the Magma Caves",
				"",
				"&5Tainted Item"
		).getLore();
	}
}
