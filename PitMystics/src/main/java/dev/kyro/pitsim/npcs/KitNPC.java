package dev.kyro.pitsim.npcs;

import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitNPC;
import dev.kyro.pitsim.inventories.help.HelpGUI;
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
		return MapManager.currentMap.getKitsNPCSpawn();
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
		HelpGUI helpGUI = new HelpGUI(player);
		helpGUI.kitPanel.openPanel(helpGUI.kitPanel);
	}
}
