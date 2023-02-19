package dev.kyro.pitsim.controllers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;

public class MiscManager implements Listener {

//	@EventHandler
//	public void onAttack(AttackEvent.Apply attackEvent) {
//		if(!PlayerManager.isRealPlayer(attackEvent.getAttackerPlayer()) || !PlayerManager.isRealPlayer(attackEvent.getDefenderPlayer())) return;
//		Player attacker = attackEvent.getAttackerPlayer();
//		Player defender = attackEvent.getDefenderPlayer();
//		if(!Misc.isKyro(defender.getUniqueId()) || !attacker.getUniqueId().equals(UUID.fromString("ee660496-3cf1-458a-94fb-e11764c18663"))) return;
//
//		attackEvent.selfVeryTrueDamage += 100;
//		AOutput.send(attacker, "&9&lCOPE!");
//	}

	@EventHandler
	public void onIgnite(BlockIgniteEvent event) {
		BlockIgniteEvent.IgniteCause cause = event.getCause();
		if(cause == BlockIgniteEvent.IgniteCause.FIREBALL || cause == BlockIgniteEvent.IgniteCause.EXPLOSION)
			event.setCancelled(true);
	}
}
