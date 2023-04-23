package dev.kyro.pitsim.tutorial;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.SpawnManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.PitEntityType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TutorialManager implements Listener {
	public static List<NPCCheckpoint> checkpoints = new ArrayList<>();
	public static Map<Player, Location> lastLocationMap = new HashMap<>();

	public static final int DARKZONE_OBJECTIVE_DISTANCE = 7;
	public static final Location FINAL_OBJECTIVE_TELEPORT = new Location(MapManager.getDarkzone(), 272.5, 91, -121, -90, 0);

	@EventHandler(priority = EventPriority.LOWEST)
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		DarkzoneTutorial darkzoneTutorial = pitPlayer.darkzoneTutorial;
		if(!darkzoneTutorial.isActive()) return;

		if(!darkzoneTutorial.isInObjective) {
			for(NPCCheckpoint checkpoint : checkpoints) {
				if(checkpoint.objective.getParticleBox().location.distance(player.getLocation()) > DARKZONE_OBJECTIVE_DISTANCE) continue;
				if(darkzoneTutorial.data.completedObjectives.contains(checkpoint.objective)) continue;
				if(darkzoneTutorial.tutorialNPC.getCheckpoint() == checkpoint) continue;

				darkzoneTutorial.tutorialNPC.setCheckpoint(checkpoint);
			}
		}

		if(SpawnManager.isInSpawn(player.getLocation()) || (isOnLastObjective(player) && player.getLocation().getY() > 88)) {
			lastLocationMap.put(player, player.getLocation());
		} else {
			player.teleport(isOnLastObjective(player) ? FINAL_OBJECTIVE_TELEPORT : lastLocationMap.get(player));
			player.setVelocity(new Vector());
			AOutput.error(event.getPlayer(), "&c&c&lERROR!&7 You must complete the tutorial before leaving this area!");
		}
	}

	@EventHandler
	public void onAttack(AttackEvent.Pre attackEvent) {
		Player attacker = attackEvent.getAttackerPlayer();
		Player defender = attackEvent.getDefenderPlayer();
		if(!Misc.isEntity(attacker, PitEntityType.REAL_PLAYER) || !Misc.isEntity(defender, PitEntityType.REAL_PLAYER)) return;

		if(attackEvent.getAttackerPitPlayer().darkzoneTutorial.isActive() || attackEvent.getDefenderPitPlayer().darkzoneTutorial.isActive()) {
			if(attackEvent.getAttackerPitPlayer().darkzoneTutorial.isActive()) {
				AOutput.error(attacker, "&c&lERROR! &7You cannot attack while in the tutorial!");
			} else {
				AOutput.error(attacker, "&c&lERROR! &7This player cannot be attacked since they are in the tutorial!");
			}
			Sounds.NO.play(attacker);
			attackEvent.setCancelled(true);
		}
	}

	@EventHandler
	public void onCommandSend(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!pitPlayer.darkzoneTutorial.isActive()) return;
		if(ChatColor.stripColor(event.getMessage()).toLowerCase().startsWith("/trade")) {
			event.setCancelled(true);
			AOutput.error(player, "&cYou must complete the tutorial before trading!");
			Sounds.NO.play(player);
			return;
		}
	}

	public static NPCCheckpoint getCheckpoint(TutorialObjective objective) {
		for(NPCCheckpoint checkpoint : checkpoints) {
			if(checkpoint.objective == objective) return checkpoint;
		}
		return null;
	}

	public static boolean isOnLastObjective(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		DarkzoneTutorial darkzoneTutorial = pitPlayer.darkzoneTutorial;
		return darkzoneTutorial.isActive() && darkzoneTutorial.data.completedObjectives.size() == darkzoneTutorial.getObjectiveSize() - 1
				&& !darkzoneTutorial.data.completedObjectives.contains(TutorialObjective.MONSTER_CAVES);
	}

	public static boolean isOnLastObjectiveSatisfy(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		DarkzoneTutorial darkzoneTutorial = pitPlayer.darkzoneTutorial;
		return darkzoneTutorial.isActive() && darkzoneTutorial.data.completedObjectives.size() == darkzoneTutorial.getObjectiveSize() - 2
				&& !darkzoneTutorial.data.completedObjectives.contains(TutorialObjective.MONSTER_CAVES);
	}


	public static NPCCheckpoint getCheckpoint(int index) {
		return checkpoints.get(index);
	}
}