package it.pagopa.atmlayer.wf.task.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
    * Converts an object to a JSON representation.
    *
    * This method serializes an object into its JSON representation using the Jackson ObjectMapper.
    * The resulting JSON string represents the provided object's state.
    * The method returns the JSON string or null if an error occurs during the conversion.
    *
    * @param object The object to be converted to a JSON string.
    * @return A JSON string representing the provided object, or null if an error occurs.
    */
    public static String getJson(Object object) {
        String result = null;
        ObjectMapper om = new ObjectMapper();
        try {
            result = om.writer().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error(" - ERROR", e);
        }
        return result;
    }

    public static byte[] setTransactionIdInJson(byte[] entity,String transactionId ) {
        String result = null;
        
        ObjectMapper om = new ObjectMapper();
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
    * This method serializes an object into its JSON representation using the Jackson ObjectMapper.
    * The resulting JSON string represents the provided object's state.
    * The method returns the JSON string or null if an error occurs during the conversion.
    *
    * @param object The object to be converted to a JSON string.
    * @return A JSON string representing the provided object, or null if an error occurs.
    */
    public static String getObscuredJson(Object object) {
        String result = null;
        ObjectMapper om = new ObjectMapper();
        try {
            result = om.writerWithView(Object.class).writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error(" - ERROR", e);
        }
        return result;
    }

    public static <T> T  getObject(String json, Class<T> clazz) {
        T result = null;
        ObjectMapper om = new ObjectMapper();
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
    * This method creates a unique transaction ID using the UUID (Universally Unique Identifier) generator.
    * The generated transaction ID is intended to uniquely identify a transaction associated with a specific device.
    *
    * @param device The device for which the transaction ID is being generated.
    * @return A unique transaction ID in UUID format.
    */
    public static String generateTransactionId(State state) {
        Device device = state.getDevice();
        return (device.getBankId()
                + "-" + (device.getBranchId() != null ? device.getBranchId() : "")
                + "-" + (device.getCode() != null ? device.getCode() : "")
                + "-" + (device.getTerminalId() != null ? device.getTerminalId() : "")
                + "-" + (device.getOpTimestamp().getTime())
                + "-" + UUID.randomUUID().toString()).substring(0, Constants.TRANSACTION_ID_LENGTH);
    }

    /**
    * Finds and extracts substrings from an input string using a regular expression.
    *
    * This method searches the provided input string for substrings that match the specified regular expression and returns them as a list of strings.
    * It iterates through the input string, finds all matching substrings, and collects them into a list.
    * The regular expression defines the pattern to search for within the input string.
    *
    * @param inputString The input string in which to search for matching substrings.
    * @param regex The regular expression pattern used to identify matching substrings.
    * @return A list of strings representing the substrings found in the input string that match the given regular expression.
    */
    public static Set<String> findStringsByGroup(String inputString, String regex) {
        Set<String> groups = new HashSet<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputString);
        while (matcher.find()) {
            int groupCount = matcher.groupCount();
            for (int counter = 1; counter <= groupCount; counter++) {
                groups.add(matcher.group(counter));
            }
        }
        return groups;
    }

    /**
    * Finds and extracts matched substrings from an input string using a regular expression.
    *
    * This method searches the provided input string for substrings that match the specified regular expression and returns them as a list of strings.
    * It iterates through the input string, identifies all matching substrings based on the regular expression, and adds them to a list.
    *
    * @param inputString The input string in which to search for matching substrings.
    * @param regex The regular expression pattern used to identify matching substrings.
    * @return A list of strings representing the substrings found in the input string that match the given regular expression.
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
    * Retrieves the IDs of HTML elements with a specific tag in the given HTML string.
    *
    * This method parses the provided HTML string and identifies elements with a specified HTML tag.
    * It then extracts and returns the IDs of those elements as a list of strings.
    * The method is useful for collecting the IDs of HTML elements, such as buttons, with a particular tag in the HTML content.
    *
    * @param htmlString The HTML string to search for elements with the specified tag.
    * @param tag The HTML tag used to identify the elements.
    * @return A list of strings containing the IDs of HTML elements with the specified tag in the HTML string.
    */
    public static Set<String> getIdOfTag(String htmlString, String tag) {
        Set<String> idList = new HashSet<>();

        Document doc = Jsoup.parse(htmlString);

        doc.getElementsByTag(tag).stream().forEach(e -> idList.add(e.id()));

        return idList;
    }

    /**
    * Retrieves an InputStream for a specified file from a remote location.
    *
    * This method constructs a URL using the provided 'path'.
    * It then attempts to open an InputStream from the constructed URL, allowing access to the content of the remote file.
    *
    * @param path The name of the file to retrieve.
    * @return An InputStream that provides access to the content of the specified file.
    * @throws IOException
    */
    public static InputStream getFileFromCdn(String path) throws IOException {

        InputStream ioStream = null;
        log.info("Getting file [{}]", path);

        ioStream = new URL(path).openStream();

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
}
