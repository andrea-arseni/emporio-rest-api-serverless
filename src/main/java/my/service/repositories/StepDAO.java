package my.service.repositories;

import my.service.entities.Step;
import my.service.utilities.QueryClause;

import java.util.List;

public interface StepDAO {

    public List<Step> getSteps(QueryClause whereClause, String sortClause, Integer page, Integer numberOfResults);

    public Long getListNumber(QueryClause whereClause);

    public Step addStep(Step step);

    public Boolean deleteStep(Integer id);

}
