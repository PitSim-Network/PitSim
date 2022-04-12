package dev.kyro.pitsim.controllers.objects;

import dev.kyro.pitsim.enums.SubLevel;
import org.bukkit.entity.Player;

public abstract class PitBoss {
    public Player target;
    public SubLevel subLevel;

    public PitBoss(Player target, SubLevel subLevel) {
        this.target = target;
        this.subLevel = subLevel;
    }

    public abstract void onAttack() throws Exception;

    public abstract void onDefend();

    public abstract void onDeath();

    public abstract Player getEntity();

}
