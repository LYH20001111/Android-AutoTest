package com.newland.iso.message.convert;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import com.newland.iso.util.SystemTime;

/**
 * Convert between a timestamp and a String. A timestamp includes both a date
 * and a time.
 */
public class UtcTimestampConverter extends AbstractDateTimeConverter {
    private static ThreadLocal<UtcTimestampConverter> utcTimestampConverter = new ThreadLocal<UtcTimestampConverter>();
    private final DateFormat utcTimestampFormat = createDateFormat("yyyyMMdd-HH:mm:ss");
    private final DateFormat utcTimestampFormatMillis = createDateFormat("yyyyMMdd-HH:mm:ss.SSS");
    private static HashMap<String, Calendar> dateCache = new HashMap<String, Calendar>();

    /**
     * Convert a timestamp (represented as a Date) to a String.
     * @param d the date to convert
     * @param includeMilliseconds controls whether milliseconds are included in the result
     * @return the formatted timestamp
     */
    public static String convert(Date d, boolean includeMilliseconds) {
        return getFormatter(includeMilliseconds).format(d);
    }

    private static DateFormat getFormatter(boolean includeMillis) {
        UtcTimestampConverter converter = utcTimestampConverter.get();
        if (converter == null) {
            converter = new UtcTimestampConverter();
            utcTimestampConverter.set(converter);
        }
        return includeMillis ? converter.utcTimestampFormatMillis : converter.utcTimestampFormat;
    }

    //
    // Performance optimization: the calendar for the start of the day is cached.
    // The time is converted to millis and then added to the millis specified by
    // the base calendar.
    //
    
    /**
     * Convert a timestamp string into a Date.
     * @param value the timestamp String
     * @return the parsed timestamp
     * @exception FieldConvertError raised if timestamp is an incorrect format.
     */
    public static Date convert(String value) throws FieldConvertException {
        verifyFormat(value);
        String dateString = value.substring(0, 8);
        Calendar c = getCalendarForDay(value, dateString);
        long timeOffset = (parseLong(value.substring(9, 11)) * 3600000L)
                + (parseLong(value.substring(12, 14)) * 60000L)
                + (parseLong(value.substring(15, 17)) * 1000L);
        if (value.length() == 21) {
            timeOffset += parseLong(value.substring(18, 21));
        }
        return new Date(c.getTimeInMillis() + timeOffset);
    }

    private static Calendar getCalendarForDay(String value, String dateString) {
        Calendar c = dateCache.get(dateString);
        if (c == null) {
            c = new GregorianCalendar(1970, 0, 1, 0, 0, 0);
            c.setTimeZone(SystemTime.UTC_TIMEZONE);
            int year = Integer.parseInt(value.substring(0, 4));
            int month = Integer.parseInt(value.substring(4, 6));
            int day = Integer.parseInt(value.substring(6, 8));
            c.set(year, month - 1, day);
            dateCache.put(dateString, c);
        }
        return c;
    }

    private static void verifyFormat(String value) throws FieldConvertException {
        String type = "timestamp";
        if (value.length() != 17 && value.length() != 21) {
            throwFieldConvertException(value, type);
        }
        assertDigitSequence(value, 0, 8, type);
        assertSeparator(value, 8, '-', type);
        assertDigitSequence(value, 9, 11, type);
        assertSeparator(value, 11, ':', type);
        assertDigitSequence(value, 12, 14, type);
        assertSeparator(value, 14, ':', type);
        assertDigitSequence(value, 15, 17, type);
        if (value.length() == 21) {
            assertSeparator(value, 17, '.', type);
            assertDigitSequence(value, 18, 21, type);
        } else if (value.length() != 17) {
            throwFieldConvertException(value, type);
        }
    }
}