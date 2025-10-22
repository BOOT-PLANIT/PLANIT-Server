package com.planit.planit.global.util;

import java.time.*;
import java.time.format.DateTimeFormatter;

public final class DateUtil {
	private DateUtil() {}
	public static String nowIso() {
		return ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	}
}
