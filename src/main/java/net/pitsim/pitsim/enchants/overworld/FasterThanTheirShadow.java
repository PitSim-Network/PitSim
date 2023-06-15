package net.pitsim.pitsim.enchants.overworld;

import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.controllers.HitCounter;
import net.pitsim.pitsim.controllers.objects.PitEnchant;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.enums.ApplyType;
import net.pitsim.pitsim.events.ArrowHitBlockEvent;
import net.pitsim.pitsim.events.AttackEvent;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.List;

public class FasterThanTheirShadow extends PitEnchant implements Listener {

	public FasterThanTheirShadow() {
		super("Faster than their shadow", false, ApplyType.BOWS,
				"fasterthantheirshadow", "ftts", "faster", "shadow", "faster-than-their-shadow");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		PitPlayer pitPlayer = attackEvent.getAttackerPitPlayer();
		HitCounter.incrementCounter(pitPlayer.player, this);
		if(!HitCounter.hasReachedThreshold(pitPlayer.player, this, getStrikes(enchantLvl))) return;

		Misc.applyPotionEffect(attackEvent.getAttacker(), PotionEffectType.SPEED,
				4 * 20, getSpeedAmplifier(enchantLvl) - 1, true, false);

		pitPlayer.stats.ftts++;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onArrowHitBlock(ArrowHitBlockEvent event) {

		Arrow arrow = event.getArrow();
		Block block = event.getBlock(); // the block that was hit
		if(block == null || arrow.getShooter() == null || !(arrow.getShooter() instanceof Player)) return;

		if(!((Player) arrow.getShooter()).isOnline()) return;

		HitCounter.resetCombo((Player) arrow.getShooter(), this);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onArrowHitBlockDelete(ArrowHitBlockEvent event) {
		if(event.getBlock() == null) return;
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					event.getArrow().remove();
				} catch(Exception ignored) {
				}
			}
		}.runTaskLater(PitSim.INSTANCE, 100L);
	}

	@EventHandler
	public void onArrowHit(ProjectileHitEvent event) {
		if(!(event.getEntity() instanceof Arrow) || !(event.getEntity().getShooter() instanceof Player)) return;

		net.minecraft.server.v1_8_R3.EntityArrow entityArrow = ((CraftArrow) event.getEntity()).getHandle();
		Bukkit.getScheduler().scheduleSyncDelayedTask(PitSim.INSTANCE, () -> {
			try {

				Field fieldX = net.minecraft.server.v1_8_R3.EntityArrow.class
						.getDeclaredField("d");
				Field fieldY = net.minecraft.server.v1_8_R3.EntityArrow.class
						.getDeclaredField("e");
				Field fieldZ = net.minecraft.server.v1_8_R3.EntityArrow.class
						.getDeclaredField("f");

				fieldX.setAccessible(true);
				fieldY.setAccessible(true);
				fieldZ.setAccessible(true);

				int x = fieldX.getInt(entityArrow);
				int y = fieldY.getInt(entityArrow);
				int z = fieldZ.getInt(entityArrow);

				if(isValidBlock(y)) {
					Block block = event.getEntity().getWorld().getBlockAt(x, y, z);
					Bukkit.getServer()
							.getPluginManager()
							.callEvent(
									new ArrowHitBlockEvent((Arrow) event.getEntity(), block));
				} else {
					Block block = event.getEntity().getWorld().getBlockAt(x, y, z);

					if(block == null || event.getEntity() == null) return;
					Bukkit.getServer()
							.getPluginManager()
							.callEvent(
									new ArrowHitBlockEvent((Arrow) event.getEntity(), null));
				}

			} catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e1) {
			}
		});

	}

	private boolean isValidBlock(int y) {
		return y != -1;
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Hitting &f" + getStrikes(enchantLvl) + " &7shots without missing grants &eSpeed "
				+ AUtil.toRoman(getSpeedAmplifier(enchantLvl)) + " &7(4s)"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that gives " +
				"&eSpeed IV&7 after two consecutive bow shots are landed";
	}

	public int getSlowDuration(int enchantLvl) {

		return Misc.linearEnchant(enchantLvl, 0.5, 0) * 3;
	}

	public int getSpeedAmplifier(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 2;
			case 2:
				return 3;
			case 3:
				return 4;

		}
		return 0;
	}

	public int getStrikes(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 3;
			case 2:
				return 2;
			case 3:
				return 2;

		}
		return 0;
	}
}
