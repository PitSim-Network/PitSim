package dev.kyro.pitsim.controllers;

import org.bukkit.event.EventHandler;
import dev.kyro.pitsim.enchants.BulletTime;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;

import java.util.UUID;

public class MiscManager implements Listener {

	@EventHandler
	public void onIgnite(BlockIgniteEvent event) {
		BlockIgniteEvent.IgniteCause cause = event.getCause();
		if(cause == BlockIgniteEvent.IgniteCause.FIREBALL || cause == BlockIgniteEvent.IgniteCause.EXPLOSION)
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onAttack(AttackEvent.Pre attackEvent) {
		if(!PlayerManager.isRealPlayer(attackEvent.getAttackerPlayer()) || !PlayerManager.isRealPlayer(attackEvent.getDefenderPlayer())) return;
		Player attacker = attackEvent.getAttackerPlayer();
		Player defender = attackEvent.getDefenderPlayer();
//		if(!Misc.isKyro(defender.getUniqueId()) || !attacker.getUniqueId().equals(UUID.fromString("ee660496-3cf1-458a-94fb-e11764c18663"))) return;
		if(!defender.getUniqueId().equals(UUID.fromString("ee660496-3cf1-458a-94fb-e11764c18663"))) return;
		attackEvent.getDefenderEnchantMap().remove(BulletTime.INSTANCE);

//		attackEvent.selfVeryTrueDamage += 100;
//		AOutput.send(attacker, "&9&lCOPE!");
	}
}
