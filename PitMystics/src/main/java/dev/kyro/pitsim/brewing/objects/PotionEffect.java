package dev.kyro.pitsim.brewing.objects;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.brewing.PotionManager;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.Potion;

public class PotionEffect implements Listener {
    public Player player;
    public BrewingIngredient potionType;
    public BrewingIngredient potency;
    public BrewingIngredient duration;

    public int ticksLeft;

    public PotionEffect(Player player, BrewingIngredient potionType, BrewingIngredient potency, BrewingIngredient duration) {
        this.player = player;
        this.potionType = potionType;
        this.potency = potency;
        this.duration = duration;

        ticksLeft = potionType.getDuration(duration);
        AOutput.send(player, "&5&lPOTION! &7Effected with " + potionType.color + potionType.name + " " +
                AUtil.toRoman(potency.tier) + " &7for &f" + Misc.ticksToTime(ticksLeft));
    }

    public void tick() {
        ticksLeft--;
        potionType.administerEffect(player, potency, getTimeLeft());
        if(getTimeLeft() == 0) onExpire();
    }

    public int getTimeLeft() {
        return ticksLeft;
    }

    public void onExpire() {
        AOutput.send(player, "&5&lPOTION! " + potionType.color + potionType.name + " &7has expired");
        PotionManager.potionEffectList.remove(this);
        if(PotionManager.playerIndex.get(player) > 0) PotionManager.playerIndex.put(player, PotionManager.playerIndex.get(player) - 1);
        else PotionManager.playerIndex.remove(player);
    }
}
