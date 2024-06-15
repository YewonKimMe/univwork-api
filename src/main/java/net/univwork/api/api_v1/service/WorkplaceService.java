package net.univwork.api.api_v1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.CommentDto;
import net.univwork.api.api_v1.domain.dto.CommentFormDto;
import net.univwork.api.api_v1.domain.entity.Workplace;
import net.univwork.api.api_v1.domain.entity.WorkplaceComment;
import net.univwork.api.api_v1.enums.SortOption;
import net.univwork.api.api_v1.enums.WorkplaceType;
import net.univwork.api.api_v1.repository.WorkplaceRepository;
import net.univwork.api.api_v1.tool.UUIDConverter;
import net.univwork.api.api_v1.tool.UserInputXSSGuard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class WorkplaceService {

    private final WorkplaceRepository repository;

    private final UserInputXSSGuard xssGuard;

    private final PasswordEncoder passwordEncoder;

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
    public Page<CommentDto> getWorkplaceComments(final Long univCode, final Long workplaceCode, final int pageNumber, final int pageLimit) {

        // pageable 객체 생성
        Pageable pageable = PageRequest.of(pageNumber, pageLimit);

        // WorkplaceComment 페이지를 가져옴
        Page<WorkplaceComment> workplaceComments = repository.getWorkplaceComments(pageable, univCode, workplaceCode);

        // Dto로 매핑
        List<CommentDto> commentDtoList = workplaceComments.getContent().stream()
                .map(CommentDto::new)
                .collect(Collectors.toList());
        // 새 페이지로 반환
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
    public CommentDto saveWorkplaceComment(CommentFormDto commentFormDto, final Long univCode, final Long workplaceCode, final String userIp, final String userCookieUuid) {

        // 근로지 우선 획득(학교 이름 필요)
        Workplace findWorkplace = getWorkplace(univCode, workplaceCode);

        // commentUUID를 바이트배열로 변경
        byte[] commentUuidByte = UUIDConverter.convertUuidToBinary16();

        // userCookie uuid를 바이트배열로 변경
        byte[] userCookieByte = UUIDConverter.convertUuidStringToBinary16(userCookieUuid);

        // 비밀번호 해싱
        String hashedPw = passwordEncoder.encode(commentFormDto.getPassword());

        // WorkplaceComment 생성
        WorkplaceComment workplaceComment = WorkplaceComment.builder()
                .univCode(univCode)
                .workplaceCode(workplaceCode)
                .workplaceName(findWorkplace.getWorkplaceName())
                .univName(findWorkplace.getUnivName())
                .comment_uuid(commentUuidByte)
                .comment(xssGuard.process(commentFormDto.getComment()))
                .nickname(commentFormDto.getNickname())
                .password(hashedPw)
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .deleteFlag(false)
                .reportFlag(false)
                .userIp(userIp)
                .author(userCookieByte)
                .build();

        // commnent 저장
        WorkplaceComment comment = repository.saveWorkplaceComment(workplaceComment);

        // CommentDto로 변환해서 반환
        return new CommentDto(comment); // 나중에 MapStruct 쓰자
    }
}
