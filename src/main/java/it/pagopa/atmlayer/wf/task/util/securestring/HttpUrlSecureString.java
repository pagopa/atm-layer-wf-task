package it.pagopa.atmlayer.wf.task.util.securestring;

import java.util.Collection;

/**
 * <b>Description:</b><br>
 * A <code>HttpUrlSecureString</code> is a specific {@link KeyValueSecureString} which can obfuscate data in a key-value manner by using two kinds of separators:
 * <ul>
 *  <li><b>KeyValueSeparator: </b>{@link #KEY_VALUE_SEPARATOR}</li>
 *  <li><b>EntrySeparator: </b>{@link #ENTRY_SEPARATOR}</li>
 * </ul>
 * This {@link SecureString} can be used when secure data must be obscured from a {@link String} representing a generic HTTP url.
 *  
 * @author Francesco Rizzi -- Auriga S.p.A.
 */
public class HttpUrlSecureString extends KeyValueSecureString {

    private static final long serialVersionUID = 1L;

    private static final String KEY_VALUE_SEPARATOR = "(?:\\/|=)";

    private static final String ENTRY_SEPARATOR = "(?:\\/|&|\\?)";

    /**
     * <b>Description:</b><br>
     * Creates a new {@link HttpUrlSecureString}.
     *
     */
    public HttpUrlSecureString() {
        super(null, null, KEY_VALUE_SEPARATOR, ENTRY_SEPARATOR);
    }

    /**
     * <b>Description:</b><br>
     * Creates a new {@link HttpUrlSecureString}.
     *
     * @param originalString The original {@link String} instance to be obscured.
     * @param secureData The {@link Collection} of secure data strings to be obscured.
     */
    public HttpUrlSecureString(String originalString, Collection<String> secureData) {
        super(originalString, secureData, KEY_VALUE_SEPARATOR, ENTRY_SEPARATOR);
    }

    /**
     * <b>Description:</b><br>
     * Creates a new {@link HttpUrlSecureString}.
     *
     * @param originalString The original {@link String} instance to be obscured.
     * @param secureData The array of secure data strings to be obscured.
     */
    public HttpUrlSecureString(String originalString, String... secureData) {
        super(originalString, KEY_VALUE_SEPARATOR, ENTRY_SEPARATOR, secureData);
    }
}
