package net.univwork.api.api_v1.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class BlockedIp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sequence;

    private String blockedIp;

    private String reason;
}
