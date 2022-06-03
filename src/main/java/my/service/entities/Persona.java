package my.service.entities;

import my.service.wrappers.PersonaWrapper;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "persona")
@Validated
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nome")
    @NotNull(message = "E' obbligatorio indicare un nome")
    private String nome;

    @Column(name = "telefono")
    @Pattern(regexp="^(\\+\\d{1,3})?(\\d{8,12})$")
    private String telefono;

    @Column(name = "email")
    @Email
    private String email;

    @Column(name = "proprietario")
    private Boolean isProprietario;

    @Column(name = "inquilino")
    private Boolean isInquilino;

    @Column(name = "importante")
    private Boolean isImportante;

    @Column(name = "ruolo")
    private String ruolo;

    @ManyToOne
    @JoinColumn(name="immobile_inquilino")
    @JsonBackReference(value="inquilini")
    private Immobile immobileInquilino;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "proprietario")
    @JsonManagedReference(value="proprietario")
    private List<Immobile> immobili;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "persona", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Evento> eventi;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "persona")
    @JsonManagedReference
    private List<File> files;

    @Column(name = "data_uscita")
    private LocalDate dataUscita;

    @Column(name = "provenienza")
    private String provenienza;

    @Column(name = "status")
    private String status;

    public Persona() {}

    public Persona(String nome, String telefono, String email, String status, String provenienza) {
        this(nome, telefono, email, status, provenienza, null, null, null, null, null, null, null);
    }

    public Persona(String nome, String telefono, String email, String status, String provenienza, Boolean proprietario, Boolean collaboratore, Boolean inquilino, Boolean importante, String ruolo, Immobile immobileInquilino, LocalDate dataUscita) {
        this.nome = nome!=null ? nome.trim().toLowerCase() : "Anonimo";
        this.telefono = telefono;
        this.email = email!=null ? email.toLowerCase():null;
        this.isProprietario = proprietario;
        this.isInquilino = inquilino;
        this.isImportante = importante;
        this.ruolo = ruolo;
        this.immobileInquilino = immobileInquilino;
        this.dataUscita = dataUscita;
        this.provenienza = provenienza;
        this.status = status;
    }

    public Persona(PersonaWrapper personaWrapper){
        this.nome = personaWrapper.getNome()!=null ? personaWrapper.getNome().trim().toLowerCase() : "Anonimo";
        this.telefono = personaWrapper.getTelefono()!=null ? personaWrapper.getTelefono(): null;
        this.email = personaWrapper.getEmail()!=null ? personaWrapper.getEmail().toLowerCase(): null;
        this.isProprietario = personaWrapper.getImmobiliProprieta()!=null && personaWrapper.getImmobiliProprieta().size()>0;
        this.isInquilino = personaWrapper.getImmobileAffitto()!=null;
        this.isImportante = personaWrapper.getImportante();
        this.ruolo = personaWrapper.getRuolo();
        this.dataUscita = null;
        this.provenienza = personaWrapper.getProvenienza();
        this.status = personaWrapper.getStatus();
        this.eventi = null;
        this.dataUscita = personaWrapper.getDataUscita();
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
        this.nome = nome.trim().toLowerCase();
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
        this.email = email.toLowerCase();
    }

    public Boolean getIsProprietario() {
        return isProprietario;
    }

    public void setIsProprietario(Boolean proprietario) {
        this.isProprietario = proprietario;
    }

    public Boolean getIsInquilino() {
        return isInquilino;
    }

    public void setIsInquilino(Boolean inquilino) {
        this.isInquilino = inquilino;
    }

    public Boolean getIsImportante() {
        return isImportante;
    }

    public void setIsImportante(Boolean importante) {
        this.isImportante = importante;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    public Immobile getImmobileInquilino() {
        return immobileInquilino;
    }

    public void setImmobileInquilino(Immobile immobileInquilino) {
        this.immobileInquilino = immobileInquilino;
    }

    public LocalDate getDataUscita() {
        return dataUscita;
    }

    public void setDataUscita(LocalDate dataUscita) {
        this.dataUscita = dataUscita;
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

    public List<Immobile> getImmobili() {
        return immobili;
    }

    public void setImmobili(List<Immobile> immobili) {
        this.immobili = immobili;
    }

    public void addImmobile(Immobile immobile){
        if(this.immobili==null) this.immobili = new ArrayList<>();
        this.immobili.add(immobile);
        immobile.setProprietario(this);
    }

    public List<Evento> getEventi() {
        return eventi;
    }

    public void setEventi(List<Evento> eventi) {
        this.eventi = eventi;
    }

    public void addEvento(Evento evento){
        if(this.eventi==null) this.eventi = new ArrayList<>();
        this.eventi.add(evento);
        evento.setPersona(this);
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    @Override
    public String toString() {
        return "Persona{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                ", proprietario=" + isProprietario +
                ", inquilino=" + isInquilino +
                ", importante=" + isImportante +
                ", ruolo='" + ruolo + '\'' +
                ", dataUscita=" + dataUscita +
                ", provenienza='" + provenienza + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
