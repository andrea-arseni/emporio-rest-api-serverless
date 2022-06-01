package my.service.repositories;

import my.service.entities.Lavoro;
import my.service.utilities.QueryClause;

import java.util.List;

public interface LavoroDAO {

    public List<Lavoro> getAllLavori(QueryClause whereClause, String sortClause, Integer page, Integer results);

    public Long getListNumber(QueryClause whereClause);

    public Lavoro getOneLavoro(Integer id);

    public Lavoro addLavoro(Lavoro lavoro);

    public void deleteLavoro(Lavoro lavoro);

}
