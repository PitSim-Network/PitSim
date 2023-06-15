package net.pitsim.pitsim.enchants.overworld;

import de.myzelyam.api.vanish.VanishAPI;
import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.controllers.Cooldown;
import net.pitsim.pitsim.controllers.EnchantManager;
import net.pitsim.pitsim.controllers.NonManager;
import net.pitsim.pitsim.controllers.objects.PitEnchant;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.enums.ApplyType;
import net.pitsim.pitsim.events.VolleyShootEvent;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Robinhood extends PitEnchant {
	public static List<Arrow> robinMap = new ArrayList<>();

	public Robinhood() {
		super("Robinhood", true, ApplyType.BOWS,
				"robinhood", "robin");
	}

//	@EventHandler
//	public void onAttack(AttackEvent.Apply attackEvent) {
//		if(!canApply(attackEvent) || attackEvent.arrow == null) return;
//		if(!robinMap.contains(attackEvent.arrow)) return;
//
//		attackEvent.multipliers.add(0.5D);
//	}

	@EventHandler
	public void onHit(ProjectileHitEvent event) {
		if(!(event.getEntity() instanceof Arrow)) return;
		new BukkitRunnable() {
			@Override
			public void run() {
				robinMap.remove((Arrow) event.getEntity());
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);
	}

	@EventHandler
	public void onBowShoot(EntityShootBowEvent event) {

		if(!(event.getEntity() instanceof Player) || !(event.getProjectile() instanceof Arrow)) return;
		Player player = ((Player) event.getEntity()).getPlayer();
		Arrow arrow = (Arrow) event.getProjectile();
		if(event instanceof VolleyShootEvent) return;

		int enchantLvl = EnchantManager.getEnchantLevel(player, this);
		if(enchantLvl == 0) return;
		robinMap.add(arrow);

		Cooldown cooldown = getCooldown(player, 40);
		if(cooldown.isOnCooldown()) return;
		else cooldown.restart();

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		pitPlayer.stats.robinhood++;

		new BukkitRunnable() {
			@Override
			public void run() {
				if(!robinMap.contains(arrow)) {
					cancel();
					return;
				}

				Map.Entry<Player, Double> targetInfo = null;
				for(Entity nearbyEntity : arrow.getWorld().getNearbyEntities(arrow.getLocation(), getRange(enchantLvl), getRange(enchantLvl), getRange(enchantLvl))) {

					if(!(nearbyEntity instanceof Player) || nearbyEntity.equals(player)) continue;
					Player target = (Player) nearbyEntity;
					if(NonManager.getNon(target) != null) continue;
					if(VanishAPI.isInvisible(target)) continue;

					double distance = arrow.getLocation().distance(target.getLocation());
					if(targetInfo == null || distance < targetInfo.getValue()) {
						targetInfo = new AbstractMap.SimpleEntry<>(target, distance);
					}
				}

				if(targetInfo == null) return;

				Vector arrowVector = arrow.getLocation().toVector();
				Vector targetVector = targetInfo.getKey().getLocation().toVector();
				targetVector.setY(targetVector.getY() + 2);

				Vector direction = targetVector.subtract(arrowVector).normalize();
				arrow.setVelocity(direction);
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}

	public double getRange(int enchantLvl) {
		return enchantLvl * 0.5 + 0.5;
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that makes your arrows home";
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		DecimalFormat format = new DecimalFormat("0.#");
		return new PitLoreBuilder(
				"&7Your shots &ehome &7from &e" + format.format(getRange(enchantLvl)) + " &7block" +
				(getRange(enchantLvl) == 1 ? "" : "s") + "away (2s cooldown)"
		).getLore();
	}
}
