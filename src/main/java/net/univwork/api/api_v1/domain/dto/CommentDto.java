package net.univwork.api.api_v1.domain.dto;

import lombok.*;
import net.univwork.api.api_v1.domain.entity.WorkplaceComment;
import net.univwork.api.api_v1.tool.UUIDConverter;

import java.sql.Timestamp;

@Getter
@ToString
public class CommentDto {
    private Long commentCode;

    private Long univCode;

    private Long workplaceCode;

    private String workplaceName;

    private String univName;

    private String comment_uuid; //Dto에서는 기존 entity의 byte16을 String으로 변환해서 저장

    private String comment;

    private int upvote;

    private Timestamp timestamp;

    private boolean deleteFlag;

    private boolean reportFlag;

    public CommentDto(WorkplaceComment workplaceComment) {
        this.commentCode = workplaceComment.getCommentCode();
        this.univCode = workplaceComment.getUnivCode();
        this.workplaceCode = workplaceComment.getWorkplaceCode();
        this.workplaceName = workplaceComment.getWorkplaceName();
        this.univName = workplaceComment.getUnivName();
        this.comment_uuid = UUIDConverter.convertBinary16ToUUID(workplaceComment.getComment_uuid()).toString();
        this.comment = workplaceComment.getComment();
        this.upvote = workplaceComment.getUpvote();
        this.timestamp = workplaceComment.getTimestamp();
        this.deleteFlag = workplaceComment.isDeleteFlag();
        this.reportFlag = workplaceComment.isReportFlag();
    }
}
