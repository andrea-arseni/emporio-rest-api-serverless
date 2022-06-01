package my.service.services;

import my.service.entities.File;
import my.service.transporters.EventoTrans;
import my.service.transporters.FileTrans;
import my.service.transporters.PersonaTrans;
import my.service.utilities.ResponseList;
import my.service.wrappers.EventoWrapper;
import my.service.wrappers.PersonaWrapper;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PersonaService {

    public ResponseList getListPersone(String filter,
                                             String value,
                                             String sort,
                                             Integer pageNumber,
                                             Integer numberOfResults);

    public PersonaTrans getPersona(Integer id);

    public PersonaTrans addPersona(PersonaWrapper personaWrapper, String userData, String forceOperation);

    public String addPersona(PersonaWrapper personaWrapper);

    public PersonaTrans patchPersona(Integer id, PersonaWrapper personaWrapper, String userData, String forceOperation);

    public String deletePersona(Integer id, String userData);

    public ResponseList getListEventi(Integer idPersona,
                                      String filter,
                                      String value,
                                      String startDate,
                                      String endDate,
                                      String sort,
                                      Integer pageNumber,
                                      Integer numberOfResults);

    public EventoTrans getEvento(Integer idPersona, Integer idEvento);

    public EventoTrans addEvento(Integer idPersona, EventoWrapper eventoWrapper, String userData);

    public EventoTrans patchEvento(Integer idPersona, Integer idEvento, EventoWrapper eventoWrapper, String userData);

    public String deleteEvento(Integer idPersona, Integer idEvento, String userData);

    public void removeUnnecessaryPeople();

    public FileTrans getFile(Integer idPersona, Integer id);

    public File addFile(Integer idPersona, MultipartFile multipartFile, String userData);

    public String deleteFile(Integer idPersona, Integer id, String userData);

}
