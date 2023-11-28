package it.pagopa.atmlayer.wf.task.util.securestring;

import java.util.Collection;
import java.util.List;

/**
 * <b>Description:</b><br>
 * An <code>UnobfuscatedSecureString</code> is a specific {@link SecureString} which provides no obfuscation.<br>
 * Instances of this class are returned by methods:
 * <ul>
 *  <li>{@link SecureString#getInstance(String, Collection)}</li>
 *  <li>{@link SecureString#getInstance(String, String...)}</li>
 * </ul>
 * When no specific {@link SecureString} class can be found between the ones available.
 * @author Francesco Rizzi -- Auriga S.p.A.
 */
public class UnobfuscatedSecureString extends SecureString {

    private static final long serialVersionUID = 1L;

    /**
     * <b>Description:</b><br>
     * Creates a new {@link UnobfuscatedSecureString}.
     *
     * @param originalString : The original string, which will not be obscured.
     */
    public UnobfuscatedSecureString(String originalString) {

        super(originalString, (List<String>) null);
    }

    /**
     * <b>Description:</b><br>
     * Creates a new {@link UnobfuscatedSecureString}.
     *
     * @param originalString : The original string, which will not be obscured.
     */
    public UnobfuscatedSecureString() {
        super();
    }

    /**
     * <b>Description:</b><br>
     * Returns the original string without any obfuscation.
     * @see util.SecureString#obscure(java.lang.String, java.util.Collection)
     */
    @Override
    protected String obscure(String originalString, Collection<String> secureData) {

        return originalString != null ? originalString : "";
    }

}
