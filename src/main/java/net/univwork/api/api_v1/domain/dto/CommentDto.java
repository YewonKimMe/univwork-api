package net.univwork.api.api_v1.domain.dto;

import lombok.Builder;
import lombok.Data;
import net.univwork.api.api_v1.domain.entity.WorkplaceComment;
import net.univwork.api.api_v1.tool.UUIDConverter;

import java.sql.Timestamp;

@Data
@Builder
public class CommentDto {
    private Long commentCode;

    private Long univCode;

    private Long workplaceCode;

    private String workplaceName;

    private String univName;

    private String comment_uuid; //Dto에서는 기존 entity의 byte16을 String으로 변환해서 저장

    private String comment;

    private int upvote;

    private String nickname;

    private Timestamp timestamp;

    private boolean deleteFlag;

    private boolean reportFlag;

    private String author;

    public CommentDto() {
    }

    public CommentDto(Long commentCode, Long univCode, Long workplaceCode, String workplaceName, String univName, String comment_uuid, String comment, int upvote, String nickname, Timestamp timestamp, boolean deleteFlag, boolean reportFlag, String author) {
        this.commentCode = commentCode;
        this.univCode = univCode;
        this.workplaceCode = workplaceCode;
        this.workplaceName = workplaceName;
        this.univName = univName;
        this.comment_uuid = comment_uuid;
        this.comment = comment;
        this.upvote = upvote;
        this.nickname = nickname;
        this.timestamp = timestamp;
        this.deleteFlag = deleteFlag;
        this.reportFlag = reportFlag;
        this.author = author;
    }

    public CommentDto(WorkplaceComment workplaceComment) {
        this.commentCode = workplaceComment.getCommentCode();
        this.univCode = workplaceComment.getUnivCode();
        this.workplaceCode = workplaceComment.getWorkplaceCode();
        this.workplaceName = workplaceComment.getWorkplaceName();
        this.univName = workplaceComment.getUnivName();
        this.comment_uuid = UUIDConverter.convertBinary16ToUUID(workplaceComment.getComment_uuid()).toString();
        this.comment = workplaceComment.getComment();
        this.upvote = workplaceComment.getUpvote();
        this.nickname = workplaceComment.getNickname();
        this.timestamp = workplaceComment.getTimestamp();
        this.deleteFlag = workplaceComment.isDeleteFlag();
        this.reportFlag = workplaceComment.isReportFlag();
        this.author = UUIDConverter.convertBinary16ToUUID(workplaceComment.getAuthor()).toString();
    }
}
