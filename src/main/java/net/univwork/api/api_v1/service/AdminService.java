package net.univwork.api.api_v1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.*;
import net.univwork.api.api_v1.domain.entity.*;
import net.univwork.api.api_v1.enums.BlockRole;
import net.univwork.api.api_v1.enums.Role;
import net.univwork.api.api_v1.exception.UnivNotFountException;
import net.univwork.api.api_v1.repository.AdminRepository;
import net.univwork.api.api_v1.repository.CommentRepository;
import net.univwork.api.api_v1.repository.ReportedCommentRepository;
import net.univwork.api.api_v1.repository.jpa.JpaUserRepository;
import net.univwork.api.api_v1.repository.jpa.JpaWorkplaceRepository;
import net.univwork.api.api_v1.tool.UUIDConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class AdminService {

    private final AdminRepository adminRepository;

    private final CommentRepository commentRepository;

    private final ReportedCommentRepository reportedCommentRepository;

    private final UnivService univService;

    private final JpaWorkplaceRepository jpaWorkplaceRepository;

    private final JpaUserRepository jpaUserRepository;

    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    private final RedisService redisService;

    // 공지사항 리스트 획득 메소드
    public Page<Notice> getNoticeList(final int pageNumber, final int pageLimit) {
        Pageable pageable = PageRequest.of(pageNumber, pageLimit);
        return adminRepository.getNoticeList(pageable);
    }

    // 공지사항 저장 메소드
    public void saveNotice(NoticeAdminDto noticeAdminDto, Authentication authentication) {
        Notice notice = Notice.builder()
                .noticeId(UUIDConverter.convertUuidStringToBinary16(UUID.randomUUID().toString()))
                .title(noticeAdminDto.getTitle())
                .classification(noticeAdminDto.getClassification())
                .author(noticeAdminDto.getAuthor())
                .hits(0)
                .noticeTimestamp(new Timestamp(System.currentTimeMillis()))
                .content(noticeAdminDto.getContent())
                .isFixed(false)
                .commentNum(0)
                .isDeleted(false)
                .build();
        adminRepository.saveNotice(notice);
        log.info("[Notice saved] Writer: {}", authentication.getName());
    }

    // 개별 공지사항 획득 메소드
    public Notice getNotice(Long no) {
        return adminRepository.getNotice(no);
    }
    public void editAndSaveNotice(NoticeAdminDto noticeAdminDto, Long no, Authentication authentication) {
        adminRepository.updateNotice(noticeAdminDto, no);
        log.info("[Notice edited] Editor: {}", authentication.getName());
    }

    // 공지사항 삭제 메소드
    public void deleteNotice(Long no, Authentication authentication) {
        // isDeleted flag = 1
        adminRepository.deleteNotice(no);
        log.info("[Notice deleted] Admin: {}", authentication.getName());
    }

    // 신고 댓글 획득 메소드
    public Page<ReportedComment> getReportedCommentList(final int pageNumber, final int pageLimit) {
        Pageable pageable = PageRequest.of(pageNumber, pageLimit);
        return adminRepository.getReportedCommentList(pageable);
    }

    // 신고된 원본 댓글 삭제(soft) 메소드
    public void deleteCommentToReported(String commentUuid) {
        byte[] uuidBytes = UUIDConverter.convertUuidStringToBinary16(commentUuid);
        Optional<WorkplaceComment> commentOpt = commentRepository.findWorkplaceCommentByCommentUuid(uuidBytes);
        if (commentOpt.isEmpty()) {
            log.debug("uuid missing, uuid={}", commentUuid);
            throw new IllegalArgumentException("해당 UUID 로 검색된 댓글이 없습니다.");
        }
        WorkplaceComment workplaceComment = commentOpt.get();
        workplaceComment.setDeleteFlag(true);

        // 신고 리스트에서 제거
        reportedCommentRepository.makeProgressOver(uuidBytes);
        log.info("[신고된 댓글 삭제] 댓글 uuid: {}", UUIDConverter.convertBinary16ToUUID(uuidBytes).toString());
    }

    public void blockUser(String userId, String commentUuidString, BlockRole blockRole) {

        byte[] uuidBytes = UUIDConverter.convertUuidStringToBinary16(commentUuidString);

        adminRepository.blockUser(userId);

        if (blockRole == BlockRole.WRITER) {
            log.info("[댓글 작성자 차단] userId: {}", userId);
            Optional<WorkplaceComment> commentOpt = commentRepository.findWorkplaceCommentByCommentUuid(uuidBytes);

            if (commentOpt.isEmpty()) {
                log.debug("uuid missing, uuid={}", commentUuidString);
                throw new IllegalArgumentException("해당 UUID 로 검색된 댓글이 없습니다.");
            }

            WorkplaceComment workplaceComment = commentOpt.get();
            workplaceComment.setDeleteFlag(true);

        } else if (blockRole == BlockRole.REPORTER) {
            log.info("[댓글 신고자 차단] userId: {}", userId);
        }

        reportedCommentRepository.makeProgressOver(uuidBytes);
    }

    public void dismissReport(String uuidString) {
        byte[] uuidBytes = UUIDConverter.convertUuidStringToBinary16(uuidString);
        reportedCommentRepository.makeProgressOver(uuidBytes);
        log.info("[신고 반려] uuid: {}", UUIDConverter.convertBinary16ToUUID(uuidBytes).toString());
    }

    public void addWorkplace(Long univCode, AddWorkplaceDto dto) {
        if (null == univService.getUniv(univCode)) {
            log.debug("[근로지 추가 중 오류] 해당 univCode 로 검색된 대학 존재 X");
            throw new UnivNotFountException("해당 univCode 로 검색된 대학교가 존재하지 않습니다\nunivCode=" + univCode);
        }
        University findUniv = univService.getUniv(univCode);
        Workplace workplace = new Workplace();
        workplace.setUnivCode(dto.getUnivCode());
        workplace.setUnivName(findUniv.getUnivName());
        workplace.setWorkplaceType(dto.getWorkplaceType());
        workplace.setWorkType(dto.getWorkType());
        workplace.setWorkplaceName(dto.getWorkplaceName());
        workplace.setWorkplaceAddress(dto.getWorkplaceAddress());
        workplace.setWorkTime(dto.getWorkTime());
        workplace.setWorkDay(dto.getWorkDay());
        workplace.setRequiredNum(dto.getRequiredNum());
        workplace.setPreferredGrade(dto.getPreferredGrade());
        workplace.setPreferredDepartment(dto.getPreferredDepartment());
        workplace.setJobDetail(dto.getJobDetail());
        workplace.setNote(dto.getNote());
        workplace.setCommentNum(0L);
        workplace.setViews(0L);

        jpaWorkplaceRepository.save(workplace);
    }

    public void addUser(SignUpFormDto signUpFormDto) {
        String hashedPw = passwordEncoder.encode(signUpFormDto.getPassword()); // 비밀번호 해싱
        String userUnivDomain = signUpFormDto.getEmail().split("@")[1]; // 이메일 도메인 획득

        // 유저 엔티티 생성
        User user = User.builder()
                .userId(signUpFormDto.getId())
                .email(signUpFormDto.getEmail())
                .pwd(hashedPw)
                .role(Role.PREFIX.getRole() + Role.USER.getRole())
                .createDate(new Timestamp(System.currentTimeMillis()))
                .verification(true)
                .univDomain(userUnivDomain)
                .authorities(new HashSet<>())
                .blockedFlag(false)
                .build();

        // Authority 생성
        Authority authority = Authority.builder()
                .name("ROLE_USER")
                .build();

        // 양방향 관계 설정
        authority.setUser(user); // user와 authority 연결
        user.getAuthorities().add(authority); // user에 authority 추가

        // 유저 등록
        jpaUserRepository.save(user); // save 후에 user 객체에 자동으로 생성된 no 값이 채워짐

        log.info("[User create - id: {}, date: {}]", signUpFormDto.getId(), new Timestamp(System.currentTimeMillis()));
    }

    public void sendVerifyEmail(SignUpEmailDto emailDto) {
        String authToken = UUID.randomUUID().toString();
        emailService.sendVerifyEmail(emailDto.getEmail(), authToken, 3);
        redisService.saveIfAbsent(authToken, emailDto.getEmail(), 3, TimeUnit.HOURS);
        log.info("[관리자-유저 재인증 메일 발송], UserEmail={} Time={}", emailDto.getEmail(), new Timestamp(System.currentTimeMillis()));
    }

    public Page<AdminAspectUserDetailDto> getUserList(int pageNumber, int pageLimit, String username) {

        Pageable pageable = PageRequest.of(pageNumber, pageLimit);

        return adminRepository.getUserList(pageable, username);
    }
}
