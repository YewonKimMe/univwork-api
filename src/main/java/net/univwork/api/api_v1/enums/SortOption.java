package net.univwork.api.api_v1.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SortOption {

    UNIV_NAME_ASC("univAsc"),

    UNIV_NAME_DESC("univDesc"),

    WORKPLACE_NAME_ASC("workplaceAsc"),

    WORKPLACE_NAME_DESC("workplaceDesc"),

    WORKPLACE_VIEW_ASC("workplaceViewAsc"),

    WORKPLACE_VIEW_DESC("workplaceViewDesc"),

    WORKPLACE_COMMENT_NUM_ASC("workplaceCommentNumAsc"),

    WORKPLACE_COMMENT_NUM_DESC("workplaceCommentNumDesc");

    private final String sortOption;

    public String getValue() {
        return sortOption;
    }

    public static SortOption fromValue(String param) {
        for (SortOption option: values()) {
            if (option.getValue().equals(param)) {
                return option;
            }
        }
        throw new IllegalArgumentException("unexpected sort option: " + param);
    }
}
