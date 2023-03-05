package dev.kyro.pitsim.enums;

import java.util.Calendar;
import java.util.TimeZone;

public enum PitCalendarEvent {
	APRIL_FOOLS(Calendar.APRIL, 1, Calendar.APRIL, 3),
	HALLOWEEN(Calendar.OCTOBER, 30, Calendar.NOVEMBER, 1),
	CHRISTMAS_SEASON(Calendar.DECEMBER, 1, Calendar.JANUARY, 9, true);

	public final int startMonth;
	public final int startDay;
	public final int endMonth;
	public final int endDay;
	public final boolean nextYear;

	PitCalendarEvent(int startMonth, int startDay, int endMonth, int endDay) {
		this(startMonth, startDay, endMonth, endDay, false);
	}

	PitCalendarEvent(int startMonth, int startDay, int endMonth, int endDay, boolean nextYear) {
		this.startMonth = startMonth;
		this.startDay = startDay;
		this.endMonth = endMonth;
		this.endDay = endDay;
		this.nextYear = nextYear;
	}

	//	Should only be called by the TimeManager class
	public boolean isCurrentlyActive() {
		return isBetweenDates(startMonth, startDay, endMonth, endDay, nextYear);
	}

	private static boolean isBetweenDates(int startMonth, int startDay, int endMonth, int endDay, boolean nextYear) {
		Calendar startDate = getDate(startMonth, startDay, false);
		Calendar endDate = getDate(endMonth, endDay, nextYear);
		Calendar currentTime = createCalendar();
		return currentTime.after(startDate) && currentTime.before(endDate);
	}

	private static Calendar getDate(int month, int day, boolean nextYear) {
		Calendar calendar = createCalendar();
		calendar.clear();
		calendar.set(Calendar.YEAR, createCalendar().get(Calendar.YEAR) + (nextYear ? 1 : 0));
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}

	private static Calendar createCalendar() {
		return Calendar.getInstance(TimeZone.getTimeZone("EST"));
	}
}
