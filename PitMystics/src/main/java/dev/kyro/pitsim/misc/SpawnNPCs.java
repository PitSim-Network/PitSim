package dev.kyro.pitsim.misc;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.inventories.PerkGUI;
import dev.kyro.pitsim.inventories.PrestigeGUI;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SpawnNPCs implements Listener {

	public static NPC upgrade = null;
	public static NPC prestige = null;

	public static void createNPCs() {
		createPrestigeNPC();
		createUpgradeNPC();
	}

	public static void removeNPCs() {
		upgrade.destroy();
		prestige.destroy();
	}

	public static void createUpgradeNPC() {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();

		NPC upgradesNPC = registry.createNPC(EntityType.VILLAGER, " ");
		upgradesNPC.spawn(MapManager.getUpgradeNPCSpawn());
		upgrade = upgradesNPC;
	}

	public static void createPrestigeNPC() {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();

		NPC prestigeNPC = registry.createNPC(EntityType.VILLAGER, " ");
		prestigeNPC.spawn(MapManager.getPrestigeNPCSpawn());
		prestige = prestigeNPC;
	}

	@EventHandler
	public void onClickEvent(NPCRightClickEvent event){

		Player player = event.getClicker();

		if(event.getNPC().getId() == upgrade.getId())  {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
			if(pitPlayer.megastreak.isOnMega()) {
				AOutput.error(player, "&cYou cannot use this command while on a megastreak!");
				return;
			}

			PerkGUI perkGUI = new PerkGUI(player);
			perkGUI.open();
		}

		if(event.getNPC().getId() == prestige.getId()) {
			PrestigeGUI prestigeGUI = new PrestigeGUI(player);
			prestigeGUI.open();
		}

	}



}
