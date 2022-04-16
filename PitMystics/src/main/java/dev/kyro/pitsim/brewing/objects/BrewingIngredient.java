package dev.kyro.pitsim.brewing.objects;

import dev.kyro.pitsim.enums.NBTTag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class BrewingIngredient {
    public int tier;
    public NBTTag nbtTag;

    public static List<BrewingIngredient> ingredients = new ArrayList<>();

    public BrewingIngredient(int tier, NBTTag nbtTag) {
        this.tier = tier;
        this.nbtTag = nbtTag;
    }

    public abstract void administerEffect(Player player, BrewingIngredient potency, BrewingIngredient duration);

    public abstract Object getPotency(BrewingIngredient potencyIngredient);

    public abstract int getDuration(BrewingIngredient durationIngredient);

    public abstract int getBrewingReductionMinutes();

    public abstract ItemStack getItem();


    public static BrewingIngredient getIngredientFromTier(int tier) {
        for (BrewingIngredient ingredient : ingredients) {
            if(ingredient.tier == tier) return ingredient;
        }
        return null;
    }

    public static void registerIngredient(BrewingIngredient ingredient) {
        ingredients.add(ingredient);
    }
}
