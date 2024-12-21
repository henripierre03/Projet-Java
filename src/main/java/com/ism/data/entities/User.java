package com.ism.data.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.ism.data.enums.Role;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false, exclude = {"client"})
@Entity
@Table(name = "users")
public class User extends AbstractEntity {
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "login", nullable = false, unique = true)
    private String login;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "status", nullable = false)
    private boolean status;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    // Association avec Client, marquée pour un chargement en mode lazy
    @OneToOne(mappedBy = "user")
    @JoinColumn(name = "client_id", nullable = true)
    private Client client;

    public User(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String email, String login, String password,
            boolean status, Role role) {
        super(id, createdAt, updatedAt);
        this.email = email;
        this.login = login;
        this.password = password;
        this.status = status;
        this.role = role;
    }

    public User() {
        super.createdAt = LocalDateTime.now();
        super.updatedAt = LocalDateTime.now();
    }

    // Pour la connexion
    public User(LocalDateTime now, LocalDateTime now2, String string, String string2, String hashPassword, boolean b,
            Role admin) {
        super.createdAt = now;
        super.updatedAt = now2;
        this.email = string;
        this.login = string2;
        this.password = hashPassword;
        this.status = b;
        this.role = admin;
    }

    @Override
    public String toString() {
        return "User [idUser=" + super.getId() + ", email=" + email + ", login=" + login + ", password=" + password
                + ", status=" + status + ", role=" + role + ", client=" + (client == null ? "N/A" : client.getSurname())
                + ", createAt=" + super.getCreatedAt() + ", updateAt=" + super.getUpdatedAt() + "]";
    }
}
