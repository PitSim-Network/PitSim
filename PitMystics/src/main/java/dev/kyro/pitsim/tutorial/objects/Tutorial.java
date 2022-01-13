package dev.kyro.pitsim.tutorial.objects;

import dev.kyro.pitsim.misc.RingCalc;
import dev.kyro.pitsim.misc.SchematicPaste;
import dev.kyro.pitsim.tutorial.Task;
import dev.kyro.pitsim.tutorial.TutorialManager;
import dev.kyro.pitsim.tutorial.sequences.*;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;

public class Tutorial {
	public Player player;
	public TutorialSequence sequence;
	public int position;
	public RingCalc.XYCoords positionCoords;
	public NPC upgradesNPC = null;
	public Location areaLocation;


	public Tutorial(Player player, int position) {
		this.player = player;
		this.sequence = new InitialSequence(player, this);
		this.position = position;
		this.positionCoords = RingCalc.getPosInRing(this.position);
		areaLocation = new Location(Bukkit.getWorld("tutorial"), positionCoords.x, 92, positionCoords.y);
		setUpTutorialArea();
		sequence.play();
	}

	public void onTaskComplete(Task task) {
		if(task.order < sequence.task.order || task.order > sequence.task.order) return;
		for(BukkitTask runnable : sequence.getRunnables()) {
			runnable.cancel();
		}
		sequence = null;

		if(task == Task.VIEW_MAP) sequence = new VampireSequence(player, this);
		if(task == Task.EQUIP_VAMPIRE) sequence = new PerkSequence(player, this);
		if(task == Task.EQUIP_PERKS) sequence = new KillstreakSequence(player, this);
		if(task == Task.EQUIP_KILLSTREAK) sequence = new MegastreakSequence(player, this);
		if(task == Task.EQUIP_MEGASTREAK) sequence = new InitialMysticWellSequence(player, this);
		if(task == Task.VIEW_MYSTIC_WELL) sequence = new ViewEnchantsSequence(player, this);
		if(task == Task.VIEW_ENCHANTS) sequence = new ViewEnchantTiersSequence(player, this);
		if(task == Task.VIEW_ENCHANT_TIERS) sequence = new EnchantBillLsSequence(player, this);

		player.closeInventory();
		if(sequence != null) sequence.play();
	}

	public void setUpTutorialArea() {
		SchematicPaste.loadSchematic(new File("plugins/WorldEdit/schematics/clear.schematic"), areaLocation);
		SchematicPaste.loadSchematic(new File("plugins/WorldEdit/schematics/tutorialArea.schematic"), areaLocation);
	}

	public void spawnUpgradesNPC() {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		NPC npc = registry.createNPC(EntityType.VILLAGER, "&e&lUPGRADES AND KILLSTREAKS");
		Location location = areaLocation.clone();
		location.setPitch(-180);
		location.setY(0);
		npc.spawn(location.add(0, 1, 8));
		upgradesNPC = npc;
	}

	public void cleanUp() {
		upgradesNPC.destroy();
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
