package it.pagopa.atmlayer.wf.task.util.securestring;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * <b>Description:</b><br>
 * The class <code>SecureString</code> represents a {@link String} object which contains XML/JSON data in text format.
 * The aim of this class is to obscure sensitive data exposed by the text.
 * A collection/array of {@link String} objects can be passed as argument to the constructors of this class:
 * <ul>
 * <li>{@link SecureString#SecureString(String, String...)}</li>
 * <li>{@link SecureString#SecureString(String, java.util.List) secureString(String, List)}</li>
 * <li>{@link SecureString#SecureString(String, java.util.Set) secureString(String, Set)}</li>
 * </ul>
 * This list will be used to match sensitive data keywords inside the original text of the string.
 * Two methods exist for the  retrieval of the text of the string:
 * <ul>
 * <li>{@link #toClearString()} : Returns the original text string without any modifications</li>
 * <li>{@link #toString()} : Returns the obfuscated text string without any sensitive data exposed</li>
 * </ul>
 * <br>
 * <b>Please note:</b><br>
 * If one wants to unmarshal a SecureString object, the returned {@link String} object by the {@link #toClearString()} method must be used. 
 * @author Francesco Rizzi -- Auriga S.p.A.
 */
public abstract class SecureString implements Serializable {

    private static final long serialVersionUID = 1L;

    private String originalString;

    private String obscuredString;

    private Set<String> secureData;

    private static char[] regexSpecialChars = new char[18];
    static {

        regexSpecialChars[0] = '<';
        regexSpecialChars[1] = '(';
        regexSpecialChars[2] = '[';
        regexSpecialChars[3] = '{';
        regexSpecialChars[4] = '^';
        regexSpecialChars[5] = '-';
        regexSpecialChars[6] = '=';
        regexSpecialChars[7] = '!';
        regexSpecialChars[8] = '|';
        regexSpecialChars[9] = ']';
        regexSpecialChars[10] = '}';
        regexSpecialChars[11] = ')';
        regexSpecialChars[12] = '?';
        regexSpecialChars[13] = '*';
        regexSpecialChars[14] = '+';
        regexSpecialChars[15] = '.';
        regexSpecialChars[16] = '>';
        regexSpecialChars[17] = '/';
    }

    protected static final String OBSCURED = "**OBSCURED**";

    /**
     * <b>Description:</b><br>
     * Creates a {@link SecureString} object.<br>
     * The provided {@link Collection} object containing the secure data information is converted into a new {@link Set} object. 
     *
     * @param originalString : The original {@link String} object from which secure data must be obscured.
     * @param secureData : The {@link java.util.List} containing all the secure data for this {@link SecureString}
     */
    public SecureString(String originalString, Collection<String> secureData) {

        this.originalString = originalString;
        this.secureData = new HashSet<>();

        setSecureData(secureData);
    }

    /**
     * <b>Description:</b><br>
     * Creates a {@link SecureString} object.
     *
     * @param originalString : The original {@link String} object from which secure data must be obscured.
     * @param secureData : The {@link String} array containing all the secure data for this {@link SecureString}
     */
    public SecureString(String originalString, String... secureData) {

        this.originalString = originalString;
        this.secureData = new HashSet<>();
        obscuredString = null;

        setSecureData(secureData);
    }

    /**
     * <b>Description:</b><br>
     * Creates a {@link SecureString} object.<br>
     *
     */
    public SecureString() {

        this.secureData = new HashSet<>();
    }

    /**
     * <b>Description:</b><br>
     * Obscures the string associated with this {@link SecureString}.
     *
     */
    @SuppressWarnings("hiding")
    protected abstract String obscure(String originalString, Collection<String> secureData);

    /**
     * <b>Description:</b><br>
     * Sets the provided {@link String} instance as the string to obscure.
     *
     * @param originalString The {@link String} instance to be obscured.
     */
    public void setOriginalString(String originalString) {

        this.originalString = originalString;
        this.obscuredString = null;
    }

    /**
     * <b>Description:</b><br>
     * Sets the provided {@link String} instance as the string to obscure.
     *
     * @param originalString The {@link String} instance to be obscured.
     * @param secureData The {@link Collection} of {@link String} instances containing secure data to be obscured.
     */
    public void setOriginalString(String originalString, Collection<String> secureData) {

        this.originalString = originalString;
        this.obscuredString = null;
        setSecureData(secureData);
    }

    /**
     * <b>Description:</b><br>
     * Sets the provided {@link String} instance as the string to obscure.
     *
     * @param originalString The {@link String} instance to be obscured.
     * @param secureData The array of {@link String} instances containing secure data to be obscured.
     */
    public void setOriginalString(String originalString, String... secureData) {

        this.originalString = originalString;
        this.obscuredString = null;
        setSecureData(secureData);
    }

    /**
     * <b>Description:</b><br>
     * Returns a new {@link Collection} object containing all the secure data managed by this {@link SecureString}.<br>
     * Changes to the returned collection will not affect this {@link SecureString}'s secure data collection. 
     *
     * @return A new {@link Collection} object containing all the secure data managed by this {@link SecureString}.
     */
    public Collection<String> getSecureData() {
        return new HashSet<>(secureData);
    }

    /**
     * <b>Description:</b><br>
     * Sets the provided {@link Collection} instance as the one to use when obscuring secure data.
     *
     * @param secureData The {@link Collection} of {@link String} instances containing secure data to be obscured.
     */
    public void setSecureData(Collection<String> secureData) {

        this.obscuredString = null;
        if (secureData != null && !secureData.isEmpty()) {

            for (String s : secureData) {

                String checkedSecureData = checkSecureData(s);
                if (checkedSecureData != null) {

                    this.secureData.add(checkedSecureData);
                }
            }
        } else {

            this.secureData.clear();
        }
    }

    /**
     * <b>Description:</b><br>
     * Checks if the {@link String} instance provided can be accepted by this {@link SecureString}.<br>
     * If the returned value is != null, it will be added to the secure data set. It is skipped otherwise.<br>
     * The returned value can be the same this method receives as input argument, or a modified one in case it is needed.<br>
     * <b>Please note:</b><br>
     * When overriding this method, one should always call the superclass implementation first. 
     *
     * @param secureDataString The {@link String} instance representing a single secure data.
     * @return A {@link String} representing the secure data {@link String} to add between the already available ones. If null, nothing is added.
     */
    protected String checkSecureData(String secureDataString) {

        String checkedSecureData = null;
        if (secureDataString != null && !secureDataString.trim().isEmpty()) {

            checkedSecureData = secureDataString;
        }

        return checkedSecureData;
    }

    /**
     * <b>Description:</b><br>
     * Sets the provided {@link Collection} instance as the one to use when obscuring secure data.
     *
     * @param secureData The {@link Collection} of {@link String} instances containing secure data to be obscured.
     */
    public final void setSecureData(String... secureData) {

        setSecureData(Arrays.asList(secureData));
    }

    /**
     * <b>Description:</b><br>
     * Returns the obscured {@link String} object.
     */
    @Override
    public String toString() {

        if (obscuredString == null && originalString != null && !originalString.isEmpty() && !secureData.isEmpty()) {

            obscuredString = obscure(originalString, secureData);

            if (obscuredString == null) {

                obscuredString = "";
            }
        } else if (obscuredString == null) {
            obscuredString = originalString;
        }

        return obscuredString;
    }

    /**
     * <b>Description:</b><br>
     * Escapes all special regex characters from the provided string
     *
     * @param string : The string to escape in case of special regex characters.
     * @return The escaped string.
     */
    protected static String escapeRegex(String string) {

        String escapedString = string;

        if (escapedString != null && !escapedString.isEmpty()) {

            escapedString = Matcher.quoteReplacement(escapedString);

            for (int counter = 0; counter < regexSpecialChars.length; counter++) {

                escapedString = escapedString.replaceAll("\\" + regexSpecialChars[counter],
                        "\\\\" + regexSpecialChars[counter]);
            }
        } else {

            escapedString = "";
        }
        return escapedString;
    }

    /**
     * <b>Description:</b><br>
     * Returns the original {@link String} object associated with this {@link SecureString}.<br>
     * The returned string has no data obscured.
     *
     * @return The {@link String} object with no data obscured.
     */
    public String toClearString() {

        return originalString;
    }

    /**
     * <b>Description:</b><br>
     * Returns true if this {@link SecureString} object has no {@link String} associated with it, false otherwise.
     *
     * @return true if this {@link SecureString} object has no {@link String} associated with it, false otherwise
     */
    public boolean isEmpty() {

        boolean isEmpty = true;

        if (originalString != null) {

            isEmpty = originalString.isEmpty();
        }

        return isEmpty;
    }

    /**
     * <b>Description:</b><br>
     * Returns a {@link SecureString} instance based on the type of the provided string.<br>
     * The returned object can handle the specific string provided, based on its type automatically detected (XML, JSON...).<br> 
     * If no {@link SecureString} implementation can be found, the provided instance is returned as implementation and, if this is null to,
     * a {@link KeyValueSecureString} is used with " " as key-value separator and "<code>(?:\.|,|;| )</code>" as entry separator.
     *
     * @param originalString The original string to obscure
     * @param secureData The {@link Collection} object contining the list of secure data to obfuscate.
     * @param defaultSecureString The default implementation to use in case no other implementations are found for the provided input string. If null, a {@link KeyValueSecureString} is used with " " as key-value separator and "<code>(?:\.|,|;| )</code>" as entry separator.
     * @return The {@link Class} object from which {@link SecureString} objects associated with the provided string type can be instantiated.<br>
     */
    public static SecureString getInstance(String originalString, Collection<String> secureData,
            SecureString defaultSecureString) {

        SecureString returnValue = new UnobfuscatedSecureString(originalString);

        if (originalString != null && !originalString.isEmpty()) {

            if (originalString.startsWith("{") || originalString.startsWith("[")) {
                returnValue = new JSONSecureString(originalString, secureData);
            } else if (originalString.startsWith("http://") || originalString.startsWith("https://")) {
                returnValue = new HttpUrlSecureString(originalString, secureData);
            } else if (defaultSecureString != null) {
                returnValue = defaultSecureString;
            }
        }
        return returnValue;
    }

    /**
     * <b>Description:</b><br>
     * Returns a {@link SecureString} instance based on the type of the provided string.<br>
     * The returned object can handle the specific string provided, based on its type automatically detected (XML, JSON...) 
     * If no {@link SecureString} implementation can be found a {@link KeyValueSecureString} is used with " " as key-value separator and "<code>(?:\.|,|;| )</code>" as entry separator.
     * @param originalString : The original string to obscure
     * @return The {@link Class} object from which {@link SecureString} objects associated with the provided string type can be instantiated.<br>
     */
    public static SecureString getInstance(String originalString, Collection<String> secureData) {

        return getInstance(originalString, secureData, null);
    }

    /**
     * <b>Description:</b><br>
     * Returns a {@link SecureString} instance based on the type of the provided string.<br>
     * The returned object can handle the specific string provided, based on its type automatically detected (XML, JSON...).<br> 
     * If no {@link SecureString} implementation can be found, the provided instance is returned as implementation and, if this is null to,
     * a {@link KeyValueSecureString} is used with " " as key-value separator and "<code>(?:\.|,|;| )</code>" as entry separator.
     * @param originalString The original string to obscure
     * @param defaultSecureString The default implementation to use in case no other implementations are found for the provided input string. 
     * If null, a {@link KeyValueSecureString} is used with " " as key-value separator and "<code>(?:\.|,|;| )</code>" as entry separator.
     * @param secureData The {@link Collection} object contining the list of secure data to obfuscate.
     * @return The {@link Class} object from which {@link SecureString} objects associated with the provided string type can be instantiated.<br>
     */
    public static SecureString getInstance(String originalString, SecureString defaultSecureString,
            String... secureData) {

        return getInstance(originalString, Arrays.asList(secureData), defaultSecureString);
    }

    /**
     * <b>Description:</b><br>
     * Returns a {@link SecureString} instance based on the type of the provided string.<br>
     * The returned {@link SecureString} object will have the provided secure data array set.
     * If no {@link SecureString} implementation can be found a {@link KeyValueSecureString} is used with " " as key-value separator and "<code>(?:\.|,|;| )</code>" as entry separator.
     * @param originalString : The original string to be obscured.
     * @param secureData : The array of {@link String} containing all the secure data to obscure. 
     * @return The {@link SecureString} object.
     */
    public static SecureString getInstance(String originalString, String... secureData) {

        return getInstance(originalString, Arrays.asList(secureData));
    }
}
