package dev.kyro.pitsim.misc;


import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.enums.KillEffect;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class KillEffects {

    public static void trigger(Player killer, KillEffect killEffect, Location location) {


        if(killEffect == KillEffect.EXE_DEATH) {
            ASound.play(killer, Sound.VILLAGER_DEATH, 1, 0.5F);
            location.getWorld().playEffect(location.add(0, 1, 0), Effect.STEP_SOUND, 152);
        } else if(killEffect == KillEffect.FIRE) {
            ASound.play(killer, Sound.FIZZ, 2, 2);
            location.getWorld().spigot().playEffect(location,
                    Effect.FLAME, 0, 2, 0F, 1F, 0F,0.08F, 100, 6);

        }

    }

}
