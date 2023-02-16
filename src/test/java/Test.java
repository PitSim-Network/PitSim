import java.time.ZoneId;

public class Test {
	public static final ZoneId TIME_ZONE = ZoneId.of("America/New_York");

	public static void main(String[] args) {

		System.out.println(humanReadableDuration(859032808, true));
	}

	public static String humanReadableDuration(long millis, boolean displaySeconds) {
		long days = millis / (24 * 60 * 60 * 1000);
		millis %= (24 * 60 * 60 * 1000);
		long hours = millis / (60 * 60 * 1000);
		millis %= (60 * 60 * 1000);
		long minutes = millis / (60 * 1000);
		millis %= (60 * 1000);
		long seconds = millis / 1000;

		String duration = "";
		if(days != 0) duration += days + "d ";
		if(hours != 0) duration += hours + "h ";
		if(minutes != 0) duration += minutes + "m ";
		if(displaySeconds && seconds != 0) duration += seconds + "s";
		return duration;
	}
}
