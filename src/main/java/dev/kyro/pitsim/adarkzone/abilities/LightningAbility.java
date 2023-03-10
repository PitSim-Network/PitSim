package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.PitBossAbility;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class LightningAbility extends PitBossAbility {
	public int strikes;
	public double damage;
	public double chance;

	public LightningAbility(int strikes, double damage, double chance) {
		this.strikes = strikes;
		this.damage = damage;
		this.chance = chance;
	}

	@EventHandler
	public void onHit(AttackEvent.Apply event) {
		if(event.getAttackerPlayer() != getPitBoss().boss) return;
		if(!event.isDefenderPlayer()) return;

		Random random = new Random();
		if(random.nextInt(100) > chance * 100) return;

		Player player = event.getDefenderPlayer();
		Sounds.JUDGEMENT_ZEUS_DEFENDER.play(player);
		new BukkitRunnable() {
			int count = 0;

			@Override
			public void run() {
				if(++count == strikes) cancel();
				Misc.strikeLightningForPlayers(player.getLocation(), 40);
				player.setHealth(Math.max(player.getHealth() - damage, 1));
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 2L);
	}

}
