package dev.kyro.pitsim.tutorial;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.TaintedWell;
import dev.kyro.pitsim.misc.MinecraftSkin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.npc.ai.CitizensNavigator;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class TutorialNPC implements Listener {
	public static final String NPC_SKIN_NAME = "wiji1";
	public static final String NPC_NAME = "tutorial";
	public static final Location NPC_SPAWN_LOCATION = MapManager.getDarkzoneSpawn();

	public NPC npc;
	public Tutorial tutorial;
	public boolean isTalking = false;
	private NPCCheckpoint currentCheckpoint;

	public TutorialNPC(Tutorial tutorial) {
		this.tutorial = tutorial;
		create();

		currentCheckpoint = null;

		Bukkit.getServer().getPluginManager().registerEvents(this, PitSim.INSTANCE);
	}

	public void create() {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		MinecraftSkin minecraftSkin = MinecraftSkin.getSkin(NPC_SKIN_NAME);
		if(minecraftSkin != null) {
			npc = registry.createNPC(EntityType.PLAYER, NPC_NAME);
			npc.spawn(NPC_SPAWN_LOCATION);

			npc.addTrait(LookClose.class);
			npc.getTrait(LookClose.class).setRange(10);
			npc.getTrait(LookClose.class).toggle();

			SkinTrait skinTrait = CitizensAPI.getTraitFactory().getTrait(SkinTrait.class);
			npc.addTrait(skinTrait);
			skinTrait.setSkinPersistent(NPC_SKIN_NAME, minecraftSkin.signature, minecraftSkin.skin);
		} else throw new RuntimeException("Could not find skin for tutorial NPC");
	}

	public void remove() {
		npc.destroy();
	}

	public void walkToCheckPoint(NPCCheckpoint checkpoint) {
		CitizensNavigator navigator = (CitizensNavigator) npc.getNavigator();
		navigator.getDefaultParameters()
				.distanceMargin(0.1)
				.pathDistanceMargin(0.1)
				.destinationTeleportMargin(-1);
		navigator.setTarget(checkpoint.location);

		System.out.println(navigator.getDefaultParameters().distanceMargin() + " " + navigator.getDefaultParameters().pathDistanceMargin());

		new BukkitRunnable() {
			@Override
			public void run() {
				if(!npc.isSpawned()) {
					cancel();
					return;
				}

				if(npc.getEntity().getLocation().distance(checkpoint.location) <= 2) {
					cancel();
					npc.getNavigator().setTarget(null, true);
					npc.faceLocation(tutorial.pitPlayer.player.getLocation());
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0, 10);
	}

	@EventHandler
	public void onNPCClick(NPCRightClickEvent event) {
		if(event.getNPC() != npc) return;

		if(currentCheckpoint == null || tutorial.isInObjective) {
			return;
		}

		boolean hasCompleted = tutorial.data.completedObjectives.contains(currentCheckpoint.objective);

		if(hasCompleted) {
			tutorial.sendMessage("You have already completed this objective", 0);
			return;
		}

		if(currentCheckpoint.canSatisfy(tutorial)) {
			currentCheckpoint.onSatisfy(tutorial, currentCheckpoint.getSatisfyDelay());
		} else if(currentCheckpoint.canEngage(tutorial) && !TaintedWell.playerItems.containsKey(tutorial.pitPlayer.player)) engage(currentCheckpoint);
		else tutorial.sendMessage("&cPlease remove some items from your inventory to continue", 0);
	}

	public void setCheckpoint(NPCCheckpoint checkpoint) {
		if(!checkpoint.canEngage(tutorial) || TaintedWell.playerItems.containsKey(tutorial.pitPlayer.player)) {
			tutorial.sendMessage("&cPlease remove some items from your inventory to continue", 0);
			return;
		}



		currentCheckpoint = checkpoint;
		walkToCheckPoint(checkpoint);
		engage(checkpoint);
	}

	public void engage(NPCCheckpoint checkpoint) {
		currentCheckpoint.onDisengage(tutorial);
		checkpoint.onEngage(tutorial, checkpoint.getEngageDelay());
	}

	public NPCCheckpoint getCheckpoint() {
		return currentCheckpoint;
	}

	public void setTalking(boolean talking, int delay) {
		new BukkitRunnable() {
			@Override
			public void run() {
				isTalking = talking;
			}
		}.runTaskLater(PitSim.INSTANCE, delay);
	}

}
