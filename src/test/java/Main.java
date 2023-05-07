import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Main {
	public static List<Integer> mobKillList = new ArrayList<>();
	public static int seconds = 0;

	public static final int MINUTES_TO_SIM = 100_000;

	public static final double KILL_TRACK_MINUTES = 1;
	public static final int ALLOWED_KILLS_PER_MINUTE = 25;

	public static void main(String[] args) {
		int totalDrops = 0;

		for(seconds = 0; seconds < 60 * MINUTES_TO_SIM; seconds++) {
			Integer currentSecond = seconds;
			mobKillList.remove(currentSecond);

//			if(Math.random() > 40 / 60.0) continue; // current spider
			if(Math.random() > 21 / 60.0) continue; // current pigman

			addMobKill();

//			if(Math.random() > 0.089 * getSoulSoftCooldownMultiplier()) continue; // past
			if(Math.random() > 0.084 * getSoulSoftCooldownMultiplier()) continue; // current

			totalDrops++;
		}

		int minutes = (int) (seconds / 60.0);
		DecimalFormat decimalFormat = new DecimalFormat("#,##0.##");
		System.out.println("Drops/Minutes: " + decimalFormat.format((double) totalDrops / minutes));
//		System.out.println("Souls/Hour (JMB Rate): " + decimalFormat.format((double) totalDrops / minutes * 60 * 10.08));
//		System.out.println("Souls/Hour (JMB Rate): " + decimalFormat.format((double) totalDrops / minutes * 60 * 6 * Math.pow(1.2, 2))); // current spider
		System.out.println("Souls/Hour (JMB Rate): " + decimalFormat.format((double) totalDrops / minutes * 60 * 6 * Math.pow(1.2, 5))); // current pigman
		System.out.println("Total Drops: " + decimalFormat.format(totalDrops));
	}

	public static void addMobKill() {
		mobKillList.add((int) (seconds + KILL_TRACK_MINUTES * 60));
	}

	public static double getSoulSoftCooldownMultiplier() {
		int reduction = (int) Math.max(mobKillList.size() / KILL_TRACK_MINUTES - ALLOWED_KILLS_PER_MINUTE, 0);
		return Math.pow(0.9, reduction);
	}

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}
}
