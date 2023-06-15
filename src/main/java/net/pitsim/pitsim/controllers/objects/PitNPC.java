package net.pitsim.pitsim.controllers.objects;

import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.pitsim.controllers.SkinManager;
import net.pitsim.pitsim.misc.MinecraftSkin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public abstract class PitNPC implements Listener {
	public static NPCRegistry registry = CitizensAPI.getNPCRegistry();

	public List<NPC> npcs = new ArrayList<>();
	public long cooldown = 0;

	public PitNPC(List<World> worlds) {
		this(worlds.toArray(new World[0]));
	}

	public PitNPC(World... worlds) {
		for(World world : worlds) createNPC(getFinalLocation(world));
	}

	//	The raw location is only needed if the final location is not being overwritten
	public abstract Location getRawLocation();

	public abstract void createNPC(Location location);

	public abstract void onClick(Player player);

	@EventHandler
	public void onClickEvent(NPCRightClickEvent event) {
		Player player = event.getClicker();

		for(NPC npc : npcs) {
			if(event.getNPC().getId() != npc.getId()) continue;

			boolean canOpen;
			if(cooldown != 0) {
				canOpen = 1000 + cooldown < System.currentTimeMillis();
				if(canOpen) cooldown = 0;
			} else {
				canOpen = true;
				cooldown = System.currentTimeMillis();
			}

			if(!canOpen) return;

			onClick(player);
			return;
		}
	}

	public Location getFinalLocation(World world) {
		Location location = getRawLocation().clone();
		location.setWorld(world);
		return location;
	}

	public void remove() {
		try {
			for(NPC npc : npcs) npc.destroy();
		} catch(Exception ignored) {
			AOutput.log("Error despawning npc");
		}
	}

	public void spawnVillagerNPC(String name, Location location) {
		NPC npc = registry.createNPC(EntityType.VILLAGER, name);
		npc.spawn(location);
		npc.getEntity().setCustomNameVisible(false);
		npcs.add(npc);
	}

	public void spawnPlayerNPC(String name, String skinName, Location location, boolean lookClose) {
		NPC tempVillager = registry.createNPC(EntityType.VILLAGER, name);
		tempVillager.spawn(location);
		tempVillager.getEntity().setCustomNameVisible(!name.isEmpty());
		npcs.add(tempVillager);

		MinecraftSkin minecraftSkin = MinecraftSkin.getSkin(skinName);
		if(minecraftSkin != null) {
			NPC npc = registry.createNPC(EntityType.PLAYER, name);
			npc.spawn(location);

			SkinTrait skinTrait = CitizensAPI.getTraitFactory().getTrait(SkinTrait.class);
			npc.addTrait(skinTrait);
			skinTrait.setSkinPersistent(skinName, minecraftSkin.signature, minecraftSkin.skin);

			setupNPC(npc, tempVillager, lookClose);
			AOutput.log("Loading the skin " + skinName + " for " + (name.isEmpty() ? "(No Name)" : name) + "&f from local data");
			return;
		}

		SkinManager.loadAndSkinNPC(skinName, new BukkitRunnable() {
			@Override
			public void run() {
				NPC npc = registry.createNPC(EntityType.PLAYER, name);
				npc.spawn(location);
				SkinManager.skinNPC(npc, skinName);
				setupNPC(npc, tempVillager, lookClose);
			}
		});
	}

	public void setupNPC(NPC npc, NPC tempVillager, boolean lookClose) {
		if(lookClose) {
			npc.addTrait(LookClose.class);
			npc.getTrait(LookClose.class).setRange(10);
			npc.getTrait(LookClose.class).toggle();
		}
		npcs.remove(tempVillager);
		tempVillager.destroy();
		npcs.add(npc);
	}
}
