package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.PitBossAbility;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

public class LightningAbility extends PitBossAbility {
	public int strikes;
	public double damage;
	public double chance;

	public long lastUse;

	public LightningAbility(int strikes, double damage, double chance) {
		this.strikes = strikes;
		this.damage = damage;
		this.chance = chance;
	}

	@EventHandler
	public void onHit(AttackEvent.Apply attackEvent) {
		if(attackEvent.getAttackerPlayer() != getPitBoss().getBoss() || !attackEvent.isDefenderPlayer()) return;
		if(attackEvent.getWrapperEvent().hasAttackInfo()) return;
		if(Math.random() > chance) return;

		if(lastUse + 2L * strikes > PitSim.currentTick) return;
		lastUse = PitSim.currentTick;

		Player player = attackEvent.getDefenderPlayer();
		Sounds.JUDGEMENT_ZEUS_DEFENDER.play(player);
		new BukkitRunnable() {
			int count = 0;

			@Override
			public void run() {
				if(!isEnabled() || !isNearToBoss(player)) return;
				if(++count == strikes) cancel();
				Misc.strikeLightningForPlayers(player.getLocation(), 40);
				player.setNoDamageTicks(0);
				DamageManager.createDirectAttack(getPitBoss().getBoss(), player, 0, newEvent -> newEvent.veryTrueDamage = damage);
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 2L);
	}

}
