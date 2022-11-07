package dev.kyro.pitsim.npcs;

import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitNPC;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.inventories.help.HelpGUI;
import dev.kyro.pitsim.tutorial.Tutorial;
import dev.kyro.pitsim.tutorial.TutorialObjective;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;

import java.util.List;

public class KitNPC extends PitNPC {

	public KitNPC(List<World> worlds) {
		super(worlds);
	}

	@Override
	public Location getRawLocation() {
		return null;
	}

	@Override
	public Location getFinalLocation(World world) {
		return MapManager.currentMap.getKitNPCSpawn(world);
	}

	@Override
	public void createNPC(Location location) {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		NPC npc = registry.createNPC(EntityType.RABBIT, " ");
		npc.spawn(location);
		Rabbit rabbit = (Rabbit) npc.getEntity();
		rabbit.setRabbitType(Rabbit.Type.WHITE);
		npc.getEntity().setCustomNameVisible(false);
		npcs.add(npc);
	}

	@Override
	public void onClick(Player player) {

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		Tutorial tutorial = pitPlayer.tutorial;

		if(tutorial.isInObjective) return;
		if(tutorial.isActive() && !tutorial.isCompleted(TutorialObjective.KITS)) {

			tutorial.sendMessage("&c&lKITS: &eIf you don't know what to use, you'll be nothing but a sitting duck...", 0);
			tutorial.sendMessage("&c&lKITS: &eLuckily, I have the essentials you'll need to play like a pro!", 20 * 4);
			tutorial.sendMessage("&c&lKITS: &eClick on me again to access the kits. Best to take 1 of each!", 20 * 8);
			tutorial.completeObjective(TutorialObjective.KITS, 20 * 12);

			return;
		}

		HelpGUI helpGUI = new HelpGUI(player);
		helpGUI.kitPanel.openPanel(helpGUI.kitPanel);
	}
}
