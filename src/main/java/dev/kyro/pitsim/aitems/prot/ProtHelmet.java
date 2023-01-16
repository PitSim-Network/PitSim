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

public class ProtHelmet extends PitItem {

	public ProtHelmet() {
		hasDropConfirm = true;
		hideExtra = true;
		isProt = true;
		auctionCategory = AuctionCategory.MISC;

		itemEnchants.put(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
	}

	@Override
	public String getNBTID() {
		return "protection-helmet";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("p1helmet", "helmet"));
	}

	@Override
	public Material getMaterial(Player player) {
		return Material.DIAMOND_HELMET;
	}

	@Override
	public String getName(Player player) {
		return "&bProtection I Helmet";
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
