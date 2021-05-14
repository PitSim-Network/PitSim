package dev.kyro.pitsim.misc;

import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.PitSim;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;

public class Misc {

	public static String ordinalWords(int num) {

		switch(num) {
			case 1:
				return "";
			case 2:
				return " second";
			case 3:
				return " third";
			case 4:
				return " fourth";
			case 5:
				return " fifth";
		}
		return "";
	}

	public static void applyPotionEffect(Player player, PotionEffectType type, int duration, int amplifier, boolean ambient, boolean particles) {
		if(amplifier < 0) return;
		if(duration == 0) return;

		for(PotionEffect potionEffect : player.getActivePotionEffects()) {
			if(!potionEffect.getType().equals(type) || potionEffect.getAmplifier() > amplifier) continue;
			if(potionEffect.getAmplifier() == amplifier && potionEffect.getDuration() >= duration) continue;
			player.removePotionEffect(type);
			break;
		}
		player.addPotionEffect(new PotionEffect(type, duration, amplifier, ambient, particles));
	}

	public static void multiKill(Player player) {

		new BukkitRunnable() {
			int count = 0;
			@Override
			public void run() {

				switch(count) {
					case 0:
						ASound.play(player, Sound.ORB_PICKUP, 1F, 1.7936507F);
						break;
					case 1:
						ASound.play(player, Sound.ORB_PICKUP, 1F, 1.8253968F);
						break;
					case 2:
						ASound.play(player, Sound.ORB_PICKUP, 1F, 1.8730159F);
						break;
					case 3:
						ASound.play(player, Sound.ORB_PICKUP, 1F, 1.9047619F);
						break;
					case 4:
						ASound.play(player, Sound.ORB_PICKUP, 1F, 1.9523809F);
						break;
				}

				if(++count > 5) cancel();
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 2L);
	}

	/**
	 * Rounds damage and then converts to hearts.
	 * Should only be used for displaying, not calculation.
	 */
	public static String getHearts(double damage) {

		return roundString(damage / 2) + "\u2764";
	}

	/**
	 * Rounds a number to 2 decimal places and trims extra zeros.
	 * Should only be used for displaying, not calculation.
	 */
	public static String roundString(double number) {

		return new DecimalFormat("##0.##").format(number);
	}

	/**
	 * Converts to multiplier
	 */
	public static double getReductionMultiplier(double reduction) {

		return Math.max(1 - (reduction / 100D), 0);
	}

	public static int linearEnchant(int level, double step, double base) {

		return (int) (level * step + base);
	}

	public static void sendActionBar(Player player, String message) {
		PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" +
				ChatColor.translateAlternateColorCodes('&', message) + "\"}"), (byte) 2);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	public static void sendTitle(Player player, String message) {
		IChatBaseComponent chatTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" +
				ChatColor.translateAlternateColorCodes('&', message) + "\"}");

		PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, chatTitle);
		PacketPlayOutTitle length = new PacketPlayOutTitle(5, 20, 5);


		((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
	}

	public static void sendSubTitle(Player player, String message) {
		IChatBaseComponent chatSubTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" +
				ChatColor.translateAlternateColorCodes('&', message) + "\"}");

		PacketPlayOutTitle subtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, chatSubTitle);
		PacketPlayOutTitle length = new PacketPlayOutTitle(5, 20, 5);


		((CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitle);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
	}

	public static boolean isCritical(Player player) {
		return player.getFallDistance() > 0.0F &&
				!player.isOnGround() &&
				!player.isInsideVehicle() &&
				!player.hasPotionEffect(PotionEffectType.BLINDNESS) &&
				player.getLocation().getBlock().getType() != Material.LADDER &&
				player.getLocation().getBlock().getType() != Material.VINE;
	}
}
