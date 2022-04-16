package dev.kyro.pitsim.brewing.objects;

import org.bukkit.entity.Player;

import java.sql.Timestamp;

public class BrewingSession {

    public Player player;
    public int brewingSlot;
    public String saveString;
    public BrewingIngredient identifier;
    public BrewingIngredient potency;
    public BrewingIngredient duration;
    public BrewingIngredient reduction;
    public long startTime;

    public BrewingSession(Player player, int brewingSlot, String saveString) {
        this.player = player;
        this.brewingSlot = brewingSlot;
        this.saveString = saveString;

        if(saveString != null) loadFromSave();
        else startTime = System.currentTimeMillis();
    }

    public void loadFromSave() {
        String[] saveValues = saveString.split(",");
        brewingSlot = Integer.parseInt(saveValues[0]);
        identifier = BrewingIngredient.getIngredientFromTier(Integer.parseInt(saveValues[1]));
        potency = BrewingIngredient.getIngredientFromTier(Integer.parseInt(saveValues[2]));
        duration = BrewingIngredient.getIngredientFromTier(Integer.parseInt(saveValues[3]));
        reduction = BrewingIngredient.getIngredientFromTier(Integer.parseInt(saveValues[4]));
        startTime = Long.parseLong(saveValues[5]);
    }


    public String getSaveString() {
        return brewingSlot + "," + identifier.tier + "," + potency.tier + "," +
                duration.tier + "," + reduction.tier + "," + startTime;
    }


}
