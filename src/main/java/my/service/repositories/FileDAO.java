package my.service.repositories;

import my.service.entities.File;
import my.service.entities.Immobile;
import my.service.entities.Persona;

import java.util.List;

public interface FileDAO {

    public List<File> getPhoto(Immobile immobile);

    public File getPhoto(Immobile immobile, Integer posizione);

    public Boolean isFileAlreadyExisting(Persona persona);

    public File readFile(String name);

    public File readFile(Integer id);

    public Boolean isFileAlreadyExisting(Immobile immobile, String name);

    public File addFile(File file);

    public Boolean removeFile(Integer id);

}
