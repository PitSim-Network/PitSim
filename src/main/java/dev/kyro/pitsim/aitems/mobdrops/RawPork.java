package dev.kyro.pitsim.aitems.mobdrops;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.PitItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RawPork extends PitItem {

	public RawPork() {
		hasDropConfirm = true;
	}

	@Override
	public String getNBTID() {
		return "raw-pork";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("pork", "rawpork"));
	}

	@Override
	public Material getMaterial(Player player) {
		return Material.PORK;
	}

	@Override
	public String getName(Player player) {
		return "&aRaw Pork";
	}

	@Override
	public List<String> getLore(Player player) {
		return new ALoreBuilder(
				"&7Pork gathered from the Pigmen",
				"&7of the Pigmen Caves",
				"",
				"&5Tainted Item"
		).getLore();
	}
}
