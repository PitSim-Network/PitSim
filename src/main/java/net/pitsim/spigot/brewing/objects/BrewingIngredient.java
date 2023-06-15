package net.pitsim.spigot.brewing.objects;

import de.tr7zw.nbtapi.NBTItem;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.aitems.PitItem;
import net.pitsim.spigot.aitems.StaticPitItem;
import net.pitsim.spigot.controllers.ItemFactory;
import net.pitsim.spigot.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

public abstract class BrewingIngredient extends StaticPitItem implements Listener {
	public int tier;
	public String name;
	public ChatColor color;
	public PotionType potionType;

	public static List<BrewingIngredient> ingredients = new ArrayList<>();

	public BrewingIngredient(int tier, String name, ChatColor color, PotionType potionType) {
		this.tier = tier;
		this.name = name;
		this.color = color;
		this.potionType = potionType;

		registerIngredient(this);
	}

	public abstract void administerEffect(Player player, BrewingIngredient potency, int duration);

	public abstract Object getPotency(BrewingIngredient potencyIngredient);

	public abstract List<String> getPotencyLore(BrewingIngredient potency);

	public abstract int getDuration(BrewingIngredient durationIngredient);

	public int getBrewingReductionMinutes() {
		return tier * 10;
	}

	public static BrewingIngredient getIngredientFromTier(int tier) {
		for(BrewingIngredient ingredient : ingredients) {
			if(ingredient.tier == tier) return ingredient;
		}
		return null;
	}

	public static boolean isIngredient(ItemStack itemStack) {
		if(Misc.isAirOrNull(itemStack)) return false;
		NBTItem nbtItem = new NBTItem(itemStack);
		for(BrewingIngredient ingredient : ingredients) {
			PitItem pitItem = ItemFactory.getItem(itemStack);
			if(!(pitItem instanceof BrewingIngredient)) continue;
			return true;
		}
		return false;
	}

	public static BrewingIngredient getIngredientFromItemStack(ItemStack itemStack) {
		if(Misc.isAirOrNull(itemStack)) return null;
		NBTItem nbtItem = new NBTItem(itemStack);
		for(BrewingIngredient ingredient : ingredients) {
			PitItem pitItem = ItemFactory.getItem(itemStack);
			if(!(pitItem instanceof BrewingIngredient)) continue;
			return (BrewingIngredient) pitItem;
		}
		return null;
	}

	public static BrewingIngredient getIngredientFromName(String name) {
		for(BrewingIngredient ingredient : ingredients) {
			if(ingredient.name.equalsIgnoreCase(name)) return ingredient;
		}
		return null;
	}

	public static boolean isSame(ItemStack item1, ItemStack item2) {
		if(Misc.isAirOrNull(item1) || Misc.isAirOrNull(item2)) return false;
		PitItem pitItem1 = ItemFactory.getItem(item1);
		PitItem pitItem2 = ItemFactory.getItem(item2);

		if(pitItem1 == null || pitItem2 == null) return false;

		return pitItem1.equals(pitItem2);
	}

	public static void registerIngredient(BrewingIngredient ingredient) {
		ingredients.add(ingredient);
		Bukkit.getServer().getPluginManager().registerEvents(ingredient, PitSim.INSTANCE);
	}
}
