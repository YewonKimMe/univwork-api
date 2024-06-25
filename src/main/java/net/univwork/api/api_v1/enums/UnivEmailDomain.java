package net.univwork.api.api_v1.enums;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.exception.UnivEmailNotFountException;

@Slf4j
@Getter
public enum UnivEmailDomain {
    GNU("gnu.ac.kr", "경상국립대학교(본교) 학부");

    private final String domain;

    private final String univName;

    UnivEmailDomain(String domain, String univName) {
        this.domain = domain;
        this.univName = univName;
    }

    public static String checkDomainFromString(String univDomainParam) {
        for (UnivEmailDomain emailDomain : values()) {
            String domain = emailDomain.getDomain();
            log.debug("emailDomain.getDomain={}, univDomainParam={}", emailDomain.getDomain(), univDomainParam);
            log.debug("equals?={}", domain.equals(univDomainParam));
            if (domain.equals(univDomainParam)) {
                return emailDomain.getUnivName();
            }
        }
        throw new UnivEmailNotFountException("존재하지 않는 학교 도메인 입니다.");
    }
}
