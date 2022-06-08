package my.service.controller;


import my.service.entities.Budget;
import my.service.services.BudgetService;
import my.service.utilities.ResponseList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/operazioni")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @GetMapping("")
    public ResponseList getAllBudgets(
            // chiave che definisce quale campo interrogare per il filtro
            @RequestParam(defaultValue = "") String filter,
            // valore che definisce il valore da ricercare - nel caso key è type string o boolean
            @RequestParam(defaultValue = "") String value,
            // valore che definisce min e max dell'intervallo da ricercare nel caso key fosse number
            @RequestParam(defaultValue = "-1000000") String min,
            @RequestParam(defaultValue = "1000000000") String max,
            // valore che definisce start e end dell'intervallo da ricercare nel caso key fosse date
            @RequestParam(defaultValue = "1900-12-31") String startDate,
            @RequestParam(defaultValue = "2100-12-31") String endDate,
            // stringa per le quali verranno ordinati i record, è possibile mettere più valori intervallati da virgola
            // il format è "campo-tipo"  es. importo-DESC,data-ASC
            @RequestParam(defaultValue = "") String sort,
            // numero di pagina necessario per calcolo offset
            @RequestParam(defaultValue = "0") Integer page,
            // numero di elementi per pagina
            @RequestParam(defaultValue = "20") Integer results,
            @RequestHeader("userId") String userData
            ) {

        return budgetService.getAllOperations(
                filter,
                value,
                min,
                max,
                startDate,
                endDate,
                sort,
                page,
                results,
                userData);
    }

    @GetMapping("/{id}")
    public Budget getBudget(@PathVariable Integer id,
                            @RequestHeader("userId") String userData) {
        return budgetService.getOneOperation(id, userData);
    }

    @PostMapping("")
    public Budget postBudget(
            @RequestBody Budget budget,
            @RequestHeader("userId") String userData
    ) {
        return budgetService.postOperation(budget, userData);
    }

    @PatchMapping("/{id}")
    public Budget patchBudget(@PathVariable Integer id,
                              @RequestBody Budget operazione,
                              @RequestHeader("userId") String userData) {
        // indipendentemente dal JSON passato dal Client Spring considera solo gli attributi della classe in questione
        return budgetService.patchOperation(id, operazione, userData);
    }

    @DeleteMapping("/{id}")
    public String deleteBudget(@PathVariable Integer id,
                               @RequestHeader("userId") String userData) {
        return budgetService.deleteOperation(id, userData);
    }
}
