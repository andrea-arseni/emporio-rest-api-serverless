package my.service.wrappers;

import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

@Validated
public class PersonaWrapper {

    private String nome;
    private String telefono;
    private String email;
    private Boolean importante;
    private String ruolo;
    private String provenienza;
    private String status;
    private String note;
    private List<Integer> immobiliProprieta;
    private Integer immobileAffitto;
    private Integer immobileInteresse;
    private LocalDate dataUscita;

    public PersonaWrapper(String nome, String telefono, String email, Boolean importante, String ruolo,
                          String provenienza, String status, List<Integer> immobiliProprieta, Integer immobileAffitto,
                          Integer immobileInteresse, LocalDate dataUscita, String note) {
        this.nome = nome;
        this.telefono = telefono;
        this.email = email;
        this.importante = importante;
        this.ruolo = ruolo;
        this.provenienza = provenienza;
        this.status = status;
        this.immobiliProprieta = immobiliProprieta;
        this.immobileAffitto = immobileAffitto;
        this.immobileInteresse = immobileInteresse;
        this.dataUscita = dataUscita;
        this.note = note;
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

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Integer> getImmobiliProprieta() {
        return immobiliProprieta;
    }

    public void setImmobiliProprieta(List<Integer> immobiliProprieta) {
        this.immobiliProprieta = immobiliProprieta;
    }

    public Integer getImmobileAffitto() {
        return immobileAffitto;
    }

    public void setImmobileAffitto(Integer immobileAffitto) {
        this.immobileAffitto = immobileAffitto;
    }

    public Integer getImmobileInteresse() {
        return immobileInteresse;
    }

    public void setImmobileInteresse(Integer immobileInteresse) {
        this.immobileInteresse = immobileInteresse;
    }

    public LocalDate getDataUscita() {
        return dataUscita;
    }

    public void setDataUscita(LocalDate dataUscita) {
        this.dataUscita = dataUscita;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
