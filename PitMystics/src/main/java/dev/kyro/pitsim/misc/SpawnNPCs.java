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
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpawnNPCs implements Listener {

	public static List<NPC> upgrade = new ArrayList<>();
	public static List<NPC> prestige = new ArrayList<>();
	public static List<NPC> kyro = new ArrayList<>();
	public static List<NPC> wiji = new ArrayList<>();
	public static List<NPC> vnx = new ArrayList<>();

	public static void createNPCs() {
		for(World world : MapManager.currentMap.lobbies) {
			createPrestigeNPC(world);
			createUpgradeNPC(world);
			createKyroNPC(world);
			createWijiNPC(world);
			createVnx2NPC(world);
		}
	}

	public static void removeNPCs() {
		try {
			for(NPC npc : upgrade) {
				npc.destroy();
			}
		} catch(Exception ignored) {
			System.out.println("error despawning npc");
		}
		try {
			for(NPC npc : prestige) {
				npc.destroy();
			}
		} catch(Exception ignored) {
			System.out.println("error despawning npc");
		}
		try {
			for(NPC npc : kyro) {
				npc.destroy();
			}
		} catch(Exception ignored) {
			System.out.println("error despawning npc");
		}
		try {
			for(NPC npc : wiji) {
				npc.destroy();
			}
		} catch(Exception ignored) {
			System.out.println("error despawning npc");
		}
		try {
			for(NPC npc : vnx) {
				npc.destroy();
			}
		} catch(Exception ignored) {
			System.out.println("error despawning npc");
		}
	}

	public static void createUpgradeNPC(World world) {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		NPC npc = registry.createNPC(EntityType.VILLAGER, " ");
		upgrade.add(npc);
		npc.spawn(MapManager.currentMap.getUpgradeNPCSpawn(world));
	}

	public static void createPrestigeNPC(World world) {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		NPC npc = registry.createNPC(EntityType.VILLAGER, " ");
		prestige.add(npc);
		npc.spawn(MapManager.currentMap.getPrestigeNPCSpawn(world));
	}

	public static void createKyroNPC(World world) {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		NPC npc = registry.createNPC(EntityType.PLAYER, "&9KyroKrypt");
		kyro.add(npc);
		npc.spawn(MapManager.currentMap.getKyroNPCSpawn(world));
		skin(npc, "KyroKrypt");
		npc.addTrait(LookClose.class);
		npc.getTrait(LookClose.class).setRange(10);
		npc.getTrait(LookClose.class).toggle();
	}

	public static void createWijiNPC(World world) {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		NPC npc = registry.createNPC(EntityType.PLAYER, "&9wiji1");
		wiji.add(npc);
		npc.spawn(MapManager.currentMap.getWijiNPCSpawn(world));
		skin(npc, "wiji1");
		npc.addTrait(LookClose.class);
		npc.getTrait(LookClose.class).setRange(10);
		npc.getTrait(LookClose.class).toggle();
	}

	public static void createVnx2NPC(World world) {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		NPC npc = registry.createNPC(EntityType.PLAYER, "&e&lSTATISTICS");
		vnx.add(npc);
		npc.spawn(MapManager.currentMap.getVnxNPCSpawn(world));
		skin(npc, Bukkit.getOfflinePlayer(UUID.fromString("e913fd01-e84e-4c6e-ad5b-7419a12de481")).getName());
		npc.addTrait(LookClose.class);
		npc.getTrait(LookClose.class).setRange(10);
		npc.getTrait(LookClose.class).toggle();
	}

	@EventHandler
	public void onClickEvent(NPCRightClickEvent event){

		Player player = event.getClicker();

		for(NPC npc : upgrade) {
			if(event.getNPC().getId() == npc.getId())  {
				PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
				if(pitPlayer.megastreak.isOnMega()) {
					AOutput.error(player, "&cYou cannot use this command while on a megastreak!");
					return;
				}

				PerkGUI perkGUI = new PerkGUI(player);
				perkGUI.open();
				return;
			}
		}

		for(NPC npc : prestige) {
			if(event.getNPC().getId() == npc.getId()) {
				PrestigeGUI prestigeGUI = new PrestigeGUI(player);
				prestigeGUI.open();
				return;
			}
		}

		for(NPC npc : vnx) {
			if(event.getNPC().getId() == npc.getId()) {
				StatGUI statGUI = new StatGUI(player);
				statGUI.open();
				return;
			}
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
