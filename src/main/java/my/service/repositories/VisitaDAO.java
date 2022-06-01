package my.service.repositories;

import my.service.entities.Visita;
import my.service.utilities.QueryClause;

import java.util.List;

public interface VisitaDAO {

    public List<Visita> getListVisite(QueryClause whereClause, String sortClause, Integer page, Integer results);

    public Long getListNumber(QueryClause whereClause);

    public Visita getVisita(Integer id);

    public Visita addVisita(Visita visita);

    public void deleteVisita(Visita visita);

}
