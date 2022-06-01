package my.service.repositories;

import my.service.entities.Immobile;
import my.service.utilities.ListHandler;
import my.service.utilities.QueryClause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Repository
public class ImmobileDAOImpl implements ImmobileDAO{

    @Autowired
    EntityManager entityManager;

    @Override
    public List<Immobile> getAllImmobili(QueryClause whereClause, String sortClause, Integer page, Integer results) {
        Query query = this.entityManager.createQuery("from Immobile"+whereClause.getQueryText()+sortClause);
        query = ListHandler.applyQueryClause(query, whereClause.getQueryParams());
        query = ListHandler.implementPagination(query, page, results);
        return query.getResultList();
    }

    @Override
    public Long getListNumber(QueryClause whereClause) {
        Query query = this.entityManager.createQuery("select COUNT(*) from Immobile "+whereClause.getQueryText());
        query = ListHandler.applyQueryClause(query, whereClause.getQueryParams());
        return (Long) query.getSingleResult();
    }

    @Override
    public Immobile getOneImmobile(Integer id) {
        return this.entityManager.find(Immobile.class, id);
    }

    @Override
    public Immobile addImmobile(Immobile immobile) {
        return this.entityManager.merge(immobile);
    }

    @Override
    public void deleteImmobile(Immobile immobile) {
        this.entityManager.remove(immobile);
    }

    @Override
    public Integer getLastRef() {
        Query query = this.entityManager.createQuery("SELECT MAX(ref) FROM Immobile");
        List<Integer> results = query.getResultList();
        return results.size()==0 ? 0 : results.get(0);
    }
}
