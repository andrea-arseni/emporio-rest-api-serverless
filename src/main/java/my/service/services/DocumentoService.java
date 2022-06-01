package my.service.services;

import my.service.entities.File;
import my.service.transporters.FileTrans;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentoService {

    FileTrans readFile(String name);

    File addFile(MultipartFile multipartFile, String name, String userData);

    File renameFile(Integer id, String updatedName, String userData);

    String deleteFile(Integer id, String userData);

}
