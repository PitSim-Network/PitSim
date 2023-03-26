package dev.kyro.pitsim.aitems.mobdrops;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.TemporaryItem;
import dev.kyro.pitsim.brewing.objects.BrewingIngredient;
import dev.kyro.pitsim.enums.AuctionCategory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlazeRod extends BrewingIngredient implements TemporaryItem {

	public BlazeRod() {
		super(5, "Mana Boost", ChatColor.LIGHT_PURPLE, PotionType.REGEN);
		hasDropConfirm = true;
		auctionCategory = AuctionCategory.DARKZONE_DROPS;
	}

	@Override
	public String getNBTID() {
		return "blaze-powder";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("powder", "blazepowder"));
	}

	@Override
	public Material getMaterial() {
		return Material.BLAZE_ROD;
	}

	@Override
	public String getName() {
		return "&aBlaze Rod";
	}

	@Override
	public List<String> getLore() {
		return new ALoreBuilder(
				"&7Blaze rods gathered from the",
				"&7Blazes of the Blaze Caves",
				"",
				"&cLost on death"
		).getLore();
	}

	@Override
	public boolean isLegacyItem(ItemStack itemStack, NBTItem nbtItem) {
		return false;
	}

	@Override
	public TemporaryType getTemporaryType() {
		return TemporaryType.LOST_ON_DEATH;
	}

	@Override
	public void administerEffect(Player player, BrewingIngredient potency, int duration) {

	}

	@Override
	public Object getPotency(BrewingIngredient potencyIngredient) {
		return 0.05 * potencyIngredient.tier;
	}

	@Override
	public List<String> getPotencyLore(BrewingIngredient potency) {
		List<String> lore = new ArrayList<>();

		lore.add("");
		lore.add(ChatColor.GRAY + "Regain your " + color + "Mana " + (int) ((double) getPotency(potency) * 100) + "% " + ChatColor.GRAY + "faster");
		lore.add(ChatColor.GRAY + "While in the " + ChatColor.DARK_PURPLE + "Darkzone" + ChatColor.GRAY + ".");
		return lore;
	}

	@Override
	public int getDuration(BrewingIngredient durationIngredient) {
		return 20 * 60 * durationIngredient.tier;
	}
}
