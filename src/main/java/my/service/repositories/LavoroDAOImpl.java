package my.service.repositories;

import my.service.entities.Lavoro;
import my.service.utilities.ListHandler;
import my.service.utilities.QueryClause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Repository
public class LavoroDAOImpl implements LavoroDAO{

    @Autowired
    EntityManager entityManager;

    @Override
    public List<Lavoro> getAllLavori(QueryClause whereClause, String sortClause, Integer page, Integer results) {
        Query query = this.entityManager.createQuery("from Lavoro"+whereClause.getQueryText()+sortClause);
        query = ListHandler.applyQueryClause(query, whereClause.getQueryParams());
        query = ListHandler.implementPagination(query, page, results);
        return query.getResultList();
    }

    @Override
    public Long getListNumber(QueryClause whereClause) {
        Query query = this.entityManager.createQuery("select COUNT(*) from Lavoro "+whereClause.getQueryText());
        query = ListHandler.applyQueryClause(query, whereClause.getQueryParams());
        return (Long) query.getSingleResult();
    }

    @Override
    public Lavoro getOneLavoro(Integer id) {
        return this.entityManager.find(Lavoro.class, id);
    }

    @Override
    public Lavoro addLavoro(Lavoro lavoro) {
        return this.entityManager.merge(lavoro);
    }

    @Override
    public void deleteLavoro(Lavoro lavoro) {
        this.entityManager.remove(lavoro);
    }
}
