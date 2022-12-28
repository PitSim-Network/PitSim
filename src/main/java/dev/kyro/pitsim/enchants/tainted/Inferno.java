package dev.kyro.pitsim.enchants.tainted;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitBoss;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Inferno extends PitEnchant {

	public static Map<UUID, UUID> fireMap = new HashMap<>();
	public static Map<UUID, Integer> stackMap = new HashMap<>();

	public Inferno() {
		super("Inferno", true, ApplyType.CHESTPLATES, "inferno", "fire", "inf");
		tainted = true;
		meleOnly = true;
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Map.Entry<UUID, UUID> entry : fireMap.entrySet()) {
					LivingEntity defender = getEntity(entry.getKey());
					Player attacker = getPlayer(entry.getValue());

					if(attacker == null || defender == null) continue;

					if(stackMap.containsKey(defender.getUniqueId())) {
						stackMap.put(defender.getUniqueId(), stackMap.get(defender.getUniqueId()) + 1);
					} else stackMap.put(defender.getUniqueId(), 1);


					double newHealth = defender.getHealth() - (stackMap.get(defender.getUniqueId()));
					if(newHealth <= 0) {
						defender.damage(1000, attacker);
					} else {
						defender.setHealth(newHealth);
						defender.damage(0);
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 20, 20);

	}

	@EventHandler
	public void onHit(AttackEvent.Apply attackEvent) {
		Player player = attackEvent.getAttackerPlayer();
		if(!attackEvent.isAttackerPlayer()) return;

		if(attackEvent.isDefenderPlayer() && !PitBoss.isPitBoss(attackEvent.getDefenderPlayer())) return;

		if(attackEvent.getAttacker() == attackEvent.getDefender()) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(fireMap.containsKey(attackEvent.getDefender().getUniqueId())) return;
		fireMap.put(attackEvent.getDefender().getUniqueId(), player.getUniqueId());
		attackEvent.getDefender().setFireTicks(10 * 20);

		new BukkitRunnable() {
			@Override
			public void run() {
				fireMap.remove(attackEvent.getDefender().getUniqueId());
				stackMap.remove(attackEvent.getDefender().getUniqueId());
			}
		}.runTaskLater(PitSim.INSTANCE, 20 * 10);

	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK ||
				event.getCause() == EntityDamageEvent.DamageCause.FIRE) event.setCancelled(true);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {
		return new ALoreBuilder("&7Set your enemies on &6Fire &7for &f10s", "&7with &c+0.5\u2764 damage &7per second", "&d&o-" + reduction(enchantLvl) + "% Mana Regen").getLore();
	}

	public static Player getPlayer(UUID uuid) {
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.getUniqueId().equals(uuid)) return player;
		}
		return null;
	}

	public static LivingEntity getEntity(UUID uuid) {
		List<Entity> entities = new ArrayList<>();
		for(World world : Bukkit.getWorlds()) {
			entities.addAll(world.getEntities());
		}

		for(Entity entity : entities) {
			if(entity.getUniqueId() == uuid) return (LivingEntity) entity;
		}

		return null;
	}

	public static int reduction(int enchantLvl) {
		return 80 - (20 * enchantLvl);
	}
}
