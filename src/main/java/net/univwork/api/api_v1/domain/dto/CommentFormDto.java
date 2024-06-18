package net.univwork.api.api_v1.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentFormDto {

    @NotBlank(message = "닉네임을 확인해 주세요.")
    @Pattern(regexp = "^(?!.*관리자)(?!.*운영자).*$", message = "사용 불가능한 닉네임 입니다.")
    @Size(max = 6, message = "닉네임은 최대 6자 까지 입력 가능합니다.")
    private String nickname;

    @NotBlank(message = "비밀번호를 확인해 주세요.")
    @Size(max = 16, message = "비밀번호는 최대 16자 까지 입력 가능합니다.")
    private String password;

    @NotBlank(message = "댓글은 공백을 입력하실 수 없습니다.")
    @Size(max = 100, message = "댓글은 최대 1000자 까지 입력 가능합니다.")
    private String comment;

//    @Email(message = "올바른 이메일을 입력해주세요.")
//    private String email;

    private Long univCode;

    private Long workplaceCode;
}
