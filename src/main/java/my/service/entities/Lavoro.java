package my.service.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lavoro")
@Validated
public class Lavoro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "titolo")
    @Valid
    @NotNull
    private String titolo;

    @Column(name = "status")
    @NotNull
    private String status;

    @OneToMany(mappedBy = "lavoro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Step> steps;

    public Lavoro() {}

    public Lavoro(String titolo, String status) {
        this.titolo = titolo;
        this.status = status;
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

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public void addStep(Step step){
        if(this.steps==null) this.steps = new ArrayList<>();
        this.steps.add(step);
    }

    @Override
    public String toString() {
        return "Lavoro{" +
                "id=" + id +
                ", titolo='" + titolo + '\'' +
                ", status='" + status + '\'' +
                ", steps=" + steps +
                '}';
    }
}
