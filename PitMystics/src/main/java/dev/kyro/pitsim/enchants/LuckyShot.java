package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class LuckyShot extends PitEnchant {

	public List<Arrow> luckyShots = new ArrayList<>();

	public LuckyShot() {
		super("Lucky Shot", true, ApplyType.BOWS,
				"luckyshot", "lucky-shot", "lucky", "luck");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {

		int enchantLvl = attackEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return;


		for(Arrow luckyShot : luckyShots) {
			if(luckyShot.equals(attackEvent.arrow)) {

				attackEvent.multiplier.add(4.0);
				AOutput.send(attackEvent.attacker, "&e&lLUCKY SHOT! &7against &b" + attackEvent.defender.getName() + "&7!");
				AOutput.send(attackEvent.defender, "&c&lOUCH! &b" + attackEvent.attacker.getName() + " &7got a lucky shot against you!");
				Misc.sendTitle(attackEvent.defender, " ");
				Misc.sendSubTitle(attackEvent.defender, "&c&lOUCH!");
				attackEvent.defender.playSound(attackEvent.defender.getLocation(), Sound.ZOMBIE_WOODBREAK, 1f, 1f);
			}
		}
	}

	@EventHandler
	public void onShoot(EntityShootBowEvent event) {
		if(!(event.getEntity() instanceof Player) || !(event.getProjectile() instanceof Arrow)) return;

		int enchantLvl = EnchantManager.getEnchantLevel(((Player) event.getEntity()).getPlayer(), this);

		double chanceCalculation = Math.random();
		double enchantChance = getChance(enchantLvl) * 0.01;

		if(chanceCalculation <= enchantChance) {
			luckyShots.add((Arrow) event.getProjectile());
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				for(int i = 0; i < luckyShots.size(); i++) {
					Arrow arrow = luckyShots.get(i);
					arrow.getWorld().playEffect(arrow.getLocation(), Effect.COLOURED_DUST, 0, 30);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 0L);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onHit(ProjectileHitEvent event) {
		if(!(event.getEntity() instanceof Arrow) || !(event.getEntity().getShooter() instanceof Player)) return;
		Player player = (Player) event.getEntity().getShooter();

		if(luckyShots.size() == 0) return;
		try {
			for(Arrow luckyShot : luckyShots) {
				if(luckyShot.equals(event.getEntity())) {

					Arrow luckyArrow = (Arrow) event.getEntity();
					if(luckyArrow.equals(luckyShot)) {

						new BukkitRunnable() {
							@Override
							public void run() {
								luckyShots.remove(luckyShot);

							}
						}.runTaskLater(PitSim.INSTANCE, 1L);

					}
				}

			}

		}catch(Exception e) {

		}

	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&e" + getChance(enchantLvl) + "&e% &7chance for a shot to deal",
				"&7quadruple damage").getLore();
	}

//	TODO: Lucky Shot damage equation
	public int getChance(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 2;
			case 2:
				return 5;
			case 3:
				return 10;
			case 20:
				return 100;
		}

		return 0;
	}
}
