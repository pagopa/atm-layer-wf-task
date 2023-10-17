package it.pagopa.atmlayer.wf.task.util;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Utility {

    public static String getJson(Object object) {
        String result = null;
        ObjectMapper om = new ObjectMapper();
        // (om.enable(SerializationFeature.WRAP_ROOT_VALUE);
        try {
            result = om.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
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
}
