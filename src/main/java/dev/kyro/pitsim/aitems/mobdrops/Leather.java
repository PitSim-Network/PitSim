package dev.kyro.pitsim.aitems.mobdrops;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.StaticPitItem;
import dev.kyro.pitsim.aitems.TemporaryItem;
import dev.kyro.pitsim.enums.AuctionCategory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Leather extends StaticPitItem implements TemporaryItem {

	public Leather() {
		hasDropConfirm = true;
		auctionCategory = AuctionCategory.DARKZONE_DROPS;
	}

	@Override
	public String getNBTID() {
		return "wolf-hide";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("hide", "wolfhide"));
	}

	@Override
	public Material getMaterial() {
		return Material.LEATHER;
	}

	@Override
	public String getName() {
		return "&aWolf Hide";
	}

	@Override
	public List<String> getLore() {
		return new ALoreBuilder(
				"&7Hide gathered from the Wolves",
				"&7of the Wolf Caves",
				"",
				"&5Tainted Item"
		).getLore();
	}

	@Override
	public boolean isLegacyItem(ItemStack itemStack, NBTItem nbtItem) {
		return false;
	}

	@Override
	public TemporaryItem.TemporaryType getTemporaryType() {
		return TemporaryType.LOST_ON_DEATH;
	}
}
