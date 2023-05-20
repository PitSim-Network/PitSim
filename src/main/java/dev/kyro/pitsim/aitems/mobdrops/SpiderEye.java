package dev.kyro.pitsim.aitems.mobdrops;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.aitems.TemporaryItem;
import dev.kyro.pitsim.brewing.PotionManager;
import dev.kyro.pitsim.brewing.objects.BrewingIngredient;
import dev.kyro.pitsim.brewing.objects.PotionEffect;
import dev.kyro.pitsim.enums.MarketCategory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpiderEye extends BrewingIngredient implements TemporaryItem {

	public SpiderEye() {
		super(3, "Cleanse", ChatColor.WHITE, PotionType.INVISIBILITY);
		hasDropConfirm = true;
		marketCategory = MarketCategory.DARKZONE_DROPS;
	}

	@Override
	public String getNBTID() {
		return "spider-eye";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("spidereye", "spider"));
	}

	@Override
	public Material getMaterial() {
		return Material.SPIDER_EYE;
	}

	@Override
	public String getName() {
		return "&aSpider Eye";
	}

	@Override
	public List<String> getLore() {
		return new ALoreBuilder(
				"&7Eye gathered from the Spiders",
				"&7of the Spider Caves",
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
		AOutput.send(player, "&5&lPOTION!&7 Effected with " + color + name + " " +
				AUtil.toRoman(potency.tier));
		for (PotionEffect potionEffect : PotionManager.getPotionEffects(player)) {
			int tier = potionEffect.potency.tier;
			if(tier - potency.tier < 1) {
				AOutput.send(player, potionEffect.potionType.color + potionEffect.potionType.name + " " +
						AUtil.toRoman(potionEffect.potency.tier) + " &7\u21e8 " + potionEffect.potionType.color +
						ChatColor.STRIKETHROUGH + potionEffect.potionType.name);
				potionEffect.onExpire(true);
			} else {
				AOutput.send(player, potionEffect.potionType.color + potionEffect.potionType.name + " " +
						AUtil.toRoman(potionEffect.potency.tier) + " &7\u21e8 " + potionEffect.potionType.color +
						potionEffect.potionType.name + " " + AUtil.toRoman(potionEffect.potency.tier - potency.tier));

				potionEffect.potency = BrewingIngredient.getIngredientFromTier(tier - potency.tier);
			}
		}
	}

	@Override
	public Object getPotency(BrewingIngredient potencyIngredient) {
		return potencyIngredient.tier;
	}

	@Override
	public List<String> getPotencyLore(BrewingIngredient potency) {
		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.GRAY + "Clears " + color + getPotency(potency) + " Tiers " + ChatColor.GRAY + "off of all");
		lore.add(ChatColor.GRAY + "active potion effects.");
		return lore;
	}

	@Override
	public int getDuration(BrewingIngredient durationIngredient) {
		return 0;
	}
}
