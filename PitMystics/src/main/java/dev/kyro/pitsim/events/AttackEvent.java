package dev.kyro.pitsim.events;

import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttackEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	public EntityDamageByEntityEvent event;
	public LivingEntity attacker;
	public LivingEntity defender;
	public boolean attackerIsPlayer;
	public boolean defenderIsPlayer;
	public Player attackerPlayer;
	public Player defenderPlayer;
	public PitPlayer attackerPitPlayer;
	public PitPlayer defenderPitPlayer;
	public Arrow arrow;
	public Fireball fireball;
	public LivingEntity pet;
	private final Map<PitEnchant, Integer> attackerEnchantMap;
	private final Map<PitEnchant, Integer> defenderEnchantMap;
	public boolean clearMaps;

	public boolean fakeHit;

	public AttackEvent(EntityDamageByEntityEvent event, Map<PitEnchant, Integer> attackerEnchantMap, Map<PitEnchant, Integer> defenderEnchantMap, boolean fakeHit) {
		this.event = event;
		this.attacker = DamageManager.getAttacker(event.getDamager());
		this.defender = (LivingEntity) event.getEntity();
		this.attackerIsPlayer = attacker instanceof Player;
		this.defenderIsPlayer = defender instanceof Player;
		this.attackerPlayer = attackerIsPlayer ? (Player) attacker : null;
		this.defenderPlayer = defenderIsPlayer ? (Player) defender : null;
		this.attackerPitPlayer = attackerIsPlayer ? PitPlayer.getPitPlayer(attackerPlayer) : null;
		this.defenderPitPlayer = defenderIsPlayer ? PitPlayer.getPitPlayer(defenderPlayer) : null;
		this.attackerEnchantMap = attackerEnchantMap;
		this.defenderEnchantMap = defenderEnchantMap;
		this.fakeHit = fakeHit;
		this.clearMaps = false;

		if(defenderIsPlayer) {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(defenderPlayer);
			if(!(pitPlayer.player == attacker)) pitPlayer.lastHitUUID = attacker.getUniqueId();
		}

		if(clearMaps) {
			defenderEnchantMap.clear();
			attackerEnchantMap.clear();
		}

		if(event.getDamager() instanceof Arrow) {
			this.arrow = (Arrow) event.getDamager();
		} else if(event.getDamager() instanceof Fireball) {
			this.fireball = (Fireball) event.getDamager();
		} else if(event.getDamager() instanceof Slime) {
			this.pet = (LivingEntity) event.getDamager();
		}
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public static class Pre extends AttackEvent implements Cancellable {
		private boolean cancel = false;

		public Pre(EntityDamageByEntityEvent event, Map<PitEnchant, Integer> attackerEnchantMap, Map<PitEnchant, Integer> defenderEnchantMap, boolean fakeHit) {
			super(event, attackerEnchantMap, defenderEnchantMap, fakeHit);
		}

		@Override
		public boolean isCancelled() {
			return cancel;
		}

		@Override
		public void setCancelled(boolean cancel) {
			this.cancel = cancel;
		}
	}

	public static class Apply extends AttackEvent {

		public double increase = 0;
		public double increasePercent = 0;
		public List<Double> multipliers = new ArrayList<>();
		public List<Double> increaseCalcDecrease = new ArrayList<>();
		public double decreasePercent = 0;
		public double trueDamage = 0;
		public double veryTrueDamage = 0;
		public double selfTrueDamage = 0;
		public double selfVeryTrueDamage = 0;
		public double executeUnder = 0;

		public Apply(AttackEvent event) {
			super(event.event, event.attackerEnchantMap, event.defenderEnchantMap, event.fakeHit);
		}

		public double getFinalDamage() {

			double damage = event.getDamage();
			damage += increase;
			damage *= 1 + increasePercent;
			for(double multiplier : multipliers) damage *= multiplier;
			for(double multiplier : increaseCalcDecrease) damage *= multiplier;
			damage *= 1 - decreasePercent;
//			damage -= decrease;
			return Math.max(damage, 0);
		}

		public double getFinalDamageIncrease() {

			double damage = event.getDamage();
			damage += increase;
			damage *= 1 + increasePercent;
			for(double multiplier : multipliers) {
				if(multiplier < 1) continue;
				damage *= multiplier;
			}
			for(double multiplier : increaseCalcDecrease) damage *= multiplier;
			return Math.max(damage, 0);
		}
	}

	public static class Post extends AttackEvent {

		public Post(AttackEvent event) {
			super(event.event, event.attackerEnchantMap, event.defenderEnchantMap, event.fakeHit);

		}
	}

	public int getAttackerEnchantLevel(PitEnchant pitEnchant) {

		return attackerEnchantMap.getOrDefault(pitEnchant, 0);
	}

	public int getDefenderEnchantLevel(PitEnchant pitEnchant) {

		return defenderEnchantMap.getOrDefault(pitEnchant, 0);
	}

	public Map<PitEnchant, Integer> getAttackerEnchantMap() {
		return attackerEnchantMap;
	}

	public Map<PitEnchant, Integer> getDefenderEnchantMap() {
		return defenderEnchantMap;
	}
}
