package my.service.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Validated
@Table(name = "step")
public class Step {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name="descrizione")
    private String descrizione;

    @Column(name="data")
    @Valid
    @NotNull
    private LocalDateTime data;

    @ManyToOne
    @JoinColumn(name="lavoro")
    @JsonBackReference(value="steps")
    private Lavoro lavoro;

    @ManyToOne
    @JoinColumn(name="user")
    private User user;

    public Step() {}

    public Step(String descrizione, LocalDateTime data) {
        this.descrizione = descrizione;
        this.data = data;
    }

    public Step(String descrizione, LocalDateTime data, Lavoro lavoro, User user) {
        this.descrizione = descrizione;
        this.data = data;
        this.lavoro = lavoro;
        this.user = user;
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

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Lavoro getLavoro() {
        return lavoro;
    }

    public void setLavoro(Lavoro lavoro) {
        this.lavoro = lavoro;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Step{" +
                "id=" + id +
                ", descrizione='" + descrizione + '\'' +
                ", data=" + data +
                '}';
    }
}
