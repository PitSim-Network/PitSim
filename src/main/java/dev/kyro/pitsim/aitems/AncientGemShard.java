package dev.kyro.pitsim.aitems;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AncientGemShard extends PitItem {

	public AncientGemShard() {
		hasDropConfirm = true;
	}

	@Override
	public String getNBTID() {
		return "gem-shard";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("gemshard", "shard"));
	}

	@Override
	public Material getMaterial(Player player) {
		return Material.PRISMARINE_SHARD;
	}

	@Override
	public String getName(Player player) {
		return "&aAncient Gem Shard";
	}

	@Override
	public List<String> getLore(Player player) {
		return new ALoreBuilder(
				"&eSpecial item",
				"&7A piece of a relic lost to time.",
				"&7Find enough shards and you may be",
				"&7able to craft an item of great power"
		).getLore();
	}
}
