package it.auties.hosting.entity;

import javax.persistence.*;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@UserDefinition
public class User extends PanacheEntity {
    @Username
    private String username;
    @Password
    private String password;
    @Roles
    private String role;
    @ManyToMany
    private List<Server> servers;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.servers = new ArrayList<>();
    }
}
