package dev.kyro.pitsim.misc;

import dev.kyro.pitsim.controllers.SkinManager;
import net.citizensnpcs.api.npc.NPC;

public class BossSkin {

	NPC npc;
	String skinName;

	public BossSkin(NPC npc, String skinName) {
		this.npc = npc;
		this.skinName = skinName;
	}

	public void skin() {
		SkinManager.skinNPC(npc, skinName);
	}
}
