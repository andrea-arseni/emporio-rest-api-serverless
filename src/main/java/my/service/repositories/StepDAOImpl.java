package my.service.repositories;

import my.service.entities.Step;
import my.service.utilities.ListHandler;
import my.service.utilities.QueryClause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Repository
public class StepDAOImpl implements StepDAO{

    @Autowired
    EntityManager entityManager;

    @Override
    public List<Step> getSteps(QueryClause whereClause, String sortClause, Integer page, Integer numberOfResults) {
        Query query = this.entityManager.createQuery("from Step"+whereClause.getQueryText()+sortClause);
        query = ListHandler.applyQueryClause(query, whereClause.getQueryParams());
        query = ListHandler.implementPagination(query, page, numberOfResults);
        List<Step> steps = query.getResultList();
        return steps;
    }

    @Override
    public Long getListNumber(QueryClause whereClause) {
        Query query = this.entityManager.createQuery("select COUNT(*) from Step "+whereClause.getQueryText());
        query = ListHandler.applyQueryClause(query, whereClause.getQueryParams());
        return (Long) query.getSingleResult();
    }

    @Override
    public Step addStep(Step step) {
        return this.entityManager.merge(step);
    }

    @Override
    public Boolean deleteStep(Integer id) {
        Query query = this.entityManager.createQuery("DELETE FROM Step WHERE id=:id");
        query.setParameter("id", id);
        Integer res = query.executeUpdate();
        return res==1;
    }
}
