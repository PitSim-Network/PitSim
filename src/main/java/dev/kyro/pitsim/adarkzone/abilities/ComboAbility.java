package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.PitBossAbility;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ComboAbility extends PitBossAbility {

	public int comboThreshold = 5;
	public int comboDuration = 40;

	public Map<UUID, Integer> comboMap = new HashMap<>();

	@EventHandler
	public void onHit(AttackEvent.Apply event) {
		if(event.getAttackerPlayer() != pitBoss.boss) return;
		if(!event.isDefenderPlayer()) return;

		Player player = event.getDefenderPlayer();
		comboMap.put(player.getUniqueId(), comboMap.getOrDefault(player.getUniqueId(), 0) + 1);

		if(comboMap.get(player.getUniqueId()) != comboThreshold) return;
		new BukkitRunnable() {
			int i = 0;

			@Override
			public void run() {
				EntityDamageByEntityEvent damage = new EntityDamageByEntityEvent(player, pitBoss.boss, EntityDamageByEntityEvent.DamageCause.ENTITY_ATTACK, 1);
				Bukkit.getPluginManager().callEvent(damage);
//				player.daam

				player.setNoDamageTicks(0);

				if(i >= comboDuration) {
					comboMap.remove(player.getUniqueId());
					cancel();
					return;
				}
				i++;
			}
		}.runTaskTimer(PitSim.INSTANCE, 1, 1);
	}
}
