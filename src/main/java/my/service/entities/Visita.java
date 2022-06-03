package my.service.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "visita")
public class Visita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name="persona")
    private Persona persona;

    @ManyToOne
    @JoinColumn(name="immobile")
    private Immobile immobile;

    @ManyToOne
    @JoinColumn(name="user")
    private User user;

    @Column(name = "dove")
    private String dove;

    @Column(name = "quando")
    private LocalDateTime quando;

    @Column(name = "note")
    private String note;

    public Visita() {}

    public Visita(Persona persona, Immobile immobile, User user, String dove, LocalDateTime quando, String note) {
        this.persona = persona;
        this.immobile = immobile;
        this.user = user;
        this.dove = dove;
        this.quando = quando;
        this.note = note;
    }

    public Visita(String dove, LocalDateTime quando, String note) {
        this.dove = dove;
        this.quando = quando;
        this.note = note;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
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

    public String getDove() {
        return dove;
    }

    public void setDove(String dove) {
        this.dove = dove;
    }

    public LocalDateTime getQuando() {
        return quando;
    }

    public void setQuando(LocalDateTime quando) {
        this.quando = quando;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "Visita{" +
                "id=" + id +
                ", persona=" + persona +
                ", immobile=" + immobile +
                ", user=" + user +
                ", dove='" + dove + '\'' +
                ", quando=" + quando +
                ", note='" + note + '\'' +
                '}';
    }
}
