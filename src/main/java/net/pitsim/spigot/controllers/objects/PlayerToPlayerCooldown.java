package net.pitsim.spigot.controllers.objects;

import net.pitsim.spigot.PitSim;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerToPlayerCooldown {
	private final long cooldownTicks;
	private final Map<UUID, Map<UUID, Long>> cooldownMap = new HashMap<>();

	public PlayerToPlayerCooldown(long cooldownTicks) {
		this.cooldownTicks = cooldownTicks;
	}

	public boolean isOnCooldown(Player mainPlayer, Player otherPlayer) {
		cooldownMap.putIfAbsent(mainPlayer.getUniqueId(), new HashMap<>());
		Map<UUID, Long> playerCooldownMap = cooldownMap.get(mainPlayer.getUniqueId());
		Long cooldown = playerCooldownMap.getOrDefault(otherPlayer.getUniqueId(), 0L);
		if(cooldown + cooldownTicks >= PitSim.currentTick) return true;
		playerCooldownMap.put(otherPlayer.getUniqueId(), PitSim.currentTick);
		return false;
	}
}
