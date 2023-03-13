package dev.kyro.pitsim.events;

import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttackEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	private final WrapperEntityDamageEvent event;
	private final Entity realDamager;
	private final LivingEntity attacker;
	private final LivingEntity defender;
	private final boolean isAttackerPlayer;
	private final boolean isDefenderPlayer;
	private final boolean isAttackerRealPlayer;
	private final boolean isDefenderRealPlayer;
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

	public AttackEvent(WrapperEntityDamageEvent event, Map<PitEnchant, Integer> attackerEnchantMap, Map<PitEnchant, Integer> defenderEnchantMap, boolean fakeHit) {
		this(event, event.getDamager(), attackerEnchantMap, defenderEnchantMap, fakeHit);
	}

	public AttackEvent(WrapperEntityDamageEvent event, Entity realDamager,
					   Map<PitEnchant, Integer> attackerEnchantMap, Map<PitEnchant, Integer> defenderEnchantMap, boolean fakeHit) {
		this.event = event;
		this.realDamager = realDamager;
		this.attacker = DamageManager.getAttacker(event.getDamager());
		this.defender = event.getEntity();
		this.isAttackerPlayer = attacker instanceof Player;
		this.isDefenderPlayer = defender instanceof Player;
		this.attackerPlayer = isAttackerPlayer() ? (Player) getAttacker() : null;
		this.defenderPlayer = isDefenderPlayer() ? (Player) getDefender() : null;
		this.isAttackerRealPlayer = PlayerManager.isRealPlayer(getAttackerPlayer());
		this.isDefenderRealPlayer = PlayerManager.isRealPlayer((getDefenderPlayer()));
		this.attackerEnchantMap = attackerEnchantMap;
		this.defenderEnchantMap = defenderEnchantMap;
		this.fakeHit = fakeHit;

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

	public WrapperEntityDamageEvent getWrapperEvent() {
		return event;
	}

	public Entity getRealDamager() {
		return realDamager;
	}

	public LivingEntity getAttacker() {
		return attacker;
	}

	public LivingEntity getDefender() {
		return defender;
	}

	public boolean isAttackerPlayer() {
		return isAttackerPlayer;
	}

	public boolean isDefenderPlayer() {
		return isDefenderPlayer;
	}

	public boolean isAttackerRealPlayer() {
		return isAttackerRealPlayer;
	}

	public boolean isDefenderRealPlayer() {
		return isDefenderRealPlayer;
	}

	public boolean hasAttacker() {
		return attacker != null;
	}

	public Player getAttackerPlayer() {
		return attackerPlayer;
	}

	public Player getDefenderPlayer() {
		return defenderPlayer;
	}

	public PitPlayer getAttackerPitPlayer() {
		if(attackerPitPlayer == null && isAttackerPlayer) attackerPitPlayer = PitPlayer.getPitPlayer(attackerPlayer);
		return attackerPitPlayer;
	}

	public PitPlayer getDefenderPitPlayer() {
		if(defenderPitPlayer == null && isDefenderPlayer) defenderPitPlayer = PitPlayer.getPitPlayer(defenderPlayer);
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

	public boolean hasFakeAttacker() {
		return event.getAttackInfo() != null && event.getAttackInfo().getFakeAttacker() != null;
	}

	public static class Pre extends AttackEvent implements Cancellable {
		private boolean cancel = false;

		public Pre(WrapperEntityDamageEvent event, Entity realDamager,
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
			super(event.getWrapperEvent(), event.realDamager, event.attackerEnchantMap, event.defenderEnchantMap, event.isFakeHit());
		}

		public double getFinalPitDamage() {
			double damage = getWrapperEvent().getDamage();
			damage += increase;
			damage *= 1 + (increasePercent / 100.0);
			for(double multiplier : multipliers) damage *= multiplier;
			for(double multiplier : increaseCalcDecrease) damage *= multiplier;
			damage *= 1 - decreasePercent;
			return Math.max(damage, 0);
		}

		public double getFinalPitDamageIncrease() {
			double damage = getWrapperEvent().getDamage();
			damage += increase;
			damage *= 1 + (increasePercent / 100.0);
			for(double multiplier : multipliers) {
				if(multiplier < 1) continue;
				damage *= multiplier;
			}
			for(double multiplier : increaseCalcDecrease) damage *= multiplier;
			return Math.max(damage, 0);
		}
	}

	public static class Post extends AttackEvent {
		private final AttackEvent.Apply applyEvent;
		private final double finalDamage;

		public Post(AttackEvent.Apply event, double finalDamage) {
			super(event.getWrapperEvent(), event.getRealDamager(), event.getAttackerEnchantMap(), event.getDefenderEnchantMap(), event.isFakeHit());
			this.applyEvent = event;
			this.finalDamage = finalDamage;
		}

		public Apply getApplyEvent() {
			return applyEvent;
		}

		public double getFinalDamage() {
			return finalDamage;
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

	public void setCancelled(boolean cancelled) {
		getWrapperEvent().setCancelled(cancelled);
	}

	public boolean isCancelled() {
		return getWrapperEvent().isCancelled();
	}
}
