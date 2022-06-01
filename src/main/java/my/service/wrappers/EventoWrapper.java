package my.service.wrappers;

public class EventoWrapper {

    private String descrizione;

    private Integer idImmobile;

    private String statusPersona;

    public EventoWrapper(String descrizione, Integer idImmobile, String statusPersona) {
        this.descrizione = descrizione;
        this.idImmobile = idImmobile;
        this.statusPersona = statusPersona;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Integer getIdImmobile() {
        return idImmobile;
    }

    public void setIdImmobile(Integer idImmobile) {
        this.idImmobile = idImmobile;
    }

    public String getStatusPersona() {
        return statusPersona;
    }

    public void setStatusPersona(String statusPersona) {
        this.statusPersona = statusPersona;
    }
}
