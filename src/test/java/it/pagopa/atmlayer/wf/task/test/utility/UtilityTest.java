package it.pagopa.atmlayer.wf.task.test.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import it.pagopa.atmlayer.wf.task.bean.State;
import it.pagopa.atmlayer.wf.task.util.Utility;

@QuarkusTest
public class UtilityTest {

    @Test
    void testGetJson() {
        State state = new State();
        state.setTaskId("1");
        assertEquals("{\"taskId\":\"1\"}", Utility.getJson(state));
    }

    @Test
    void testFindStringsByGroup() {
        String testTxt = "aaaaaaaaaaaaa${test1} prova test ${test2}";
        List<String> result = new ArrayList<>();
        result.add("test1");
        result.add("test2");
        assertEquals(result, Utility.findStringsByGroup(testTxt, "\\$\\{(.*?)\\}"));
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

        List<String> result = new ArrayList<>();
        result.add("cancel");
        assertEquals(result, Utility.getIdOfTag(htmlString, "button"));
    }

}
