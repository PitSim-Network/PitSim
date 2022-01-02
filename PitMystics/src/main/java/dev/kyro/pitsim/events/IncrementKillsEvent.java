package dev.kyro.pitsim.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class IncrementKillsEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	public Player player;
	public double currentAmount;
	public double amountChanged;
	public double newAmount;

	public IncrementKillsEvent(Player player, double currentAmount, double amountChanged) {
		this.player = player;
		this.currentAmount = currentAmount;
		this.amountChanged = amountChanged;
		this.newAmount = currentAmount + amountChanged;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
