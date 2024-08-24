package net.univwork.api.api_v1.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Preview {

    private String univName;

    private String workplaceName;

    private String commentPreview;

    private Long univCode;

    private Long workplaceCode;

    private Double rating;

    private int upvote;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private Timestamp timestamp;
}
