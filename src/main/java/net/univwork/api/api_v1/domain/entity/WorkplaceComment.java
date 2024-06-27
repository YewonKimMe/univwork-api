package net.univwork.api.api_v1.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@Table(name = "workplace_comment")
@AllArgsConstructor
@NoArgsConstructor
@ToString
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

    @Column(name = "user_id")
    private String userId;

    @Column
    private Timestamp timestamp;

    @Column(name = "is_deleted")
    private boolean deleteFlag;

    @Column(name = "is_reported")
    private boolean reportFlag;

    @Column
    private String userIp;

}
