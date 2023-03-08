package dev.kyro.pitsim.cosmetics.aura;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.cosmetics.CosmeticManager;
import dev.kyro.pitsim.cosmetics.CosmeticType;
import dev.kyro.pitsim.cosmetics.PitCosmetic;
import dev.kyro.pitsim.cosmetics.particles.ParticleColor;
import dev.kyro.pitsim.cosmetics.particles.RedstoneParticle;
import dev.kyro.pitsim.misc.math.MathUtils;
import dev.kyro.pitsim.misc.math.Point3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KyroAura2 extends PitCosmetic {

	public KyroAura2() {
		super("&b&k|&9Kyro's Other Aura&b&k|", "kyroaura2", CosmeticType.AURA);
	}

	public static void drawSphere(Location location, int count, double offset, int number, double xRadius, double yRadius, double zRadius) {
		RedstoneParticle particle = new RedstoneParticle();
		List<Point3D> spherePoints = MathUtils.getSphere(offset, number, xRadius, yRadius, zRadius);
		for(int i = 0; i < spherePoints.size(); i++) {
			Point3D point = spherePoints.get(i);
			Location displayLocation = location.clone().add(point.getX(), point.getY(), point.getZ());
			ParticleColor particleColor = getRandomParticleColor(i, count);
			particle.display(new ArrayList<>(Bukkit.getOnlinePlayers()), displayLocation, particleColor);
		}
	}

	@Override
	public void onEnable(PitPlayer pitPlayer) {
		runnableMap.put(pitPlayer.player.getUniqueId(), new BukkitRunnable() {
			int count = 0;
			double offset = 0.5;
			double velocity = 0.02700;
//			double velocity = 0.2; // constant that looks cool (but u have to disable the acceleration change)
			boolean movingForward = true;

			@Override
			public void run() {
				if(!CosmeticManager.isStandingStill(pitPlayer.player)) return;

				drawSphere(pitPlayer.player.getLocation().add(0, 1, 0), count, offset,
						400, 10, 10, 10);
				offset += velocity;
				if(movingForward) {
					if(velocity > 0.03150) movingForward = false;
					velocity += 0.000_012;
				} else {
					if(velocity < 0.02700) movingForward = true;
					velocity -= 0.000_012;
				}
				count++;
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L));
	}

	@Override
	public void onDisable(PitPlayer pitPlayer) {
		if(runnableMap.containsKey(pitPlayer.player.getUniqueId()))
			runnableMap.get(pitPlayer.player.getUniqueId()).cancel();
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.SKULL_ITEM, 1, 3)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7featuring some trippy",
						"&7fibbinoci particle math"
				))
				.getItemStack();
		SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
		skullMeta.setOwner("KyroKrypt");
		itemStack.setItemMeta(skullMeta);
		return itemStack;
	}

	public static ParticleColor getRandomParticleColor(int seed, int count) {
		List<ParticleColor> particleColors = new ArrayList<>();
		particleColors.add(ParticleColor.DARK_AQUA);
		particleColors.add(ParticleColor.LIGHT_PURPLE);
		particleColors.add(ParticleColor.DARK_PURPLE);
		particleColors.add(ParticleColor.WHITE);
		return particleColors.get(new Random(seed * 50L + count * 25L).nextInt(particleColors.size()));
	}
}