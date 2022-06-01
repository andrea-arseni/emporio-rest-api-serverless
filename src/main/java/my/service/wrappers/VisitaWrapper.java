package my.service.wrappers;

import java.time.LocalDateTime;

public class VisitaWrapper {

    private Integer idPersona;

    private Integer idImmobile;

    private String idUser;

    private String dove;

    private LocalDateTime quando;

    private String note;

    public VisitaWrapper() {}

    public VisitaWrapper(Integer idPersona, Integer idImmobile, String idUser, String dove, LocalDateTime quando, String note) {
        this.idPersona = idPersona;
        this.idImmobile = idImmobile;
        this.idUser = idUser;
        this.dove = dove;
        this.quando = quando;
        this.note = note;
    }

    public Integer getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Integer idPersona) {
        this.idPersona = idPersona;
    }

    public Integer getIdImmobile() {
        return idImmobile;
    }

    public void setIdImmobile(Integer idImmobile) {
        this.idImmobile = idImmobile;
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

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }
}
