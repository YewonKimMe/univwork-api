package net.univwork.api.api_v1.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@Table(name = "workplace_comment")
@Entity
public class WorkplaceComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentCode;

    @Column
    private Long univCode;

    @Column
    private Long workplaceCode;

    @Column
    private String workplaceName;

    @Column
    private String univName;

    @Column
    private byte[] comment_uuid; //UUID 가 생성 후 객체에 String type 으로 저장되고 DB에는 16진수 Binary로 저장

    @Column
    private String comment;

    @Column
    private int upvote;

    @Column
    private String nickname;

    @Column
    private String password;

    @Column
    private Timestamp timestamp;

    @Column(name = "is_deleted")
    private boolean deleteFlag;

    @Column(name = "is_reported")
    private boolean reportFlag;

    @Column
    private String userIp;

    @Column
    private byte[] author;

    public WorkplaceComment() {
    }

    public WorkplaceComment(Long commentCode, Long univCode, Long workplaceCode, String workplaceName, String univName, byte[] comment_uuid, String comment, int upvote, String nickname, String password, Timestamp timestamp, boolean deleteFlag, boolean reportFlag, String userIp, byte[] author) {
        this.commentCode = commentCode;
        this.univCode = univCode;
        this.workplaceCode = workplaceCode;
        this.workplaceName = workplaceName;
        this.univName = univName;
        this.comment_uuid = comment_uuid;
        this.comment = comment;
        this.upvote = upvote;
        this.nickname = nickname;
        this.password = password;
        this.timestamp = timestamp;
        this.deleteFlag = deleteFlag;
        this.reportFlag = reportFlag;
        this.userIp = userIp;
        this.author = author;
    }
}
