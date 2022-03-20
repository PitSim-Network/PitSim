package dev.kyro.pitsim.misc;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.commands.LightningCommand;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.GoldenHelmet;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.megastreaks.Overdrive;
import dev.kyro.pitsim.megastreaks.RNGesus;
import dev.kyro.pitsim.megastreaks.Uberstreak;
import net.minecraft.server.v1_8_R3.World;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Material;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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

	public static void applyPotionEffect(LivingEntity entity, PotionEffectType type, int duration, int amplifier, boolean ambient, boolean particles) {
		if(amplifier < 0) return;
		if(duration == 0) return;

		if(NonManager.getNon(entity) == null && entity instanceof Player) {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer((Player) entity);
			if(pitPlayer.megastreak.getClass() == Uberstreak.class) {
				Uberstreak uberstreak = (Uberstreak) pitPlayer.megastreak;
				if(uberstreak.uberEffects.contains(Uberstreak.UberEffect.NO_SPEED) && type == PotionEffectType.SPEED)
					return;
			} else if(pitPlayer.megastreak.getClass() != Overdrive.class && pitPlayer.megastreak.isOnMega() && type == PotionEffectType.SLOW) return;
		}

		for(PotionEffect potionEffect : entity.getActivePotionEffects()) {
			if(!potionEffect.getType().equals(type) || potionEffect.getAmplifier() > amplifier) continue;
			if(potionEffect.getAmplifier() == amplifier && potionEffect.getDuration() >= duration) continue;
			entity.removePotionEffect(type);
			break;
		}
		entity.addPotionEffect(new PotionEffect(type, duration, amplifier, ambient, particles));
		if(type == PotionEffectType.POISON) {
			if(GoldenHelmet.abilities.get(entity) != null) {
				GoldenHelmet.abilities.get(entity).onDeactivate();
			}
			GoldenHelmet.toggledPlayers.remove(entity);
		}
	}

	public static void multiKill(Player player) {

		new BukkitRunnable() {
			int count = 0;

			@Override
			public void run() {

				switch(count) {
					case 0:
						Sounds.MULTI_1.play(player);
						break;
					case 1:
						Sounds.MULTI_2.play(player);
						break;
					case 2:
						Sounds.MULTI_3.play(player);
						break;
					case 3:
						Sounds.MULTI_4.play(player);
						break;
					case 4:
						Sounds.MULTI_5.play(player);
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

		return new DecimalFormat("#,##0.##").format(number);
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
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(pitPlayer.megastreak.getClass() == RNGesus.class && pitPlayer.getKills() < RNGesus.INSTABILITY_THRESHOLD && pitPlayer.megastreak.isOnMega())
			return;

		PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" +
				ChatColor.translateAlternateColorCodes('&', message) + "\"}"), (byte) 2);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	public static void sendTitle(Player player, String message, int length) {
		IChatBaseComponent chatTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" +
				ChatColor.translateAlternateColorCodes('&', message) + "\"}");

		PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, chatTitle);
		PacketPlayOutTitle titleLength = new PacketPlayOutTitle(5, length, 5);


		((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
	}

	public static void sendSubTitle(Player player, String message, int length) {
		IChatBaseComponent chatSubTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" +
				ChatColor.translateAlternateColorCodes('&', message) + "\"}");

		PacketPlayOutTitle subtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, chatSubTitle);
		PacketPlayOutTitle subTitleLength = new PacketPlayOutTitle(5, length, 5);

		((CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitle);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(subTitleLength);
	}

	public static boolean isCritical(LivingEntity entity) {
		return entity.getFallDistance() > 0.0F &&
				!entity.isOnGround() &&
				!entity.isInsideVehicle() &&
				!entity.hasPotionEffect(PotionEffectType.BLINDNESS) &&
				entity.getLocation().getBlock().getType() != Material.LADDER &&
				entity.getLocation().getBlock().getType() != Material.VINE;
	}

	public static boolean isAirOrNull(ItemStack itemStack) {

		return itemStack == null || itemStack.getType() == Material.AIR;
	}

	public static String getFormattedKills(int kills) {

		DecimalFormat decimalFormat = new DecimalFormat("#.##");
		decimalFormat.setGroupingUsed(true);
		decimalFormat.setGroupingSize(3);

		return decimalFormat.format(kills);
	}

	public static void strikeLightningForPlayers(Location location, double radius) {
		List<Player> nearbyPlayers = new ArrayList<>();
		for(Entity nearbyEntity : location.getWorld().getNearbyEntities(location, radius, radius, radius)) {
			if(!(nearbyEntity instanceof Player) || NonManager.getNon((Player) nearbyEntity) != null) continue;
			nearbyPlayers.add((Player) nearbyEntity);
		}
		for(Player lightningPlayer : LightningCommand.lightningPlayers) nearbyPlayers.remove(lightningPlayer);

		Player[] lightningPlayers = new Player[nearbyPlayers.size()];
		lightningPlayers = nearbyPlayers.toArray(lightningPlayers);
		strikeLightningForPlayers(location, lightningPlayers);
	}

	public static void strikeLightningForPlayers(Location location, Player... players) {
		World world = ((CraftWorld) location.getWorld()).getHandle();

		for(Player player : players) {
			EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
			entityPlayer.playerConnection.sendPacket(new PacketPlayOutSpawnEntityWeather(
					new EntityLightning(world, location.getX(), location.getY(), location.getZ(), false, false)));

			player.playSound(location, Sound.AMBIENCE_THUNDER, 10, 1);
			player.playSound(location, Sound.EXPLODE, 10, (float) (Math.random() * 0.2 + 0.6));
		}
	}

	public static long getRunnableOffset(int minutes) {
		return (long) (Math.random() * 20 * 60 * minutes);
	}

	public static String formatDuration(double seconds) {
		DecimalFormat decimalFormat = new DecimalFormat("0.#");
		if(seconds < 60) return decimalFormat.format(seconds) + " seconds";
		if(seconds < 60 * 60) return decimalFormat.format(seconds / 60.0) + " minutes";
		if(seconds < 60 * 60 * 24) return decimalFormat.format(seconds / 60.0 / 60.0) + " hours";
		return decimalFormat.format(seconds / 60.0 / 60.0 / 24.0) + " days";
	}

	public static String formatLarge(double large) {
		DecimalFormat decimalFormat = new DecimalFormat("#,##0.#");
		if(large < 1_000) return decimalFormat.format(large);
		if(large < 1_000_000) return decimalFormat.format(large / 1_000.0) + "K";
		if(large < 1_000_000_000) return decimalFormat.format(large / 1_000_000.0) + "M";
		return decimalFormat.format(large / 1_000_000_000) + "B";
	}

	public static String formatRatio(double ratio) {
		if(ratio < 1_000) return new DecimalFormat("#,##0.###").format(ratio);
		if(ratio < 1_000_000) return new DecimalFormat("#,##0.#").format(ratio / 1_000) + "K";
		return new DecimalFormat("#,##0.#").format(ratio / 1_000_000) + "M";
	}

	public static String formatPercent(double percent) {
		return new DecimalFormat("0.0").format(percent * 100) + "%";
	}

	public static HealEvent heal(LivingEntity entity, double amount, HealEvent.HealType healType, int max) {
		if(max == -1) max = Integer.MAX_VALUE;

		HealEvent healEvent = new HealEvent(entity, amount, healType, max);
		Bukkit.getServer().getPluginManager().callEvent(healEvent);

		if(healType == HealEvent.HealType.HEALTH) {
			entity.setHealth(Math.min(entity.getHealth() + healEvent.getFinalHeal(), entity.getMaxHealth()));
		} else {
			EntityPlayer nmsPlayer = ((CraftPlayer) entity).getHandle();
			if(nmsPlayer.getAbsorptionHearts() < healEvent.max)
				nmsPlayer.setAbsorptionHearts(Math.min((float) (nmsPlayer.getAbsorptionHearts() + healEvent.getFinalHeal()), max));
		}
		return healEvent;
	}
}
