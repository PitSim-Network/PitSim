package dev.kyro.pitsim.controllers.objects;

import dev.kyro.pitsim.enums.SubLevel;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import org.bukkit.Location;
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

    public static void skin(NPC npc, String name) {
        npc.data().setPersistent(NPC.PLAYER_SKIN_UUID_METADATA, name);
        npc.data().setPersistent(NPC.PLAYER_SKIN_USE_LATEST, false);

        // send skin change to online players by removing and adding this fake player
        if (npc.isSpawned()) {
            Location loc = npc.getStoredLocation();
            npc.despawn();
            npc.spawn(loc);
        }
    }




}
