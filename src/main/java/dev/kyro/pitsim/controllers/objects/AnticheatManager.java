package dev.kyro.pitsim.controllers.objects;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class AnticheatManager implements Listener {
	public abstract void exemptPlayer(Player player, long ms, String... args);
}
