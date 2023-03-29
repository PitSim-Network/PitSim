import java.text.DecimalFormat;

public class TurmoilCalculations {
	public static int MAX_TURMOIL_TICKS = 60;

	public static void main(String[] args) {
		int timesHitMaxTick = 0;
		int totalRuns = 1_000_000;
		double average = 0;

		for(int i = 0; i < totalRuns; i++) {
			int ticks = getTurmoilTicks();
			if(ticks == MAX_TURMOIL_TICKS) timesHitMaxTick++;
			average += ticks;
		}
		average /= totalRuns;

		DecimalFormat decimalFormat = new DecimalFormat("0.###");
		System.out.println("Percent Hit Max: " + decimalFormat.format((double) timesHitMaxTick * 100 / totalRuns) + "%");
		System.out.println("Average Multiplier: " + decimalFormat.format(average * 0.1) + "x");
	}

	public static int getTurmoilTicks() {
		double breakChance = Math.random() < 0.2 ? 0.025 : 0.14;

		int ticks = 1;
		while(Math.random() > breakChance && ticks < MAX_TURMOIL_TICKS) ticks++;
		return ticks;
	}
}
