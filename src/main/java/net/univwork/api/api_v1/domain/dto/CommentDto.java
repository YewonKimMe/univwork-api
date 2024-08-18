package net.univwork.api.api_v1.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    private String commentUuid; //Dto에서는 기존 entity의 byte16을 String으로 변환해서 저장

    private String comment;

    private int upvote;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private Timestamp timestamp;

    private Double rating;

    private boolean deleteFlag;

    private boolean reportFlag;

    public void setComment(String comment) {
        this.comment = comment;
    }

    public CommentDto(Long commentCode, Long univCode, Long workplaceCode, String workplaceName, String univName, byte[] commentUuid, String comment, int upvote, Timestamp timestamp, Double rating) {
        this.commentCode = commentCode;
        this.univCode = univCode;
        this.workplaceCode = workplaceCode;
        this.workplaceName = workplaceName;
        this.univName = univName;
        this.commentUuid = UUIDConverter.convertBinary16ToUUID(commentUuid).toString();
        this.comment = comment;
        this.upvote = upvote;
        this.timestamp = timestamp;
        this.rating = rating;
    }

    public CommentDto(WorkplaceComment workplaceComment) {
        this.commentCode = workplaceComment.getCommentCode();
        this.univCode = workplaceComment.getUnivCode();
        this.workplaceCode = workplaceComment.getWorkplaceCode();
        this.workplaceName = workplaceComment.getWorkplaceName();
        this.univName = workplaceComment.getUnivName();
        this.commentUuid = UUIDConverter.convertBinary16ToUUID(workplaceComment.getCommentUuid()).toString();
        this.comment = workplaceComment.getComment();
        this.upvote = workplaceComment.getUpvote();
        this.timestamp = workplaceComment.getTimestamp();
        this.rating = workplaceComment.getRating();
        this.deleteFlag = workplaceComment.isDeleteFlag();
        this.reportFlag = workplaceComment.isReportFlag();
    }
}
