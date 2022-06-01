package my.service.repositories;

import my.service.entities.Budget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Repository
public class BudgetRepositoryJPAImplementation implements BudgetRepository {

    @Autowired
    EntityManager entityManager;

    @Override
    public List<Budget> getAllOperations(String whereClause, String sortClause, Integer page, Integer results) {
        Query query = this.entityManager.createQuery("FROM Budget"+whereClause+sortClause);
        Integer offset = (page - 1)*results;
        query.setFirstResult(offset);
        query.setMaxResults(results);
        List<Budget> operationsList = query.getResultList();
        return operationsList;
    }

    @Override
    public Budget getOneOperation(Integer id) {
        return this.entityManager.find(Budget.class, id);
    }

    @Override
    public Budget postOperation(Budget budget) { return this.entityManager.merge(budget); }

    @Override
    public Boolean deleteOperation(Integer id) {
         Query query = this.entityManager.createQuery("DELETE FROM Budget WHERE id=:id");
         query.setParameter("id", id);
         Integer result = query.executeUpdate();
         return result==1;
    }
}
