package net.univwork.api.api_v1.enums;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public enum BlockRole {

    WRITER("writer"),
    REPORTER("reporter");
    private final String role;

    public static BlockRole fromValue(String roleString) {
        for (BlockRole role : values()) {
            if (role.role.equals(roleString)) {
                return role;
            }
        }
        log.error("올바르지 않은 role 입력이 감지되었습니다. {}", roleString);
        throw new IllegalArgumentException("잘못된 role 입니다.");
    }
}
