package it.pagopa.atmlayer.wf.task.util.securestring;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>Description:</b><br>
 * A <code>KeyValueSecureString</code> is a specific {@link SecureString} which can obfuscate data in a key-value manner.<br>
 * A <code>KeyValueSecureString</code> is instantiated with two specific arguments:<br>
 * <ul>
 *  <li><b>KeyValueSeparator</b> - The separator used to split each key from its respective value</li>
 *  <li><b>EntrySeparator</b> - The separator used to split each key-value entry</li>
 * </ul>
 * <b>Example:</b><br> 
 * <ul>
 * <li><b>Original String:</b> <code>key1=value1,key2=value2</code></li>
 * <li><b>Entry separator:</b> <code>,</code></li>
 * <li><b>Key-Value separator:</b> <code>=</code></li>
 * </ul>
 * When not specified, a generic <code>KeyValueSecureString</code> uses the following default separators:
 * <ul>
 *  <li><b>KeyValueSeparator: </b>{@link #DEFAULT_KEY_VALUE_SEPARATOR}</li>
 *  <li><b>EntrySeparator: </b>{@link #DEFAULT_ENTRY_SEPARATOR}</li>
 * </ul>
 * @author Francesco Rizzi -- Auriga S.p.A.
 */
public class KeyValueSecureString extends SecureString {

    public static final String DEFAULT_KEY_VALUE_SEPARATOR = "=";

    public static final String DEFAULT_ENTRY_SEPARATOR = "&";

    private static final long serialVersionUID = 1L;

    private String keyValueSeparator = null;

    private String entrySeparator = null;

    /**
     * <b>Description:</b><br>
     * Creates a new {@link KeyValueSecureString} instance.
     *
     * @param originalString The original {@link String} to be obscured
     * @param secureData The {@link Collection} of secure data containing the keys to be obscured
     * @param keyValueSeparator The specific key-value separator used
     * @param entrySeparator The specific entry separator used.
     */
    public KeyValueSecureString(String originalString, Collection<String> secureData, String keyValueSeparator,
            String entrySeparator) {
        super(originalString, secureData);
        this.keyValueSeparator = keyValueSeparator;
        this.entrySeparator = entrySeparator;
    }

    /**
     * <b>Description:</b><br>
     * Creates a new {@link KeyValueSecureString} instance.
     *
     * @param originalString The original {@link String} to be obscured
     * @param keyValueSeparator The specific key-value separator used
     * @param entrySeparator The specific entry separator used.
     * @param secureData The array of {@link String} instances containing the keys to be obscured
     */
    public KeyValueSecureString(String originalString, String keyValueSeparator, String entrySeparator,
            String... secureData) {
        super(originalString, secureData);
        this.keyValueSeparator = keyValueSeparator;
        this.entrySeparator = entrySeparator;
    }

    /**
     * <b>Description:</b><br>
     * Creates a new {@link KeyValueSecureString} instance with a default key-value separator (<code>=</code>) and entry separator (<code>&</code>).
     *
     * @param originalString The original {@link String} to be obscured
     * @param secureData The array of {@link String} instances containing the keys to be obscured
     */
    public KeyValueSecureString(String originalString, Collection<String> secureData) {

        this(originalString, secureData, DEFAULT_KEY_VALUE_SEPARATOR, DEFAULT_ENTRY_SEPARATOR);
    }

    /**
     * <b>Description:</b><br>
     * Creates a new {@link KeyValueSecureString} instance with a default key-value separator (<code>=</code>) and entry separator (<code>&</code>).
     *
     * @param originalString The original {@link String} to be obscured
     * @param secureData The array of {@link String} instances containing the keys to be obscured
     */
    public KeyValueSecureString(String originalString, String... secureData) {
        this(originalString, Arrays.asList(secureData), DEFAULT_KEY_VALUE_SEPARATOR, DEFAULT_ENTRY_SEPARATOR);
    }

    /**
     * <b>Description:</b><br>
     * Creates a new {@link KeyValueSecureString} instance with a default key-value separator (<code>=</code>) and entry separator (<code>&</code>).
     *
     */
    public KeyValueSecureString() {
        super();
        this.keyValueSeparator = DEFAULT_KEY_VALUE_SEPARATOR;
        this.entrySeparator = DEFAULT_ENTRY_SEPARATOR;
    }

    /**
     * 
     * @see com.aurigaspa.basewebapp.util.SecureString#obscure(java.lang.String, java.util.Collection)
     */
    @Override
    protected String obscure(String originalString, Collection<String> secureData) {

        String obscuredString = originalString;

        if (keyValueSeparator != null && !keyValueSeparator.isEmpty() && entrySeparator != null
                && !entrySeparator.isEmpty()) {

            Pattern p = Pattern.compile(buildRegex(secureData));
            Matcher m = p.matcher(obscuredString);

            int matcherPosition = 0;
            while (matcherPosition < obscuredString.length() && m.find(matcherPosition)) {

                /*
                 * m.group(1) = key
                 * m.group(2) = key-value separator
                 * m.group(3) = value
                 */
                obscuredString = obscuredString.substring(0, m.start(3)) + OBSCURED
                        + obscuredString.substring(m.end(3));
                matcherPosition = m.end(1) + m.group(2).length() + OBSCURED.length();
                m = p.matcher(obscuredString);
            }
        }

        return obscuredString;
    }

    /**
     * <b>Description:</b><br>
     * Returns a {@link String} representing the regex to use when searching for secure data inside the input string provided to this {@link KeyValueSecureString}
     *
     * @param secureData The {@link Collection} of secure data containing the keys to be obscured
     * @return A {@link String} representing the regex to use when searching for secure data
     */
    private String buildRegex(Collection<String> secureData) {

        StringBuilder regexBuilder = new StringBuilder();
        regexBuilder.append("(?i)([\\r\\n\\s]*?(?:");
        Iterator<String> it = secureData.iterator();

        while (it.hasNext()) {

            String currentSecureData = it.next();
            regexBuilder.append(escapeRegex(currentSecureData));
            if (it.hasNext()) {

                regexBuilder.append("|");
            }
        }
        regexBuilder.append(")[\\r\\n\\s]*?)");
        regexBuilder.append("(").append(keyValueSeparator).append(")");
        regexBuilder.append("([\\s\\S.]+?)(?:");
        regexBuilder.append(entrySeparator + "|$)");

        return regexBuilder.toString();
    }
}
