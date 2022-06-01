package my.service.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "log")
@Validated
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name="azione")
    @Valid
    @NotNull
    private String azione;

    @Column(name="data")
    private LocalDateTime data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="immobile")
    @JsonBackReference
    private Immobile immobile;

    @ManyToOne
    @JoinColumn(name="user")
    private User user;

    public Log() {}

    public Log(String azione, LocalDateTime data, Immobile immobile, User user) {
        this.azione = azione;
        this.data = data;
        this.immobile = immobile;
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAzione() {
        return azione;
    }

    public void setAzione(String azione) {
        this.azione = azione;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Immobile getImmobile() {
        return immobile;
    }

    public void setImmobile(Immobile immobile) {
        this.immobile = immobile;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Log{" +
                "id=" + id +
                ", azione='" + azione + '\'' +
                ", data=" + data +
                ", immobile=" + immobile +
                ", user=" + user +
                '}';
    }
}
