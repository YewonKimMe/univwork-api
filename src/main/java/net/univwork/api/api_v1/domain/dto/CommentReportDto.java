package net.univwork.api.api_v1.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class CommentReportDto {
    private String commentUuid;
    private String reason;
}
