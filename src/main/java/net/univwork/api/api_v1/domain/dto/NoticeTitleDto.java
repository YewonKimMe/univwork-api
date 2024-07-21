package net.univwork.api.api_v1.domain.dto;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class NoticeTitleDto {

    private Long no;

    private byte[] noticeId;

    private String title;

    private String classification;

    private String author;

    private Timestamp noticeTimestamp;

    private boolean isFixed;
}
