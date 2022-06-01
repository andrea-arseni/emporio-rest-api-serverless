package my.service.repositories;

import my.service.entities.Evento;
import my.service.utilities.QueryClause;

import java.util.List;

public interface EventoDAO {

    public List<Evento> getEventList(QueryClause whereQueryClause, String sortClausText,
                                     Integer page, Integer numberOfResults);

    public Long getListNumber(QueryClause whereClause);

    public Evento addEvento(Evento evento);

    public Boolean removeEvento(Integer idEvento);

}
