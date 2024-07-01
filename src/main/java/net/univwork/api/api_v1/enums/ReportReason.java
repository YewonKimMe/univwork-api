package net.univwork.api.api_v1.enums;

import lombok.Getter;

@Getter
public enum ReportReason {
    ABUSE("욕설"),
    SPREAD("개인정보유포"),
    DEFAMATION("명예훼손"),
    IMPERSONATION("사칭"),
    AD("광고");

    private final String reason;

    ReportReason(String reason) {
        this.reason = reason;
    }

    public static ReportReason fromValue(String value) {
        for (ReportReason reason  : values()) {
            if (reason.reason.equals(value)) {
                return reason;
            }
        }
        throw new IllegalArgumentException("신고 사유가 올바르지 않습니다.");
    }
}
