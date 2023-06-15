package net.pitsim.pitsim.aitems.mobdrops;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.pitsim.aitems.TemporaryItem;
import net.pitsim.pitsim.brewing.objects.BrewingIngredient;
import net.pitsim.pitsim.controllers.DamageManager;
import net.pitsim.pitsim.enums.MarketCategory;
import net.pitsim.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.*;

public class Charcoal extends BrewingIngredient implements TemporaryItem {

	public Map<Player, Integer> tickMap = new HashMap<>();

	public Charcoal() {
		super(7, "Wither", ChatColor.DARK_GRAY, PotionType.WEAKNESS);
		hasDropConfirm = true;
		itemData = 1;
		marketCategory = MarketCategory.DARKZONE_DROPS;
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
				"&7Charcoal gathered from the Wither",
				"&7Skeletons of the Wither Skeleton",
				"&7Caves",
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
		Misc.applyPotionEffect(player, PotionEffectType.WITHER, duration, 0, false, false);
		if(duration == 0) tickMap.remove(player);
		else tickMap.putIfAbsent(player, 1);

		for (Map.Entry<Player, Integer> entry : tickMap.entrySet()) {
			if(entry.getValue() - 1 == 0) {
				DamageManager.createIndirectAttack(null, player, (double) getPotency(potency) * 2);
				tickMap.put(entry.getKey(), 5 * 20);
			} else tickMap.put(entry.getKey(), entry.getValue() - 1);

		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if(!(event.getEntity() instanceof Player)) return;
		if(!tickMap.containsKey((Player) event.getEntity())) return;

		if(event.getCause() == EntityDamageEvent.DamageCause.WITHER) event.setCancelled(true);
	}

	@Override
	public Object getPotency(BrewingIngredient potencyIngredient) {
		return 0.5 * potencyIngredient.tier;
	}

	@Override
	public List<String> getPotencyLore(BrewingIngredient potency) {
		List<String> lore = new ArrayList<>();

		lore.add("");
		lore.add(ChatColor.GRAY + "Lose " + color + "" + getPotency(potency) + "\u2764 " +  ChatColor.GRAY + "Every 5 seconds.");
		return lore;
	}

	@Override
	public int getDuration(BrewingIngredient durationIngredient) {
		return 20 * durationIngredient.tier;
	}
}
