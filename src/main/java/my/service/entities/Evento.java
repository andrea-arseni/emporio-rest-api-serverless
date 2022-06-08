package my.service.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evento")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "data")
    private LocalDateTime data;

    @Column(name = "descrizione")
    private String descrizione;

    @ManyToOne
    @JoinColumn(name="persona")
    @JsonBackReference(value="eventi")
    private Persona persona;

    @ManyToOne
    @JoinColumn(name="immobile")
    private Immobile immobile;

    @ManyToOne
    @JoinColumn(name="user")
    private User user;

    public Evento() {}

    public Evento(String descrizione){
        this.data = LocalDateTime.now();
        this.descrizione = descrizione;
    }

    public Evento(LocalDateTime data, String descrizione, Persona persona, Immobile immobile, User user) {
        this.data = data;
        this.descrizione = descrizione;
        this.persona = persona;
        this.immobile = immobile;
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
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

    @Override
    public String toString() {
        return "Evento{" +
                "id=" + id +
                ", data=" + data +
                ", descrizione='" + descrizione + '\'' +
                ", persona=" + persona +
                ", immobile=" + immobile +
                ", user=" + user +
                '}';
    }
}
