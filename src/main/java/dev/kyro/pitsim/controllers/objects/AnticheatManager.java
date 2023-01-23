package dev.kyro.pitsim.controllers.objects;

import org.bukkit.entity.Player;

public abstract class AnticheatManager {
	public abstract void exemptPlayer(Player player, long ticks, String... args);
}
