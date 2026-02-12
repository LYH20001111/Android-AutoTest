package com.newland.iso.message.convert;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Convert between a date and a String
 */
public class UtcDateOnlyConverter extends AbstractDateTimeConverter {
    // SimpleDateFormats are not thread safe. A thread local is being
    // used to maintain high concurrency among multiple session threads
    private static ThreadLocal<UtcDateOnlyConverter> utcDateConverter = new ThreadLocal<UtcDateOnlyConverter>();
    private DateFormat dateFormat = createDateFormat("yyyyMMdd");

    /**
     * Convert a date to a String ("YYYYMMDD")
     * @param d the date to convert
     * @return the formatted date
     */
    public static String convert(Date d) {
        return getFormatter().format(d);
    }

    private static DateFormat getFormatter() {
        UtcDateOnlyConverter converter = utcDateConverter.get();
        if (converter == null) {
            converter = new UtcDateOnlyConverter();
            utcDateConverter.set(converter);
        }
        return converter.dateFormat;
    }

    /**
     * Convert between a String and a date
     * @param value the String to convert
     * @return the parsed Date
     * @throws FieldConvertError raised for an invalid date string.
     */
    public static Date convert(String value) throws FieldConvertException {
        Date d = null;
        String type = "date";
        assertLength(value, 8, type);
        assertDigitSequence(value, 0, 8, type);
        try {
            d = getFormatter().parse(value);
        } catch (ParseException e) {
            throwFieldConvertException(value, type);
        }
        return d;
    }

}