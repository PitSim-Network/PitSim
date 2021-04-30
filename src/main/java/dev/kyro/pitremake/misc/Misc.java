package dev.kyro.pitremake.misc;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Misc {

	public static double getDistance(Location loc1, Location loc2) {

		double x1 = loc1.getX();
		double y1 = loc1.getY();
		double z1 = loc1.getZ();

		double x2 = loc2.getX();
		double y2 = loc2.getY();
		double z2 = loc2.getZ();

		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2));
	}

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

	public static void applyPotionEffect(Player player, PotionEffectType type, int duration, int amplifier) {

		for(PotionEffect potionEffect : player.getActivePotionEffects()) {
			if(!potionEffect.getType().equals(type) || potionEffect.getAmplifier() > amplifier) continue;
			if(potionEffect.getAmplifier() == amplifier && potionEffect.getDuration() >= duration) continue;
			player.removePotionEffect(type);
			break;
		}
		player.addPotionEffect(new PotionEffect(type, duration, amplifier, true));
	}

	public static String getHearts(double damage) {

		String string = (damage / 2) % 1 == 0 ? String.valueOf((int) (damage / 2)) : String.valueOf((Math.floor(damage * 50)) / 100);
		return string + "\u2764";
	}
}
