package org.fsg1.fmms.backend.util;

/**
 * Utility class to escape certain characters to not make them break LaTeX generation.
 */
public final class StringEscaper {
    private StringEscaper(){

    }

    /**
     * @param stringToEscape String to escape.
     * @return Escaped string.
     */
    public static String escapeString(final String stringToEscape){
        return stringToEscape
                .replace("\\n", "\\newline")
                .replace("&", "\\&")
                .replace("%", "\\%");
    }
}
