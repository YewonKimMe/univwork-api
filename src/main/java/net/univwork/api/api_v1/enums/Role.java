package net.univwork.api.api_v1.enums;

import lombok.Getter;

@Getter
public enum Role {

    PREFIX("ROLE_"),

    USER("USER"),

    ADMIN("ADMIN");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    public static Role fromValue(String role) {
        for (Role value : values()) {
            if (value.getRole().equals(role)) {
                return value;
            }
        }
        throw new IllegalArgumentException("잘못된 Role 문자열 입니다.");
    }
}
