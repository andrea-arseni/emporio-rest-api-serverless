package my.service.repositories;

import my.service.entities.Visita;
import my.service.utilities.ListHandler;
import my.service.utilities.QueryClause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Repository
public class VisitaDAOImpl implements VisitaDAO {

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Visita> getListVisite(QueryClause whereClause, String sortClause, Integer page, Integer results) {
        Query query = this.entityManager.createQuery("FROM Visita "+whereClause.getQueryText()+sortClause);
        query = ListHandler.applyQueryClause(query, whereClause.getQueryParams());
        query = ListHandler.implementPagination(query, page, results);
        return query.getResultList();
    }

    @Override
    public Long getListNumber(QueryClause whereClause) {
        Query query = this.entityManager.createQuery("select COUNT(*) from Visita "+whereClause.getQueryText());
        query = ListHandler.applyQueryClause(query, whereClause.getQueryParams());
        return (Long) query.getSingleResult();
    }

    @Override
    public Visita getVisita(Integer id) {
        return this.entityManager.find(Visita.class, id);
    }

    @Override
    public Visita addVisita(Visita visita) {
        return this.entityManager.merge(visita);
    }

    @Override
    public void deleteVisita(Visita visita) {
        this.entityManager.remove(visita);
    }
}
