package my.service.entities;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="contabilita")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name="importo")
    private Integer importo;

    @Column(name="data")
    private LocalDate data;

    @Column(name="descrizione")
    private String descrizione;

    @ManyToOne
    @JoinColumn(name="user")
    private User user;

    public Budget() {}

    public Budget(Integer importo, LocalDate data, String descrizione, User user) {
        this.importo = importo;
        this.data = data;
        this.descrizione = descrizione;
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getImporto() {
        return importo;
    }

    public void setImporto(Integer importo) {
        this.importo = importo;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public User getUser() {return user;}

    public void setUser(User user) {this.user = user;}

    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", importo=" + importo +
                ", data=" + data +
                ", descrizione='" + descrizione + '\'' +
                '}';
    }
}
