package my.service.services;

import my.service.transporters.VisitaTrans;
import my.service.utilities.ResponseList;
import my.service.wrappers.VisitaWrapper;

public interface VisitaService {

    public ResponseList getListVisite(String filter,
                                      String value,
                                      String startDate,
                                      String endDate,
                                      String sort,
                                      Integer pageNumber,
                                      Integer numberOfResults,
                                      Integer immobile,
                                      Integer persona);

    public VisitaTrans getVisita(Integer id);

    public VisitaTrans addVisita(VisitaWrapper visitaWrapper, String userData);

    public VisitaTrans patchVisita(Integer id, VisitaWrapper visitaWrapper, String userData);

    public String deleteVisita(Integer id, String userData);

}
