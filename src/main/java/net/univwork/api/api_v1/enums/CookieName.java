package net.univwork.api.api_v1.enums;

import lombok.Getter;

@Getter
public enum CookieName {

    USER_COOKIE("PFUCU_4FC2127GC12K41"),

    BLOCKED_FIRST_CHECK_COOKIE("K4F3S1F4528P12MKO41ZZX0"),

    REPEAT_REQUEST("SPECEDCONNECTION"),

    FIND_PASSWORD_EMAIL("_fa_40123f84723ci"),

    INITIAL_CONNECTION_COOKIE("_target_status_connection"),

    WORKPLACE_COMMENT_COOKIE("_service_maintain_operation_value");

    private final String cookieName;

    CookieName(String value) {
        this.cookieName = value;
    }

}