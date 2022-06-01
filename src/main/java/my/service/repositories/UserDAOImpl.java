package my.service.repositories;

import my.service.entities.User;
import my.service.utilities.ListHandler;
import my.service.utilities.QueryClause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Repository
public class UserDAOImpl implements UserDAO{

    @Autowired
    EntityManager entityManager;

    @Override
    public List<User> getAllUsers(QueryClause whereClause, String sortClause, Integer page, Integer results) {
        Query query = this.entityManager.createQuery("from User"+whereClause.getQueryText()+sortClause);
        query = ListHandler.applyQueryClause(query, whereClause.getQueryParams());
        query = ListHandler.implementPagination(query, page, results);
        return query.getResultList();
    }

    @Override
    public Long getListNumber(QueryClause whereClause) {
        Query query = this.entityManager.createQuery("select COUNT(*) from User "+whereClause.getQueryText());
        query = ListHandler.applyQueryClause(query, whereClause.getQueryParams());
        return (Long) query.getSingleResult();
    }

    @Override
    public User getOneUser(String id) {
        return this.entityManager.find(User.class, id);
    }

    @Override
    public User addUser(User user) {
        return this.entityManager.merge(user);
    }

    @Override
    public void deleteUser(User user) {
        this.entityManager.remove(user);
    }
}
