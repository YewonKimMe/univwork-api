package net.univwork.api.api_v1.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "comment_like_log")
public class CommentLikeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comment_code")
    private Long commentCode;

    private String ip;

    public CommentLikeLog() {
    }

    public CommentLikeLog(Long commentCode, String ip) {
        this.commentCode = commentCode;
        this.ip = ip;
    }
}
