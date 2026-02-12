package com.newland.iso.message.convert;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts between a double and a String.
 */
public class DoubleConverter {

    private static final Pattern decimalPattern = Pattern.compile("-?\\d*(\\.\\d*)?");
    private static ThreadLocal<DecimalFormat[]> threadDecimalFormats = new ThreadLocal<DecimalFormat[]>();

    /**
     * Converts a double to a string with no padding.
     * @param d the double to convert
     * @return the formatted String representing the double.
     * @see #convert(double, int)
     */
    public static String convert(double d) {
        return convert(d, 0);
    }

    static DecimalFormat getDecimalFormat(int padding) {
        if (padding > 14) {
            // FieldConvertError not supported in setDouble methods on Message
            throw new RuntimeException("maximum padding of 14 zeroes is supported: " + padding);
        }
        DecimalFormat[] decimalFormats = threadDecimalFormats.get();
        if (decimalFormats == null) {
            decimalFormats = new DecimalFormat[14];
            threadDecimalFormats.set(decimalFormats);
        }
        DecimalFormat f = decimalFormats[padding];
        if (f == null) {
            StringBuffer buffer = new StringBuffer("0.");
            for (int i = 0; i < padding; i++) {
                buffer.append('0');
            }
            for (int i = padding; i < 14; i++) {
                buffer.append('#');
            }
            f = new DecimalFormat(buffer.toString());
            f.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
            decimalFormats[padding] = f;
        }
        return f;
    }


    /**
     * add by chenwy 20111208
     * @param length
     * @param padding
     * @return
     */
    static DecimalFormat getDecimalFormat(int length, int padding) {
        if (length > 12) {
            // FieldConvertError not supported in setDouble methods on Message
            throw new RuntimeException("maximum Length is supported: 12,but actually " + length);
        }

        if (padding > 14) {
            // FieldConvertError not supported in setDouble methods on Message
            throw new RuntimeException("maximum padding of 14 zeroes is supported: 14,but actually " + padding);
        }

        if ((length + padding) > 14) {
            throw new RuntimeException("maximum tatal of length+padding is supported:14,but actually " + (length + padding));
        }

        DecimalFormat f = null;
        if (f == null) {

            StringBuffer buffer = new StringBuffer("");
            for (int i = length - 1; i < 12; i++) {
                buffer.append('#');
            }

            for (int i = 0; i < length; i++) {
                buffer.append('0');
            }

            if (length >= 1) {
                buffer.append('.');
            } else {
                buffer.append("0.");

            }

            for (int i = 0; i < padding; i++) {
                buffer.append('0');
            }
            for (int i = padding; i < 14; i++) {
                buffer.append('#');
            }

            f = new DecimalFormat(buffer.toString());
            f.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));

        }
        return f;
    }

    /**
     * Converts a double to a string with padding.
     * @param d the double to convert
     * @param padding the number of zeros to add to end of the formatted double
     * @return the formatted String representing the double.
     */
    public static String convert(double d, int padding) {
        return getDecimalFormat(padding).format(d);
    }

    /**
     * Convert a String value to a double.
     * @param value the String value to convert
     * @return the parsed double
     * @throws FieldConvertError if the String is not a valid double pattern.
     */
    public static double convert(String value) throws FieldConvertException {
        try {
            Matcher matcher = decimalPattern.matcher(value);
            if (!matcher.matches()) {
                throw new NumberFormatException();
            }
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new FieldConvertException("invalid double value: " + value);
        }
    }
}
