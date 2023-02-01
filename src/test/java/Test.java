import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

public class Test {
	public static final ZoneId TIME_ZONE = ZoneId.of("America/New_York");

	public static void main(String[] args) {

		System.out.println(getDate("8/01/23"));
	}

	public static Date getDate(String dateString) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		TimeZone est = TimeZone.getTimeZone("EST");
		TimeZone edt = TimeZone.getTimeZone("EDT");

		try {
			dateFormat.setTimeZone(est);
			Date date = dateFormat.parse(dateString);

			if(est.inDaylightTime(date)) {
				dateFormat.setTimeZone(edt);
				return dateFormat.parse(dateString);
			} else return date;
		} catch(Exception ignored) {
			return null;
		}
	}
}
