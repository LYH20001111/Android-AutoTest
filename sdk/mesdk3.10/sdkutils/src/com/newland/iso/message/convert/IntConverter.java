package com.newland.iso.message.convert;


/**
 * Convert between an integer and a String
 */
public final class IntConverter {
    
    /**
     * Convert and integer to a String
     * @param i the integer to convert
     * @return the String representing the integer
     * @see java.lang.Long#toString(long)
     */
    public static String convert(int i) {
        return Long.toString(i);
    }

    /**
     * Convert a String to an integer.
     * @param value the String to convert
     * @return the converted integer
     * @throws FieldConvertError raised if the String does not represent a
     * valid integer.
     * @see java.lang.Integer#parseInt(String)
     */
    public static int convert(String value) throws FieldConvertException {
        try {
            for (int i = 0; i < value.length(); i++) {
                if (!Character.isDigit(value.charAt(i)) && !(i == 0 && value.charAt(i) == '-')) {
                    throw new NumberFormatException();
                }
            }
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new FieldConvertException("invalid integral value: " + value);
        }
    }
}