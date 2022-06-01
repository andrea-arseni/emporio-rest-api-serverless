package my.service.transporters;

import my.service.entities.*;

import java.util.List;

public class ImmobileTrans {

    private Integer id;
    private Integer ref;
    private String titolo;
    private Integer superficie;
    private String tipologia;
    private Integer locali;
    private String indirizzo;
    private String zona;
    private String comune;
    private Integer prezzo;
    private String riscaldamento;
    private String classeEnergetica;
    private Double consumo;
    private String contratto;
    private String categoria;
    private String stato;
    private String libero;
    private String status;
    private String piano;

    private PersonaTrans proprietario;
    private List<PersonaTrans> inquilini;
    private List<Log> logs;
    private List<File> files;
    private CaratteristicheImmobile caratteristicheImmobile;

    public ImmobileTrans(Immobile immobile) {
        this.id = immobile.getId();
        this.ref = immobile.getRef();
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
        this.proprietario = immobile.getProprietario()==null ? null : new PersonaTrans(immobile.getProprietario());
        this.caratteristicheImmobile = immobile.getCaratteristicheImmobile();
        this.files = immobile.getFiles();
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

    public List<Log> getLogs() {
        return logs;
    }

    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }

    public CaratteristicheImmobile getCaratteristicheImmobile() {
        return caratteristicheImmobile;
    }

    public void setCaratteristicheImmobile(CaratteristicheImmobile caratteristicheImmobile) {
        this.caratteristicheImmobile = caratteristicheImmobile;
    }

    public PersonaTrans getProprietario() {
        return proprietario;
    }

    public void setProprietario(PersonaTrans proprietario) {
        this.proprietario = proprietario;
    }

    public List<PersonaTrans> getInquilini() {
        return inquilini;
    }

    public void setInquilini(List<PersonaTrans> inquilini) {
        this.inquilini = inquilini;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
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
