package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.PitBossAbility;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ComboAbility extends PitBossAbility {
	public Map<UUID, Integer> comboMap = new HashMap<>();
	public int comboThreshold;
	public int comboDuration;
	public double damage;

	public ComboAbility(int comboThreshold, int comboDuration, double damage) {
		this.comboThreshold = comboThreshold;
		this.comboDuration = comboDuration;
		this.damage = damage;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(attackEvent.getAttackerPlayer() != getPitBoss().getBoss() || !attackEvent.isDefenderPlayer()) return;
		if(attackEvent.getWrapperEvent().hasAttackInfo()) return;

		Player player = attackEvent.getDefenderPlayer();
		comboMap.put(player.getUniqueId(), comboMap.getOrDefault(player.getUniqueId(), 0) + 1);

		if(comboMap.get(player.getUniqueId()) != comboThreshold) return;
		new BukkitRunnable() {
			int count = 0;

			@Override
			public void run() {
				if(!isEnabled() || !isNearToBoss(player)) return;

				if(count >= comboDuration) {
					comboMap.remove(player.getUniqueId());
					cancel();
					return;
				}

				player.setNoDamageTicks(0);
				DamageManager.createDirectAttack(getPitBoss().getBoss(), player, damage);

				count++;
			}
		}.runTaskTimer(PitSim.INSTANCE, 0, 2);
	}
}
