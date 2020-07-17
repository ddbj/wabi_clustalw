package jp.ac.nig.ddbj.wabi.util;

import java.util.Calendar;

public class CalendarUtil {
	
	public static String getTime() {
		Calendar c = Calendar.getInstance();
		return format(c);
	}

	public static String format(Calendar c) {
		return String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS", c);
	}
}
