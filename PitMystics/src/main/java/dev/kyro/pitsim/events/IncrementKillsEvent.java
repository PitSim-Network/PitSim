package dev.kyro.pitsim.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class IncrementKillsEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	public Player player;
	public int kills;

	public IncrementKillsEvent(Player player, int kills) {
		this.player = player;
		this.kills = kills;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
