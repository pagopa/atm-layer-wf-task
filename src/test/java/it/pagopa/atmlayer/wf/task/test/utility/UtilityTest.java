package it.pagopa.atmlayer.wf.task.test.utility;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
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

    @Test
    void testGenerateRSAPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        assertNotNull(Utility.buildRSAPublicKey("RSA", Base64.getDecoder().decode("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3iu1kH1foan71+X13MQ6WIRhuTw70zhtXxC5UyHGmNcDabqqrzdKovlPDZt05VuktpP+di0ZtKnwjRxzx2IUwO2s05kT8qI+acfEf4IJR3J6yCrnYmSdVtdb+Oy5VkqbUn/xVLidOED2dfMgvCobfDdiLL1dqp7Ll8i+UUvcDTvQ/c2LwSqHT5vY8n5mXWPRzHundNG8572AqI6DNQSCo3rRFtgP4vwbsYZX5+4o/Jvk4qrBALkfbq1RGmM6kVGokEG53yjlmAuDb2OEOeqYtQxFUulcVYRMZZY5ruuuOst77+U72hT1YHXA/gJexDVsetZnfzgMQUZABw+1ZjFjTwIDAQAB")));
    }

    @Test
    void testEscapeHtml() {
        String html = "<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?><!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\"><head><link rel=\"stylesheet\" href=\"css/emulatore.css\" /></head><body><img id=\"logo\" src=\"https://d2xduy7tbgu2d3.cloudfront.net/files/ICON/default_logo.svg\"/><h1>Servizi di pubblica utilit&agrave;</h1><h2>Riepilogo del pagamento</h2><table id=\"table\"><thead><tr><th><span>${company}</span></th><th class=\"right\"><span>${amount} &euro;</span></th></tr></thead><tbody><tr><td><span>Commissioni</span></td><td class=\"right\"><span>${fee} &euro;</span></td></tr></tbody><tfoot><tr><td><span class=\"large\">Totale</span></td><td class=\"right\"><span class=\"large\">${totale} &euro;</span></td></tr></tfoot></table><button class=\"negative\" data-fdk=\"S4\" id=\"back\"><span>Indietro</span></button><button class=\"negative\" id=\"exit\"><span>Esci</span></button><button class=\"positive\" data-fdk=\"S8\" id=\"confirm\"><span>Paga  ${totale} &euro;</span></button><for object=\"pulsante\" list=\"pulsanti\"><button class=\"negative\" data-fdk=\"S${pulsante.i}\" id=\"${pulsante}\"><span>${pulsante}</span><span>${pulsante.paragrafo}</span></button></for></body></html>";
        String newHtml = Utility.escape(html);
        assertFalse(Utility.ESCAPE_CHARACTER.values().stream().anyMatch(value -> newHtml.contains(value)));
    }

}
