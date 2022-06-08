package my.service.controller;

import my.service.entities.Lavoro;
import my.service.entities.Step;
import my.service.services.LavoroService;
import my.service.transporters.LavoroTrans;
import my.service.utilities.ResponseList;
import my.service.wrappers.StepWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/lavori")
public class LavoroController {

    @Autowired
    private LavoroService lavoroService;

    @GetMapping("/{idLavoro}/steps")
    public ResponseList getSteps(
            @PathVariable Integer idLavoro,
            // chiave che definisce quale campo interrogare per il filtro
            @RequestParam(defaultValue = "") String filter,
            // valore che definisce il valore da ricercare - nel caso key è type string o boolean
            @RequestParam(defaultValue = "") String value,
            // valore che definisce start e end dell'intervallo da ricercare nel caso key fosse date
            @RequestParam(defaultValue = "1900-12-31") String startDate,
            @RequestParam(defaultValue = "2100-12-31") String endDate,
            // stringa per le quali verranno ordinati i record, è possibile mettere più valori intervallati da virgola
            // il format è "campo-tipo"  es. importo-DESC,data-ASC
            @RequestParam(defaultValue = "") String sort,
            // numero di pagina necessario per calcolo offset
            @RequestParam(defaultValue = "1") Integer page,
            // numero di elementi per pagina
            @RequestParam(defaultValue = "20") Integer results
    ) {
        return this.lavoroService.getAllSteps(idLavoro, filter, value, startDate, endDate, sort, page, results);
    }

    @GetMapping("/{idLavoro}/steps/{id}")
    public Step getStep(@PathVariable Integer idLavoro, @PathVariable Integer id) {
        return this.lavoroService.getOneStep(idLavoro, id);
    }

    @PostMapping("{idLavoro}/steps")
    public Step postStep(@PathVariable Integer idLavoro,
                           @Valid @RequestBody StepWrapper stepWrapper, @RequestHeader("userId") String userData) {
        return this.lavoroService.postStep(idLavoro, stepWrapper, userData);
    }

    @PatchMapping("/{idLavoro}/steps/{id}")
    public Step patchStep(@PathVariable Integer idLavoro, @PathVariable Integer id, @RequestBody Step patchStep,
                          @RequestHeader("userId") String userData) {
        return this.lavoroService.patchStep(idLavoro, id, patchStep, userData);
    }

    @DeleteMapping("/{idLavoro}/steps/{id}")
    public String deleteStep(@PathVariable Integer idLavoro, @PathVariable Integer id, @RequestHeader("userId") String userData) {
        return this.lavoroService.deleteStep(idLavoro, id, userData);
    }

    @GetMapping("")
    public ResponseList getAllLavori(
            // chiave che definisce quale campo interrogare per il filtro
            @RequestParam(defaultValue = "") String filter,
            // valore che definisce il valore da ricercare - nel caso key è type string o boolean
            @RequestParam(defaultValue = "") String value,
            // stringa per le quali verranno ordinati i record, è possibile mettere più valori intervallati da virgola
            // il format è "campo-tipo"  es. importo-DESC,data-ASC
            @RequestParam(defaultValue = "") String sort,
            // numero di pagina necessario per calcolo offset
            @RequestParam(defaultValue = "1") Integer page,
            // numero di elementi per pagina
            @RequestParam(defaultValue = "20") Integer results
    ) {
        return this.lavoroService.getAllLavori(filter, value, sort, page, results);
    }

    @GetMapping("/{id}")
    public Lavoro getSingleLavoro(@PathVariable Integer id) {
        return this.lavoroService.getOneLavoro(id);
    }

    @PostMapping("")
    public Object postLavoro(@RequestBody Lavoro lavoro, @RequestHeader("userId") String userData) {
        return this.lavoroService.postLavoro(lavoro, userData);
    }

    @PatchMapping("/{id}")
    public Object patchLavoro(@PathVariable Integer id,
                              @RequestBody Lavoro patchLavoro,
                              @RequestHeader("userId") String userData) {
        return this.lavoroService.patchLavoro(id, patchLavoro, userData);
    }

    @DeleteMapping("/{id}")
    public String deleteLavoro(@PathVariable Integer id,
                               @RequestHeader("userId") String userData) {
        return this.lavoroService.deleteLavoro(id, userData);
    }

}
