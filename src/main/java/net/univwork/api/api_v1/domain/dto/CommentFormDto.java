package net.univwork.api.api_v1.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentFormDto {

    @NotBlank(message = "댓글은 공백을 입력하실 수 없습니다.")
    @Size(max = 1000, message = "댓글은 최대 1000자 까지 입력 가능합니다.")
    private String comment;

    private Long univCode;

    private Long workplaceCode;
}
