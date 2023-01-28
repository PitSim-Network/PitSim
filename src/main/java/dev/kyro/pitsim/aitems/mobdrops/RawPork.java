package dev.kyro.pitsim.aitems.mobdrops;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.StaticPitItem;
import dev.kyro.pitsim.enums.AuctionCategory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RawPork extends StaticPitItem {

	public RawPork() {
		hasDropConfirm = true;
		auctionCategory = AuctionCategory.DARKZONE_DROPS;
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
	public Material getMaterial() {
		return Material.PORK;
	}

	@Override
	public String getName() {
		return "&aRaw Pork";
	}

	@Override
	public List<String> getLore() {
		return new ALoreBuilder(
				"&7Pork gathered from the Pigmen",
				"&7of the Pigmen Caves",
				"",
				"&5Tainted Item"
		).getLore();
	}

	@Override
	public boolean isLegacyItem(ItemStack itemStack, NBTItem nbtItem) {
		return false;
	}
}
