package dev.kyro.pitsim.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

public class ManaRegenEvent extends Event implements Cancellable {
	private static final HandlerList HANDLERS_LIST = new HandlerList();
	private boolean isCancelled;
	private Player player;
	public double baseMana;
	public List<Double> multipliers = new ArrayList<>();

	public ManaRegenEvent(Player player, double baseMana) {
		this.isCancelled = false;
		this.player = player;
		this.baseMana = baseMana;
	}

	public double getFinalMana() {
		double finalMana = baseMana;
		for(Double multiplier : multipliers) finalMana *= multiplier;
		return finalMana;
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

	public Player getPlayer() {
		return player;
	}

}
