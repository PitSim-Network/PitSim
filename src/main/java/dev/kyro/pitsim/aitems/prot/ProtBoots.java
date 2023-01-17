package dev.kyro.pitsim.aitems.prot;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.StaticPitItem;
import dev.kyro.pitsim.enums.AuctionCategory;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProtBoots extends StaticPitItem {

	public ProtBoots() {
		hasDropConfirm = true;
		hideExtra = true;
		isProt = true;
		auctionCategory = AuctionCategory.MISC;

		itemEnchants.put(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
	}

	@Override
	public String getNBTID() {
		return "protection-boots";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("p1boots", "boots"));
	}

	@Override
	public Material getMaterial() {
		return Material.DIAMOND_BOOTS;
	}

	@Override
	public String getName() {
		return "&bProtection I Boots";
	}

	@Override
	public List<String> getLore() {
		return new ALoreBuilder(
				"&7A relic back from when knights",
				"&7had shining armor",
				"",
				"&cLost on death"
		).getLore();
	}
}
