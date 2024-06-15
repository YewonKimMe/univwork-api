package net.univwork.api.api_v1.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum WorkplaceType {

    IN("in"),
    OUT("out"),
    ALL("all");

    private final String workplaceType;

    private String getWorkplaceType() {
        return workplaceType;
    }

    public static WorkplaceType fromValue(String param) {
        for (WorkplaceType value : values()) {
            if (param == null) {
                return ALL;
            }
            if (value.getWorkplaceType().equals(param)) {
                return value;
            }
        }
        throw new IllegalArgumentException("unexpected work type: " + param);
    }
}
