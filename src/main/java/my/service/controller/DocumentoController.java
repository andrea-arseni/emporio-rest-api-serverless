package my.service.controller;

import my.service.entities.File;
import my.service.services.DocumentoService;
import my.service.transporters.FileTrans;
import my.service.utilities.BadRequestException;
import my.service.utilities.ResponseList;
import my.service.wrappers.StringWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/documenti")
public class DocumentoController {

    @Autowired
    DocumentoService documentoService;

    @GetMapping("")
    public FileTrans getSingleDocument(@RequestParam(defaultValue = "") String name) {
        if(name.equals("")) throw new BadRequestException("Necessario indicare il nome del documento da leggere");
        return this.documentoService.readFile(name);
    }

    @PostMapping(path = "", consumes = {"multipart/form-data"})
    public File postDocumento(
            @RequestParam(name = "file") MultipartFile multipartFile,
            @RequestHeader("userid") String userData,
            @RequestParam(name = "name") String name
            ) {
        return this.documentoService.addFile(multipartFile, name, userData);
    }

    @PatchMapping("/{id}")
    public File renameDocumento(
            @RequestHeader("userid") String userData,
            @RequestBody StringWrapper name,
            @PathVariable Integer id
    ) {
        return this.documentoService.renameFile(id, name.getName(), userData);
    }

    @DeleteMapping("/{id}")
    public String deleteDocumento(
            @RequestHeader("userid") String userData,
            @PathVariable Integer id) {
        return this.documentoService.deleteFile(id, userData);
    }

}
