package com.newland.iso.message.convert;


/**
 * Converts between a character and a String.
 */
public class CharConverter {
    /**
     * Converts a character to a String
     * @param c the character to convert
     * @return a single character String based on the converted character
     * @see java.lang.Character#toString(char)
     */
    public static String convert(char c) {
        return Character.toString(c);
    }

    /**
     * Convert a String value to a character.
     * @param value
     * @return the converted character
     * @throws FieldConvertError if String length != 1
     */
    public static char convert(String value) throws FieldConvertException {
        if (value.length() != 1) {
            throw new FieldConvertException("invalid character value: " + value);
        }
        return value.charAt(0);
    }
}