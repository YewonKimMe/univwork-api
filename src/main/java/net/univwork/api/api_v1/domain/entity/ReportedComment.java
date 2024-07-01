package net.univwork.api.api_v1.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reported_comment")
@Builder
public class ReportedComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long No;

    @Column(name = "comment_uuid")
    private byte[] commentUuid;

    @Column(name = "reported_user_id")
    private String reportedUserId;

    @Column(name = "reported_user_ip")
    private String reportedUserIp;

    private String comment;

    @Column(name = "report_user_id")
    private String reportUserId; // 익명 유저도 가능.

    @Column(name = "report_user_ip")
    private String reportUserIp;

    private String reason;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private Timestamp time;

    @Column(name = "in_progress")
    private boolean inProgress;
}
