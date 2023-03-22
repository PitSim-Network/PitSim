package dev.kyro.pitsim.aitems.mobdrops;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.StaticPitItem;
import dev.kyro.pitsim.aitems.TemporaryItem;
import dev.kyro.pitsim.brewing.objects.BrewingIngredient;
import dev.kyro.pitsim.enums.AuctionCategory;
import dev.kyro.pitsim.enums.NBTTag;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RottenFlesh extends BrewingIngredient implements TemporaryItem {

	public RottenFlesh() {
		super(1, "rotten-flesh", "&aRotten Flesh", );
		hasDropConfirm = true;
		auctionCategory = AuctionCategory.DARKZONE_DROPS;
	}

	@Override
	public String getNBTID() {
		return "rotten-flesh";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("flesh", "rottenflesh"));
	}

	@Override
	public Material getMaterial() {
		return Material.ROTTEN_FLESH;
	}

	@Override
	public String getName() {
		return "&aRotten Flesh";
	}

	@Override
	public List<String> getLore() {
		return new ALoreBuilder(
				"&7Flesh gathered from the zombies",
				"&7of the Zombie Caves",
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

	@Override
	public void administerEffect(Player player, BrewingIngredient potency, int duration) {

	}

	@Override
	public Object getPotency(BrewingIngredient potencyIngredient) {
		return null;
	}

	@Override
	public List<String> getPotencyLore(BrewingIngredient potency) {
		return null;
	}

	@Override
	public int getDuration(BrewingIngredient durationIngredient) {
		return 0;
	}

	@Override
	public int getBrewingReductionMinutes() {
		return 0;
	}

	@Override
	public ItemStack getItem() {
		return null;
	}
}
