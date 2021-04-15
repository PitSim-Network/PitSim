package dev.kyro.pitremake.controllers;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.List;

public class DamageEvent {

	public EntityDamageByEntityEvent event;
	public Player attacker;
	public Player defender;
	public Arrow arrow;

	public double increase = 0;
	public double increasePercent = 0;
	public List<Double> multiplier = new ArrayList<>();
	public double decreasePercent = 0;
	public double decrease = 0;

	public double trueDamage = 0;
	public double selfTrueDamage = 0;

	public double executeUnder = 0;

	public DamageEvent(EntityDamageByEntityEvent event) {
		this.event = event;

		if(event.getDamager() instanceof Arrow) {
			this.arrow = (Arrow) event.getDamager();
			this.attacker = (Player) arrow.getShooter();
		} else {
			this.attacker = (Player) event.getDamager();
		}
		this.defender = (Player) event.getEntity();
	}
}
