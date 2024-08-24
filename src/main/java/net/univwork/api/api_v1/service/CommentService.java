package net.univwork.api.api_v1.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.CommentReportDto;
import net.univwork.api.api_v1.domain.entity.CommentLikeLog;
import net.univwork.api.api_v1.domain.entity.ReportedComment;
import net.univwork.api.api_v1.domain.entity.WorkplaceComment;
import net.univwork.api.api_v1.enums.CookieName;
import net.univwork.api.api_v1.enums.ReportReason;
import net.univwork.api.api_v1.exception.AlreadyReportedException;
import net.univwork.api.api_v1.exception.DuplicationException;
import net.univwork.api.api_v1.exception.NoAuthenticationException;
import net.univwork.api.api_v1.repository.CommentRepository;
import net.univwork.api.api_v1.repository.ReportedCommentRepository;
import net.univwork.api.api_v1.tool.CookieUtils;
import net.univwork.api.api_v1.tool.IpTool;
import net.univwork.api.api_v1.tool.UUIDConverter;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    private final ReportedCommentRepository reportedCommentRepository;

    public void reportComment(CommentReportDto reportDto, HttpServletRequest request, Authentication authentication) {

        String reportUserIp = IpTool.getIpAddr(request); // 신고 유저 IP
        if (reportUserIp.contains(",")) {
            reportUserIp = reportUserIp.split(",")[0];
        }
        String reportUserId = null;

        String userCookie = CookieUtils.getUserCookie(request, CookieName.USER_COOKIE);

        if (null != authentication) { // 로그인된 회원일 경우, ID 획득
            reportUserId = authentication.getName();
        } else { // 비회원인 경우,
            reportUserId = userCookie;
            if (reportUserId == null) {
                log.error("신고 권한이 존재하지 않습니다");
                throw new NoAuthenticationException("인증 정보가 없어 신고할 수 없습니다.");
            }
        }

        ReportReason reportReason = ReportReason.fromValue(reportDto.getReason()); // 신고 사유 적합성 검증

        byte[] commentUuidBytes = UUIDConverter.convertUuidStringToBinary16(reportDto.getCommentUuid()); // 댓글 UUID Bytes

        Optional<WorkplaceComment> findCommentOpt = commentRepository.findWorkplaceCommentByCommentUuid(commentUuidBytes); // 댓글 Optional 획득

        if (findCommentOpt.isEmpty()) { // 댓글이 존재하지 않는 경우
            log.error("해당 댓글이 존재하지 않습니다, Param UUID = {}", reportDto.getCommentUuid());
            throw new IllegalArgumentException("잘못된 댓글 코드입니다.");
        }

        WorkplaceComment workplaceComment = findCommentOpt.get(); // Entity 획득

        if (workplaceComment.isReportFlag()) { // 이미 신고된 댓글인 경우
            throw new AlreadyReportedException("이미 신고된 댓글입니다.");
        }
        workplaceComment.setReportFlag(true); // 신고된 댓글이 아닌 경우, 신고 상태로 전환
        log.info("근로지 댓글이 신고되었습니다. UUID: {}, TIME: {}", reportDto.getCommentUuid(), new Timestamp(System.currentTimeMillis()));


        ReportedComment reportedCommentResult = ReportedComment.builder() // 신고 결과
                .commentUuid(workplaceComment.getCommentUuid())
                .reportedUserId(workplaceComment.getUserId())
                .reportedUserIp(workplaceComment.getUserIp())
                .comment(workplaceComment.getComment())
                .reportUserId(reportUserId)
                .reportUserIp(reportUserIp)
                .reason(reportReason.getReason())
                .time(new Timestamp(System.currentTimeMillis()))
                .inProgress(true)
                .build();

        reportedCommentRepository.save(reportedCommentResult); // 신고 결과 저장
    }

    public void likeComment(Long commentCode, HttpServletRequest request) {

        String ipAddr = IpTool.getIpAddr(request);
        if (ipAddr.contains(",")) {
            ipAddr = ipAddr.split(",")[0];
        }
        log.info("[좋아요 요청], 댓글 코드: {}, ip: {}", commentCode, ipAddr);
        // 중복 좋아요 감지 로직 - ip
        if (commentRepository.checkDuplicatedLikeToIp(commentCode, ipAddr)) {
            throw new DuplicationException("이미 좋아요를 누르셨습니다.");
        }
        // 좋아요 업데이트
        commentRepository.likeComment(commentCode);

        // 로그 기록
        CommentLikeLog commentLikeLog = new CommentLikeLog(commentCode, ipAddr);
        commentRepository.saveCommentLog(commentLikeLog);

        log.info("[좋아요 업데이트], 댓글 코드: {}, ip: {}", commentCode, ipAddr);
    }
}
