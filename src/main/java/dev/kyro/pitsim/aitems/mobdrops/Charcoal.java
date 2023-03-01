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

public class Charcoal extends StaticPitItem implements TemporaryItem {

	public Charcoal() {
		hasDropConfirm = true;
		itemData = 1;
		auctionCategory = AuctionCategory.DARKZONE_DROPS;
	}

	@Override
	public String getNBTID() {
		return "charcoal";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("charcoal"));
	}

	@Override
	public Material getMaterial() {
		return Material.COAL;
	}

	@Override
	public String getName() {
		return "&aCharcoal";
	}

	@Override
	public List<String> getLore() {
		return new ALoreBuilder(
				"&7Eye gathered from the Spiders",
				"&7of the Spider Caves",
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
