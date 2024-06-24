package net.univwork.api.api_v1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.SignUpEmailDto;
import net.univwork.api.api_v1.domain.dto.SignUpFormDto;
import net.univwork.api.api_v1.domain.dto.SignUpIdDto;
import net.univwork.api.api_v1.domain.entity.User;
import net.univwork.api.api_v1.enums.UnivEmailDomain;
import net.univwork.api.api_v1.exception.EmailAlreadyExistException;
import net.univwork.api.api_v1.exception.UserAlreadyExistException;
import net.univwork.api.api_v1.repository.jpa.JpaUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class SingUpService {

    private final EmailService emailService;

    private final JpaUserRepository userRepository;

    @Value("email.verify.path.host")
    private String host;

    @Transactional
    public void createUser(SignUpFormDto formDto) {

        // 유저 엔티티 생성

        // 유저 등록

        // uuid 생성

        // 이메일 발송
    }

    public String checkValidUnivEmailAddr(SignUpEmailDto emailDto) {

        String email = emailDto.getEmail();

        // 이메일 도메인 획득
        String domain = email.split("@")[1];
        log.debug("domain={}", domain);

        // 학교 메일 유효 여부 확인, 학교명 반환
        return UnivEmailDomain.checkDomainFromString(domain);
    }

    public void checkDuplicateEmail(SignUpEmailDto emailDto) {

        String email = emailDto.getEmail();

        // 유저 저장소 에서 이메일 중복 체크
        Optional<User> findUser = userRepository.findUserByEmail(email);
        if (findUser.isPresent()) {
            throw new EmailAlreadyExistException("이미 사용중인 이메일 입니다.");
        }
    }

    public void checkDuplicatedId(SignUpIdDto idDto) {

        String id = idDto.getId();

        // 유저 저장소 에서 아이디 중복 체크
        Optional<User> findUser = userRepository.findUserByUserId(id);
        if (findUser.isPresent()) {
            throw new UserAlreadyExistException("이미 사용중인 아이디 입니다.");
        }
    }

}
