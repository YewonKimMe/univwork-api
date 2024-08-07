package net.univwork.api.api_v1.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AdminAspectUserDetailDto {

    private String userId;

    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createDate;

    private String domain;

    private boolean verification;

    private boolean blocked;

    public AdminAspectUserDetailDto(String userId, String email, Timestamp createDate, String domain, boolean verification, boolean blocked) {
        this.userId = userId;
        this.email = email;
        this.createDate = createDate.toLocalDateTime();
        this.domain = domain;
        this.verification = verification;
        this.blocked = blocked;
    }
}
