package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class TestManager implements Listener {
	public static int count = 0;
	public static double offset = 0.5;
	public static double acceleration = 0.02700;
	public static boolean movingForward = true;
//	public static double acceleration = 0.2;

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) drawSphere(onlinePlayer.getLocation()
						.add(0, 1, 0), 400, 10, 10, 10);
				offset += acceleration;
				if(movingForward) {
					if(acceleration > 0.03150) movingForward = false;
					acceleration += 0.000_012;
				} else {
					if(acceleration < 0.02700) movingForward = true;
					acceleration -= 0.000_012;
				}
				count++;
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1);
	}

	public static void drawSphere(Location location, int number, double xRadius, double yRadius, double zRadius) {
		for(int i = 0; i < number; i++) {
			double k = i + (offset % 1);
			double phi = Math.acos(1 - 2 * k / number);
			double theta = Math.PI * (1 + Math.sqrt(5)) * k;
			double x = Math.cos(theta) * Math.sin(phi) * xRadius;
			double y = Math.sin(theta) * Math.sin(phi) * yRadius;
			double z = Math.cos(phi) * zRadius;
			Location displayLocation = location.clone().add(x, -z, y);
			for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				EntityPlayer entityPlayer = ((CraftPlayer) onlinePlayer).getHandle();
				ParticleColor particleColor = ParticleColor.getRandomParticleColor(i);
				entityPlayer.playerConnection.sendPacket(
						new PacketPlayOutWorldParticles(
								EnumParticle.REDSTONE, true, (float) displayLocation.getX(), (float) displayLocation.getY(), (float) displayLocation.getZ(),
								particleColor.red, particleColor.green, particleColor.blue, particleColor.brightness, 0
						)
				);
			}
		}
	}

	public static void drawCircle(Location location, int number, double xRadius, double zRadius) {
		for(int i = 0; i < number; i++) {
			double k = i + 0.5;
			double r = Math.sqrt(k / number);
			double theta = Math.PI * (1 + Math.sqrt(5)) * k;
			double x = r * Math.cos(theta) * xRadius;
			double z = r * Math.sin(theta) * zRadius;
			Location displayLocation = location.clone().add(x, 0, z);
			displayLocation.getWorld().playEffect(displayLocation, Effect.COLOURED_DUST, 1);
		}
	}

	public enum ParticleColor {
		//		DARK_RED("&4Dark Red", 1,  0.66406F, 0F, 0F, 1),
//		RED("&cRed", 1,  1F, 0.33203F, 0.33203F, 1),
//		GOLD("&6Orange", 14,  1F, 0.66406F, 0F, 1),
//		YELLOW("&eYellow", 11,  1F, 1F, 0.33203F, 1),
//		DARK_GREEN("&2Dark Green", 2,  Float.MIN_VALUE, 0.66406F, 0F, 1),
//		GREEN("&aLime", 10,  0.33203F, 1F, 0.33203F, 1),
//		AQUA("&bLight Blue", 12,  0.33203F, 1F, 1F, 1),
		DARK_AQUA("&3Cyan", 6,  Float.MIN_VALUE, 0.66406F, 0.66406F, 1),
		//		BLUE("&9Blue", 4,  0.33203F, 0.33203F, 1F, 1),
//		DARK_BLUE("&1Dark Blue", 4,  Float.MIN_VALUE, Float.MIN_VALUE, 0.66406F, 1),
		LIGHT_PURPLE("&dPink", 13,  1F, 0.33203F, 1F, 1),
		DARK_PURPLE("&5Purple", 5,  0.66406F, 0F, 0.66406F, 1),
		WHITE("&fWhite", 15,  1F, 1F, 1F, 1),
//		GRAY("&7Gray", 7,  0.66406F, 0.66406F, 0.66406F, 1),
//		DARK_GRAY("&8Dark Gray", 8,  0.33203F, 0.33203F, 0.33203F, 1),
//		BLACK("&0Black", 0,  Float.MIN_VALUE, 0F, 0F, 1),
		;

		public String displayName;
		public int data;
		public float red;
		public float green;
		public float blue;
		public float brightness;

		ParticleColor(String displayName, int data, float red, float green, float blue, float brightness) {
			this.displayName = displayName;
			this.data = data;
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.brightness = brightness;
		}
		public static ParticleColor getRandomParticleColor() {
			return getRandomParticleColor(0);
		}

		public static ParticleColor getRandomParticleColor(int seed) {
			return values()[new Random(seed * 50L + count * 25L).nextInt(values().length)];
		}

		public static ParticleColor getParticleColor(String refName) {
			for(ParticleColor value : values()) {
				if(refName.equalsIgnoreCase(value.name().replaceAll("_", ""))) return value;
			}
			return null;
		}
	}
}