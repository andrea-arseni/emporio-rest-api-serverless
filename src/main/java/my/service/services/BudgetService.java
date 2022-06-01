package my.service.services;

import my.service.entities.Budget;
import my.service.utilities.ResponseList;

public interface BudgetService {

    public ResponseList getAllOperations(String filter,
                                         String value,
                                         String min,
                                         String max,
                                         String startDate,
                                         String endDate,
                                         String sort,
                                         Integer pageNumber,
                                         Integer numberOfResults,
                                         String userData);

    public Budget getOneOperation(Integer id, String userData);

    public Budget postOperation(Budget budget, String userData);

    public Budget patchOperation(Integer id, Budget patchBudget, String userData);

    public String deleteOperation(Integer id, String userData);

}
