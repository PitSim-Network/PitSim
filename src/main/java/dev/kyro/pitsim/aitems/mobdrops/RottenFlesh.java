package dev.kyro.pitsim.aitems.mobdrops;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.TemporaryItem;
import dev.kyro.pitsim.brewing.PotionManager;
import dev.kyro.pitsim.brewing.objects.BrewingIngredient;
import dev.kyro.pitsim.brewing.objects.PotionEffect;
import dev.kyro.pitsim.enums.AuctionCategory;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RottenFlesh extends BrewingIngredient implements TemporaryItem {

	public RottenFlesh() {
		super(1, "Gold Boost", ChatColor.GOLD, PotionType.FIRE_RESISTANCE);
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
				"&cLost on death"
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

	@EventHandler
	public void onKill(KillEvent event) {
		PotionEffect effect = PotionManager.getEffect(event.getKillerPlayer(), this);
		if(effect == null) return;

		event.goldMultipliers.add((Double) getPotency(effect.potency) + 1);
	}

	@Override
	public Object getPotency(BrewingIngredient potencyIngredient) {
		int tier = potencyIngredient.tier;

		return 0.050 * tier;
	}

	@Override
	public List<String> getPotencyLore(BrewingIngredient potency) {
		List<String> lore = new ArrayList<>();

		lore.add("");
		lore.add(ChatColor.GRAY + "Earn " + ChatColor.GOLD + "+" + (int) ((double) getPotency(potency) * 100) + "% gold " + ChatColor.GRAY + "from");
		lore.add(ChatColor.GRAY + "kills and assists.");
		return lore;
	}

	@Override
	public int getDuration(BrewingIngredient durationIngredient) {
		int tier = durationIngredient.tier;
		return (20 * 60 * 3) * tier;
	}
}
