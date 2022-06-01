package my.service.transporters;

import my.service.entities.Lavoro;

public class LavoroTrans {

    private Integer id;
    private String titolo;
    private String status;

    public LavoroTrans(Lavoro lavoro) {
        this.id = lavoro.getId();
        this.titolo = lavoro.getTitolo();
        this.status = lavoro.getStatus();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
