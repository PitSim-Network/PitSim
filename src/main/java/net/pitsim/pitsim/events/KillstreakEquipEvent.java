package net.pitsim.pitsim.events;

import net.pitsim.pitsim.controllers.objects.Killstreak;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KillstreakEquipEvent extends Event implements Cancellable {
	private static final HandlerList HANDLERS_LIST = new HandlerList();
	private boolean isCancelled;
	private Killstreak killstreak;
	private Player player;
	private Killstreak replacedKillstreak;

	public KillstreakEquipEvent(Killstreak killstreak, Player player, Killstreak replacedKillstreak) {
		this.killstreak = killstreak;
		this.player = player;
		this.replacedKillstreak = replacedKillstreak;
		this.isCancelled = false;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.isCancelled = cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}

	public Killstreak getKillstreak() {
		return killstreak;
	}

	public Player getPlayer() {
		return player;
	}

	public Killstreak getReplacedKillstreak() {
		return replacedKillstreak;
	}

}
