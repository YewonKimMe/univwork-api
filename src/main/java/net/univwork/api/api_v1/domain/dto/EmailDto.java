package net.univwork.api.api_v1.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailDto {

    @NotBlank(message = "이메일을 입력해 주세요.")
    @Email(message = "유효한 이메일 양식을 입력해 주세요.")
    private String email;

    public EmailDto() {
    }
}
