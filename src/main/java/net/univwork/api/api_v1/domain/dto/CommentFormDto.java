package net.univwork.api.api_v1.domain.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CommentFormDto {

    @NotNull(message = "댓글은 NULL 일 수 없습니다.")
    @Size(max = 1000, message = "댓글은 최대 1000자 까지 입력 가능합니다.")
    private String comment;

    private Long univCode;

    private Long workplaceCode;

    @Min(value = 0, message = "평점은 0 보다 낮을 수 없습니다.")
    @Max(value = 5, message = "평점은 5 보다 높을 수 없습니다.")
    private Double rating;
}
