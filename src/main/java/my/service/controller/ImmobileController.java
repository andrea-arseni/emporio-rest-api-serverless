package my.service.controller;

import my.service.entities.File;
import my.service.entities.Log;
import my.service.services.ImmobileService;
import my.service.transporters.FileTrans;
import my.service.transporters.ImmobileTrans;
import my.service.utilities.ResponseList;
import my.service.wrappers.ImmobileWrapper;
import my.service.wrappers.StringWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/immobili")
public class ImmobileController {

    @Autowired
    private ImmobileService immobileService;

    /* IMMOBILE PART */

    @GetMapping("")
    public ResponseList getAllImmobili(
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
            @RequestParam(defaultValue = "1") Integer page,
            // numero di elementi per pagina
            @RequestParam(defaultValue = "20") Integer results,
            // chiave che definisce quale campo interrogare per il filtro
            @RequestParam(defaultValue = "TUTTI") String contratto,
            // chiave che definisce quale campo interrogare per il filtro
            @RequestParam(defaultValue = "TUTTI") String categoria,
            @RequestParam(defaultValue = "0") String priceMin,
            // chiave che definisce quale campo interrogare per il filtro
            @RequestParam(defaultValue = "10000000") String priceMax,
            @RequestHeader(value = "userid", required = false) String userData
    ) {
        return this.immobileService.getAllImmobili(filter, value, min, max, startDate, endDate,
                sort, page, results, contratto, categoria, priceMin, priceMax, userData);
    }

    @GetMapping("/{id}")
    public ImmobileTrans getSingleImmobile(@PathVariable Integer id, @RequestHeader(value="userid", required = false) String userData) {
        return this.immobileService.getOneImmobile(id, userData);
    }

    @PostMapping("")
    public ImmobileTrans postImmobile(@RequestBody ImmobileWrapper immobileWrapper, @RequestHeader("userid") String userData) {
        return this.immobileService.postImmobile(immobileWrapper, userData);
    }

    @PostMapping("/{id}/duplicate")
    public ImmobileTrans copyImmobile(@PathVariable Integer id, @RequestHeader("userid") String userData) {
        return this.immobileService.duplicateImmobile(id, userData);
    }

    @PatchMapping("/{id}")
    public ImmobileTrans patchImmobile(
            @PathVariable Integer id,
            @RequestBody ImmobileWrapper immobile,
            @RequestHeader("userid") String userData) {
        return this.immobileService.patchImmobile(id, immobile, userData);
    }

    @DeleteMapping("/{id}")
    public String deleteImmobile(@PathVariable Integer id,
                                 @RequestHeader("userid") String userData) {
        return this.immobileService.deleteImmobile(id, userData);
    }

    /* LOGS PART */

    @GetMapping("/{idImmobile}/logs")
    public ResponseList getLogs(@PathVariable Integer idImmobile,
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
        return this.immobileService.getAllLogs(idImmobile, filter, value, startDate, endDate, sort, page, results);
    }

    @GetMapping("/{idImmobile}/logs/{id}")
    public Log getLog(@PathVariable Integer idImmobile, @PathVariable Integer id){
        return this.immobileService.getOneLog(idImmobile, id);
    }

    @PostMapping("/{idImmobile}/logs")
    public Log postLog(@PathVariable Integer idImmobile,
                       @RequestBody Log log,
                       @RequestHeader("userid") String userData) {
        return this.immobileService.addLog(idImmobile, log.getAzione(), userData);
    }

    @PatchMapping("/{idImmobile}/logs/{id}")
    public Log patchLog(@PathVariable Integer idImmobile,
                        @PathVariable Integer id,
                        @RequestBody Log patchLog,
                        @RequestHeader("userid") String userData) {
        return this.immobileService.patchLog(idImmobile, id, patchLog.getAzione(), userData);
    }

    @DeleteMapping("/{idImmobile}/logs/{id}")
    public String deleteLog(@PathVariable Integer idImmobile,
                            @PathVariable Integer id,
                            @RequestHeader("userid") String userData){
        return this.immobileService.deleteLog(idImmobile, id, userData);
    }

    /* FILE PART */

    @GetMapping("/{idImmobile}/files/{id}")
    public FileTrans getFile(@PathVariable Integer idImmobile,
                             @PathVariable Integer id,
                             @RequestParam(defaultValue = "SIGNED") String photoType
    ){
        return this.immobileService.getFile(idImmobile, id, photoType);
    }

    @PostMapping(path ="/{idImmobile}/files", consumes = {"multipart/form-data"})
    public File postFile(@PathVariable Integer idImmobile,
                         @RequestParam(name = "file") MultipartFile multipartFile,
                         @RequestHeader("userid") String userData) {
        return this.immobileService.addFile(idImmobile, multipartFile, userData);
    }

    @PatchMapping("/{idImmobile}/files/{id}")
    public File patchFile(@PathVariable Integer idImmobile,
                        @PathVariable Integer id,
                        @RequestBody StringWrapper patchFile,
                          @RequestHeader("userid") String userData) {
        return this.immobileService.patchFile(idImmobile, id, patchFile, userData);
    }

    @DeleteMapping("/{idImmobile}/files/{id}")
    public String deleteFile(@PathVariable Integer idImmobile,
                            @PathVariable Integer id,
                            @RequestHeader("userid") String userData){
        return this.immobileService.deleteFile(idImmobile, id, userData);
    }
}
