package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.arcticapi.misc.AUtil;
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

public class Volley extends PitEnchant {

	public Volley() {
		super("Volley", true, ApplyType.BOWS,
				"volley");
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
			@Override
			public void run() {

				Arrow volleyArrow = player.launchProjectile(Arrow.class);
				volleyArrow.setVelocity(player.getEyeLocation().getDirection().normalize().multiply(arrow.getVelocity().length()));

				VolleyShootEvent volleyShootEvent = new VolleyShootEvent(event.getEntity(), event.getBow(), volleyArrow, event.getForce());
				PitRemake.INSTANCE.getServer().getPluginManager().callEvent(volleyShootEvent);

				ASound.play(player, Sound.SHOOT_ARROW, 1, 1);
			}
		}.runTaskTimer(PitRemake.INSTANCE, 2L, 1L);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7One shot per second, this bow is",
				"&7automatically fully drawn and", "&7grants &aJump Boost " + AUtil.toRoman(getArrows(enchantLvl) + 1) + " &7(2s)").getLore();
	}

	public int getArrows(int enchantLvl) {

		return enchantLvl + 2;
	}
}
