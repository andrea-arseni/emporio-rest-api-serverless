package my.service.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
@Table(name = "file")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "codice_bucket")
    private String codiceBucket;

    @Column(name = "nome")
    private String nome;

    @Column(name="tipologia")
    private String tipologia;

    @ManyToOne
    @JoinColumn(name="immobile")
    @JsonBackReference(value="immobileFiles")
    private Immobile immobile;

    @ManyToOne
    @JoinColumn(name="persona")
    @JsonBackReference(value="personaFiles")
    private Persona persona;

    public File() {}

    public File(String tipologia, String codiceBucket, String nome) {
        this.tipologia = tipologia;
        this.codiceBucket = codiceBucket;
        this.nome = nome;
    }

    public File(Immobile immobile, String tipologia, String codiceBucket, String nome) {
        this.immobile = immobile;
        this.tipologia = tipologia;
        this.codiceBucket = codiceBucket;
        this.nome = nome;
    }

    public File(Persona persona, String tipologia, String codiceBucket, String nome) {
        this.persona = persona;
        this.tipologia = tipologia;
        this.codiceBucket = codiceBucket;
        this.nome = nome;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Immobile getImmobile() {
        return immobile;
    }

    public void setImmobile(Immobile immobile) {
        this.immobile = immobile;
    }

    public String getTipologia() {
        return tipologia;
    }

    public void setTipologia(String tipologia) {
        this.tipologia = tipologia;
    }

    public String getCodiceBucket() {
        return codiceBucket;
    }

    public void setCodiceBucket(String codiceBucket) {
        this.codiceBucket = codiceBucket;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Persona getPersona() { return persona;}

    public void setPersona(Persona persona) {this.persona = persona;}

    @Override
    public String toString() {
        return "File{" +
                "id=" + id +
                ", immobile=" + immobile +
                ", tipologia='" + tipologia + '\'' +
                ", codiceBucket=" + codiceBucket +
                ", nome='" + nome + '\'' +
                ", persona='" + persona + '\'' +
                '}';
    }
}
