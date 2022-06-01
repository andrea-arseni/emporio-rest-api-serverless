package my.service.wrappers;

import my.service.entities.CaratteristicheImmobile;
import my.service.entities.Immobile;
import my.service.entities.Persona;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Validated
public class ImmobileWrapper {

    @Valid
    @NotNull
    private Immobile immobile;

    @Valid
    @NotNull
    private CaratteristicheImmobile caratteristicheImmobile;

    @Valid
    @NotNull
    private Persona proprietario;

    public ImmobileWrapper() {}

    public ImmobileWrapper(Immobile immobile, CaratteristicheImmobile caratteristicheImmobile, Persona proprietario) {
        this.immobile = immobile;
        this.caratteristicheImmobile = caratteristicheImmobile;
        this.proprietario = proprietario;
    }

    public Immobile getImmobile() {
        return immobile;
    }

    public void setImmobile(Immobile immobile) {
        this.immobile = immobile;
    }

    public CaratteristicheImmobile getCaratteristicheImmobile() {
        return caratteristicheImmobile;
    }

    public void setCaratteristicheImmobile(CaratteristicheImmobile caratteristicheImmobile) {
        this.caratteristicheImmobile = caratteristicheImmobile;
    }

    public Persona getProprietario() {
        return proprietario;
    }

    public void setProprietario(Persona proprietario) {
        this.proprietario = proprietario;
    }
}
