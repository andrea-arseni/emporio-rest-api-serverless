package my.service.services;

import my.service.entities.Lavoro;
import my.service.entities.Step;
import my.service.utilities.ResponseList;
import my.service.wrappers.StepWrapper;

import java.util.List;

public interface LavoroService {

    public ResponseList getAllLavori(String filter,
                                     String value,
                                     String sort,
                                     Integer pageNumber,
                                     Integer numberOfResults);

    public Lavoro getOneLavoro(Integer id);

    public Lavoro postLavoro(Lavoro lavoro, String userData);

    public Lavoro patchLavoro(Integer id, Lavoro patchLavoro, String userData);

    public String deleteLavoro(Integer id, String userData);

    public ResponseList getAllSteps(Integer idLavoro, String filter,
                                  String value,
                                  String startDate,
                                  String endDate,
                                  String sort,
                                  Integer pageNumber,
                                  Integer numberOfResults);

    public Step getOneStep(Integer idLavoro, Integer idStep);

    public Step postStep(Integer idLavoro, StepWrapper stepwrapper, String userData);

    public Step patchStep(Integer idLavoro, Integer idStep, Step patchStep, String userData);

    public String deleteStep(Integer idLavoro, Integer idStep, String userData);

}
