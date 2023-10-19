package it.pagopa.atmlayer.wf.task.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.logging.Log;

public class Utility {

    public static String getJson(Object object) {
        String result = null;
        ObjectMapper om = new ObjectMapper();
        // (om.enable(SerializationFeature.WRAP_ROOT_VALUE);
        try {
            result = om.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            Log.error(" - ERROR", e);
        }
        return result;
    }

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

    public static List<String> findStrings(String inputString, String regex) {
        List<String> matches = new LinkedList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputString);
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        return matches;
    }

    public static List<String> getIdOfTag(String htmlString, String tag) {
        List<String> buttons = new ArrayList<>();

        Document doc = Jsoup.parse(htmlString);

        doc.getElementsByTag(tag).stream().forEach(e -> buttons.add(e.id()));

        return buttons;
    }
}
