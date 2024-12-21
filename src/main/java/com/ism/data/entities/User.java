package com.ism.data.entities;

import java.time.LocalDateTime;

import com.ism.data.enums.Role;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class User extends AbstractEntity {
    public User(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String email, String login, String password,
            boolean status, Role role) {
        super(id, createdAt, updatedAt);
        this.email = email;
        this.login = login;
        this.password = password;
        this.status = status;
        this.role = role;
    }

    // Unique identifier
    private String email;
    // Unique identifier
    private String login;
    private String password;
    private boolean status;
    private Role role;

    private Client client; // N'est pas dans la base de données

    public User() {
        super.createdAt = LocalDateTime.now();
        super.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "User [idUser=" + super.getId() + ", email=" + email + ", login=" + login + ", password=" + password
                + ", status=" + status + ", role=" + role + ", client=" + (client == null ? "N/A" : client.getSurname())
                + ", createAt=" + super.getCreatedAt() + ", updateAt=" + super.getUpdatedAt() + "]";
    }
}
