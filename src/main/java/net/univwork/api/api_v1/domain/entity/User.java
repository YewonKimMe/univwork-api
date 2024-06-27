package net.univwork.api.api_v1.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "no")
    private Long No;

    private String userId;

    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password")
    private String pwd;

    private String role;

    @Column(name = "create_date")
    private Timestamp createDate;

    private boolean verification;

    @JsonIgnore
    @OneToMany(mappedBy="user", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Authority> authorities;

}
