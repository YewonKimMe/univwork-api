package net.univwork.api.api_v1.enums;

import lombok.Getter;
import net.univwork.api.api_v1.exception.UnivEmailNotFountException;

@Getter
public enum UnivEmailDomain {
    GNU("gun.ac.kr", "경상국립대학교");

    private final String domain;

    private final String univName;

    UnivEmailDomain(String domain, String univName) {
        this.domain = domain;
        this.univName = univName;
    }

    public static String checkDomainFromString(String univDomainParam) {
        for (UnivEmailDomain emailDomain : values()) {
            if (emailDomain.getDomain().equals(univDomainParam)) {
                return emailDomain.getDomain();
            }
        }
        throw new UnivEmailNotFountException("존재하지 않는 학교 도메인 입니다.");
    }
}
