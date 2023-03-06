package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.enchants.BulletTime;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;

public class MiscManager implements Listener {

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
