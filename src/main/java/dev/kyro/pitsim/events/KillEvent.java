package dev.kyro.pitsim.events;

import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.Non;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KillEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	private EntityDamageByEntityEvent event;
	private AttackEvent attackEvent;

	private final LivingEntity killer;
	private final LivingEntity dead;
	private final boolean killerIsPlayer;
	private final boolean deadIsPlayer;
	private final Player killerPlayer;
	private final Player deadPlayer;
	private PitPlayer killerPitPlayer;
	private PitPlayer deadPitPlayer;
	private final Map<PitEnchant, Integer> killerEnchantMap;
	private final Map<PitEnchant, Integer> deadEnchantMap;
	private final boolean exeDeath;

	public int xpReward;
	public int bonusXpReward;
	public int xpCap = 50;
	public double goldReward = 20;
	public List<Double> xpMultipliers = new ArrayList<>();
	public List<Double> maxXPMultipliers = new ArrayList<>();
	public List<Double> goldMultipliers = new ArrayList<>();

	public boolean isLuckyKill = false;

	public KillEvent(AttackEvent attackEvent, LivingEntity killer, LivingEntity dead, boolean exeDeath) {
		this.attackEvent = attackEvent;
		this.event = attackEvent.getEvent();
		this.killerEnchantMap = killer == attackEvent.getAttacker() ? attackEvent.getAttackerEnchantMap() : attackEvent.getDefenderEnchantMap();
		this.deadEnchantMap = killer == attackEvent.getAttacker() ? attackEvent.getDefenderEnchantMap() : attackEvent.getAttackerEnchantMap();
		this.killer = killer;
		this.dead = dead;
		this.killerIsPlayer = killer instanceof Player;
		this.deadIsPlayer = dead instanceof Player;
		this.killerPlayer = isKillerPlayer() ? (Player) killer : null;
		this.deadPlayer = isDeadPlayer() ? (Player) dead : null;
		this.exeDeath = exeDeath;

		Non defendingNon = NonManager.getNon(this.getDead());
		xpReward = defendingNon == null ? 5 : 20;
	}

	public int getFinalXp() {

		double xpReward = this.xpReward;
		int xpCap = this.xpCap;
		for(Double xpMultiplier : xpMultipliers) {
			xpReward *= xpMultiplier;
		}
		for(Double maxXPMultiplier : maxXPMultipliers) {
			xpCap *= maxXPMultiplier;
		}
		xpReward += bonusXpReward;

		if(!(getDead() instanceof Player)) return 0;
		else if(xpReward > xpCap) return xpCap;
		else return (int) xpReward;
	}

	public double getFinalGold() {
		double goldReward = this.goldReward;
		for(Double goldMultiplier : goldMultipliers) {
			goldReward *= goldMultiplier;
		}
		if(!(getDead() instanceof Player)) return 0;
		else return Math.min(goldReward, 2000);
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

	public LivingEntity getKiller() {
		return killer;
	}

	public LivingEntity getDead() {
		return dead;
	}

	public boolean isKillerPlayer() {
		return killerIsPlayer;
	}

	public boolean isDeadPlayer() {
		return deadIsPlayer;
	}

	public Player getKillerPlayer() {
		return killerPlayer;
	}

	public Player getDeadPlayer() {
		return deadPlayer;
	}

	public PitPlayer getKillerPitPlayer() {
		if(killerPitPlayer == null && killerIsPlayer) killerPitPlayer = PitPlayer.getPitPlayer(killerPlayer);
		return killerPitPlayer;
	}

	public PitPlayer getDeadPitPlayer() {
		if(deadPitPlayer == null && deadIsPlayer) deadPitPlayer = PitPlayer.getPitPlayer(deadPlayer);
		return deadPitPlayer;
	}

	public boolean isExeDeath() {
		return exeDeath;
	}

	public EntityDamageByEntityEvent getEvent() {
		return event;
	}

	public AttackEvent getAttackEvent() {
		return attackEvent;
	}
}
