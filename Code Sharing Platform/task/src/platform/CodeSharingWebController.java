package platform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping(value = "/code")
public class CodeSharingWebController {
    @Autowired
    CodeEntityRepository codeEntityRepository;

    @GetMapping(value = "/new")
    //public String newCode() {
    public @ResponseBody String newCode() {
        //Temporary - getting resource because unit tests are incorrect where working with freemarker
        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Create</title>\n" +
                "    <script>\n" +
                "        function send() {\n" +
                "            let object = {\n" +
                "                \"code\": document.getElementById(\"code_snippet\").value," +
                "                \"time\": parseInt(document.getElementById(\"time_restriction\").value)," +
                "                \"views\": parseInt(document.getElementById(\"views_restriction\").value)," +
                "            };\n" +
                "\n" +
                "            let json = JSON.stringify(object);\n" +
                "\n" +
                "            let xhr = new XMLHttpRequest();\n" +
                "            xhr.open(\"POST\", '/api/code/new', false)\n" +
                "            xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');\n" +
                "            xhr.send(json);\n" +
                "\n" +
                "            if (xhr.status == 200) {\n" +
                "              alert(\"Success!\");\n" +
                "            }\n" +
                "        }\n" +
                "        </script>\n" +
                "</head>\n" +
                "<body>\n" +
                "<textarea id=\"code_snippet\">// Write your code here</textarea>\n" +
                "<br>\n" +
                "<label>Times restriction:</label><input id=\"time_restriction\" type=\"text\"/>" +
                "<label>Views restriction:</label><input id=\"views_restriction\" type=\"text\"/>" +
                "<br>\n" +
                "<button id=\"send_snippet\" type=\"submit\" onclick=\"send()\">Submit</button>\n" +
                "</body>\n" +
                "</html>";
        return html;
        //
        //
        //return "newcode";
    }

    @GetMapping(value = "/{id}")
    //public String getHtmlCode(@PathVariable int id, Model model) {
    public @ResponseBody String getHtmlCode(@PathVariable String id, Model model) throws ResponseStatusException {
        CodeEntity codeEntity = codeEntityRepository.findById(id).orElse(null);
        if (codeEntity == null
                || codeEntity.isSecret()
                && (codeEntity.getInitialTime() > 0 && codeEntity.getTime() == 0
                || codeEntity.getInitialViews() > 0 && codeEntity.getViews() == 0)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Code not found");
        }
        if (codeEntity.isSecret() && codeEntity.getViews() > 0) {
            codeEntity.setViews(codeEntity.getViews() - 1);
            codeEntityRepository.save(codeEntity);
        }

        //Temporary - getting resource because unit tests are incorrect where working with freemarker
        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Code</title>\n" +
                "    <style type=\"text/css\">\n" +
                "        #load_date {color: green}\n" +
                "        #code_snippet {background: lightGray; border-style: solid}\n" +
                "    </style>\n" +
                "<link rel=\"stylesheet\"\n" +
                "       href=\"//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/styles/default.min.css\">\n" +
                "<script src=\"//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/highlight.min.js\"></script>\n" +
                "<script>hljs.initHighlightingOnLoad();</script>" +
                "</head>\n" +
                "<body>";

        if (codeEntity != null) {
            int timeLeft = codeEntity.getTime();

            //System.out.println(codeEntity.getId() + " " + codeEntity.getInitialViews());

            html +=
                    "<span id=\"load_date\">" + codeEntity.getFormattedLoadDate() + "</span>\n" +
                            (codeEntity.getInitialTime() > 0 ? "<br><label>Seconds left :</label><span id='time_restriction'>" + timeLeft + "</span>" : "") +
                            (codeEntity.getInitialViews() > 0 ? "<br><label>Views left:</label><span id='views_restriction'>" + codeEntity.getViews() + "</span>" : "") +
                            "<pre id=\"code_snippet\"><code class='language-java'>\n" +
                            codeEntity.getCode();
        }

        html +=
                "</code></pre>\n" +
                        "<br>\n" +
                        "</body>\n" +
                        "</html>";

        return html;
        //
        //

        //model.addAttribute("codes", codeEntity != null ? List.of(codeEntity) : new ArrayList<>());
        //return "showcode";
    }

    @GetMapping(value = "/latest")
    //public String getLatest(Model model) {
    public @ResponseBody String getLatest(Model model) {
        List<CodeEntity> latest = codeEntityRepository.findTop10BySecretOrderByDateDesc(false);
        //Temporary - getting resource because unit tests are incorrect where working with freemarker
        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Latest</title>\n" +
                "    <style type=\"text/css\">\n" +
                "        #load_date {color: green}\n" +
                "        #code_snippet {background: lightGray; border-style: solid}\n" +
                "    </style>\n" +
                "<link rel=\"stylesheet\"\n" +
                "       href=\"//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/styles/default.min.css\">\n" +
                "<script src=\"//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/highlight.min.js\"></script>\n" +
                "<script>hljs.initHighlightingOnLoad();</script>" +
                "</head>\n" +
                "<body>";

        for (CodeEntity codeEntity : latest) {
            int timeLeft = codeEntity.getTime();

            html +=
                    "<span id=\"load_date\">" + codeEntity.getFormattedLoadDate() + "</span>\n" +
                            "<pre id=\"code_snippet\"><code class='language-java'>\n" +
                            codeEntity.getCode();
            html += "</code></pre>\n" +
                    "<br>\n";
        }

        html +=
                "</body>\n" +
                        "</html>";

        return html;
        //
        //
        //model.addAttribute("codes", codeRepository.latest());
        //return "showcode";
    }

}
