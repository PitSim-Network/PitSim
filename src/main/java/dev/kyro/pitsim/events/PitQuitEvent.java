package dev.kyro.pitsim.events;

import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.storage.StorageProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerQuitEvent;

public class PitQuitEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	private final PlayerQuitEvent event;
	private final Player player;
	private final PitPlayer pitPlayer;
	private final StorageProfile profile;


	public PitQuitEvent(PlayerQuitEvent event, Player player, PitPlayer pitPlayer, StorageProfile profile) {
		this.event = event;
		this.player = player;
		this.pitPlayer = pitPlayer;
		this.profile = profile;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public PlayerQuitEvent getEvent() {
		return event;
	}

	public Player getPlayer() {
		return player;
	}

	public PitPlayer getPitPlayer() {
		return pitPlayer;
	}

	public StorageProfile getProfile() {
		return profile;
	}
}
