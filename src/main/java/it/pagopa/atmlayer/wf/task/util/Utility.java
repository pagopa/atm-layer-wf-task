package it.pagopa.atmlayer.wf.task.util;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.pagopa.atmlayer.wf.task.bean.Device;
import it.pagopa.atmlayer.wf.task.bean.State;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Utility {
    private static ObjectMapper om = new ObjectMapper();

    private static final String CDN_GET_FILE = "CDN.getResource";
    
    /*
     * Caratteri di escape standard HTML
     */
    public final static HashMap<String, String> ESCAPE_CHARACTER = new HashMap<>();

    static {
        ESCAPE_CHARACTER.put("&#192;", "À");
        ESCAPE_CHARACTER.put("&#193;", "Á");
        ESCAPE_CHARACTER.put("&#200;", "È");
        ESCAPE_CHARACTER.put("&#201;", "É");
        ESCAPE_CHARACTER.put("&#204;", "Ì");
        ESCAPE_CHARACTER.put("&#205;", "Í");
        ESCAPE_CHARACTER.put("&#210;", "Ò");
        ESCAPE_CHARACTER.put("&#211;", "Ó");
        ESCAPE_CHARACTER.put("&#217;", "Ù");
        ESCAPE_CHARACTER.put("&#218;", "Ú");
        ESCAPE_CHARACTER.put("&#224;", "à");
        ESCAPE_CHARACTER.put("&#225;", "á");
        ESCAPE_CHARACTER.put("&#232;", "è");
        ESCAPE_CHARACTER.put("&#233;", "é");
        ESCAPE_CHARACTER.put("&#236;", "ì");
        ESCAPE_CHARACTER.put("&#237;", "í");
        ESCAPE_CHARACTER.put("&#242;", "ò");
        ESCAPE_CHARACTER.put("&#243;", "ó");
        ESCAPE_CHARACTER.put("&#249;", "ù");
        ESCAPE_CHARACTER.put("&#250;", "ú");
        ESCAPE_CHARACTER.put("&#8364;", "€");
    }

    /**
     * Converts an object to a JSON representation.
     *
     * This method serializes an object into its JSON representation using the
     * Jackson ObjectMapper.
     * The resulting JSON string represents the provided object's state.
     * The method returns the JSON string or null if an error occurs during the
     * conversion.
     *
     * @param object The object to be converted to a JSON string.
     * @return A JSON string representing the provided object, or null if an error
     *         occurs.
     */
    public static String getJson(Object object) {
        String result = null;
        try {
            result = om.writer().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error(" - ERROR", e);
        }
        return result;
    }

    public static byte[] setTransactionIdInJson(byte[] entity, String transactionId) {
        String result = null;
        try {
            JsonNode jn = om.readTree(new String(entity));
            ((ObjectNode) jn).put("transactionId", transactionId);
            result = om.writeValueAsString(jn);

        } catch (JsonProcessingException e) {
            log.error(" - ERROR", e);
            return entity;
        }
        return result.getBytes();
    }

    /**
     * Converts an object to a JSON representation.
     *
     * This method serializes an object into its JSON representation using the
     * Jackson ObjectMapper.
     * The resulting JSON string represents the provided object's state.
     * The method returns the JSON string or null if an error occurs during the
     * conversion.
     *
     * @param object The object to be converted to a JSON string.
     * @return A JSON string representing the provided object, or null if an error
     *         occurs.
     */
    public static String getObscuredJson(Object object) {
        String result = null;

        try {
            result = om.writerWithView(Object.class).writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error(" - ERROR", e);
        }
        return result;
    }

    public static <T> T getObject(String json, Class<T> clazz) {
        T result = null;
        try {
            result = om.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Error deserializing: {}", e);
        }
        return result;
    }

    /**
     * Generates a unique transaction ID for a device.
     *
     * This method creates a unique transaction ID using the UUID (Universally
     * Unique Identifier) generator.
     * The generated transaction ID is intended to uniquely identify a transaction
     * associated with a specific device.
     *
     * @param device The device for which the transaction ID is being generated.
     * @return A unique transaction ID in UUID format.
     */
    public static String generateTransactionId(State state) {
        Device device = state.getDevice();
        return (device.getBankId() + "-" + (device.getBranchId() != null ? device.getBranchId() : "") + "-" + (device.getCode() != null ? device.getCode() : "") + "-" + (device.getTerminalId() != null ? device.getTerminalId() : "") + "-" + (device.getOpTimestamp().getTime()) + "-" + UUID.randomUUID().toString()).substring(0, Constants.TRANSACTION_ID_LENGTH);
    }

    /**
     * Finds and extracts substrings from an input string using a regular
     * expression.
     *
     * This method searches the provided input string for substrings that match the
     * specified regular expression and returns them as a list of strings.
     * It iterates through the input string, finds all matching substrings, and
     * collects them into a list.
     * The regular expression defines the pattern to search for within the input
     * string.
     *
     * @param inputString The input string in which to search for matching
     *                    substrings.
     * @param regex       The regular expression pattern used to identify matching
     *                    substrings.
     * @return A list of strings representing the substrings found in the input
     *         string that match the given regular expression.
     */
    public static Set<String> findStringsByGroup(String inputString, String regex) {
        Set<String> groups = new HashSet<>();

        /* Set<String> forObjectsAttributes = extractObjects(regex);
        log.debug("For object attributes {} :", forObjectsAttributes); */

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputString);
        while (matcher.find()) {
            int groupCount = matcher.groupCount();
            for (int counter = 1; counter <= groupCount; counter++) {
                groups.add(matcher.group(counter));
            }
        }

        /* groups = forObjectsAttributes.isEmpty() ? groups
                : groups.stream()
                        .filter(groupsElement -> forObjectsAttributes.stream()
                                .noneMatch(groupsElement::startsWith))
                        .collect(Collectors.toSet()); */

        return groups;
    }

    /**
    
     * Finds and extracts matched substrings from an input string using a regular
     * expression.
     *
     * This method searches the provided input string for substrings that match the
     * specified regular expression and returns them as a list of strings.
     * It iterates through the input string, identifies all matching substrings
     * based on the regular expression, and adds them to a list.
     *
     * @param inputString The input string in which to search for matching
     *                    substrings.
     * @param regex       The regular expression pattern used to identify matching
     *                    substrings.
     * @return A list of strings representing the substrings found in the input
     *         string that match the given regular expression.
     */
    public static List<String> findStrings(String inputString, String regex) {
        List<String> matches = new LinkedList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputString);
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        return matches;
    }

    /**
     * Retrieves the IDs of HTML elements with a specific tag in the given HTML
     * string.
     *
     * This method parses the provided HTML string and identifies elements with a
     * specified HTML tag.
     * It then extracts and returns the IDs of those elements as a list of strings.
     * The method is useful for collecting the IDs of HTML elements, such as
     * buttons, with a particular tag in the HTML content.
     *
     * @param htmlString The HTML string to search for elements with the specified
     *                   tag.
     * @param tag        The HTML tag used to identify the elements.
     * @return A list of strings containing the IDs of HTML elements with the
     *         specified tag in the HTML string.
     */
    public static Set<String> getIdOfTag(String htmlString, String tag) {
        Set<String> idList = new HashSet<>();

        Document doc = Jsoup.parse(htmlString);

        doc.getElementsByTag(tag).stream().forEach(e -> idList.add(e.id()));

        return idList;
    }

    /**
     *
     * @param htmlString The HTML string to search for elements with the specified
     *                   tag.
     * @param tag        The HTML tag used to identify the elements.
     * @return A list of strings containing the IDs of HTML elements with the
     *         specified tag in the HTML string.
     */
    public static Set<String> getForVar(String htmlString) {
        Set<String> idList = new HashSet<>();

        Document doc = Jsoup.parse(htmlString);

        doc.getElementsByTag("for").stream().forEach(e -> idList.add(e.attr("list")));

        return idList;
    }

    /**
     * Retrieves an InputStream for a specified file from a remote location.
     *
     * This method constructs a URL using the provided 'path'.
     * It then attempts to open an InputStream from the constructed URL, allowing
     * access to the content of the remote file.
     *
     * @param path The name of the file to retrieve.
     * @return An InputStream that provides access to the content of the specified
     *         file.
     * @throws IOException
     */
    public static InputStream getFileFromCdn(String path) throws IOException {

        InputStream ioStream = null;
        log.info("Getting file [{}]", path);
        long start = System.currentTimeMillis();
        ioStream = new URL(path).openStream();
        long stop = System.currentTimeMillis();
        log.info(" {} - Elapsed time [ms] = {}", CDN_GET_FILE , stop - start);
        return ioStream;
    }

    /**
     * Logs the elapsed time occurred for the processing.
     * 
     * @param label - the function to display in the log
     * @param start - the start time, when the execution is started
     * @param stop  - the stop time, when the execution is finished
     */
    public static void logElapsedTime(String label, long start, long stop) {
        log.info(" - {} - Elapsed time [ms] = {}", label, stop - start);
    }

    /**
     * <p>Test input String value to check if it's null or empty</p>
     *
     * @param value - value to be checked
     * @return true if the input value is null or empty, false otherwise
     */
    public static boolean nullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * <p>Test input Collection value to check if it's null or empty</p>
     *
     * @param value - value to be checked
     * @return true if the input value is null or empty, false otherwise
     */
    public static boolean nullOrEmpty(Collection<?> value) {
        return value == null || value.isEmpty();
    }

    public static byte[] encryptRSA(byte[] dataToEncrypt, RSAPublicKey encryptionKey) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {

        Cipher cipher;
        cipher = Cipher.getInstance(Constants.RSA_ALGORITHM_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);

        return cipher.doFinal(dataToEncrypt);
    }

    public static RSAPublicKey buildRSAPublicKey(String algorithm, byte[] keyModulus) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory factory = KeyFactory.getInstance(algorithm);
        RSAPublicKeySpec publicSpec = new RSAPublicKeySpec(new BigInteger(format(keyModulus), 16), BigInteger.valueOf(65537));
        return (RSAPublicKey) factory.generatePublic(publicSpec);
    }

    /**
     * 
     * @param stream
     * @param separator
     * @return
     */
    public static String format(byte[] stream) {
        if (stream != null) {
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < stream.length; i++) {
                if (stream[i] >= 0x00 && stream[i] <= 0x0F) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(((stream[i] < 0) ? (stream[i] + 256) : stream[i])));
            }
            return buf.toString().toUpperCase();
        }
        return null;
    }

    public static RSAPublicKey generateRandomRSAPublicKey() {
        log.info(" - Generating random RSA public key");
        RSAPublicKey rsaPublicKey = null;
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair kp = kpg.generateKeyPair();
            rsaPublicKey = (RSAPublicKey) kp.getPublic();
        } catch (NoSuchAlgorithmException e) {
            log.error(" - Error generating public key", e);
        }
        return rsaPublicKey;
    }

    /**
     * Escape a string.
     * @param text
     * @charactersEscapeList contains the list of characters to escape, associated to the corresponding escape sequence
     * @return
     * @author Simone Miccoli
     * 
     */
    public static String escape(String text) {
        String result = null;
        log.debug(" - Using default escape chars map");
        if (text != null) {
            result = text;
            Set<Entry<String, String>> charactersToEscape = null;
            charactersToEscape = ESCAPE_CHARACTER.entrySet();
            for (Entry<String, String> entry : charactersToEscape) {
                result = result.replaceAll(entry.getValue(), entry.getKey());
            }
        }
        return result;
    }

    public static String escape(String text, Map<String, String> escapeHtmlChars) {
        String result = null;
        if (escapeHtmlChars == null) {
            result = escape(text);
        } else {
            if (text != null) {
                result = text;
                Set<Entry<String, String>> charactersToEscape = null;
                charactersToEscape = escapeHtmlChars.entrySet();
                for (Entry<String, String> entry : charactersToEscape) {
                    result = result.replaceAll(entry.getValue(), entry.getKey());
                }
            }
        }
        return result;
    }

}
