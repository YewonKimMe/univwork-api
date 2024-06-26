package net.univwork.api.api_v1.domain.dto;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerificationDto {

    private String loginId;

    private String email;

}
