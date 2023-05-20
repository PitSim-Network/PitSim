package dev.kyro.pitsim.aitems.mobdrops;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.TemporaryItem;
import dev.kyro.pitsim.brewing.PotionManager;
import dev.kyro.pitsim.brewing.objects.BrewingIngredient;
import dev.kyro.pitsim.brewing.objects.PotionEffect;
import dev.kyro.pitsim.enums.MarketCategory;
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

public class Bone extends BrewingIngredient implements TemporaryItem {

	public Bone() {
		super(2, "XP Boost", ChatColor.AQUA, PotionType.SPEED);
		hasDropConfirm = true;
		marketCategory = MarketCategory.DARKZONE_DROPS;
	}

	@Override
	public String getNBTID() {
		return "bone";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("bone"));
	}

	@Override
	public Material getMaterial() {
		return Material.BONE;
	}

	@Override
	public String getName() {
		return "&aBone";
	}

	@Override
	public List<String> getLore() {
		return new ALoreBuilder(
				"&7Flesh gathered from the skeletons",
				"&7of the Skeleton Caves",
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

		String[] values = ((String) getPotency(effect.potency)).split(",");
		event.postMultiplierXpReward += Integer.parseInt(values[0]);
		event.xpCap += Integer.parseInt(values[1]);
	}

	@Override
	public Object getPotency(BrewingIngredient potencyIngredient) {
		int tier = potencyIngredient.tier;

		return 10 * tier + "," + 30 * tier;
	}

	@Override
	public List<String> getPotencyLore(BrewingIngredient potency) {
		List<String> lore = new ArrayList<>();
		String[] values = ((String)getPotency(potency)).split(",");

		lore.add("");
		lore.add(ChatColor.GRAY + "Earn " + ChatColor.AQUA + "+" + values[0] + " XP " + ChatColor.GRAY + "and " + ChatColor.AQUA + "+" + values[1] + " Max XP");
		lore.add(ChatColor.GRAY + "from kills and assists.");
		return lore;
	}

	@Override
	public int getDuration(BrewingIngredient durationIngredient) {
		int tier = durationIngredient.tier;
		return (20 * 60 * 3) * tier;
	}
}
