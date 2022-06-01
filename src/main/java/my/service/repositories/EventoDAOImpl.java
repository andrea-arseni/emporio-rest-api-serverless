package my.service.repositories;

import my.service.entities.Evento;
import my.service.utilities.ListHandler;
import my.service.utilities.QueryClause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Repository
public class EventoDAOImpl implements EventoDAO {

    @Autowired
    EntityManager entityManager;

    @Override
    public List<Evento> getEventList(QueryClause whereClause, String sortClause,
                                     Integer page, Integer numberOfResults) {
        Query query = this.entityManager.createQuery("from Evento "+whereClause.getQueryText());
        query = ListHandler.applyQueryClause(query, whereClause.getQueryParams());
        query = ListHandler.implementPagination(query, page, numberOfResults);
        List<Evento> eventi = query.getResultList();
        return eventi;
    }

    @Override
    public Long getListNumber(QueryClause whereClause){
        Query query = this.entityManager.createQuery("select COUNT(*) from Evento "+whereClause.getQueryText());
        query = ListHandler.applyQueryClause(query, whereClause.getQueryParams());
        return (Long) query.getSingleResult();
    }

    @Override
    public Evento addEvento(Evento evento) {
        return this.entityManager.merge(evento);
    }

    @Override
    public Boolean removeEvento(Integer id) {
        Query query = this.entityManager.createQuery("DELETE FROM Evento WHERE id=:id");
        query.setParameter("id", id);
        Integer res = query.executeUpdate();
        return res==1;
    }
}
