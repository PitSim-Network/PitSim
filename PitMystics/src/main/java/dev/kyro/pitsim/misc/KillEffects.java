package dev.kyro.pitsim.misc;


import dev.kyro.pitsim.enums.KillEffect;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class KillEffects {

    public static void trigger(Player killer, KillEffect killEffect, Location location) {


        if(killEffect == KillEffect.EXE_DEATH) {
            Sounds.EXE.play(killer);
            location.getWorld().playEffect(location.add(0, 1, 0), Effect.STEP_SOUND, 152);
        } else if(killEffect == KillEffect.FIRE) {
            Sounds.KILL_FIRE.play(killer);
            location.getWorld().spigot().playEffect(location,
                    Effect.FLAME, 0, 2, 0F, 1F, 0F,0.08F, 100, 6);

        }

    }

}
