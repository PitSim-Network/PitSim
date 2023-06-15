package net.pitsim.spigot.items.mobdrops;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.items.TemporaryItem;
import net.pitsim.spigot.brewing.PotionManager;
import net.pitsim.spigot.brewing.objects.BrewingIngredient;
import net.pitsim.spigot.brewing.objects.PotionEffect;
import net.pitsim.spigot.enums.MarketCategory;
import net.pitsim.spigot.events.AttackEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Gunpowder extends BrewingIngredient implements TemporaryItem {

	public Gunpowder() {
		super(8, "Damage Boost", ChatColor.RED, PotionType.STRENGTH);
		hasDropConfirm = true;
		marketCategory = MarketCategory.DARKZONE_DROPS;
	}

	@Override
	public String getNBTID() {
		return "gunpowder";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("gunpowder"));
	}

	@Override
	public Material getMaterial() {
		return Material.SULPHUR;
	}

	@Override
	public String getName() {
		return "&aGunpowder";
	}

	@Override
	public List<String> getLore() {
		return new ALoreBuilder(
				"&7Gunpowder gathered from the Creepers",
				"&7of the Creeper Caves",
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
		PotionEffect effect = PotionManager.getEffect(event.getAttackerPlayer(), this);
		if(effect == null) return;

		double dmg = (double) getPotency(effect.potency);
		event.increasePercent += dmg;
	}

	@Override
	public Object getPotency(BrewingIngredient potencyIngredient) {
		return 0.05 * potencyIngredient.tier;
	}

	@Override
	public List<String> getPotencyLore(BrewingIngredient potency) {
		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.GRAY + "Deal " + color + "+" + (int) ((double) getPotency(potency) * 100) + "% Damage");
		lore.add(ChatColor.GRAY + "to bots and players.");
		return lore;
	}

	@Override
	public int getDuration(BrewingIngredient durationIngredient) {
		return 20 * 60 * durationIngredient.tier;
	}
}
