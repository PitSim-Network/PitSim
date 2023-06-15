package net.pitsim.spigot.events;

import net.pitsim.spigot.controllers.objects.PitPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class IncrementKillsEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();

	private PitPlayer pitPlayer;
	private int kills;

	public IncrementKillsEvent(Player player, int kills) {
		super(player);
		this.pitPlayer = PitPlayer.getPitPlayer(player);
		this.kills = kills;
	}

	public PitPlayer getPitPlayer() {
		return pitPlayer;
	}

	public int getKills() {
		return kills;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}