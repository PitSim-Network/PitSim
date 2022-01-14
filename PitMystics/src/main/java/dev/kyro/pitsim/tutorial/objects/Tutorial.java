package dev.kyro.pitsim.tutorial.objects;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.killstreaks.NoKillstreak;
import dev.kyro.pitsim.megastreaks.NoMegastreak;
import dev.kyro.pitsim.misc.RingCalc;
import dev.kyro.pitsim.misc.SchematicPaste;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.perks.NoPerk;
import dev.kyro.pitsim.tutorial.Task;
import dev.kyro.pitsim.tutorial.TutorialManager;
import dev.kyro.pitsim.tutorial.inventories.ApplyEnchantPanel;
import dev.kyro.pitsim.tutorial.sequences.*;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.Arrays;

public class Tutorial {
	public Player player;
	public TutorialSequence sequence;
	public int position;
	public RingCalc.XYCoords positionCoords;
	public NPC upgradesNPC = null;
	public Location areaLocation;
	public ApplyEnchantPanel panel = null;


	public Tutorial(Player player, int position) {
		this.player = player;
		this.sequence = new InitialSequence(player, this);
		this.position = position;
		this.positionCoords = RingCalc.getPosInRing(this.position);
		areaLocation = new Location(Bukkit.getWorld("tutorial"), positionCoords.x, 92, positionCoords.y, -180, 0);
		setUpTutorialArea();
		sequence.play();

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		Arrays.fill(pitPlayer.pitPerks, NoPerk.INSTANCE);

		for(int i = 0; i < pitPlayer.killstreaks.size(); i++) {
			pitPlayer.killstreaks.set(i, NoKillstreak.INSTANCE);
		}

		pitPlayer.megastreak = new NoMegastreak(pitPlayer);
	}

	public void onTaskComplete(Task task) {
		if(task.order < sequence.task.order || task.order > sequence.task.order) return;
		for(BukkitTask runnable : sequence.getRunnables()) {
			runnable.cancel();
		}
		Sounds.LEVEL_UP.play(player);

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

		if(sequence != null) sequence.play();
	}

	public void setUpTutorialArea() {
		SchematicPaste.loadSchematic(new File("plugins/WorldEdit/schematics/clear.schematic"), areaLocation);
		SchematicPaste.loadSchematic(new File("plugins/WorldEdit/schematics/tutorialArea.schematic"), areaLocation);
	}

	public void spawnUpgradesNPC() {
		Bukkit.broadcastMessage("test");
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		NPC npc = registry.createNPC(EntityType.VILLAGER, "&e&lUPGRADES AND KILLSTREAKS");
//		Location location = areaLocation.clone();
//		location.setPitch(-180);
//		location.setY(0);
		npc.spawn(areaLocation.add(0.5, 1, 8));
		upgradesNPC = npc;
	}

	public void cleanUp() {
		if(upgradesNPC != null) upgradesNPC.destroy();
		for(BukkitTask runnable : sequence.getRunnables()) {
			runnable.cancel();
		}
		SchematicPaste.loadSchematic(new File("plugins/WorldEdit/schematics/clear.schematic"), areaLocation);
		TutorialManager.tutorials.remove(this.player);
	}

	public void addOffset(Location location) {
		location.add(positionCoords.x, 0, positionCoords.y);
	}

}
