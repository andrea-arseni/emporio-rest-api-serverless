package my.service.transporters;

import my.service.entities.Evento;
import my.service.entities.Immobile;
import my.service.entities.User;
import java.time.LocalDateTime;

public class EventoTrans {

    private Integer id;
    private LocalDateTime data;
    private String descrizione;
    private ImmobileTrans immobile;
    private User user;

    public EventoTrans(Evento evento) {
        this.id = evento.getId();
        this.data = evento.getData();
        this.descrizione = evento.getDescrizione();
        this.immobile = evento.getImmobile()!=null ? new ImmobileTrans(evento.getImmobile()) : null;
        this.user = evento.getUser();
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
}
