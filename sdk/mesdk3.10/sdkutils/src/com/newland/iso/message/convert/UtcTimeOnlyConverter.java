package com.newland.iso.message.convert;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Convert between a time and a String.
 */
public class UtcTimeOnlyConverter extends AbstractDateTimeConverter {
    // SimpleDateFormats are not thread safe. A thread local is being
    // used to maintain high concurrency among multiple session threads
    private static ThreadLocal<UtcTimeOnlyConverter> utcTimeConverter = new ThreadLocal<UtcTimeOnlyConverter>();
    private DateFormat utcTimeFormat = createDateFormat("HH:mm:ss");
    private DateFormat utcTimeFormatMillis = createDateFormat("HH:mm:ss.SSS");

    /**
     * Convert a time (represented as a Date) to a String (HH:MM:SS or HH:MM:SS.SSS)
     * @param d the date with the time to convert
     * @param includeMilliseconds controls whether milliseconds are included in the result
     * @return a String representing the time.
     */
    public static String convert(Date d, boolean includeMilliseconds) {
        return getFormatter(includeMilliseconds).format(d);
    }

    private static DateFormat getFormatter(boolean includeMillis) {
        UtcTimeOnlyConverter converter = utcTimeConverter.get();
        if (converter == null) {
            converter = new UtcTimeOnlyConverter();
            utcTimeConverter.set(converter);
        }
        return includeMillis ? converter.utcTimeFormatMillis : converter.utcTimeFormat;
    }

    /**
     * Convert between a String and a time
     * @param value the string to parse
     * @return a date object representing the time
     * @throws FieldConvertError raised for invalid time string
     */
    public static Date convert(String value) throws FieldConvertException {
        Date d = null;
        try {
            d = getFormatter(value.length() == 12).parse(value);
        } catch (ParseException e) {
            throwFieldConvertException(value, "time");
        }
        return d;
    }

}