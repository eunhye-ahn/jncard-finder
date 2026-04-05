package com.esstudy.jncardsearch.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *  id       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 *  name     VARCHAR(50)  NOT NULL,
 *  email    VARCHAR(100) NOT NULL UNIQUE,
 *  password VARCHAR(255),
 *  role     VARCHAR(20)  NOT NULL DEFAULT 'ROLE_USER'
 *  CHECK (role IN ('ROLE_USER', 'ROLE_ADMIN')),
 *  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
 */

@Entity
@Getter
@NoArgsConstructor
@Table(name="users")
public class User extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role = Role.ROLE_USER;
}
