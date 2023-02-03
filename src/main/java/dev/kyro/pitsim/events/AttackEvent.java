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

	private final EntityDamageByEntityEvent event;
	private final Entity realDamager;
	private final LivingEntity attacker;
	private final LivingEntity defender;
	private final boolean attackerIsPlayer;
	private final boolean defenderIsPlayer;
	private final Player attackerPlayer;
	private final Player defenderPlayer;
	private PitPlayer attackerPitPlayer;
	private PitPlayer defenderPitPlayer;
	private Arrow arrow;
	private Fireball fireball;
	private LivingEntity pet;
	private final Map<PitEnchant, Integer> attackerEnchantMap;
	private final Map<PitEnchant, Integer> defenderEnchantMap;

	private final boolean fakeHit;

	public AttackEvent(EntityDamageByEntityEvent event, Map<PitEnchant, Integer> attackerEnchantMap, Map<PitEnchant, Integer> defenderEnchantMap, boolean fakeHit) {
		this(event, event.getDamager(), attackerEnchantMap, defenderEnchantMap, fakeHit);
	}

	public AttackEvent(EntityDamageByEntityEvent event, Entity realDamager,
					   Map<PitEnchant, Integer> attackerEnchantMap, Map<PitEnchant, Integer> defenderEnchantMap, boolean fakeHit) {
		this.event = event;
		this.realDamager = realDamager;
		this.attacker = DamageManager.getAttacker(event.getDamager());
		this.defender = (LivingEntity) event.getEntity();
		this.attackerIsPlayer = getAttacker() instanceof Player;
		this.defenderIsPlayer = getDefender() instanceof Player;
		this.attackerPlayer = isAttackerPlayer() ? (Player) getAttacker() : null;
		this.defenderPlayer = isDefenderPlayer() ? (Player) getDefender() : null;
		this.attackerEnchantMap = attackerEnchantMap;
		this.defenderEnchantMap = defenderEnchantMap;
		this.fakeHit = fakeHit;

		if(isDefenderPlayer()) {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(getDefenderPlayer());
			if(!(pitPlayer.player == getAttacker())) pitPlayer.lastHitUUID = getAttacker().getUniqueId();
		}

		if(realDamager instanceof Arrow) {
			this.arrow = (Arrow) realDamager;
		} else if(realDamager instanceof Fireball) {
			this.fireball = (Fireball) realDamager;
		} else if(realDamager instanceof Slime) {
			this.pet = (LivingEntity) realDamager;
		}
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public EntityDamageByEntityEvent getEvent() {
		return event;
	}

	public LivingEntity getAttacker() {
		return attacker;
	}

	public LivingEntity getDefender() {
		return defender;
	}

	public boolean isAttackerPlayer() {
		return attackerIsPlayer;
	}

	public boolean isDefenderPlayer() {
		return defenderIsPlayer;
	}

	public Player getAttackerPlayer() {
		return attackerPlayer;
	}

	public Player getDefenderPlayer() {
		return defenderPlayer;
	}

	public PitPlayer getAttackerPitPlayer() {
		if(attackerPitPlayer == null && attackerIsPlayer) attackerPitPlayer = PitPlayer.getPitPlayer(attackerPlayer);
		return attackerPitPlayer;
	}

	public PitPlayer getDefenderPitPlayer() {
		if(defenderPitPlayer == null && defenderIsPlayer) defenderPitPlayer = PitPlayer.getPitPlayer(defenderPlayer);
		return defenderPitPlayer;
	}

	public Arrow getArrow() {
		return arrow;
	}

	public Fireball getFireball() {
		return fireball;
	}

	public LivingEntity getPet() {
		return pet;
	}

	public boolean isFakeHit() {
		return fakeHit;
	}

	public static class Pre extends AttackEvent implements Cancellable {
		private boolean cancel = false;

		public Pre(EntityDamageByEntityEvent event, Entity realDamager,
				   Map<PitEnchant, Integer> attackerEnchantMap, Map<PitEnchant, Integer> defenderEnchantMap, boolean fakeHit) {
			super(event, realDamager, attackerEnchantMap, defenderEnchantMap, fakeHit);
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
			super(event.getEvent(), event.realDamager, event.attackerEnchantMap, event.defenderEnchantMap, event.isFakeHit());
		}

		public double getFinalDamage() {

			double damage = getEvent().getDamage();
			damage += increase;
			damage *= 1 + increasePercent;
			for(double multiplier : multipliers) damage *= multiplier;
			for(double multiplier : increaseCalcDecrease) damage *= multiplier;
			damage *= 1 - decreasePercent;
//			damage -= decrease;
			return Math.max(damage, 0);
		}

		public double getFinalDamageIncrease() {

			double damage = getEvent().getDamage();
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
			super(event.getEvent(), event.realDamager, event.attackerEnchantMap, event.defenderEnchantMap, event.isFakeHit());

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
