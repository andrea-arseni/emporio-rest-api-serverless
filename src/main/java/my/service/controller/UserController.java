package my.service.controller;

import my.service.entities.User;
import my.service.services.UserService;
import my.service.transporters.UserTrans;
import my.service.utilities.ResponseList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseList getAllUsers(
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
        return this.userService.getAllUsers(filter, value, startDate, endDate, sort, page, results);
    }

    @GetMapping("/{id}")
    public UserTrans getSingleUser(@PathVariable String id) {
        return this.userService.getOneUser(id);
    }

    // CHIAMATA ELIMINATA PERCHE' NON SI CREANO USER DALLA APP
    /*@PostMapping("")
    public Object postUser(
            @RequestBody User user,
            @RequestHeader("x-amzn-oidc-data") String userData
    ) {
        return this.userService.postUser(user, userData);
    }*/

    @PatchMapping("/{id}")
    public Object patchUser(@PathVariable String id,
                            @RequestBody User patchUser,
                            @RequestHeader("x-amzn-oidc-data") String userData) {
        return this.userService.patchUser(id, patchUser, userData);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable String id,
                             @RequestHeader("x-amzn-oidc-data") String userData
                             ) {
        return this.userService.deleteUser(id, userData);
    }

}
