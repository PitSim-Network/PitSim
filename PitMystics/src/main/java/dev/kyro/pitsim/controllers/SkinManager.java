package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.skin.Skin;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkinManager implements Listener {

	public static Map<String, List<BukkitRunnable>> callbackMap = new HashMap<>();
	public static List<String> loadingSkins = new ArrayList<>();

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Map.Entry<String, List<BukkitRunnable>> entry : new HashMap<>(callbackMap).entrySet()) {
					if(!isSkinLoaded(entry.getKey())) continue;
					for(BukkitRunnable runnable : callbackMap.remove(entry.getKey())) runnable.runTask(PitSim.INSTANCE);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20L);
	}
	public static void skinNPC(NPC npc, String name) {
		if(!isSkinLoaded(name)) {
			AOutput.log("Skin isn't loaded for npc " + name);
			return;
		}
		if(npc.isSpawned()) {
//			npc.addTrait(SkinTrait.class);
//			npc.getTrait(SkinTrait.class).setSkinName(name);
			SkinTrait skinTrait = CitizensAPI.getTraitFactory().getTrait(SkinTrait.class);
			npc.addTrait(skinTrait);
			skinTrait.setSkinName(name);
		} else {
			System.out.println("could not skin " + name + " npc as it is not spawned");
		}
	}

	public static void loadAndSkinNPC(String name, BukkitRunnable callback) {
		if(isSkinLoaded(name)) {
			callback.runTask(PitSim.INSTANCE);
			return;
		}
		loadSkin(name);
		callbackMap.putIfAbsent(name, new ArrayList<>());
		callbackMap.get(name).add(callback);
	}

	public static void loadSkin(String name) {
		if(isSkinLoaded(name)) return;

		if(loadingSkins.contains(name)) return;
		loadingSkins.add(name);
		Skin.get(name, true);
	}

	public static boolean isSkinLoaded(String name) {
		return Skin.get(name, false).getSkinId() != null;
	}
}
