package my.service.wrappers;

import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Validated
public class StepWrapper {

    @Valid
    @NotNull
    private String stepMessage;
    @Valid
    @NotNull
    private String lavoroStatus;

    public StepWrapper(String step, String status) {
        this.stepMessage = step;
        this.lavoroStatus = status;
    }

    public String getStepMessage() {
        return stepMessage;
    }

    public void setStepMessage(String stepMessage) {
        this.stepMessage = stepMessage;
    }

    public String getLavoroStatus() {
        return lavoroStatus;
    }

    public void setLavoroStatus(String lavoroStatus) {
        this.lavoroStatus = lavoroStatus;
    }
}
