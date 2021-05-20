package dev.kyro.pitsim.events;

import dev.kyro.pitsim.nons.Non;
import dev.kyro.pitsim.nons.NonManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

public class KillEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	public AttackEvent.Apply attackEvent;
	public Player attacker;
	public Player defender;
	public boolean exeDeath;

	public int xpReward;
	public int goldReward = 5;
	public List<Double> xpMultipliers = new ArrayList<>();
	public List<Double> goldMultipliers = new ArrayList<>();

	public KillEvent(AttackEvent.Apply attackEvent, boolean exeDeath) {
		this.attackEvent = attackEvent;
		this.attacker = attackEvent.attacker;
		this.defender = attackEvent.defender;
		this.exeDeath = exeDeath;

		Non defendingNon = NonManager.getNon(defender);
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
