package dev.kyro.pitsim.acosmetics;

import dev.kyro.pitsim.RedstoneColor;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CosmeticManager implements Listener {
	public static Map<CosmeticType, List<PitCosmetic>> cosmeticMap = new LinkedHashMap<>();

	static {
		for(CosmeticType value : CosmeticType.values()) {
			cosmeticMap.put(value, new ArrayList<>());
		}
	}

	public static void loadForOnlinePlayers() {
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
			List<PitCosmetic> activeCosmetics = CosmeticManager.getActiveCosmetics(pitPlayer);
			for(PitCosmetic activeCosmetic : activeCosmetics) {
				activeCosmetic.onEnable(pitPlayer);
			}
		}
	}

	public static void registerCosmetic(PitCosmetic pitCosmetic) {
		cosmeticMap.get(pitCosmetic.cosmeticType).add(pitCosmetic);
	}

	public static PitCosmetic getCosmetic(String refName) {
		for(Map.Entry<CosmeticType, List<PitCosmetic>> entry : cosmeticMap.entrySet()) {
			for(PitCosmetic pitCosmetic : entry.getValue()) {
				if(pitCosmetic.refName.equals(refName)) return pitCosmetic;
			}
		}
		return null;
	}

	public static void unlockCosmetic(PitPlayer pitPlayer, PitCosmetic pitCosmetic, RedstoneColor redstoneColor) {
		PitPlayer.UnlockedCosmeticData cosmeticData = new PitPlayer.UnlockedCosmeticData();
		if(pitCosmetic.isColorCosmetic) {
			if(!pitPlayer.unlockedCosmeticsMap.containsKey(pitCosmetic.refName)) {
				cosmeticData.unlockedColors = new ArrayList<>();
			}
			cosmeticData.unlockedColors.add(redstoneColor);
		}
		pitPlayer.unlockedCosmeticsMap.put(pitCosmetic.refName, cosmeticData);
	}

	public static List<PitCosmetic> getUnlockedCosmetics(PitPlayer pitPlayer, CosmeticType cosmeticType) {
		List<PitCosmetic> cosmeticList = CosmeticManager.cosmeticMap.get(cosmeticType);
		cosmeticList.removeIf(pitCosmetic -> !pitCosmetic.isUnlocked(pitPlayer));
		return cosmeticList;
	}

	public static List<PitCosmetic> getActiveCosmetics(PitPlayer pitPlayer) {
		List<PitCosmetic> activeCosmetics = new ArrayList<>();
		loop:
		for(Map.Entry<CosmeticType, List<PitCosmetic>> entry : cosmeticMap.entrySet()) {
			for(PitCosmetic pitCosmetic : entry.getValue()) {
				if(!pitCosmetic.isEquipped(pitPlayer)) continue;
				activeCosmetics.add(pitCosmetic);
				continue loop;
			}
		}
		return activeCosmetics;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		List<PitCosmetic> activeCosmetics = getActiveCosmetics(pitPlayer);
		for(PitCosmetic activeCosmetic : activeCosmetics) activeCosmetic.onEnable(pitPlayer);
	}

	@EventHandler
	public void onJoin(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		List<PitCosmetic> activeCosmetics = getActiveCosmetics(pitPlayer);
		for(PitCosmetic activeCosmetic : activeCosmetics) activeCosmetic.onDisable(pitPlayer);
	}
}
