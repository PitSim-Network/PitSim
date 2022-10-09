package dev.kyro.pitsim.misc;

import net.citizensnpcs.api.npc.NPC;

public class BossSkin {

    NPC npc;
    String skinName;

    public BossSkin(NPC npc, String skinName){
        this.npc = npc;
        this.skinName = skinName;
    }

    public void skin() {
//        this.npc.data().setPersistent(NPC.PLAYER_SKIN_UUID_METADATA, this.skinName);
//        this.npc.data().setPersistent(NPC.PLAYER_SKIN_USE_LATEST, false);
    }
}
