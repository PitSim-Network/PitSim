package dev.kyro.pitsim.brewing.objects;

import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.brewing.BrewingManager;
import dev.kyro.pitsim.brewing.PotionManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BrewingSession {

    public Player player;
    public int brewingSlot;
    public String saveString;
    public BrewingIngredient identifier;
    public BrewingIngredient potency;
    public BrewingIngredient duration;
    public BrewingIngredient reduction;
    public long startTime;

    public BrewingSession(Player player, int brewingSlot, String saveString, BrewingIngredient identifier, BrewingIngredient potency, BrewingIngredient duration, BrewingIngredient reduction) {
        this.player = player;
        this.brewingSlot = brewingSlot;
        this.saveString = saveString;

        if(saveString != null) loadFromSave();
        else {
            this.identifier = identifier;
            this.potency = potency;
            this.duration = duration;
            this.reduction = reduction;
            startTime = System.currentTimeMillis();
            PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
            pitPlayer.brewingSessions.set(brewingSlot - 1, getSaveString());
        }
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

    public void givePotion() {
        ItemStack potion = PotionManager.createPotion(identifier, potency, duration);
        AUtil.giveItemSafely(player, potion);
        BrewingManager.brewingSessions.remove(this);
        PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
        pitPlayer.brewingSessions.set(brewingSlot - 1, null);
    }


    public String getSaveString() {
        return brewingSlot + "," + identifier.tier + "," + potency.tier + "," +
                duration.tier + "," + reduction.tier + "," + startTime;
    }


}
