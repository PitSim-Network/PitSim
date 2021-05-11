package dev.kyro.pitremake.controllers;

import dev.kyro.pitremake.enchants.PitBlob;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DamageEvent {

	public EntityDamageByEntityEvent event;
	public Player attacker;
	public Player defender;
	public Arrow arrow;
	public Slime slime;
	public Boolean hitByArrow;
	private final Map<PitEnchant, Integer> attackerEnchantMap;

	public double increase = 0;
	public double increasePercent = 0;
	public List<Double> multiplier = new ArrayList<>();
	public double decreasePercent = 0;
	public double decrease = 0;

	public double trueDamage = 0;
	public double selfTrueDamage = 0;

	public double executeUnder = 0;

	public DamageEvent(EntityDamageByEntityEvent event, Map<PitEnchant, Integer> attackerEnchantMap) {
		this.event = event;
		this.attackerEnchantMap = attackerEnchantMap;

		if(event.getDamager() instanceof Player) {
			this.hitByArrow = false;
			this.attacker = (Player) event.getDamager();
		} else if(event.getDamager() instanceof Arrow) {
			this.hitByArrow = true;
			this.arrow = (Arrow) event.getDamager();
			this.attacker = (Player) arrow.getShooter();
		} else if(event.getDamager() instanceof Slime) {
			this.slime = (Slime) event.getDamager();
			this.attacker = PitBlob.getOwner((Slime) event.getDamager());
		}
		this.defender = (Player) event.getEntity();
	}

	public int getEnchantLevel(PitEnchant pitEnchant) {

		return attackerEnchantMap.getOrDefault(pitEnchant, 0);
	}

	public double getFinalDamageIncrease() {

		double damage = event.getDamage();
		damage += increase;
		damage *= 1 + increasePercent;
		for(double multiplier : multiplier) {
			if(multiplier < 1) continue;
			damage *= multiplier;
		}
		return Math.max(damage, 0);
	}

	public double getFinalDamage() {

		double damage = event.getDamage();
		damage += increase;
		damage *= 1 + increasePercent;
		for(double multiplier : multiplier) {
			damage *= multiplier;
		}
		damage *= 1 - decreasePercent;
		damage -= decrease;
		return Math.max(damage, 0);
	}
}
