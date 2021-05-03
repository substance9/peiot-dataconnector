package edu.uci.ics.peiot.dataconnector.wifi.util;//Got from: https://www.sourcecodeexamples.net/2019/12/java-string-trimleadingcharacter-utility-method.html

public class StringTrimUtil {

    /**
     * Trim all occurrences of the supplied leading character from the given {@code String}.
     * @param str the {@code String} to check
     * @param leadingCharacter the leading character to be trimmed
     * @return the trimmed {@code String}
     */
    public static String trimLeadingCharacter(String str, char leadingCharacter) {
        if (!hasLength(str)) {
            return str;
        }

        StringBuilder sb = new StringBuilder(str);
        while (sb.length() > 0 && sb.charAt(0) == leadingCharacter) {
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }

    /**
     * Trim all occurrences of the supplied leading character from the given {@code String}.
     * @param str the {@code String} to check
     * @param trailingCharacter the leading character to be trimmed
     * @return the trimmed {@code String}
     */
    public static String trimTrailingCharacter(String str, char trailingCharacter) {
        if (!hasLength(str)) {
            return str;
        }

        StringBuilder sb = new StringBuilder(str);
        while (sb.length() > 0 && sb.charAt(sb.length()-1) == trailingCharacter) {
            sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();
    }

    public static boolean hasLength(String str) {
        return (str != null && str.length() > 0);
    }
}