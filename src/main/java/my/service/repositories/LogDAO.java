package my.service.repositories;

import my.service.entities.Log;
import my.service.utilities.QueryClause;

import java.util.List;

public interface LogDAO {

    public List<Log> getLogs(QueryClause whereClause, String sortClause, Integer page, Integer numberOfResults);

    public Long getListNumber(QueryClause whereClause);

    public Log addLog(Log log);

    public Boolean removeLog(Integer idLog);

}
