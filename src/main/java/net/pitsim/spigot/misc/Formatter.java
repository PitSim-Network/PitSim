package net.pitsim.spigot.misc;

import org.bukkit.ChatColor;

import java.text.DecimalFormat;
import java.time.Duration;

public class Formatter {
	public static DecimalFormat decimalCommaFormat = new DecimalFormat("#,##0.##");
	public static DecimalFormat commaFormat = new DecimalFormat("#,##0");

	public static String formatRenown(int renown) {
		return translate("&e" + commaFormat.format(renown) + " Renown");
	}

	public static String formatSouls(int souls) {
		return formatSouls(souls, true);
	}

	public static String formatSouls(int souls, boolean color) {
		return (color ? "&f" : "") + translate(commaFormat.format(souls) + " Soul" + Misc.s(souls));
	}

	public static String formatGoldFull(double amount) {
		return translate("&6" + decimalCommaFormat.format(amount) + "g");
	}

	public static String formatDurationFull(long millis, boolean displaySeconds) {
		return formatDurationFull(Duration.ofMillis(millis), displaySeconds);
	}

	public static String formatDurationFull(Duration duration, boolean displaySeconds) {
		long millis = duration.toMillis();
		long days = millis / (24 * 60 * 60 * 1000);
		millis %= (24 * 60 * 60 * 1000);
		long hours = millis / (60 * 60 * 1000);
		millis %= (60 * 60 * 1000);
		long minutes = millis / (60 * 1000);
		millis %= (60 * 1000);
		long seconds = millis / 1000;
		if(!displaySeconds) {
			if(seconds != 0) minutes++;
			if(minutes == 60) {
				minutes = 0;
				hours++;
			}
			if(hours == 24) {
				hours = 0;
				days++;
			}
		}

		String durationString = "";
		if(days != 0) durationString += days + "d ";
		if(hours != 0) durationString += hours + "h ";
		if(minutes != 0) durationString += minutes + "m ";
		if(displaySeconds && seconds != 0) durationString += seconds + "s";
		return durationString.trim();
	}

	public static String formatDurationMostSignificant(double seconds) {
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

	public static String translate(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}
}
