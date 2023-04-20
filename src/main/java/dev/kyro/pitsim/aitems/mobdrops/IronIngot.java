package dev.kyro.pitsim.aitems.mobdrops;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.TemporaryItem;
import dev.kyro.pitsim.brewing.PotionManager;
import dev.kyro.pitsim.brewing.objects.BrewingIngredient;
import dev.kyro.pitsim.brewing.objects.PotionEffect;
import dev.kyro.pitsim.enums.MarketCategory;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IronIngot extends BrewingIngredient implements TemporaryItem {

	public IronIngot() {
		super(9, "Defense", ChatColor.BLUE, PotionType.NIGHT_VISION);
		hasDropConfirm = true;
		marketCategory = MarketCategory.DARKZONE_DROPS;
	}

	@Override
	public String getNBTID() {
		return "iron-ingot";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("iron", "ironingot"));
	}

	@Override
	public Material getMaterial() {
		return Material.IRON_INGOT;
	}

	@Override
	public String getName() {
		return "&aIron Ingot";
	}

	@Override
	public List<String> getLore() {
		return new ALoreBuilder(
				"&7Ingot gathered from the Golems",
				"&7of the Golem Caves",
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
	public void onHit(AttackEvent.Apply event) {
		PotionEffect effect = PotionManager.getEffect(event.getDefenderPlayer(), this);
		if(effect == null) return;

		double defense = (double) getPotency(effect.potency);
		event.multipliers.add(Misc.getReductionMultiplier(defense));
	}

	@Override
	public Object getPotency(BrewingIngredient potencyIngredient) {
		return (5 * potencyIngredient.tier);
	}

	@Override
	public List<String> getPotencyLore(BrewingIngredient potency) {
		List<String> lore = new ArrayList<>();

		lore.add("");
		lore.add(ChatColor.GRAY + "Receive " + color + "-" + (potency.tier * 5) + "% Damage" +  ChatColor.GRAY + ".");
		return lore;
	}

	@Override
	public int getDuration(BrewingIngredient durationIngredient) {
		return 20 * 30 * durationIngredient.tier;
	}
}
