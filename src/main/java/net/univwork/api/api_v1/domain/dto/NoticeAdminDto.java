package net.univwork.api.api_v1.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.univwork.api.api_v1.domain.entity.Notice;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class NoticeAdminDto {

    @NotBlank(message = "공지 제목 입력 필수")
    @Size(max = 128, message = "제목 최대 128자")
    private String title;

    @NotBlank
    private String classification;

    @NotBlank(message = "작성자 입력 필수, 기본=운영자")
    @Size(max = 10, message = "최대 10글자")
    private String author;

    @NotBlank(message = "공지 본문 필수")
    @Size(max = 5000, message = "공지 본문 최대 5000자")
    private String content;

    public NoticeAdminDto(Notice notice) {
        this.title = notice.getTitle();
        this.classification = notice.getClassification();
        this.author = notice.getAuthor();
        this.content = notice.getContent();
    }
}
