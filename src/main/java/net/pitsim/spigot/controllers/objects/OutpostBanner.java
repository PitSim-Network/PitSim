package net.pitsim.spigot.controllers.objects;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.pitsim.arcticguilds.Guild;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.controllers.MapManager;
import net.pitsim.spigot.controllers.OutpostManager;
import net.pitsim.spigot.cosmetics.ParticleOffset;
import net.pitsim.spigot.cosmetics.PitParticle;
import net.pitsim.spigot.cosmetics.particles.BlockCrackParticle;
import net.pitsim.spigot.cosmetics.particles.ExplosionHugeParticle;
import net.pitsim.spigot.cosmetics.particles.ParticleColor;
import net.pitsim.spigot.cosmetics.particles.RedstoneParticle;
import net.pitsim.spigot.enums.PitEntityType;
import net.pitsim.spigot.holograms.Hologram;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class OutpostBanner {
	public static final Location CENTER_LOCATION = OutpostManager.CENTER_LOCATION;
	public static final Location BANNER_LOCATION = new Location(MapManager.getDarkzone(), 227.5, 89.15, -189.25);

	public Hologram hologram;
	public ArmorStand armorStand;

	public ChatColor color = ChatColor.WHITE;
	public int percent = 0;
	PitParticle pitParticle = new RedstoneParticle();

	public OutpostBanner() {
		this.armorStand = MapManager.getDarkzone().spawn(BANNER_LOCATION, ArmorStand.class);
		armorStand.setGravity(false);
		armorStand.setVisible(false);
		armorStand.setCustomNameVisible(false);
		armorStand.setMarker(true);

		new BukkitRunnable() {
			@Override
			public void run() {
				if(percent == 0) return;

				double radius = percent * 0.075;
				double halfSideLength = radius / Math.sqrt(2);
				double centerX = CENTER_LOCATION.getX();
				double centerZ = CENTER_LOCATION.getZ();

				double x1 = centerX - halfSideLength;
				double z1 = centerZ - halfSideLength;
				double x2 = centerX + halfSideLength;
				double z2 = centerZ + halfSideLength;

				double step = (x2 - x1) / (percent / 2D); //Change this value to effect points per side

				for (double x = x1; x <= x2; x += step) {
					displayParticle(x, z1);
				}

				for (double z = z1 + step; z < z2; z += step) {
					displayParticle(x2, z);
				}

				for (double x = x2 - step; x >= x1; x -= step) {
					displayParticle(x, z2);
				}

				for (double z = z2 - step; z > z1; z -= step) {
					displayParticle(x1, z);
				}

			}
		}.runTaskTimer(PitSim.INSTANCE, 0, 5);
	}

	public void displayParticle(double x, double z) {
		for(Entity nearbyEntity : MapManager.getDarkzone().getNearbyEntities(CENTER_LOCATION, 20, 20, 20)) {
			if(!Misc.isEntity(nearbyEntity, PitEntityType.REAL_PLAYER)) continue;
			EntityPlayer nmsPlayer = ((CraftPlayer) nearbyEntity).getHandle();
			pitParticle.display(nmsPlayer, new Location(MapManager.getDarkzone(), x, CENTER_LOCATION.getY(), z), ParticleColor.valueOf(color.name()));
		}
	}

	public void setBanner(Guild guild) {
		ItemStack banner = new ItemStack(Material.BANNER);
		BannerMeta bannerMeta = (BannerMeta) banner.getItemMeta();
		bannerMeta.setBaseColor(guild == null ? DyeColor.WHITE : getDyeColor(guild.color));
		banner.setItemMeta(bannerMeta);

		armorStand.setHelmet(banner);
		color = guild == null ? ChatColor.WHITE : guild.color;
	}

	public void setPercent(int percent) {
		if(percent > 100 || percent < 0) return;

		double blocksPerPercent = 0.02;

		double previousBlocks = blocksPerPercent * this.percent;
		double newBlocks = blocksPerPercent * percent;

		double increase = newBlocks - previousBlocks;

		Location location = armorStand.getLocation().clone();
		location.add(0, increase, 0);
		armorStand.teleport(location);

		this.percent = percent;


		PitParticle explosion = new ExplosionHugeParticle();
		PitParticle dirt = new BlockCrackParticle(new MaterialData(Material.DIRT));

		for(Entity player : MapManager.getDarkzone().getNearbyEntities(BANNER_LOCATION, 20, 20, 20)) {
			if(!Misc.isEntity(player, PitEntityType.REAL_PLAYER)) continue;

			EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
			Location centerLocation = BANNER_LOCATION.clone().add(0, 4, -0.25);

			for(int i = 0; i < 200; i++) {
				dirt.display(nmsPlayer, centerLocation, new ParticleOffset(0, 0, 0, 5, 0, 5));
			}

			if(percent == 100) {
				Vector force = player.getLocation().toVector().subtract(BANNER_LOCATION.toVector())
						.setY(1).normalize().multiply(3.5);
				player.setVelocity(force);

				for(int i = 0; i < 10; i++) {
					explosion.display(nmsPlayer, centerLocation, new ParticleOffset(0, 0, 0, 10, 10, 10));
				}

			}
		}

		Sounds.EXTRACT.play(BANNER_LOCATION, 20);
		if(percent == 100) Sounds.CREEPER_EXPLODE.play(BANNER_LOCATION, 20);
	}

	public static DyeColor getDyeColor(ChatColor chatColor) {
		switch (chatColor) {
			case AQUA:
				return DyeColor.LIGHT_BLUE;
			case BLACK:
				return DyeColor.BLACK;
			case BLUE:
			case DARK_BLUE:
				return DyeColor.BLUE;
			case DARK_AQUA:
				return DyeColor.CYAN;
			case DARK_GRAY:
				return DyeColor.GRAY;
			case DARK_GREEN:
				return DyeColor.GREEN;
			case DARK_PURPLE:
				return DyeColor.PURPLE;
			case DARK_RED:
			case RED:
				return DyeColor.RED;
			case GOLD:
				return DyeColor.ORANGE;
			case GRAY:
				return DyeColor.SILVER;
			case GREEN:
				return DyeColor.LIME;
			case LIGHT_PURPLE:
				return DyeColor.PINK;
			case WHITE:
				return DyeColor.WHITE;
			case YELLOW:
				return DyeColor.YELLOW;
			default:
				break;
		}

		return null;
	}
}
