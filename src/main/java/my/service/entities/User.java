package my.service.entities;

import my.service.types.UserType;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
public class User {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name="name")
    private String name;

    @Column(name="email")
    private String email;

    @Column(name="role")
    private String role;

    @Column(name="first_access")
    private LocalDateTime firstAccess;

    public User() {}

    public User(String sub, String name, String email, LocalDateTime localDateTime) {
        this.id = sub;
        this.name = name;
        this.email = email;
        this.firstAccess = localDateTime;
        this.role = UserType.NORMALE.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getFirstAccess() {
        return firstAccess;
    }

    public void setFirstAccess(LocalDateTime firstAccess) {
        this.firstAccess = firstAccess;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", firstAccess=" + firstAccess +
                '}';
    }
}
