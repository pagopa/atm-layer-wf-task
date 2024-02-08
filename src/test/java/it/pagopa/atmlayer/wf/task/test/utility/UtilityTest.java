package it.pagopa.atmlayer.wf.task.test.utility;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import it.pagopa.atmlayer.wf.task.bean.Device;
import it.pagopa.atmlayer.wf.task.bean.State;
import it.pagopa.atmlayer.wf.task.bean.enumartive.Channel;
import it.pagopa.atmlayer.wf.task.util.Utility;

@QuarkusTest
class UtilityTest {

    @Test
    void testGetJson() {
        State state = new State();
        state.setTaskId("1");
        assertEquals("{\"taskId\":\"1\"}", Utility.getJson(state));
    }

    @Test
    void testfindStrings() {
        String testTxt = "test.prova@auriga.com";
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        List<String> result = new ArrayList<>();
        result.add(testTxt);
        assertEquals(result, Utility.findStrings(testTxt, regex));

        assertEquals(new ArrayList<String>(), Utility.findStrings("test.@prova@auriga.com", regex));
    }

    @Test
    void testGetIdOfTag() {
        String htmlString = "<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n" + //
                "<head></head>\r\n" + //
                "<body>\r\n" + //
                "\t<div id=\"title_page\"><img src=\"${CDN_URL}ICON/logo-pagoPA.svg\" />Servizi Pagopa </div>\r\n" + //
                "\t<div id=\"subtitle\">MENU</div>\r\n" + //
                "\t\r\n" + //
                "\t\r\n" + //
                "\t\r\n" + //
                "\t<ul id=\"menu\">\r\n" + //
                "\t\t<li id=\"pagamentoAviso\" >\t\r\n" + //
                "\t\t\t<span>Pagamento spontaneo</span>\r\n" + //
                "\t\t\t<img src=\"${CDN_URL}ICON/logo-pagoPA-nero.svg\"/>\r\n" + //
                "\t\t</li>\r\n" + //
                "\t\t<li id=\"button02\">\r\n" + //
                "\t\t\t<span>Pagamento spontaneo 2</span>\r\n" + //
                "\t\t\t<img src=\"${CDN_URL}ICON/logo-pagoPA-nero.svg\" />\r\n" + //
                "\t\t\t<img src=\"${CDN_URL}ICON/logo-pagoPA-nero.svg\" />\r\n" + //
                "\t\t</li>\r\n" + //
                "\t</ul>\r\n" + //
                "\t\r\n" + //
                "    <button class=\"exit\" id=\"cancel\">\r\n" + //
                "      <span>Esci</span>\r\n" + //
                "    </button>\r\n" + //
                "\r\n" + //
                "\t</body>\r\n" + //
                "\t\r\n" + //
                "</html>";

        Set<String> result = new HashSet<>();
        result.add("cancel");
        assertEquals(result, Utility.getIdOfTag(htmlString, "button"));
    }

    @Test
    void testGetObject() throws ParseException {

        String json = "{\r\n" + //
                "    \"device\": {\r\n" + //
                "        \"bankId\": \"06789\",\r\n" + //
                "        \"branchId\": \"12345\",\r\n" + //
                "        \"code\": \"0001\",\r\n" + //
                "        \"terminalId\": \"64874412\",\r\n" + //
                "        \"opTimestamp\": \"2023-10-31T16:30:00\",\r\n" + //
                "        \"channel\": \"ATM\"" + "}}";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = sdf.parse("2023-10-31T16:30:00");
        State state = new State();
        Device device = Device.builder().bankId("06789").branchId("12345").channel(Channel.ATM).code("0001").terminalId("64874412").opTimestamp(date).build();
        state.setDevice(device);
        assertEquals(state, Utility.getObject(json, State.class));
    }

    @Test
    void testGetObjectKo() throws ParseException {

        String json = "{\r\n" + //
                "    \"device\": {\r\n" + //
                "        \"bankId\": \"06789\",\r\n" + //
                "        \"branchId\": \"12345\",\r\n" + //
                "        \"code\": \"0001\",\r\n" + //
                "        \"terminalId\": \"64874412\",\r\n" + //
                "        \"opTimestamp\": \"2023-10-31T16:30:00\",\r\n" + //
                "        \"channel\": \"ATM\"" + "}";

        assertEquals(null, Utility.getObject(json, State.class));
    }

    @Test
    void testNullOrEmptyStringEmpty() {
        assertEquals(true, Utility.nullOrEmpty(""));
    }

    @Test
    void testNullOrEmptyStringNull() {
        String test = null;
        assertEquals(true, Utility.nullOrEmpty(test));
    }

    @Test
    void testNullOrEmptyStringOk() {
        String test = "test";
        assertEquals(false, Utility.nullOrEmpty(test));
    }

    @Test
    void testNullOrEmptyCollectionEmpty() {
        List<String> test = List.of();
        assertEquals(true, Utility.nullOrEmpty(test));
    }

    @Test
    void testNullOrEmptyCollectionNull() {
        List<String> test = null;
        assertEquals(true, Utility.nullOrEmpty(test));
    }

    @Test
    void testNullOrEmptyCollectionOk() {
        List<String> test = List.of("Test");
        assertEquals(false, Utility.nullOrEmpty(test));
    }

    @Test
    void testFormatOk() {
        assertNotNull(Utility.format(new byte[] {
                10,
                22
        }));
    }

    @Test
    void testFormatEmpty() {
        assertNotNull(Utility.format(new byte[] {}));
    }

    @Test
    void testFormatNull() {
        assertNull(Utility.format(null));
    }

}
