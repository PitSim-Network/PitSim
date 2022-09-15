package dev.kyro.pitsim.misc;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.inventories.*;
import dev.kyro.pitsim.inventories.stats.StatGUI;
import dev.kyro.pitsim.slayers.tainted.SimpleSkin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpawnNPCs implements Listener {

	public static List<NPC> upgrade = new ArrayList<>();
	public static List<NPC> prestige = new ArrayList<>();
	public static List<NPC> kyro = new ArrayList<>();
	public static List<NPC> wiji = new ArrayList<>();
	public static List<NPC> splk = new ArrayList<>();
	public static List<NPC> vnx = new ArrayList<>();
	public static List<NPC> keeper = new ArrayList<>();

	public static NPC taintedShop;
	public static NPC leggingMerchant;
	public static NPC potionMaster;
	public static NPC auctioneer;

	public static NPC skinTest;

//	@EventHandler
//	public void onTeleport(PlayerTeleportEvent event) {
//		List<NPC> destroy = new ArrayList<>();
//
//		for (NPC npc : CitizensAPI.getNPCRegistry()) {
//			if(!(npc.getEntity() instanceof Player)) continue;
//			if(NonManager.getNon((LivingEntity) npc.getEntity()) != null) continue;
//			if(upgrade.contains(npc)) continue;
//			if(prestige.contains(npc)) continue;
//			if(kyro.contains(npc)) continue;
//			if(keeper.contains(npc)) continue;
//			if(wiji.contains(npc)) continue;
//			if(!SpawnManager.isInSpawn(npc.getEntity().getLocation())) continue;
//
//			destroy.add(npc);
//		}
//
//		destroy.forEach(NPC::destroy);
//	}

	public static void createNPCs() {
		for(World world : MapManager.currentMap.lobbies) {
			createPrestigeNPC(world);
			createUpgradeNPC(world);
			createKyroNPC(world);
			createWijiNPC(world);
			createSplkNPC(world);
			createVnx2NPC(world);
			createKeeperNPC(world);
		}
		createTaintedShopNPC();
		createLeggingNPC();
		createPotionNPC();
		createAuctioneer();
		createSkinTest();
	}

	public static void removeNPCs() {
		try {
			potionMaster.destroy();
		} catch(Exception ignored) {
			System.out.println("error despawning npc");
		}
		try {
			taintedShop.destroy();
		} catch(Exception ignored) {
			System.out.println("error despawning npc");
		}
		try {
			leggingMerchant.destroy();
		} catch(Exception ignored) {
			System.out.println("error despawning npc");
		}
		try {
			auctioneer.destroy();
		} catch(Exception ignored) {
			System.out.println("error despawning npc");
		}
		try {
			skinTest.destroy();
		} catch(Exception ignored) {
			System.out.println("error despawning npc");
		}
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
			for(NPC npc : splk) {
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
		try {
			for(NPC npc : keeper) {
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

	public static void createSplkNPC(World world) {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		NPC npc = registry.createNPC(EntityType.PLAYER, "&9Splkpig");
		splk.add(npc);
		npc.spawn(MapManager.currentMap.getSplkNPCSpawn(world));
		skin(npc, "Splkpig");
		npc.addTrait(LookClose.class);
		npc.getTrait(LookClose.class).setRange(10);
		npc.getTrait(LookClose.class).toggle();
	}

	public static void createVnx2NPC(World world) {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		NPC npc = registry.createNPC(EntityType.PLAYER, "&e&lLB AND STATS");
		vnx.add(npc);
		npc.spawn(MapManager.currentMap.getVnxNPCSpawn(world));
		skin(npc, Bukkit.getOfflinePlayer(UUID.fromString("e913fd01-e84e-4c6e-ad5b-7419a12de481")).getName());
		npc.addTrait(LookClose.class);
		npc.getTrait(LookClose.class).setRange(10);
		npc.getTrait(LookClose.class).toggle();
	}

	public static void createKeeperNPC(World world) {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		NPC npc = registry.createNPC(EntityType.PLAYER, "&2&lTHE KEEPER");
		keeper.add(npc);
		npc.spawn(MapManager.currentMap.getKeeperNPC(world));
		skin(npc, "googasesportsog");
	}


	public static void createTaintedShopNPC() {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		NPC npc = registry.createNPC(EntityType.PLAYER, "&d");
		npc.spawn(new Location(MapManager.getDarkzone(), 214, 91, -113, 25, 0));
		taintedShop = npc;
		skin(npc, "Yeung_1217");

	}

	public static void createLeggingNPC() {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		NPC npc = registry.createNPC(EntityType.PLAYER, ChatColor.AQUA + "" + ChatColor.BOLD + "ARMOR TRADER");
		npc.spawn(new Location(MapManager.getDarkzone(), 186, 91, -103, -40, 0));
		leggingMerchant = npc;
		skin(npc, "Merchant");

	}

	public static void createPotionNPC() {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		NPC npc = registry.createNPC(EntityType.PLAYER, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "POTION MASTER");
		npc.spawn(new Location(MapManager.getDarkzone(), 195.5, 91, -84.5, -147, 0));
		potionMaster = npc;
		skin(npc, "Wiizard");
	}

	public static void createAuctioneer() {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		NPC npc = registry.createNPC(EntityType.PLAYER, ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "SHADY FIGURE");
		npc.spawn(new Location(MapManager.getDarkzone(), 254.5, 91, -128.5, -14, 0));
		auctioneer = npc;
		skin(npc, "Itz_Aethan");
	}

	public static void createSkinTest() {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		NPC npc = registry.createNPC(EntityType.PLAYER, ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "");
		npc.spawn(new Location(MapManager.getDarkzone(), 155, 93, -118, -14, 0));
		skinTest = npc;


		for(int i = 0; i < SimpleSkin.values().length; i++) {

			int finalI = i;
			new BukkitRunnable() {
				@Override
				public void run() {
					skin(npc, SimpleSkin.values()[finalI].skin);
				}
			}.runTaskLater(PitSim.INSTANCE, i * 20L);
		}
	}


	@EventHandler
	public void onClickEvent(NPCRightClickEvent event) {

		Player player = event.getClicker();

		for(NPC npc : upgrade) {
			if(event.getNPC().getId() == npc.getId()) {
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

		for(NPC npc : keeper) {
			if(event.getNPC().getId() == npc.getId()) {
				MapManager.changeLobbies(player);
				return;
			}
		}

		if(event.getNPC().getId() == taintedShop.getId()) {
			TaintedGUI taintedGUI = new TaintedGUI(player);
			taintedGUI.open();
		}

		if(event.getNPC().getId() == leggingMerchant.getId()) {
			LeggingsGUI leggingsGUI = new LeggingsGUI(player);
			leggingsGUI.open();
		}

		if(event.getNPC().getId() == potionMaster.getId()) {
			PotionMasterGUI potionMasterGUI = new PotionMasterGUI(player);
			potionMasterGUI.open();
		}

		if(event.getNPC().getId() == auctioneer.getId()) {
			AOutput.send(player, "&8&lSHADY FIGURE&7: &ePsst. Visit the door behind me to spend your &fTainted Souls&e.");
		}
	}

	public static void skin(NPC npc, String name) {
		npc.data().set(NPC.PLAYER_SKIN_UUID_METADATA, name);
		npc.data().set(NPC.PLAYER_SKIN_USE_LATEST, false);
		if(npc.isSpawned()) {
			SkinnableEntity skinnable = (SkinnableEntity) npc.getEntity();
			if(skinnable != null) {
				skinnable.setSkinName(name);
			}
		}
	}
}
