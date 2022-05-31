package dev.kyro.pitsim.slayers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitBoss;
import dev.kyro.pitsim.enums.SubLevel;
import dev.kyro.pitsim.slayers.tainted.SimpleBoss;
import dev.kyro.pitsim.slayers.tainted.SimpleSkin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class test extends PitBoss {

    public NPC npc;
    public Player entity;
    public Player target;
    public String name = "&c&lSkeleton Boss";
    public SubLevel subLevel = SubLevel.SKELETON_CAVE;
    public SimpleBoss boss;

    public test(Player target, SubLevel subLevel) {
        super(target, subLevel);

        npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);

        this.boss = new SimpleBoss(npc, target, subLevel, 2, SimpleSkin.SKELETON);
        this.entity = (Player) npc.getEntity();



        boss.run();

    }

    @Override
    public void onAttack() throws Exception {

    }

    @Override
    public void onDefend() {

    }

    @Override
    public void onDeath() {
        boss.hideActiveBossBar(PitSim.adventure.player(target));
    }

    @Override
    public Player getEntity() {
        return entity;
    }
}
