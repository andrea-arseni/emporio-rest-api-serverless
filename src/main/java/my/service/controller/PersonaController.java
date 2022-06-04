package my.service.controller;

import my.service.entities.Evento;
import my.service.entities.File;
import my.service.entities.Persona;
import my.service.services.PersonaService;
import my.service.transporters.EventoTrans;
import my.service.transporters.FileTrans;
import my.service.transporters.PersonaTrans;
import my.service.utilities.ResponseList;
import my.service.wrappers.EventoWrapper;
import my.service.wrappers.PersonaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/persone")
public class PersonaController {

    @Autowired
    PersonaService personaService;

    @GetMapping("{idPersona}/eventi")
    public ResponseList getAllEvents(@PathVariable Integer idPersona,
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
                                     @RequestParam(defaultValue = "20") Integer results) {
        return this.personaService.getListEventi(idPersona, filter, value, startDate, endDate, sort, page, results);
    }

    @GetMapping("{idPersona}/eventi/{id}")
    public EventoTrans getEvento(@PathVariable Integer idPersona,
                            @PathVariable Integer id) {
        return this.personaService.getEvento(idPersona, id);
    }

    @PostMapping("{idPersona}/eventi")
    public EventoTrans postEvent(@PathVariable Integer idPersona,
                            @RequestHeader("userid") String userData,
                            @RequestBody EventoWrapper eventoWrapper) {
        return this.personaService.addEvento(idPersona, eventoWrapper, userData);
    }

    @PatchMapping("{idPersona}/eventi/{id}")
    public EventoTrans patchEvent(@PathVariable Integer idPersona,
                             @PathVariable Integer id,
                             @RequestHeader("userid") String userData,
                             @RequestBody EventoWrapper eventoWrapper) {
        return this.personaService.patchEvento(idPersona, id, eventoWrapper, userData);
    }

    @DeleteMapping("{idPersona}/eventi/{id}")
    public String deleteEvent(@PathVariable Integer idPersona,
                              @PathVariable Integer id,
                              @RequestHeader("userid") String userData) {
        return this.personaService.deleteEvento(idPersona, id, userData);
    }

    @GetMapping("{idPersona}/files/{id}")
    public FileTrans getFile(@PathVariable Integer idPersona,
                               @PathVariable Integer id) {
        return this.personaService.getFile(idPersona, id);
    }

    @PostMapping(path ="/{idPersona}/files", consumes = {"multipart/form-data"})
    public File postFile(@PathVariable Integer idPersona,
                         @RequestParam(name = "file") MultipartFile multipartFile,
                         @RequestHeader("userid") String userData) {
        return this.personaService.addFile(idPersona, multipartFile, userData);
    }

    @DeleteMapping("/{idPersona}/files/{id}")
    public String deleteFile(@PathVariable Integer idPersona,
                             @PathVariable Integer id,
                             @RequestHeader("userid") String userData){
        return this.personaService.deleteFile(idPersona, id, userData);
    }

    @GetMapping("")
    public ResponseList getAllPersona(
            @RequestParam(defaultValue = "") String filter,
            @RequestParam(defaultValue = "") String value,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer results
    ) {
        return this.personaService.getListPersone(filter, value, sort, page, results);
    }

    @GetMapping("{id}")
    public PersonaTrans getSinglePersona(@PathVariable Integer id) {
        return this.personaService.getPersona(id);
    }

    @PostMapping("")
    public PersonaTrans postPersona(
            @RequestBody PersonaWrapper personaWrapper,
            @RequestParam(defaultValue = "false") String forceOperation,
            @RequestHeader("userid") String userData
            ) {
        return this.personaService.addPersona(personaWrapper, userData, forceOperation);
    }

    @PostMapping("/private")
    public String postPersonaDaFormHomePage(
            @RequestBody PersonaWrapper personaWrapper
    ) {
        return this.personaService.addPersona(personaWrapper);
    }

    @PatchMapping("{id}")
    public PersonaTrans patchPersona(
            @PathVariable Integer id,
            @RequestBody PersonaWrapper personaWrapper,
            @RequestParam(defaultValue = "false") String forceOperation,
            @RequestHeader("userid") String userData
    ) {
        return this.personaService.patchPersona(id, personaWrapper, userData, forceOperation);
    }

    @DeleteMapping("{id}")
    public String deletePersona(@PathVariable Integer id,
                                @RequestHeader("userid") String userData) {
        return this.personaService.deletePersona(id, userData);
    }

    // second, minute, hour, day of month, month, day(s) of week
    // ogni primo del mese rimuovi tutti i contatti obsoleti
    @Scheduled(cron = "00 00 00 1 * ?")
    public void removeUnnecessaryData() {
        this.personaService.removeUnnecessaryPeople();
    }

}
