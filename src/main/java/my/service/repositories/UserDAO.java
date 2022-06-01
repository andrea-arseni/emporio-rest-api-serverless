package my.service.repositories;

import my.service.entities.User;
import my.service.utilities.QueryClause;

import java.util.List;

public interface UserDAO {

    public List<User> getAllUsers(QueryClause whereClause, String sortClause, Integer page, Integer results);

    public Long getListNumber(QueryClause whereClause);

    public User getOneUser(String id);

    public User addUser(User user);

    public void deleteUser(User user);

}
