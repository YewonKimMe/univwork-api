package net.univwork.api.api_v1.tool;

import lombok.Getter;

@Getter
public enum ConstNum {
    UNIVPAGE(30),
    WORKPLACEPAGE(30), // 20 -> 30, 페이지 당 리스트 요소 몇개 표시 .. 서비스 계층에서 사용
    ADMIN_COMMENT(10),
    WORKPLACECOMMENTPAGE(20),
    ADMIN_USERMANAGE(30),
    BLOCKLIMIT(5);
    private final int value;
    ConstNum(int value) {
        this.value = value;
    }
}
