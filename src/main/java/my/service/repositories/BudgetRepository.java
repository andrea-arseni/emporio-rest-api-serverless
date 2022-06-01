package my.service.repositories;

import my.service.entities.Budget;

import java.util.List;

public interface BudgetRepository {

    public List<Budget> getAllOperations(String filterClause, String sortClause, Integer page, Integer results);

    public Budget getOneOperation(Integer id);

    public Budget postOperation(Budget budget);

    public Boolean deleteOperation(Integer id);

}
