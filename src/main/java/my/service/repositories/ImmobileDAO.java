package my.service.repositories;

import my.service.entities.Immobile;
import my.service.utilities.QueryClause;

import java.util.List;

public interface ImmobileDAO {

    public List<Immobile> getAllImmobili(QueryClause whereClause, String sortClause, Integer page, Integer results);

    public Long getListNumber(QueryClause whereClause);

    public Immobile getOneImmobile(Integer id);

    public Immobile addImmobile(Immobile immobile);

    public void deleteImmobile(Immobile immobile);

    public Integer getLastRef();

}
