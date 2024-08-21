package net.univwork.api.api_v1.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.CommentDto;
import net.univwork.api.api_v1.domain.dto.CommentFormDto;
import net.univwork.api.api_v1.domain.dto.UserDetailDto;
import net.univwork.api.api_v1.domain.dto.WorkplaceRatingDto;
import net.univwork.api.api_v1.domain.entity.University;
import net.univwork.api.api_v1.domain.entity.Workplace;
import net.univwork.api.api_v1.domain.entity.WorkplaceComment;
import net.univwork.api.api_v1.enums.CookieName;
import net.univwork.api.api_v1.enums.SortOption;
import net.univwork.api.api_v1.enums.WorkplaceType;
import net.univwork.api.api_v1.exception.DomainNotMatchException;
import net.univwork.api.api_v1.exception.DuplicationException;
import net.univwork.api.api_v1.exception.NoAuthenticationException;
import net.univwork.api.api_v1.exception.NoCookieValueException;
import net.univwork.api.api_v1.repository.WorkplaceRepository;
import net.univwork.api.api_v1.tool.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class WorkplaceService {

    private final WorkplaceRepository repository;

    private final UserInputXSSGuard xssGuard;

    private final UserService userService;

    private final UnivService univService;

    /**
     * 근로지 정보를 획득하는 메소드
     * @param univCode 학교 코드
     * @param workplaceCode 근로지코드
     * @since 1.0.0
     * @return workplace 객체
     * @see net.univwork.api.api_v1.repository.UnivRepository#getWorkplaces(Pageable, Long, String, WorkplaceType, SortOption)
     * */
    public Workplace getWorkplace(final Long univCode, final Long workplaceCode) {
        return repository.getWorkplace(univCode, workplaceCode);
    }

    /**
     * 근로지 댓글 리스트를 가져오는 메소드
     * @param univCode 학교 코드
     * @param workplaceCode 근로지 코드
     * @param pageNumber 페이지 번호
     * @param pageLimit 페이지 당 요소 수
     * @since 1.0.0
     * @return CommentDto 페이지 객체
     * @see net.univwork.api.api_v1.repository.WorkplaceRepository#getWorkplaceComments(Pageable, Long, Long)
     * */
    public Page<CommentDto> getWorkplaceComments(final Long univCode, final Long workplaceCode, final int pageNumber, final int pageLimit, Authentication authentication, final boolean isAllowAnonymousUsers) {

        Pageable pageable = PageRequest.of(pageNumber, pageLimit); // pageable 객체 생성

        Page<WorkplaceComment> workplaceComments = repository.getWorkplaceComments(pageable, univCode, workplaceCode); // WorkplaceComment 페이지를 가져옴

        log.info("[근로지 댓글 조회 요청] isAllowAnonymousUsers={}", isAllowAnonymousUsers);
        List<CommentDto> commentDtoList = workplaceComments.getContent().stream()
                .map(comment -> {
                    CommentDto dto = new CommentDto(comment);
                    if (!isAllowAnonymousUsers) {
                        if (authentication == null || !authentication.isAuthenticated()) {
                            dto.setComment("로그인 후 근로지 댓글을 확인하실 수 있습니다.");
                        }
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(commentDtoList, pageable, workplaceComments.getTotalElements());
    }

    /**
     * 근로지 조회수를 업데이트 하는 메소드
     * @param univCode 학교 코드
     * @param workplaceCode 근로지 코드
     * @since 1.0.0
     * */
    public void updateView(final Long univCode, final Long workplaceCode) {
        repository.updateView(univCode, workplaceCode);
    }

    /**
     * 근로지 댓글 dto를 WorkplaceComment 객체로 바꿔서 저장하는 메소드
     * @param univCode 학교 코드
     * @param workplaceCode 근로지 코드
     * @since 1.0.0
     * @return CommentDto 페이지 객체
     * @see net.univwork.api.api_v1.repository.WorkplaceRepository#saveWorkplaceComment(WorkplaceComment) 
     * */
    public CommentDto saveWorkplaceComment(CommentFormDto commentFormDto, final Long univCode, final Long workplaceCode, final Authentication authentication, boolean isAllowAnonymousUsers, HttpServletRequest request, HttpServletResponse response) {

        String commentCheckCookieBas64 = CookieUtils.getUserCookie(request, CookieName.WORKPLACE_COMMENT_COOKIE);
        log.debug("commentCheckCookieBas64={}", commentCheckCookieBas64);
        String userCookie = CookieUtils.getUserCookie(request, CookieName.USER_COOKIE);
        String decodedCommentCheckCookie = null;

        String responseCommentCookie = null;

        StringBuilder beforeEncodedSb = new StringBuilder();

        boolean isAuthenticated = authentication != null;

        String userIpAddr = IpTool.getIpAddr(request);
        if (userIpAddr.contains(",")) {
            userIpAddr = userIpAddr.split(",")[0];
        }

        // 익명유저 댓글 등록이 허용되지 않는 경우
        if (!isAllowAnonymousUsers) {
            if (!isAuthenticated) { // 인증 정보가 없을 경우 댓글 작성 불가능
                throw new NoAuthenticationException("로그인 정보가 없습니다.");
            }

            if (this.countUserComments(authentication, univCode, workplaceCode) > 0) { // 같은 근로지 중복 작성 방지
                throw new DuplicationException("이미 같은 근로지에 작성한 댓글이 존재합니다.\n근로지 당 하나의 댓글만 작성할 수 있습니다.");
            }
        }

        // 로그인 유저가 학교, 유저 획득 후, domain 이 다르면 예외 발생 처리
        if (isAuthenticated) {
            University univ = univService.getUniv(univCode);

            UserDetailDto userDto = userService.findUserById(authentication.getName());

            if (!univ.getDomain().equals(userDto.getDomain())) {
                throw new DomainNotMatchException("재학중인 대학 근로지에만 댓글 작성이 가능합니다.");
            }
        }

        // 익명 유저 댓글 등록 허용, 로그인 유저도 확인
        // commentCheckCookie 가 null 인 경우, 오류
        if (commentCheckCookieBas64 == null || !commentCheckCookieBas64.startsWith("s")) {
            log.error("[댓글 등록 쿠키값 위변조:제거됨] ip={}, commentCheckCookieBase64={}", userIpAddr, commentCheckCookieBas64);
            throw new NoCookieValueException("잘못된 요청입니다.");
        }

        // commentCheckCookie 가 빈 문자열이 아닌 경우, 익명 유저 중복 방지 댓글 확인 시작
        // 댓글 등록 확인자는 대학코드:근로지코드;대학코드:근로지코드; 로 구성되어 있음
        if (!commentCheckCookieBas64.isEmpty() || !isAuthenticated) {
            if (commentCheckCookieBas64.charAt(0) == 's') {
                commentCheckCookieBas64 = commentCheckCookieBas64.substring(1);
            }
            byte[] decodedCommentCheckCookieBytes = Base64.getDecoder().decode(commentCheckCookieBas64);
            decodedCommentCheckCookie = new String(decodedCommentCheckCookieBytes);

            log.debug("decodedCommentCheckCookie={}", decodedCommentCheckCookie);
            if (!RegexCheckTool.commentCookiePatternCheck(decodedCommentCheckCookie)) {
                log.error("[댓글 등록 쿠키값 위변조:변조됨] ip={}, decodedCommentCheckCookie={}", userIpAddr, decodedCommentCheckCookie);
                throw new RuntimeException("쿠키값 위변조 오류 발생");
            }

            // 응답값 StringBuilder 에 기존 쿠키값 세팅
            beforeEncodedSb.append(decodedCommentCheckCookie);
            if (decodedCommentCheckCookie.contains(";")) {
                String[] cookieArray = decodedCommentCheckCookie.split(";");

                for (String cookieValue : cookieArray) {
                    if (cookieValue.isEmpty()) {
                        continue;
                    }
                    Long cookieUnivCode = Long.valueOf(cookieValue.split(":")[0]);
                    Long cookieWorkplaceCode = Long.valueOf(cookieValue.split(":")[1]);
                    if (univCode.equals(cookieUnivCode) && workplaceCode.equals(cookieWorkplaceCode)) {
                        throw new DuplicationException("이미 같은 근로지에 작성한 댓글이 존재합니다.\n근로지 당 하나의 댓글만 작성할 수 있습니다.");
                    }
                }
            }
        }


        // 근로지 우선 획득(학교 이름 필요)
        Workplace findWorkplace = getWorkplace(univCode, workplaceCode);

        // commentUUID를 바이트배열로 변경
        byte[] commentUuidByte = UUIDConverter.convertUuidToBinary16();

        log.debug("comment dto={}", commentFormDto);
        // WorkplaceComment 생성
        WorkplaceComment workplaceComment = WorkplaceComment.builder()
                .univCode(univCode)
                .workplaceCode(workplaceCode)
                .workplaceName(findWorkplace.getWorkplaceName())
                .univName(findWorkplace.getUnivName())
                .commentUuid(commentUuidByte)
                .comment(xssGuard.process(commentFormDto.getComment()))
                .userId(isAuthenticated ? authentication.getName() : userCookie)
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .deleteFlag(false)
                .reportFlag(false)
                .userIp(userIpAddr)
                .rating(commentFormDto.getRating())
                .build();

        // commnent 저장
        WorkplaceComment comment = repository.saveWorkplaceComment(workplaceComment);
        log.debug("saved comment={}", comment);

        // 댓글 갯수 update
        findWorkplace.setCommentNum(findWorkplace.getCommentNum() + 1);

        // workplace rating update
        this.updateWorkplaceRating(univCode, workplaceCode, findWorkplace);

        // 댓글 등록 완료 후 univCode, workplaceCode 을 cookie 에 세팅 절차
        beforeEncodedSb.append(univCode).append(":").append(workplaceCode).append(";");

        byte[] afterCookieByte = beforeEncodedSb.toString().getBytes();
        responseCommentCookie = "s" + Base64.getEncoder().encodeToString(afterCookieByte);
        log.debug("responseCommentCookie={}", responseCommentCookie);
        Cookie cookie = new Cookie(CookieName.WORKPLACE_COMMENT_COOKIE.getCookieName(), responseCommentCookie);
        cookie.setMaxAge((int) TimeUnit.DAYS.toSeconds(14));
        cookie.setPath("/");
        response.addCookie(cookie);

        // CommentDto로 변환해서 반환
        return new CommentDto(comment); // 나중에 MapStruct 쓰자
    }

    public int countUserComments(Authentication authentication, final Long univCode, final Long workplaceCode) {
        String userId = authentication.getName();
        return repository.countUserComments(userId, univCode, workplaceCode);
    }

    // count comment rating
    public int countCommentRating(Long univCode, Long workplaceCode) {
        return repository.countCommentRating(univCode, workplaceCode);
    }

    // sum comment rating
    public Double sumCommentRating(Long univCode, Long workplaceCode) {
        return repository.sumCommentRating(univCode, workplaceCode);
    }

    // calculate rating average
    public WorkplaceRatingDto calculateRatingResult(Long univCode, Long workplaceCode) {

        Double sumRating = this.sumCommentRating(univCode, workplaceCode);

        if (sumRating == null) {
            return new WorkplaceRatingDto(0.0, 0);
        }

        int ratingCount = this.countCommentRating(univCode, workplaceCode);
        double ratingAverage = Math.round((sumRating / ratingCount) * 100) / 100.0;

        return new WorkplaceRatingDto(ratingAverage, ratingCount);
    }

    private void updateWorkplaceRating(final Long univCode, final Long workplaceCode, Workplace findWorkplace) {
        Double calculatedAveragedRating = this.calculateRatingResult(univCode, workplaceCode).getAverage(); // 댓글 저장 후 평균평점 계산

        findWorkplace.setRating(calculatedAveragedRating); // rating update

        log.debug("findWorkplace before={}", findWorkplace);
        log.debug("ratingAvg={}", calculatedAveragedRating);
        log.debug("findWorkplace after={}", findWorkplace);
    }
}
