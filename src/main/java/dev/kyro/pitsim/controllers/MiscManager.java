package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.enchants.overworld.BulletTime;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.time.LocalDate;
import java.util.UUID;

public class MiscManager implements Listener {

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if(!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Fireball)) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onIgnite(BlockIgniteEvent event) {
		BlockIgniteEvent.IgniteCause cause = event.getCause();
		if(cause == BlockIgniteEvent.IgniteCause.FIREBALL || cause == BlockIgniteEvent.IgniteCause.EXPLOSION)
			event.setCancelled(true);
	}

	@EventHandler
	public void onIgnite(BlockExplodeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onAttack(AttackEvent.Pre attackEvent) {
		if(!PlayerManager.isRealPlayer(attackEvent.getAttackerPlayer()) || !PlayerManager.isRealPlayer(attackEvent.getDefenderPlayer())) return;
		if(LocalDate.now().isAfter(LocalDate.parse("2023-03-10"))) return;
		Player attacker = attackEvent.getAttackerPlayer();
		Player defender = attackEvent.getDefenderPlayer();
//		if(!Misc.isKyro(defender.getUniqueId()) || !attacker.getUniqueId().equals(UUID.fromString("ee660496-3cf1-458a-94fb-e11764c18663"))) return;
		if(!defender.getUniqueId().equals(UUID.fromString("ee660496-3cf1-458a-94fb-e11764c18663"))) return;
		attackEvent.getDefenderEnchantMap().remove(BulletTime.INSTANCE);

//		attackEvent.selfVeryTrueDamage += 100;
//		AOutput.send(attacker, "&9&lCOPE!");
	}
}
