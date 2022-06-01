package my.service.utilities;

import my.service.types.FileStorageType;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class FileUtils {

    public static File convertMultipartToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fileOutputStream = new FileOutputStream(convertedFile);
        fileOutputStream.write(file.getBytes());
        fileOutputStream.close();
        return convertedFile;
    }

    public static String getFileName(MultipartFile multipartFile) {
        return Objects.requireNonNull(multipartFile.getOriginalFilename()).replace(" ", "_");
    }

    public static String getExtension(MultipartFile multipartFile){
        return FilenameUtils.getExtension(multipartFile.getOriginalFilename()).toLowerCase();
    }

    public static void validateExtension(String extension, FileStorageType type){
        List<String> validExtensions = (type.name().equals(FileStorageType.IMMOBILI.name())) ?
                Arrays.asList("jpeg", "jpg", "png", "pdf"): Arrays.asList("doc", "docx", "odt", "pdf", "csv", "xls", "xlsx");
        if (!validExtensions.contains(extension)){
            String list = "";
            for(String ext : validExtensions) list = list+ "\""+ext+"\" ";
            throw new BadRequestException("Estensione file non valida. Elenco estensioni valide: "+list);
        }

    }

}