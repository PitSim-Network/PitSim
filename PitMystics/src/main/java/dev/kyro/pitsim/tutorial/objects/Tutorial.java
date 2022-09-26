package dev.kyro.pitsim.tutorial.objects;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.killstreaks.NoKillstreak;
import dev.kyro.pitsim.megastreaks.NoMegastreak;
import dev.kyro.pitsim.misc.RingCalc;
import dev.kyro.pitsim.misc.SchematicPaste;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.perks.NoPerk;
import dev.kyro.pitsim.tutorial.Task;
import dev.kyro.pitsim.tutorial.TutorialManager;
import dev.kyro.pitsim.tutorial.TutorialMessage;
import dev.kyro.pitsim.tutorial.inventories.ApplyEnchantPanel;
import dev.kyro.pitsim.tutorial.sequences.*;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Tutorial {
	public Player player;
	public TutorialSequence sequence;
	public int position;
	public RingCalc.XYCoords positionCoords;
	public NPC upgradesNPC = null;
	public NPC prestigeNPC = null;
	public Location areaLocation;
	public Location nonSpawn;
	public Location playerSpawn;
	public ApplyEnchantPanel panel = null;
	public List<NPC> nons = new ArrayList<>();
	public Hologram mysticWellHolo;
	public Hologram skipHolo;


	public Tutorial(Player player, int position) {
		this.player = player;
		this.sequence = new InitialSequence(player, this);
		this.position = position;
		this.positionCoords = RingCalc.getPosInRing(this.position);
		areaLocation = new Location(Bukkit.getWorld("tutorial"), positionCoords.x, 92, positionCoords.y, 180, 0);
		nonSpawn = areaLocation.clone();
		playerSpawn = areaLocation.clone();
		playerSpawn.setPitch(0);
		playerSpawn.setYaw(0);
		setUpTutorialArea();
		sequence.play();

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		for(int i = 0; i < pitPlayer.pitPerks.size(); i++) {
			pitPlayer.pitPerks.set(i, NoPerk.INSTANCE);
		}

		for(int i = 0; i < pitPlayer.killstreaks.size(); i++) {
			pitPlayer.killstreaks.set(i, NoKillstreak.INSTANCE);
		}

		pitPlayer.megastreak = new NoMegastreak(pitPlayer);
		spawnSkipText();
	}

	public void onTaskComplete(Task task) {
		if(player.getWorld() != MapManager.getTutorial()) player.teleport(playerSpawn);
		if(task.order < sequence.task.order || task.order > sequence.task.order) return;
		for(BukkitTask runnable : sequence.getRunnables()) {
			runnable.cancel();
		}
		Sounds.LEVEL_UP.play(player);
		AOutput.send(player, TutorialMessage.SPACER.message);

		player.closeInventory();

		new BukkitRunnable() {
			@Override
			public void run() {
				player.closeInventory();
			}
		}.runTaskLater(PitSim.INSTANCE, 5L);
		sequence = null;

		if(task == Task.VIEW_MAP) sequence = new VampireSequence(player, this);
		if(task == Task.EQUIP_VAMPIRE) sequence = new PerkSequence(player, this);
		if(task == Task.EQUIP_PERKS) sequence = new KillstreakSequence(player, this);
		if(task == Task.EQUIP_KILLSTREAK) sequence = new MegastreakSequence(player, this);
		if(task == Task.EQUIP_MEGASTREAK) sequence = new InitialMysticWellSequence(player, this);
		if(task == Task.VIEW_MYSTIC_WELL) sequence = new ViewEnchantsSequence(player, this);
		if(task == Task.VIEW_ENCHANTS) sequence = new ViewEnchantTiersSequence(player, this);
		if(task == Task.VIEW_ENCHANT_TIERS) sequence = new EnchantBillLsSequence(player, this);
		if(task == Task.ENCHANT_BILL_LS) sequence = new EnchantRGMSequence(player, this);
		if(task == Task.ENCHANT_RGM) sequence = new EnchantMegaDrainSequence(player, this);
		if(task == Task.ENCHANT_MEGA_DRAIN) sequence = new EquipArmorSequence(player, this);
		if(task == Task.EQUIP_ARMOR) sequence = new SpawnNonSequence(player, this);
		if(task == Task.VIEW_NON) sequence = new ActivateMegastreakSequence(player, this);
		if(task == Task.ACTIVATE_MEGASTREAK) sequence = new PrestigeSequence(player, this);
		if(task == Task.PRESTIGE) sequence = new BuyTenacitySequence(player, this);
		if(task == Task.BUY_TENACITY) sequence = new FinalSequence(player, this);
		if(task == Task.FINISH_TUTORIAL) cleanUp();

		if(sequence != null) sequence.play();
	}

	public void setUpTutorialArea() {
		SchematicPaste.loadSchematic(new File("plugins/WorldEdit/schematics/clear.schematic"), areaLocation);
		SchematicPaste.loadTutorialSchematic(new File("plugins/WorldEdit/schematics/tutorialArea.schematic"), areaLocation);
	}

	public void spawnUpgradesNPC() {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		NPC npc = registry.createNPC(EntityType.VILLAGER, ChatColor.YELLOW + "" + ChatColor.BOLD + "UPGRADES AND KILLSTREAKS");
//		Location location = areaLocation.clone();
//		location.setPitch(-180);
//		location.setY(0);
		npc.spawn(areaLocation.add(0.5, 1, 8));
		upgradesNPC = npc;
	}

	public void spawnPrestigeNPC() {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		NPC npc = registry.createNPC(EntityType.VILLAGER, ChatColor.YELLOW + "" + ChatColor.BOLD + "PRESTIGE AND RENOWN");
//		Location location = areaLocation.clone();
//		location.setPitch(-180);
//		location.setY(0);
		npc.spawn(areaLocation);
		prestigeNPC = npc;
	}

	public void spawnSkipText() {
		Location holoLocation = areaLocation.clone().add(0.5, 2.5, 4);
		Hologram holo = HologramsAPI.createHologram(PitSim.INSTANCE, holoLocation);
		holo.appendTextLine(ChatColor.YELLOW + "Skip tutorial with " + ChatColor.WHITE + "\"/tutorial skip\"" + ChatColor.YELLOW + ".");
		mysticWellHolo = holo;
	}

	public void cleanUp() {
		if(upgradesNPC != null) upgradesNPC.destroy();
		if(prestigeNPC != null) prestigeNPC.destroy();
		if(mysticWellHolo != null) mysticWellHolo.delete();
		if(skipHolo != null) skipHolo.delete();
		try {
			if(sequence.getRunnables() != null) {
				for(BukkitTask runnable : sequence.getRunnables()) {
					runnable.cancel();
				}
			}
		} catch(Exception ignored) {
		}
		SchematicPaste.loadSchematic(new File("plugins/WorldEdit/schematics/clear.schematic"), areaLocation);
		TutorialManager.tutorials.remove(this.player);

		for(NPC non : nons) {
			non.destroy();
		}

	}

	public void addOffset(Location location) {
		location.add(positionCoords.x, 0, positionCoords.y);
	}

}
