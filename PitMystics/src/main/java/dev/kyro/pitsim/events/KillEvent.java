package dev.kyro.pitsim.events;

import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.Non;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KillEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

//	public AttackEvent.Apply attackEvent;
	public Player killer;
	public Player dead;
	private final Map<PitEnchant, Integer> killerEnchantMap;
	private final Map<PitEnchant, Integer> deadEnchantMap;

	public boolean exeDeath;
	public int xpReward;
	public int xpCap = 50;
	public double goldReward = 20;
	public List<Double> xpMultipliers = new ArrayList<>();
	public List<Double> goldMultipliers = new ArrayList<>();

	public boolean isLuckyKill = false;
	public int playerKillWorth = 1;

	public KillEvent(AttackEvent attackEvent, Player killer, Player dead, boolean exeDeath) {
		this.killerEnchantMap = killer == attackEvent.attacker ? attackEvent.getAttackerEnchantMap() : attackEvent.getDefenderEnchantMap();
		this.deadEnchantMap = killer == attackEvent.attacker ? attackEvent.getDefenderEnchantMap() : attackEvent.getAttackerEnchantMap();
		this.killer = killer;
		this.dead = dead;
		this.exeDeath = exeDeath;

		Non defendingNon = NonManager.getNon(this.dead);
		xpReward = defendingNon == null ? 5 : 20;
	}

	public int getFinalXp() {

		double xpReward = this.xpReward;
		for(Double xpMultiplier : xpMultipliers) {
			xpReward *= xpMultiplier;
		}
		if(xpReward > xpCap) return xpCap;
		else return (int) xpReward;
	}

	public double getFinalGold() {
		double goldReward = this.goldReward;
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

	public int getKillerEnchantLevel(PitEnchant pitEnchant) {

		return killerEnchantMap.getOrDefault(pitEnchant, 0);
	}

	public int getDeadEnchantLevel(PitEnchant pitEnchant) {

		return deadEnchantMap.getOrDefault(pitEnchant, 0);
	}

	public Map<PitEnchant, Integer> getKillerEnchantMap() {
		return killerEnchantMap;
	}

	public Map<PitEnchant, Integer> getDeadEnchantMap() {
		return deadEnchantMap;
	}
}
