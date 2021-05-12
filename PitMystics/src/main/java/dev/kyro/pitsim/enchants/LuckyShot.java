package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitRemake;
import dev.kyro.pitsim.controllers.DamageEvent;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class LuckyShot extends PitEnchant {

	public List<Arrow> luckyShots = new ArrayList<>();

	public LuckyShot() {
		super("Lucky Shot", true, ApplyType.BOWS,
				"luckyshot", "lucky-shot", "lucky", "luck");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = damageEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return damageEvent;


		return damageEvent;
	}


	@EventHandler
	public void onShoot(EntityShootBowEvent event) {
		if(!(event.getEntity() instanceof Player) || !(event.getProjectile() instanceof Arrow)) return;

		int enchantLvl = EnchantManager.getEnchantLevel(((Player) event.getEntity()).getPlayer(), this);

		double chanceCalculation = Math.random();
		double enchantChance = getChance(enchantLvl) * 0.01;

		if(chanceCalculation <= enchantChance) {
			luckyShots.add((Arrow) event.getProjectile());
			Bukkit.broadcastMessage("Lucky Shot!");
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				for(int i = 0; i < luckyShots.size(); i++) {
					Arrow arrow = luckyShots.get(i);
					arrow.getWorld().playEffect(arrow.getLocation(), Effect.COLOURED_DUST, 0, 30);
				}

			}
		}.runTaskTimer(PitRemake.INSTANCE, 0L, 1L);

	}



	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Deal &c+" + getChance(enchantLvl) + " &7bow damage").getLore();
	}

//	TODO: Fletching damage equation
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
