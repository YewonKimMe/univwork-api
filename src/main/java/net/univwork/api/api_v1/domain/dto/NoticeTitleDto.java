package net.univwork.api.api_v1.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    private int hits;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private Timestamp noticeTimestamp;

    private boolean isFixed;
}
