package my.service.entities;

import javax.persistence.*;

@Entity
@Table(name = "caratteristiche_immobile")
public class CaratteristicheImmobile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "descrizione")
    private String descrizione;

    @Column(name = "esposizione")
    private String esposizione;

    @Column(name = "spese_condominiali")
    private Integer speseCondominiali;

    @Column(name = "spese_extra_note")
    private String speseExtraNote;

    @Column(name = "ascensore")
    private Boolean ascensore;

    @Column(name = "arredamento")
    private String arredamento;

    @Column(name = "balconi")
    private String balconi;

    @Column(name = "terrazzi")
    private String terrazzi;

    @Column(name = "box")
    private String box;

    @Column(name = "giardino")
    private String giardino;

    @Column(name = "taverna")
    private String taverna;

    @Column(name = "mansarda")
    private String mansarda;

    @Column(name = "cantina")
    private String cantina;

    @Column(name = "spese_riscaldamento")
    private Integer speseRiscaldamento;

    @Column(name = "aria_condizionata")
    private String ariaCondizionata;

    @Column(name = "proprieta")
    private String proprieta;

    @Column(name = "categoria_catastale")
    private String categoriaCatastale;

    @Column(name = "rendita")
    private Double rendita;

    @Column(name = "impianto_elettrico")
    private String impiantoElettrico;

    @Column(name = "impianto_idraulico")
    private String impiantoIdraulico;

    @Column(name = "livelli")
    private Integer livelli;

    @Column(name = "serramenti_interni")
    private String serramentiInterni;

    @Column(name = "serramenti_esterni")
    private String serramentiEsterni;

    @Column(name = "porta_blindata")
    private Boolean portaBlindata;

    @Column(name = "antifurto")
    private String antifurto;

    @Column(name = "citofono")
    private String citofono;

    @Column(name = "anno_costruzione")
    private Integer annoCostruzione;

    @Column(name = "portineria")
    private String portineria;

    @Column(name = "combustibile")
    private String combustibile;

    @Column(name = "cablato")
    private String cablato;

    @Column(name = "tipo_contratto")
    private String tipoContratto;

    @Column(name = "cauzione")
    private String cauzione;

    @Column(name = "reception")
    private String reception;

    @Column(name = "altezza")
    private String altezza;

    public CaratteristicheImmobile() {}

    public CaratteristicheImmobile(Immobile immobile, String descrizione, String esposizione, Integer speseCondominiali, String speseExtraNote, Boolean ascensore, String arredamento, String balconi, String terrazzi, String box, String giardino, String taverna, String mansarda, String cantina, Integer speseRiscaldamento, String ariaCondizionata, String proprieta, String categoriaCatastale, Double rendita, String impiantoElettrico, String impiantoIdraulico, Integer livelli, String serramentiInterni, String serramentiEsterni, Boolean portaBlindata, String antifurto, String citofono, Integer annoCostruzione, String portineria, String combustibile, String cablato, String tipoContratto, String cauzione, String reception, String altezza) {
        this.descrizione = descrizione;
        this.esposizione = esposizione;
        this.speseCondominiali = speseCondominiali;
        this.speseExtraNote = speseExtraNote;
        this.ascensore = ascensore;
        this.arredamento = arredamento;
        this.balconi = balconi;
        this.terrazzi = terrazzi;
        this.box = box;
        this.giardino = giardino;
        this.taverna = taverna;
        this.mansarda = mansarda;
        this.cantina = cantina;
        this.speseRiscaldamento = speseRiscaldamento;
        this.ariaCondizionata = ariaCondizionata;
        this.proprieta = proprieta;
        this.categoriaCatastale = categoriaCatastale;
        this.rendita = rendita;
        this.impiantoElettrico = impiantoElettrico;
        this.impiantoIdraulico = impiantoIdraulico;
        this.livelli = livelli;
        this.serramentiInterni = serramentiInterni;
        this.serramentiEsterni = serramentiEsterni;
        this.portaBlindata = portaBlindata;
        this.antifurto = antifurto;
        this.citofono = citofono;
        this.annoCostruzione = annoCostruzione;
        this.portineria = portineria;
        this.combustibile = combustibile;
        this.cablato = cablato;
        this.tipoContratto = tipoContratto;
        this.cauzione = cauzione;
        this.reception = reception;
        this.altezza = altezza;
    }

    public CaratteristicheImmobile(CaratteristicheImmobile caratteristicheImmobile) {
        this.descrizione = caratteristicheImmobile.getDescrizione();
        this.esposizione = caratteristicheImmobile.getEsposizione();
        this.speseCondominiali = caratteristicheImmobile.getSpeseCondominiali();
        this.speseExtraNote = caratteristicheImmobile.getSpeseExtraNote();
        this.ascensore = caratteristicheImmobile.getAscensore();
        this.arredamento = caratteristicheImmobile.getArredamento();
        this.balconi = caratteristicheImmobile.getBalconi();
        this.terrazzi = caratteristicheImmobile.getTerrazzi();
        this.box = caratteristicheImmobile.getBox();
        this.giardino = caratteristicheImmobile.getGiardino();
        this.taverna = caratteristicheImmobile.getTaverna();
        this.mansarda = caratteristicheImmobile.getMansarda();
        this.cantina = caratteristicheImmobile.getCantina();
        this.speseRiscaldamento = caratteristicheImmobile.getSpeseRiscaldamento();
        this.ariaCondizionata = caratteristicheImmobile.getAriaCondizionata();
        this.proprieta = caratteristicheImmobile.getProprieta();
        this.categoriaCatastale = caratteristicheImmobile.getCategoriaCatastale();
        this.rendita = caratteristicheImmobile.getRendita();
        this.impiantoElettrico = caratteristicheImmobile.getImpiantoElettrico();
        this.impiantoIdraulico = caratteristicheImmobile.getImpiantoIdraulico();
        this.livelli = caratteristicheImmobile.getLivelli();
        this.serramentiInterni = caratteristicheImmobile.getSerramentiInterni();
        this.serramentiEsterni = caratteristicheImmobile.getSerramentiEsterni();
        this.portaBlindata = caratteristicheImmobile.getPortaBlindata();
        this.antifurto = caratteristicheImmobile.getAntifurto();
        this.citofono = caratteristicheImmobile.getCitofono();
        this.annoCostruzione = caratteristicheImmobile.getAnnoCostruzione();
        this.portineria = caratteristicheImmobile.getPortineria();
        this.combustibile = caratteristicheImmobile.getCombustibile();
        this.cablato = caratteristicheImmobile.getCablato();
        this.tipoContratto = caratteristicheImmobile.getTipoContratto();
        this.cauzione = caratteristicheImmobile.getCauzione();
        this.reception = caratteristicheImmobile.getReception();
        this.altezza = caratteristicheImmobile.getAltezza();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getEsposizione() {
        return esposizione;
    }

    public void setEsposizione(String esposizione) {
        this.esposizione = esposizione;
    }

    public Integer getSpeseCondominiali() {
        return speseCondominiali;
    }

    public void setSpeseCondominiali(Integer speseCondominiali) {
        this.speseCondominiali = speseCondominiali;
    }

    public String getSpeseExtraNote() {
        return speseExtraNote;
    }

    public void setSpeseExtraNote(String speseExtraNote) {
        this.speseExtraNote = speseExtraNote;
    }

    public Boolean getAscensore() {
        return ascensore;
    }

    public void setAscensore(Boolean ascensore) {
        this.ascensore = ascensore;
    }

    public String getArredamento() {
        return arredamento;
    }

    public void setArredamento(String arredamento) {
        this.arredamento = arredamento;
    }

    public String getBalconi() {
        return balconi;
    }

    public void setBalconi(String balconi) {
        this.balconi = balconi;
    }

    public String getTerrazzi() {
        return terrazzi;
    }

    public void setTerrazzi(String terrazzi) {
        this.terrazzi = terrazzi;
    }

    public String getBox() {
        return box;
    }

    public void setBox(String box) {
        this.box = box;
    }

    public String getGiardino() {
        return giardino;
    }

    public void setGiardino(String giardino) {
        this.giardino = giardino;
    }

    public String getTaverna() {
        return taverna;
    }

    public void setTaverna(String taverna) {
        this.taverna = taverna;
    }

    public String getMansarda() {
        return mansarda;
    }

    public void setMansarda(String mansarda) {
        this.mansarda = mansarda;
    }

    public String getCantina() {
        return cantina;
    }

    public void setCantina(String cantina) {
        this.cantina = cantina;
    }

    public Integer getSpeseRiscaldamento() {
        return speseRiscaldamento;
    }

    public void setSpeseRiscaldamento(Integer speseRiscaldamento) {
        this.speseRiscaldamento = speseRiscaldamento;
    }

    public String getAriaCondizionata() {
        return ariaCondizionata;
    }

    public void setAriaCondizionata(String ariaCondizionata) {
        this.ariaCondizionata = ariaCondizionata;
    }

    public String getProprieta() {
        return proprieta;
    }

    public void setProprieta(String proprieta) {
        this.proprieta = proprieta;
    }

    public String getCategoriaCatastale() {
        return categoriaCatastale;
    }

    public void setCategoriaCatastale(String categoriaCatastale) {
        this.categoriaCatastale = categoriaCatastale;
    }

    public Double getRendita() {
        return rendita;
    }

    public void setRendita(Double rendita) {
        this.rendita = rendita;
    }

    public String getImpiantoElettrico() {
        return impiantoElettrico;
    }

    public void setImpiantoElettrico(String impiantoElettrico) {
        this.impiantoElettrico = impiantoElettrico;
    }

    public String getImpiantoIdraulico() {
        return impiantoIdraulico;
    }

    public void setImpiantoIdraulico(String impiantoIdraulico) {
        this.impiantoIdraulico = impiantoIdraulico;
    }

    public Integer getLivelli() {
        return livelli;
    }

    public void setLivelli(Integer livelli) {
        this.livelli = livelli;
    }

    public String getSerramentiInterni() {
        return serramentiInterni;
    }

    public void setSerramentiInterni(String serramentiInterni) {
        this.serramentiInterni = serramentiInterni;
    }

    public String getSerramentiEsterni() {
        return serramentiEsterni;
    }

    public void setSerramentiEsterni(String serramentiEsterni) {
        this.serramentiEsterni = serramentiEsterni;
    }

    public Boolean getPortaBlindata() {
        return portaBlindata;
    }

    public void setPortaBlindata(Boolean portaBlindata) {
        this.portaBlindata = portaBlindata;
    }

    public String getAntifurto() {
        return antifurto;
    }

    public void setAntifurto(String antifurto) {
        this.antifurto = antifurto;
    }

    public String getCitofono() {
        return citofono;
    }

    public void setCitofono(String citofono) {
        this.citofono = citofono;
    }

    public Integer getAnnoCostruzione() {
        return annoCostruzione;
    }

    public void setAnnoCostruzione(Integer annoCostruzione) {
        this.annoCostruzione = annoCostruzione;
    }

    public String getPortineria() {
        return portineria;
    }

    public void setPortineria(String portineria) {
        this.portineria = portineria;
    }

    public String getCombustibile() {
        return combustibile;
    }

    public void setCombustibile(String combustibile) {
        this.combustibile = combustibile;
    }

    public String getCablato() {
        return cablato;
    }

    public void setCablato(String cablato) {
        this.cablato = cablato;
    }

    public String getTipoContratto() {
        return tipoContratto;
    }

    public void setTipoContratto(String tipoContratto) {
        this.tipoContratto = tipoContratto;
    }

    public String getCauzione() {
        return cauzione;
    }

    public void setCauzione(String cauzione) {
        this.cauzione = cauzione;
    }

    public String getReception() {
        return reception;
    }

    public void setReception(String reception) {
        this.reception = reception;
    }

    public String getAltezza() {
        return altezza;
    }

    public void setAltezza(String altezza) {
        this.altezza = altezza;
    }

}
