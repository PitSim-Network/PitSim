package dev.kyro.pitsim.cosmetics;

import de.myzelyam.api.vanish.PlayerVanishStateChangeEvent;
import de.myzelyam.api.vanish.VanishAPI;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.cosmetics.particles.ParticleColor;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CosmeticManager implements Listener {
	public static Map<CosmeticType, List<PitCosmetic>> cosmeticMap = new LinkedHashMap<>();
	public static Map<UUID, LocationData> playerPositionMap = new HashMap<>();

	static {
		for(CosmeticType value : CosmeticType.values()) {
			cosmeticMap.put(value, new ArrayList<>());
		}

		new BukkitRunnable() {
			int count = 0;

			@Override
			public void run() {
				if(count++ % 60 * 4 == 0) {
					loop:
					for(Map.Entry<UUID, LocationData> entry : new HashMap<>(playerPositionMap).entrySet()) {
						for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
							if(!onlinePlayer.getUniqueId().equals(entry.getKey())) continue;
							playerPositionMap.remove(onlinePlayer.getUniqueId());
							continue loop;
						}
					}
				}
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					Location playerLocation = onlinePlayer.getLocation();
					playerPositionMap.putIfAbsent(onlinePlayer.getUniqueId(), new LocationData(playerLocation));
					LocationData previousLocation = playerPositionMap.get(onlinePlayer.getUniqueId());
					LocationData newLocation = new LocationData(playerLocation);
					if(previousLocation.equals(newLocation)) {
						previousLocation.count++;
					} else {
						playerPositionMap.put(onlinePlayer.getUniqueId(), newLocation);
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 4L);
	}

	public static List<Player> getDisplayPlayers(Player mainPlayer, Location location) {
		return getDisplayPlayers(mainPlayer, location, 20);
	}

	public static List<Player> getDisplayPlayers(Player mainPlayer, Location location, double range) {
		List<Player> displayPlayers = Misc.getNearbyRealPlayers(location, range);
		if(!displayPlayers.contains(mainPlayer)) displayPlayers.add(mainPlayer);
		return displayPlayers;
	}

	public static void sendEnableMessage(PitPlayer pitPlayer, PitCosmetic pitCosmetic, ParticleColor particleColor) {
		if(pitCosmetic == null) return;
		if(pitCosmetic.isColorCosmetic) {
			AOutput.send(pitPlayer.player, "&e&lFANCY!&7 Enabled " + pitCosmetic.getDisplayName() + " &7(" + particleColor.displayName + "&7)");
		} else {
			AOutput.send(pitPlayer.player, "&e&lFANCY!&7 Enabled " + pitCosmetic.getDisplayName());
		}
	}

	public static void sendDisableMessage(PitPlayer pitPlayer, PitCosmetic pitCosmetic) {
		if(pitCosmetic == null) return;
		AOutput.send(pitPlayer.player, "&e&lFANCY!&7 Disabled " + pitCosmetic.getDisplayName());
	}

	public static boolean isStandingStill(Player player) {
		LocationData locationData = playerPositionMap.get(player.getUniqueId());
		if(locationData == null) return false;
		return locationData.count >= 2;
	}

	public static void loadForOnlinePlayers() {
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if(VanishAPI.isInvisible(onlinePlayer)) continue;
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
			List<PitCosmetic> activeCosmetics = CosmeticManager.getEquippedCosmetics(pitPlayer);
			for(PitCosmetic activeCosmetic : activeCosmetics) activeCosmetic.enable(pitPlayer);
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

	public static void unlockCosmetic(PitPlayer pitPlayer, PitCosmetic pitCosmetic, ParticleColor particleColor) {
		PitPlayer.UnlockedCosmeticData cosmeticData = pitPlayer.unlockedCosmeticsMap.get(pitCosmetic.refName);
		if(cosmeticData == null) cosmeticData = new PitPlayer.UnlockedCosmeticData();
		if(pitCosmetic.isColorCosmetic) {
			if(cosmeticData.unlockedColors == null) cosmeticData.unlockedColors = new ArrayList<>();
			cosmeticData.unlockedColors.add(particleColor);
		}
		pitPlayer.unlockedCosmeticsMap.put(pitCosmetic.refName, cosmeticData);
	}

	public static List<PitCosmetic> getUnlockedCosmetics(PitPlayer pitPlayer, CosmeticType cosmeticType) {
		List<PitCosmetic> cosmeticList = new ArrayList<>(CosmeticManager.cosmeticMap.get(cosmeticType));
		cosmeticList.removeIf(pitCosmetic -> !pitCosmetic.isUnlocked(pitPlayer));
		return cosmeticList;
	}

	public static PitCosmetic getEquippedCosmetic(PitPlayer pitPlayer, CosmeticType cosmeticType) {
		for(PitCosmetic pitCosmetic : cosmeticMap.get(cosmeticType))
			if(pitCosmetic.isEnabled(pitPlayer)) return pitCosmetic;
		return null;
	}

	public static List<PitCosmetic> getEquippedCosmetics(PitPlayer pitPlayer) {
		List<PitCosmetic> activeCosmetics = new ArrayList<>();
		loop:
		for(Map.Entry<CosmeticType, List<PitCosmetic>> entry : cosmeticMap.entrySet()) {
			for(PitCosmetic pitCosmetic : entry.getValue()) {
				if(!pitCosmetic.isEnabled(pitPlayer, pitCosmetic.getColor(pitPlayer))) continue;
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
		if(VanishAPI.isInvisible(player)) return;

		List<PitCosmetic> activeCosmetics = getEquippedCosmetics(pitPlayer);
		for(PitCosmetic activeCosmetic : activeCosmetics) activeCosmetic.enable(pitPlayer);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(VanishAPI.isInvisible(player)) return;

		List<PitCosmetic> activeCosmetics = getEquippedCosmetics(pitPlayer);
		for(PitCosmetic activeCosmetic : activeCosmetics) activeCosmetic.disable(pitPlayer);
	}

	@EventHandler
	public void onVanish(PlayerVanishStateChangeEvent event) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(Bukkit.getPlayer(event.getUUID()));
		List<PitCosmetic> activeCosmetics = getEquippedCosmetics(pitPlayer);
		if(event.isVanishing()) {
			for(PitCosmetic activeCosmetic : activeCosmetics) activeCosmetic.disable(pitPlayer);
		} else {
			for(PitCosmetic activeCosmetic : activeCosmetics) activeCosmetic.enable(pitPlayer);
		}
	}

	public static class LocationData {
		public int count = 0;
		public double lastX;
		public double lastY;
		public double lastZ;

		public LocationData(Location location) {
			this.lastX = location.getX();
			this.lastY = location.getY();
			this.lastZ = location.getZ();
		}

		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof LocationData)) return false;
			LocationData locationData = (LocationData) obj;
			return lastX == locationData.lastX && lastY == locationData.lastY && lastZ == locationData.lastZ;
		}

//		public boolean mostlyEquals(LocationData locationData) {
//			return Math.abs(lastX - locationData.lastX) < 0.5 && Math.abs(lastY - locationData.lastY) < 0.5 && Math.abs(lastZ - locationData.lastZ) < 0.5;
//		}
	}
}
