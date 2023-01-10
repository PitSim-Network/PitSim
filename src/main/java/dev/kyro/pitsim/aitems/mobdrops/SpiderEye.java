package dev.kyro.pitsim.aitems.mobdrops;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.PitItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpiderEye extends PitItem {

	public SpiderEye() {
		hasDropConfirm = true;
	}

	@Override
	public String getNBTID() {
		return "spider-eye";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("spidereye", "spider"));
	}

	@Override
	public Material getMaterial(Player player) {
		return Material.PORK;
	}

	@Override
	public String getName(Player player) {
		return "&aSpider Eye";
	}

	@Override
	public List<String> getLore(Player player) {
		return new ALoreBuilder(
				"&7Eye gathered from the Spiders",
				"&7of the Spider Caves",
				"",
				"&5Tainted Item"
		).getLore();
	}
}
