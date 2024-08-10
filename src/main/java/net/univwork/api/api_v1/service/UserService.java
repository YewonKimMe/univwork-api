package net.univwork.api.api_v1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.UserDetailDto;
import net.univwork.api.api_v1.domain.entity.User;
import net.univwork.api.api_v1.enums.UnivEmailDomain;
import net.univwork.api.api_v1.exception.PasswordNotMatchException;
import net.univwork.api.api_v1.exception.UserNotExistException;
import net.univwork.api.api_v1.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserDetailDto findUserById(final String userId) {
        Optional<User> findUserOpt = userRepository.findUserByUserId(userId);
        if (findUserOpt.isEmpty()) {
            log.error("findUserById-USER ID does not exist - parameter userId={}", userId);
            throw new UserNotExistException("아이디로 검색된 유저가 존재하지 않습니다.");
        }
        User user = findUserOpt.get();

        UserDetailDto userDetailDto = UserDetailDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .univ(UnivEmailDomain.checkDomainFromString(user.getUnivDomain()))
                .createDate(user.getCreateDate().toLocalDateTime())
                .verification(user.isVerification())
                .domain(user.getUnivDomain())
                .build();
        return userDetailDto;
    }

    public User findUserByUserId(final String userId) {
        Optional<User> findUserOpt = userRepository.findUserByUserId(userId);
        if (findUserOpt.isEmpty()) {
            log.error("findUserByUserId-USER ID does not exist - parameter userId={}", userId);
            throw new UserNotExistException("아이디로 검색된 유저가 존재하지 않습니다.");
        }
        return findUserOpt.get();
    }

    public User findUserByEmail(final String userEmail) {
        Optional<User> findUserOpt = userRepository.findUserByEmail(userEmail);
        if (findUserOpt.isEmpty()) {
            log.error("USER EMAIL does not exist - parameter email={}", userEmail);
            throw new UserNotExistException("이메일으로 검색된 유저가 존재하지 않습니다.");
        }

        return findUserOpt.get();
    }

    public int updateUserPassword(final String userId, final String rawCurrentPwd, final String rawNewPwd) {

        final int sameAsExistPwdCode = -1; // 변경할 비밀번호가 기존 비밀번호와 같을 경우, 리턴

        Optional<User> findUserOpt = userRepository.findUserByUserId(userId);
        if (findUserOpt.isEmpty()) {
            log.error("USER ID does not exist - parameter userId={}", userId);
            throw new UserNotExistException("ID로 검색된 유저가 존재하지 않습니다.");
        }
        
        User user = findUserOpt.get();
        if (passwordEncoder.matches(rawNewPwd, user.getPwd())) { // 유저가 입력한 새 pwd와 DB의 기존 pwd(Hashed) 가 같으면
            log.debug("기존 비밀번호와 새 비밀번호가 일치함");
            return sameAsExistPwdCode;
        }
        
        if (passwordEncoder.matches(rawCurrentPwd, user.getPwd())) { // 유저가 입력한 기존 pwd(텍스트) 와 DB의 pwd(Hashed) 가 일치하면 변경
            log.info("User Pwd Changed - userId={}", userId);
            return userRepository.updatePassword(userId, passwordEncoder.encode(rawNewPwd));
        } else { // 유저 입력 비밀번호 != DB 유저 비밀번호
            throw new PasswordNotMatchException("현재 비밀번호가 일치하지 않습니다.");
        }
    }

    public int withdraw(final String userId, final String userInputPwd) {
        Optional<User> findUserOpt = userRepository.findUserByUserId(userId);
        if (findUserOpt.isEmpty()) {
            log.error("USER ID does not exist - parameter userId={}", userId);
            throw new UserNotExistException("ID로 검색된 유저가 존재하지 않습니다.");
        }
        User user = findUserOpt.get();
        if (passwordEncoder.matches(userInputPwd, user.getPwd())) {
            log.info("유저 계정 삭제 처리: userId={}, email={}", userId, user.getEmail());
            return userRepository.withdraw(userId);
        }
        throw new PasswordNotMatchException("비밀번호가 일치하지 않습니다.");
    }

}
