package my.service.controller;

import my.service.entities.Visita;
import my.service.services.VisitaService;
import my.service.transporters.VisitaTrans;
import my.service.utilities.ResponseList;
import my.service.wrappers.VisitaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/visite")
public class VisitaController {

    @Autowired
    private VisitaService visitaService;

    @GetMapping("")
    public ResponseList getListVisite(
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
            @RequestParam(defaultValue = "20") Integer results,
            // per query anche con immobile e persona
            @RequestParam(required = false) Integer immobile,
            @RequestParam(required = false) Integer persona
    ) {
        return this.visitaService.getListVisite(filter, value, startDate, endDate, sort, page, results, immobile, persona);
    }

    @GetMapping("{id}")
    public VisitaTrans getVisita(@PathVariable Integer id) {
        return this.visitaService.getVisita(id);
    }

    @PostMapping("")
    public VisitaTrans postVisita(
            @RequestBody VisitaWrapper visitaWrapper,
            @RequestHeader("x-amzn-oidc-data") String userData
            ) {
        return this.visitaService.addVisita(visitaWrapper, userData);
    }

    @PatchMapping("{id}")
    public VisitaTrans patchVisita(
            @RequestBody VisitaWrapper visitaWrapper,
            @PathVariable Integer id,
            @RequestHeader("x-amzn-oidc-data") String userData
    ) {
        return this.visitaService.patchVisita(id, visitaWrapper, userData);
    }

    @DeleteMapping("{id}")
    public String deleteVisita(@PathVariable Integer id,
                               @RequestHeader("x-amzn-oidc-data") String userData) {
        return this.visitaService.deleteVisita(id, userData);
    }

}
