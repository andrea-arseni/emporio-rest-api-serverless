package my.service.transporters;

import my.service.entities.Immobile;
import my.service.entities.Persona;
import my.service.entities.User;
import my.service.entities.Visita;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

public class VisitaTrans {

    private Integer id;

    private PersonaTrans persona;

    private ImmobileTrans immobile;

    private User user;

    private String dove;

    private LocalDateTime quando;

    private String note;

    public VisitaTrans(Visita visita) {
        this.id = visita.getId();
        this.persona = visita.getPersona()==null ? null : new PersonaTrans(visita.getPersona());
        this.immobile = visita.getImmobile()==null ? null : new ImmobileTrans(visita.getImmobile());
        this.user = visita.getUser();
        this.dove = visita.getDove();
        this.quando = visita.getQuando();
        this.note = visita.getNote();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PersonaTrans getPersona() {
        return persona;
    }

    public void setPersona(PersonaTrans persona) {
        this.persona = persona;
    }

    public ImmobileTrans getImmobile() {
        return immobile;
    }

    public void setImmobile(ImmobileTrans immobile) {
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
}
