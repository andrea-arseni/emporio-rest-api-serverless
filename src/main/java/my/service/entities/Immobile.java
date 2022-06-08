package my.service.entities;

import my.service.utilities.BadRequestException;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "immobile")
@Validated
public class Immobile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name="ref")
    @Min(value = 1)
    private Integer ref;

    @Column(name="titolo")
    @Valid
    @NotNull
    @Length(min = 15, message = "Titolo troppo corto: deve contenere almeno 15 caratteri")
    @Length(max = 60, message = "Titolo troppo lungo: non può superare i 60 caratteri")
    private String titolo;

    @Column(name="superficie")
    @Valid
    @NotNull
    private Integer superficie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="proprietario")
    @JsonBackReference(value="proprietario")
    private Persona proprietario;

    @Column(name="tipologia")
    @Valid
    @NotNull
    private String tipologia;

    @Column(name="locali")
    @Valid
    @NotNull
    private Integer locali;

    @Column(name="indirizzo")
    @Valid
    @NotNull
    private String indirizzo;

    @Column(name="zona")
    private String zona;

    @Column(name="comune")
    @Valid
    @NotNull
    private String comune;

    @Column(name="prezzo")
    @Valid
    @NotNull
    private Integer prezzo;

    @Column(name="riscaldamento")
    @Valid
    @NotNull
    private String riscaldamento;

    @Column(name="classe_energetica")
    @Valid
    @NotNull
    private String classeEnergetica;

    @Column(name="consumo")
    @Valid
    @NotNull
    @Min(value = 0)
    private Double consumo;

    @Column(name="contratto")
    @Valid
    @NotNull
    private String contratto;

    @Column(name="categoria")
    @Valid
    @NotNull
    private String categoria;

    @Column(name="stato")
    @Valid
    @NotNull
    private String stato;

    @Column(name="libero")
    private String libero;

    @Column(name="status")
    @Valid
    @NotNull
    private String status;

    @Column(name="piano")
    private String piano;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "caratteristiche")
    private CaratteristicheImmobile caratteristicheImmobile;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "immobile")
    @JsonManagedReference(value="logs")
    private List<Log> logs;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "immobile")
    @JsonManagedReference(value="immobileFiles")
    private List<File> files;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "immobileInquilino")
    @JsonManagedReference(value="inquilini")
    private List<Persona> inquilini;

    public Immobile() {}

    public Immobile(Immobile immobile) {
        this.ref = null;
        this.titolo = immobile.getTitolo();
        this.superficie = immobile.getSuperficie();
        this.tipologia = immobile.getTipologia();
        this.locali = immobile.getLocali();
        this.indirizzo = immobile.getIndirizzo();
        this.zona = immobile.getZona();
        this.comune = immobile.getComune();
        this.prezzo = immobile.getPrezzo();
        this.riscaldamento = immobile.getRiscaldamento();
        this.classeEnergetica = immobile.getClasseEnergetica();
        this.consumo = immobile.getConsumo();
        this.contratto = immobile.getContratto();
        this.categoria = immobile.getCategoria();
        this.stato = immobile.getStato();
        this.libero = immobile.getLibero();
        this.status = immobile.getStatus();
        this.piano = immobile.getPiano();
        this.caratteristicheImmobile = immobile.getCaratteristicheImmobile();
    }

    public Immobile(Integer ref, String titolo, Integer superficie, String tipologia, Integer locali, String indirizzo, String zona, String comune, Integer prezzo, String riscaldamento, String classeEnergetica, Double consumo, String contratto, String categoria, String stato, String libero, String status, String piano, CaratteristicheImmobile caratteristiche, Persona proprietario) {
        this.ref = ref;
        this.titolo = titolo;
        this.superficie = superficie;
        this.proprietario = proprietario;
        this.tipologia = tipologia;
        this.locali = locali;
        this.indirizzo = indirizzo;
        this.zona = zona;
        this.comune = comune;
        this.prezzo = prezzo;
        this.riscaldamento = riscaldamento;
        this.classeEnergetica = classeEnergetica;
        this.consumo = consumo;
        this.contratto = contratto;
        this.categoria = categoria;
        this.stato = stato;
        this.libero = libero;
        this.status = status;
        this.piano = piano;
        this.caratteristicheImmobile = caratteristiche;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRef() {
        return ref;
    }

    public void setRef(Integer ref) {
        this.ref = ref;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public Integer getSuperficie() {
        return superficie;
    }

    public void setSuperficie(Integer superficie) {
        this.superficie = superficie;
    }

    public Persona getProprietario() {
        return proprietario;
    }

    public void setProprietario(Persona proprietario) {
        this.proprietario = proprietario;
    }

    public String getTipologia() {
        return tipologia;
    }

    public void setTipologia(String tipologia) {
        this.tipologia = tipologia;
    }

    public Integer getLocali() {
        return locali;
    }

    public void setLocali(Integer locali) {
        this.locali = locali;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public String getComune() {
        return comune;
    }

    public void setComune(String comune) {
        this.comune = comune;
    }

    public Integer getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(Integer prezzo) {
        this.prezzo = prezzo;
    }

    public String getRiscaldamento() {
        return riscaldamento;
    }

    public void setRiscaldamento(String riscaldamento) {
        this.riscaldamento = riscaldamento;
    }

    public String getClasseEnergetica() {
        return classeEnergetica;
    }

    public void setClasseEnergetica(String classeEnergetica) {
        this.classeEnergetica = classeEnergetica;
    }

    public Double getConsumo() {
        return consumo;
    }

    public void setConsumo(Double consumo) {
        this.consumo = consumo;
    }

    public String getContratto() {
        return contratto;
    }

    public void setContratto(String contratto) {
        this.contratto = contratto;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public String getLibero() {
        return libero;
    }

    public void setLibero(String libero) {
        this.libero = libero;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPiano() {
        return piano;
    }

    public void setPiano(String piano) {
        this.piano = piano;
    }

    public CaratteristicheImmobile getCaratteristicheImmobile() {
        return caratteristicheImmobile;
    }

    public void setCaratteristicheImmobile(CaratteristicheImmobile caratteristicheImmobile) {
        this.caratteristicheImmobile = caratteristicheImmobile;
    }

    public List<Log> getLogs() {
        return logs;
    }

    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }

    public void addLog(Log log){
        if(this.logs==null) this.logs = new ArrayList<>();
        this.logs.add(log);
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public void addFile(File file){
        if(this.files==null) this.files = new ArrayList<>();
        this.files.add(file);
        file.setImmobile(this);
    }

    public List<Persona> getInquilini() {
        return inquilini;
    }

    public void setInquilini(List<Persona> inquilini) {
        this.inquilini = inquilini;
    }

    public void addInquilino(Persona inquilino){
        if(this.inquilini == null) this.inquilini = new ArrayList<>();
        this.inquilini.add(inquilino);
        inquilino.setImmobileInquilino(this);
    }

    public void checkFieldNull() {
        List<String> nullFields = new ArrayList<>();
        // lista metodi
        List<Method> methodlist = new LinkedList<Method>(Arrays.asList(Immobile.class.getDeclaredMethods()));
        // non considerare tutto cià che non inizia con get e getId
        methodlist.forEach(method -> {
            if(method.getName().startsWith("get") && !method.getName().equals("getId")
            && !method.getName().equals("getCaratteristicheImmobile")){
                // invocali tutti, se qualcuno è null throw BadRequestError
                try {
                    Object value = method.invoke(this);
                    if(value==null){
                        nullFields.add(method.getName().substring(3));
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
        String message = "";
        if(nullFields.size()==0) return;
        if(nullFields.size()==1){
            message = nullFields.get(0)+" è un campo obbligatorio, aggiungerlo";
        }else{
            for(String field : nullFields){
                message = message+field+" ";
            }
            message = message+"sono campi obbligatori, aggiungerli";
        }
        throw new BadRequestException(message);
    }

    @Override
    public String toString() {
        return "Immobile{" +
                "id=" + id +
                ", ref=" + ref +
                ", titolo='" + titolo + '\'' +
                ", superficie=" + superficie +
                ", tipologia='" + tipologia + '\'' +
                ", locali=" + locali +
                ", indirizzo='" + indirizzo + '\'' +
                ", zona='" + zona + '\'' +
                ", comune='" + comune + '\'' +
                ", prezzo=" + prezzo +
                ", riscaldamento='" + riscaldamento + '\'' +
                ", classeEnergetica='" + classeEnergetica + '\'' +
                ", consumo=" + consumo +
                ", contratto='" + contratto + '\'' +
                ", categoria='" + categoria + '\'' +
                ", stato='" + stato + '\'' +
                ", libero='" + libero + '\'' +
                ", status='" + status + '\'' +
                ", piano='" + piano + '\'' + '}';
    }
}
