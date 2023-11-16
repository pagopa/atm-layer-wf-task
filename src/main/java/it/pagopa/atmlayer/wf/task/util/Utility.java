package it.pagopa.atmlayer.wf.task.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    public static List<String> findStringsByGroup(String inputString, String regex) {
        List<String> groups = new LinkedList<>();
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
    public static List<String> getIdOfTag(String htmlString, String tag) {
        List<String> idList = new ArrayList<>();

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
}
