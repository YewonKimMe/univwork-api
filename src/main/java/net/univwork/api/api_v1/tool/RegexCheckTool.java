package net.univwork.api.api_v1.tool;

import java.util.regex.Pattern;

public class RegexCheckTool {

    public static boolean emailPatternCheck(String input) {
        // 이메일 패턴 정의
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        // 패턴 컴파일
        Pattern pattern = Pattern.compile(emailRegex);

        // input이  null이면 false 반환
        if (input == null) {
            return false;
        }

        // 패턴에 맞는지 검사
        return pattern.matcher(input).matches();
    }

    public static boolean commentCookiePatternCheck(String input) {
        String regex = "^(\\d+:\\d+;)*$";
        Pattern pattern = Pattern.compile(regex);

        return pattern.matcher(input).matches();
    }
}
