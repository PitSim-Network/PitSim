package dev.kyro.pitsim.misc;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.inventories.PerkGUI;
import dev.kyro.pitsim.inventories.PrestigeGUI;
import dev.kyro.pitsim.inventories.StatGUI;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SpawnNPCs implements Listener {

	public static NPC upgrade = null;
	public static NPC prestige = null;
	public static NPC kyro = null;
	public static NPC wiji = null;
	public static NPC vnx2 = null;

	public static void createNPCs() {
		createPrestigeNPC();
		createUpgradeNPC();
		createKyroNPC();
		createWijiNPC();
		createVnx2NPC();
	}

	public static void removeNPCs() {
		try {
			upgrade.destroy();
		} catch(Exception ignored) {
			System.out.println("error despawning npc");
		}
		try {
			prestige.destroy();
		} catch(Exception ignored) {
			System.out.println("error despawning npc");
		}
		try {
			kyro.destroy();
		} catch(Exception ignored) {
			System.out.println("error despawning npc");
		}
		try {
			wiji.destroy();
		} catch(Exception ignored) {
			System.out.println("error despawning npc");
		}
		try {
			vnx2.destroy();
		} catch(Exception ignored) {
			System.out.println("error despawning npc");
		}
	}

	public static void createUpgradeNPC() {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		upgrade = registry.createNPC(EntityType.VILLAGER, " ");
		upgrade.spawn(MapManager.getUpgradeNPCSpawn());
	}

	public static void createPrestigeNPC() {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		prestige = registry.createNPC(EntityType.VILLAGER, " ");
		prestige.spawn(MapManager.getPrestigeNPCSpawn());
	}

	public static void createKyroNPC() {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		kyro = registry.createNPC(EntityType.PLAYER, "&9KyroKrypt");
		kyro.spawn(MapManager.getKyroNPCSpawn());
		skin(kyro, "KyroKrypt");
		kyro.addTrait(LookClose.class);
		kyro.getTrait(LookClose.class).setRange(10);
		kyro.getTrait(LookClose.class).toggle();
	}

	public static void createWijiNPC() {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		wiji = registry.createNPC(EntityType.PLAYER, "&9wiji1");
		wiji.spawn(MapManager.getWijiNPCSpawn());
		skin(wiji, "wiji1");
		wiji.addTrait(LookClose.class);
		wiji.getTrait(LookClose.class).setRange(10);
		wiji.getTrait(LookClose.class).toggle();
	}

	public static void createVnx2NPC() {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		vnx2 = registry.createNPC(EntityType.PLAYER, "&e&lSTATISTICS");
		vnx2.spawn(MapManager.getVnx2NPCSpawn());
		skin(vnx2, "vnxz");
		vnx2.addTrait(LookClose.class);
		vnx2.getTrait(LookClose.class).setRange(10);
		vnx2.getTrait(LookClose.class).toggle();
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

		if(event.getNPC().getId() == vnx2.getId()) {
			StatGUI statGUI = new StatGUI(player);
			statGUI.open();
		}
	}

	public static void skin(NPC npc, String name) {
		npc.data().set(NPC.PLAYER_SKIN_UUID_METADATA, name);
		npc.data().set(NPC.PLAYER_SKIN_USE_LATEST, false);
		if (npc.isSpawned()) {
			SkinnableEntity skinnable = (SkinnableEntity) npc.getEntity();
			if (skinnable != null) {
				skinnable.setSkinName(name);
			}
		}
	}
}
