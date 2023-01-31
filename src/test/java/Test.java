import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Test {
	public static final ZoneId TIME_ZONE = ZoneId.of("America/New_York");

	public static void main(String[] args) {

		OffsetDateTime date = OffsetDateTime.now(TIME_ZONE);
		System.out.println(date.toInstant().toEpochMilli());
		System.out.println(Instant.now().toEpochMilli());

		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
		System.out.println(dateFormat.format(date) + " EST");
	}
}
