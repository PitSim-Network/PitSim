package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.misc.Misc;
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
				for(Map.Entry<String, List<BukkitRunnable>> entry : new ArrayList<>(callbackMap.entrySet())) {
					System.out.println("Loading Skin: " + entry.getKey());
					loadSkin(entry.getKey());
					if(!isSkinLoaded(entry.getKey())) break;
					for(BukkitRunnable runnable : callbackMap.remove(entry.getKey())) runnable.runTask(PitSim.INSTANCE);
					break;
				}
			}
		}.runTaskTimerAsynchronously(PitSim.INSTANCE, Misc.getRunnableOffset(1), 20 * 60);
	}

	public static void skinNPC(NPC npc, String skinName) {
		if(!isSkinLoaded(skinName)) {
			AOutput.log("Skin isn't loaded for npc " + skinName);
			return;
		}
		if(npc.isSpawned()) {
			SkinTrait skinTrait = CitizensAPI.getTraitFactory().getTrait(SkinTrait.class);
			npc.addTrait(skinTrait);
			skinTrait.setSkinName(skinName);
		} else {
			System.out.println("could not skin " + skinName + " npc as it is not spawned");
		}
	}

	public static void loadAndSkinNPC(String skinName, BukkitRunnable callback) {
		if(isSkinLoaded(skinName)) {
			callback.runTask(PitSim.INSTANCE);
			return;
		}
		loadSkin(skinName);
		callbackMap.putIfAbsent(skinName, new ArrayList<>());
		callbackMap.get(skinName).add(callback);
	}

	public static void loadSkin(String skinName) {
		if(isSkinLoaded(skinName)) return;

		if(loadingSkins.contains(skinName)) return;
		loadingSkins.add(skinName);
		Skin.get(skinName, true);
	}

	public static boolean isSkinLoaded(String skinName) {
		return Skin.get(skinName, false).getSkinId() != null;
	}
}
