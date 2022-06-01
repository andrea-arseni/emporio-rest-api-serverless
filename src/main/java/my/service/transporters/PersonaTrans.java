package my.service.transporters;

import my.service.entities.Evento;
import my.service.entities.Persona;

import java.util.ArrayList;
import java.util.List;

public class PersonaTrans {

    private Integer id;
    private String nome;
    private String telefono;
    private String email;
    private Boolean proprietario;
    private Boolean inquilino;
    private Boolean importante;
    private String ruolo;
    private String provenienza;
    private String status;
    private List<ImmobileTrans> immobili;
    private List<EventoTrans> eventi;

    public PersonaTrans(Persona persona) {
        this.id = persona.getId();
        this.nome = persona.getNome();
        this.telefono = persona.getTelefono();
        this.email = persona.getEmail();
        this.proprietario = persona.getIsProprietario();
        this.inquilino = persona.getIsInquilino();
        this.importante = persona.getIsImportante();
        this.ruolo = persona.getRuolo();
        this.provenienza = persona.getProvenienza();
        this.status = persona.getStatus();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getProprietario() {
        return proprietario;
    }

    public void setProprietario(Boolean proprietario) {
        this.proprietario = proprietario;
    }

    public Boolean getInquilino() {
        return inquilino;
    }

    public void setInquilino(Boolean inquilino) {
        this.inquilino = inquilino;
    }

    public Boolean getImportante() {
        return importante;
    }

    public void setImportante(Boolean importante) {
        this.importante = importante;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    public String getProvenienza() {
        return provenienza;
    }

    public void setProvenienza(String provenienza) {
        this.provenienza = provenienza;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {this.status = status;}

    public List<ImmobileTrans> getImmobili() {return immobili;}

    public void setImmobili(List<ImmobileTrans> immobili) {this.immobili = immobili;}

    public List<EventoTrans> getEventi() {
        return eventi;
    }

    public void setEventi(List<EventoTrans> eventi) {
        this.eventi = eventi;
    }

    public void addEvento(Evento evento){
        if(this.eventi==null) this.eventi = new ArrayList<>();
        this.eventi.add(new EventoTrans(evento));
    }
}
