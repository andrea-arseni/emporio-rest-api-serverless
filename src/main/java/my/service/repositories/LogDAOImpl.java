package my.service.repositories;

import my.service.entities.Log;
import my.service.utilities.ListHandler;
import my.service.utilities.QueryClause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Repository
public class LogDAOImpl implements LogDAO{

    @Autowired
    EntityManager entityManager;

    @Override
    public List<Log> getLogs(QueryClause whereClause, String sortClause, Integer page, Integer numberOfResults) {
        Query query = this.entityManager.createQuery("from Log"+whereClause.getQueryText()+sortClause);
        query = ListHandler.applyQueryClause(query, whereClause.getQueryParams());
        query = ListHandler.implementPagination(query, page, numberOfResults);
        return query.getResultList();
    }

    @Override
    public Long getListNumber(QueryClause whereClause) {
        Query query = this.entityManager.createQuery("select COUNT(*) from Log "+whereClause.getQueryText());
        query = ListHandler.applyQueryClause(query, whereClause.getQueryParams());
        return (Long) query.getSingleResult();
    }

    @Override
    public Log addLog(Log log) {
        return this.entityManager.merge(log);
    }

    @Override
    public Boolean removeLog(Integer idLog) {
        Query query = this.entityManager.createQuery("DELETE FROM Log WHERE id = :id");
        query.setParameter("id", idLog);
        Integer res = query.executeUpdate();
        return res==1;
    }
}
