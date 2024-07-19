package net.univwork.api.api_v1.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(name = "notice_id")
    private byte[] noticeId;

    @Column
    private String title;

    @Column
    private String classification;

    @Column
    private String author;

    @Column
    private int hits;

    @Column(name = "notice_timestamp")
    private Timestamp noticeTimestamp;

    @Column
    private String content;

    @Column(name = "is_fixed")
    private boolean isFixed;

    @Column(name = "comment_num")
    private int commentNum;

    @Column
    private boolean isDeleted;

    public Notice() {
    }
}
