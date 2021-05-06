package dev.kyro.pitremake.nons;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.EntityType;

public class Non {

	public NPC npc;

	public Non(String name) {

		npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);
	}
}
