package dev.kyro.pitsim.brewing.objects;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.aitems.StaticPitItem;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
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
	public String nbtTag;
	public String name;
	public ChatColor color;
	public PotionType potionType;

	public static List<BrewingIngredient> ingredients = new ArrayList<>();

	public BrewingIngredient(int tier, String nbtTag, String name, ChatColor color, PotionType potionType) {
		this.tier = tier;
		this.nbtTag = nbtTag;
		this.name = name;
		this.color = color;
		this.potionType = potionType;
	}

	public abstract void administerEffect(Player player, BrewingIngredient potency, int duration);

	public abstract Object getPotency(BrewingIngredient potencyIngredient);

	public abstract List<String> getPotencyLore(BrewingIngredient potency);

	public abstract int getDuration(BrewingIngredient durationIngredient);

	public abstract int getBrewingReductionMinutes();

	public abstract ItemStack getItem();

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
			if(nbtItem.hasKey(ingredient.nbtTag.getRef())) return true;
		}
		return false;
	}

	public static BrewingIngredient getIngredientFromItemStack(ItemStack itemStack) {
		if(Misc.isAirOrNull(itemStack)) return null;
		NBTItem nbtItem = new NBTItem(itemStack);
		for(BrewingIngredient ingredient : ingredients) {
			if(nbtItem.hasKey(ingredient.nbtTag.getRef())) return ingredient;
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
		NBTItem nbtItem = new NBTItem(item1);
		NBTItem nbtItem2 = new NBTItem(item2);
		for(BrewingIngredient ingredient : ingredients) {
			if(nbtItem.hasKey(ingredient.nbtTag.getRef())) {
				if(nbtItem2.hasKey(ingredient.nbtTag.getRef())) return true;
			}
		}
		return false;
	}

	public static void registerIngredient(BrewingIngredient ingredient) {
		ingredients.add(ingredient);
		Bukkit.getServer().getPluginManager().registerEvents(ingredient, PitSim.INSTANCE);
	}
}
