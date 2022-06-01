package my.service.repositories;

import my.service.entities.Budget;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class BudgetRepositoryHibernateImplementation implements BudgetRepository {

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Budget> getAllOperations(String whereClause, String sortClause, Integer page, Integer results) {

        Session session = this.entityManager.unwrap(Session.class);
        Query<Budget> query = session.createQuery("FROM Budget"+whereClause+sortClause);
        Integer offset = (page - 1)*results;
        query.setFirstResult(offset);
        query.setMaxResults(results);
        List<Budget> operationsList = query.getResultList();
        return operationsList;
    }

    @Override
    public Budget getOneOperation(Integer id) {
        Session session = this.entityManager.unwrap(Session.class);
        return session.get(Budget.class, id);
    }

    @Override
    public Budget postOperation(Budget budget) {
        Session session = this.entityManager.unwrap(Session.class);
        session.save(budget);
        return budget;
    }

    @Override
    public Boolean deleteOperation(Integer id) {
        Session session = this.entityManager.unwrap(Session.class);
        Query<Budget> deleteQuery = session.createQuery("delete from Budget where id=:id");
        deleteQuery.setParameter("id", id);
        Integer result = deleteQuery.executeUpdate();
        return result==1;
    }
}
