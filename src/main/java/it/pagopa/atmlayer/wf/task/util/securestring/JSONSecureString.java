package it.pagopa.atmlayer.wf.task.util.securestring;

import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>Description:</b><br>
 * This class represents a specific {@link SecureString} which contains JSON data in text format.
 * @author Francesco Rizzi -- Auriga S.p.A.
 */
public class JSONSecureString extends SecureString {

    private static final long serialVersionUID = 1L;

    /**
     * <b>Description:</b><br>
     * Creates a new {@link JSONSecureString}.
     *
     * @param originalString : The original string to obscure.
     * @param secureData : The secure data {@link Collection} object.
     */
    public JSONSecureString(String originalString, Collection<String> secureData) {

        super(originalString, secureData);
    }

    /**
     * <b>Description:</b><br>
     * Creates a new {@link JSONSecureString}.
     *
     * @param originalString : The original string to obscure.
     * @param secureData : The secure data {@link String} array.
     */
    public JSONSecureString(String originalString, String... secureData) {

        super(originalString, secureData);
    }

    /**
     * <b>Description:</b><br>
     * Creates a new {@link JSONSecureString}.
     */
    public JSONSecureString() {
        super();
    }

    /**
     * 
     * @see util.SecureString#obscure(java.lang.String, java.util.Collection)
     */
    @Override
    protected String obscure(String originalString, Collection<String> secureData) {

        String obscuredString = originalString;

        Pattern p = Pattern.compile(buildJSONRegex(secureData));
        Matcher m = p.matcher(obscuredString);

        int cursorIndex = 0;
        while (m.find(cursorIndex)) {

            String group1 = m.group(1);
            boolean skip = mustSkip(group1);

            if (!skip) {

                char[] chars = getChars(group1);
                boolean checkBounds = mustCheckBounds(group1);

                int currentIndex = m.end();
                if (checkBounds && chars != null) {

                    currentIndex = move(obscuredString, currentIndex, chars[0], chars[1]);

                } else {

                    currentIndex = m.end(1);
                }

                obscuredString = obscuredString.substring(0, m.start(1)) + "\"" + OBSCURED + "\""
                        + obscuredString.substring(currentIndex);
                cursorIndex = m.start(1) + ("\"" + OBSCURED + "\"").length();
                m = p.matcher(obscuredString);
            } else {

                cursorIndex = m.end(1);
            }
        }

        return obscuredString;
    }

    /**
     * <b>Description:</b><br>
     * Returns true if <code>catchedGroup</code> is:
     * <ul>
     * <li>!= "true", and</li>
     * <li>!= "false", and</li>
     * <li>is not a number</li>
     * </ul>
     *
     * @param catchedGroup : The {@link String} object representing the first catched group of the regular expression 
     * returned by {@link #buildJSONRegex(Collection)}
     * @return true if the above conditions are satisfied, false otherwise.
     */
    private boolean mustCheckBounds(String group1) {
        boolean checkBounds = true;

        if ("true".equals(group1) || "false".equals(group1) || group1.matches("^\\-?\\d+(?:\\.?\\d+$|$)")) {

            checkBounds = false;
        }
        return checkBounds;
    }

    /**
     * <b>Description:</b><br>
     * Returns true if the provided {@link String} is null or == "null"
     *
     * @param catchedGroup : The {@link String} object representing the first catched group of the regular expression 
     * returned by {@link #buildJSONRegex(Collection)}
     * @return true if the provided {@link String} is null or == "null", false otherwise.
     */
    private boolean mustSkip(String catchedGroup) {

        boolean mustSkip = false;

        if (catchedGroup == null || catchedGroup.equals("null")) {

            mustSkip = true;
        }

        return mustSkip;
    }

    /**
     * <b>Description:</b><br>
     * Returns a two chars array containing:
     * <ul>
     * <li>index 0: The opening character of a JSON element (can be ' <b>[</b> ',' <b>{</b> ', ' <b>"</b> ')</li>
     * <li>index 1: The closing character of a JSON element (can be ' <b>]</b> ',' <b>}</b> ', ' <b>"</b> ')</li>
     * </ul>
     * The returned array values depends on the provided string argument:
     * <ul>
     * <li>String == ' <b>[</b> ' : opening char = ' <b>[</b> ', closing char = ' <b>]</b> '</li>
     * <li>String == ' <b>{</b> ' : opening char = ' <b>{</b> ', closing char = ' <b>}</b> '</li>
     * <li>String == ' <b>"</b> ' : opening char = ' <b>"</b> ', closing char = ' <b>"</b> '</li>
     * </ul>
     * @param group1 : The first catched group of the regular expression string returned by {@link #buildJSONRegex(Collection)} 
     * containing the opening character for a specific json element value. Allowed values are: ' <b>[</b> ', ' <b>{</b> ', ' <b>"</b> '.
     * @return The two chars array, or null if the provided string is not equal to any of the allowed values.
     */
    private char[] getChars(String group1) {

        char[] chars = null;
        if (group1.equals("[")) {

            chars = new char[] {
                    '[',
                    ']'
            };

        } else if (group1.equals("{")) {

            chars = new char[] {
                    '{',
                    '}'
            };

        } else if (group1.equals("\"")) {

            chars = new char[] {
                    '\"',
                    '\"'
            };
        }

        return chars;
    }

    /**
     * <b>Description:</b><br>
     * Moves along the provided string and hides secure data.<br>
     * Returned int value specifies the position from which obfuscation process must continue.
     *
     * @param obscuredString : The string which is being obscured
     * @param lastCurrentIndex : The last current index before this method executes any operation
     * @param openingChar : The opening char to search for the next JSON element to analyze.
     * @param closingChar : The closing char to search for the next JSON element to analyze.
     * @return The updated index from which string obfuscation process must continue.
     */
    private int move(String obscuredString, int lastCurrentIndex, char openingChar, char closingChar) {

        int currentIndex = lastCurrentIndex;

        LinkedList<Character> stack = new LinkedList<>();

        stack.addLast(Character.valueOf(openingChar));

        while (!stack.isEmpty()) {

            int closingCharIndex = obscuredString.indexOf(closingChar, currentIndex);
            int openingCharIndex = obscuredString.indexOf(openingChar, currentIndex);

            if (openingCharIndex != -1 && openingCharIndex < closingCharIndex) {

                stack.addFirst(Character.valueOf(openingChar));
                currentIndex = openingCharIndex + 1;
            } else {

                stack.removeFirst();
                currentIndex = closingCharIndex + 1;
            }
        }

        return currentIndex;
    }

    /**
     * <b>Description:</b><br>
     * Returns the {@link String} object representing a regular expression for JSON strings.<br>
     * The returned regular expression is used to find all JSON elements.<br>
     * The returned regular expression is not case sensitive.
     *
     * @param secureData : The {@link Collection} of {@link String} objects containing all the secure data, which will be present in the returned regular expression.
     * @return The regular expression
     */
    private String buildJSONRegex(Collection<String> secureData) {

        StringBuilder regexBuilder = new StringBuilder(150);
        regexBuilder.append("(?i)\"");

        int size = secureData.size();
        int counter = 0;
        for (String s : secureData) {

            if (s != null) {

                if (counter == 0) {

                    regexBuilder.append("(?:");
                }

                regexBuilder.append(escapeRegex(s));

                if (counter == size - 1) {

                    regexBuilder.append(")");
                } else {

                    regexBuilder.append("|");
                }
            }
            counter++;
        }

        regexBuilder.append("\":[\\w\\W\\s\\S\\r\\n]*?((?:\\[|\\{|\"|\\-?\\d+(?:\\.?\\d+|)|true|false|null))");
        return regexBuilder.toString();
    }
}
