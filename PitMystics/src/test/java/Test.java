import java.util.Calendar;
import java.util.TimeZone;

public class Test {
	public static void main(String[] args) {
		System.out.println(isChristmasImminent());
	}

	public static boolean isChristmasImminent() {
		Calendar eventStart = Calendar.getInstance(TimeZone.getTimeZone("EST"));
		Calendar eventEnd = Calendar.getInstance(TimeZone.getTimeZone("EST"));

		setDate(eventStart, Calendar.DECEMBER, 1);
		setDate(eventEnd, Calendar.JANUARY, 9, true);

		Calendar currentTime = Calendar.getInstance(TimeZone.getTimeZone("EST"));
		System.out.println(eventStart);
		System.out.println(eventEnd);
		return currentTime.after(eventStart) && currentTime.before(eventEnd);
	}

	private static void setDate(Calendar calendar, int month, int date) {
		setDate(calendar, month, date, false);
	}

	private static void setDate(Calendar calendar, int month, int date, boolean nextYear) {
		calendar.clear();
		calendar.set(Calendar.YEAR,
				Calendar.getInstance(TimeZone.getTimeZone("EST")).get(Calendar.YEAR) + (nextYear ? 1 : 0));
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	}
}
