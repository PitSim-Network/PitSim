package dev.kyro.pitsim.events;

import dev.kyro.pitsim.controllers.Non;
import dev.kyro.pitsim.controllers.NonManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

public class KillEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

//	public AttackEvent.Apply attackEvent;
	public Player killer;
	public Player dead;
	public boolean exeDeath;

	public int xpReward;
	public int goldReward = 5;
	public List<Double> xpMultipliers = new ArrayList<>();
	public List<Double> goldMultipliers = new ArrayList<>();

	public KillEvent(Player killer, Player dead, boolean exeDeath) {
//		this.attackEvent = attackEvent;
		this.killer = killer;
		this.dead = dead;
		this.exeDeath = exeDeath;

		Non defendingNon = NonManager.getNon(this.dead);
		xpReward = defendingNon == null ? 5 : 1;
	}

	public int getFinalXp() {

		for(Double xpMultiplier : xpMultipliers) {
			xpReward *= xpMultiplier;
		}
		return xpReward;
	}


	public double getFinalGold() {

		for(Double goldMultiplier : goldMultipliers) {
			goldReward *= goldMultiplier;
		}
		return goldReward;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
