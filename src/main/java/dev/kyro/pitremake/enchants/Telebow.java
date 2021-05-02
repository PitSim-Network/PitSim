package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitremake.PitRemake;
import dev.kyro.pitremake.controllers.DamageEvent;
import dev.kyro.pitremake.controllers.EnchantManager;
import dev.kyro.pitremake.controllers.PitEnchant;
import dev.kyro.pitremake.enums.ApplyType;
import dev.kyro.pitremake.events.VolleyShootEvent;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Telebow extends PitEnchant {

	public Telebow() {
		super("Telebow", true, ApplyType.BOWS,
				"telebow", "tele");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {
		return damageEvent;
	}

	@EventHandler
	public void onBowShoot(EntityShootBowEvent event) {

		if(event instanceof VolleyShootEvent) return;

		if(!(event.getEntity() instanceof Player) || !(event.getProjectile() instanceof Arrow)) return;
		Player player = ((Player) event.getEntity()).getPlayer();
		Arrow arrow = (Arrow) event.getProjectile();

		int enchantLvl = EnchantManager.getEnchantLevel(player, this);
		if(enchantLvl == 0) return;

		new BukkitRunnable() {
			int count = 0;
			double arrowVelo = arrow.getVelocity().length();
			@Override
			public void run() {

				if(++count == getArrows(enchantLvl)) {

					cancel();
					return;
				}

				Arrow volleyArrow = player.launchProjectile(Arrow.class);
				volleyArrow.setVelocity(player.getEyeLocation().getDirection().normalize().multiply(arrowVelo));

				VolleyShootEvent volleyShootEvent = new VolleyShootEvent(event.getEntity(), event.getBow(), volleyArrow, event.getForce());
				PitRemake.INSTANCE.getServer().getPluginManager().callEvent(volleyShootEvent);

				new BukkitRunnable() {
					@Override
					public void run() {
						player.getWorld().playSound(player.getLocation(), Sound.SHOOT_ARROW, 1, 1);
					}
				}.runTaskLater(PitRemake.INSTANCE, 1L);
			}
		}.runTaskTimer(PitRemake.INSTANCE, 2L, 2L);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Sneak to shoot a teleportation &f", "&7arrow" + getArrows(enchantLvl) + " arrows &7at once").getLore();
	}

	public int getArrows(int enchantLvl) {

		return enchantLvl + 2;
	}
}
