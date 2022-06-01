package my.service.services;

import my.service.entities.Budget;
import my.service.repositories.BudgetRepository;
import my.service.utilities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BudgetServiceImplementation implements BudgetService{

    @Autowired
    @Qualifier("budgetRepositoryJPAImplementation")
    private BudgetRepository budgetRepository;

    @Override
    @Transactional
    public ResponseList getAllOperations(String filter,
                                         String value,
                                         String min,
                                         String max,
                                         String startDate,
                                         String endDate,
                                         String sort,
                                         Integer pageNumber,
                                         Integer numberOfResults,
                                         String userData) {

        QueryClause whereClause = ListHandler.getQueryWhereClause(Budget.class, filter, "", 0,
                value, startDate, endDate, min, max);
        String sortClause = ListHandler.getSortClause(Budget.class, sort);

        List<Budget> res = budgetRepository.getAllOperations(whereClause.getQueryText(), sortClause, pageNumber, numberOfResults);
        return new ResponseList(null, res);
    }

    @Override
    @Transactional
    public Budget getOneOperation(Integer id, String userData) {
        Budget budgetFound = budgetRepository.getOneOperation(id);
        if(budgetFound==null) throw new ItemNotFoundException("Operazione con id "+id+" non presente");
        return budgetFound;
    }

    @Override
    @Transactional
    public Budget postOperation(Budget budget, String userData) {
        return budgetRepository.postOperation(budget);
    }

    @Override
    @Transactional
    public Budget patchOperation(Integer id, Budget patchBudget, String userData) {

        // retrieve originalObject, if not found throw error
        Budget originalBudget = budgetRepository.getOneOperation(id);
        if(originalBudget==null) throw new ItemNotFoundException("Operazione con id "+id+" non presente");

        return (Budget) ObjectPatcher.patchObject(Budget.class, originalBudget, patchBudget);
    }

    @Override
    @Transactional
    public String deleteOperation(Integer id, String userData) {
        if(!budgetRepository.deleteOperation(id)) throw new ItemNotFoundException("Operazione con id "+id+" non trovata");
        return "Operazione con id "+id+" cancellata con successo";
    }
}
