package dev.kyro.pitsim.aitems.mobdrops;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.PitItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IronIngot extends PitItem {

	public IronIngot() {
		hasDropConfirm = true;
	}

	@Override
	public String getNBTID() {
		return "iron-ingot";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("iron", "ironingot"));
	}

	@Override
	public Material getMaterial(Player player) {
		return Material.IRON_INGOT;
	}

	@Override
	public String getName(Player player) {
		return "&aIron Ingot";
	}

	@Override
	public List<String> getLore(Player player) {
		return new ALoreBuilder(
				"&7Ingot gathered from the Golems",
				"&7of the Golem Caves",
				"",
				"&5Tainted Item"
		).getLore();
	}
}
