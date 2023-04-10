package dev.kyro.pitsim.tutorial;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.misc.MinecraftSkin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

public class TutorialNPC {
	public static final String NPC_SKIN_NAME = "wiji1";
	public static final String NPC_NAME = "tutorial";

	public NPC npc;
	public Tutorial tutorial;

	public TutorialNPC(Tutorial tutorial) {
		this.tutorial = tutorial;
		create();

		new BukkitRunnable() {
			@Override
			public void run() {
				walkToCheckPoint(NPCCheckpoint.getCheckpoint(1));
			}
		}.runTaskLater(PitSim.INSTANCE, 20);

	}

	public void create() {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		NPCCheckpoint checkpoint = NPCCheckpoint.getCheckpoint(0);
		MinecraftSkin minecraftSkin = MinecraftSkin.getSkin(NPC_SKIN_NAME);
		if(minecraftSkin != null) {
			npc = registry.createNPC(EntityType.PLAYER, NPC_NAME);
			npc.spawn(checkpoint.location);

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
		npc.getNavigator().setTarget(checkpoint.location);

		new BukkitRunnable() {
			@Override
			public void run() {
				if(!npc.isSpawned()) {
					cancel();
					return;
				}

				System.out.println(npc.getEntity().getLocation().distance(checkpoint.location));
				if(npc.getEntity().getLocation().distance(checkpoint.location) <= 2) {
					cancel();
					npc.getNavigator().setTarget(null, true);
					npc.faceLocation(tutorial.pitPlayer.player.getLocation());
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0, 10);
	}

	public enum NPCCheckpoint {
		INITIAL(new Location(MapManager.getDarkzone(), 185, 91, -93), 60),
		WELL(new Location(MapManager.getDarkzone(), 189.5, 92, -105.5, 13, 0), 60);

		public final Location location;
		public final int walkTime;


		NPCCheckpoint(Location location, int walkTime) {
			this.location = location;
			this.walkTime = walkTime;
		}

		public static NPCCheckpoint getCheckpoint(int index) {
			return NPCCheckpoint.values()[index];
		}
	}

}
