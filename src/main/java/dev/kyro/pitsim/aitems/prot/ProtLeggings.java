package dev.kyro.pitsim.aitems.prot;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.enums.AuctionCategory;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProtLeggings extends PitItem {

	public ProtLeggings() {
		hasDropConfirm = true;
		hideExtra = true;
		isProt = true;
		auctionCategory = AuctionCategory.MISC;

		itemEnchants.put(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
	}

	@Override
	public String getNBTID() {
		return "protection-leggings";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("p1leggings", "leggings"));
	}

	@Override
	public Material getMaterial(Player player) {
		return Material.DIAMOND_LEGGINGS;
	}

	@Override
	public String getName(Player player) {
		return "&bProtection I Leggings";
	}

	@Override
	public List<String> getLore(Player player) {
		return new ALoreBuilder(
				"&7A relic back from when knights",
				"&7had shining armor",
				"",
				"&cLost on death"
		).getLore();
	}
}
