package net.univwork.api.api_v1.tool;

import org.springframework.stereotype.Component;

@Component
public class UserInputXSSGuard {

    public String process(String userString) {

        // 주석 제거
        userString = userString.replaceAll("<!--(.*)-->", "");

        // 이벤트 핸들러 제거
        String[] events = new String[]{"onblur", "onchange", "onclick", "ondblclick", "onerror", "onfocus", "onkeydown", "onkeypress", "onkeyup", "onload", "onmouse", "onmouseover", "onreset", "onresize", "onselect", "onsubmit", "onunload"};
        for (String event : events) {
            userString = userString.replaceAll("(?i)(" + event + ")(.*)=", "");
        }

        // 태그 속성 제거
        userString = userString.replaceAll("(?i)(<[^>]*?)\\s+[^>]*?\\s?=\\s*('|\")(.*?)\\1", "$1");

        // 유니코드 및 16진수 이스케이프 제거
        userString = userString.replaceAll("\\\\u[0-9a-fA-F]{4}", "");
        userString = userString.replaceAll("&#x[0-9a-fA-F]{2,4};", "");

        return userString
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
//                .replaceAll("\\(", "&#40;")
//                .replaceAll("\\)", "&#41;")
                .replaceAll("'", "&#x27;")
                .replaceAll("\"", "&quot;")
//                .replaceAll("&", "&amp;")
                .replaceAll("(<script.*?>)|(</script>)", "")
                .replaceAll("(<style.*?>)|(</style>)", "")
                .replaceAll("(?:\r\n|\r|\n)", "<br>");
    }
}
