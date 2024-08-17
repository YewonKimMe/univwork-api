package net.univwork.api.api_v1.tool;

import java.util.regex.Pattern;

public class RegexCheckTool {

    private static Pattern getPattern(String regex) {
        return Pattern.compile(regex);
    }

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

    public static boolean isValidUUIDStrings(String input) {
        String regex = "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$";
        Pattern pattern = getPattern(regex);

        return pattern.matcher(input).matches();
    }
}
