package my.service.services;

import my.service.entities.File;
import my.service.entities.Log;
import my.service.transporters.FileTrans;
import my.service.transporters.ImmobileTrans;
import my.service.utilities.ResponseList;
import my.service.wrappers.ImmobileWrapper;
import my.service.wrappers.StringWrapper;
import org.springframework.web.multipart.MultipartFile;

public interface ImmobileService {

    public ResponseList getAllImmobili(String filter,
                                       String value,
                                       String min,
                                       String max,
                                       String startDate,
                                       String endDate,
                                       String sort,
                                       Integer pageNumber,
                                       Integer numberOfResults,
                                       String contratto,
                                       String categoria,
                                       String priceMin,
                                       String priceMax,
                                       String userData);

    public ImmobileTrans getOneImmobile(Integer id, String userData);

    public ImmobileTrans postImmobile(ImmobileWrapper immobileWrapper, String userData);

    public ImmobileTrans postImmobile(ImmobileWrapper immobileWrapper);

    public ImmobileTrans patchImmobile(Integer id, ImmobileWrapper patchImmobile, String userData);

    public String deleteImmobile(Integer id, String userData);

    public ImmobileTrans duplicateImmobile(Integer id, String userData);

    public ResponseList getAllLogs(Integer idImmobile,
                                String filter,
                                String value,
                                String startDate,
                                String endDate,
                                String sort,
                                Integer pageNumber,
                                Integer numberOfResults);

    public Log getOneLog(Integer idImmobile, Integer idLog);

    public Log addLog(Integer idImmobile, String azione, String userData);

    public Log patchLog(Integer idImmobile, Integer idLog, String newAzione, String userData);

    public String deleteLog(Integer idImmobile, Integer idLog, String userData);

    public FileTrans getFile(Integer idImmobile, Integer id, String photoType);

    public File addFile(Integer idImmobile, MultipartFile multipartFile, String userData);

    public File patchFile(Integer idImmobile, Integer id, StringWrapper file, String userData);

    public String deleteFile(Integer idImmobile, Integer id, String userData);

}
