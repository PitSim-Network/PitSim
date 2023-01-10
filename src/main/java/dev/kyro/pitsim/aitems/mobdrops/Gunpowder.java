package dev.kyro.pitsim.aitems.mobdrops;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.PitItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Gunpowder extends PitItem {

	public Gunpowder() {
		hasDropConfirm = true;
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
	public Material getMaterial(Player player) {
		return Material.SULPHUR;
	}

	@Override
	public String getName(Player player) {
		return "&aGunpowder";
	}

	@Override
	public List<String> getLore(Player player) {
		return new ALoreBuilder(
				"&7Gunpowder gathered from the Creepers",
				"&7of the Creeper Caves",
				"",
				"&5Tainted Item"
		).getLore();
	}
}
