package my.service.services;

import my.service.entities.Budget;
import my.service.entities.User;
import my.service.repositories.BudgetRepositoryDataJPA;
import my.service.repositories.UserDAO;
import my.service.types.UserType;
import my.service.utilities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Service
@Primary
public class BudgetServiceImplDataJPA implements  BudgetService{

    @Autowired
    BudgetRepositoryDataJPA budgetRepository;

    @Autowired
    UserDAO userDAO;

    /* TUTTE LE CHIAMATE INERENTI OPERAZIONI FINANZIARIE POSSONO ESSERE GESTITE SOLO DA ADMINS */

    @Override
    public ResponseList getAllOperations(String filter, String value,
                                         String min, String max,
                                         String startDate, String endDate,
                                         String sort, Integer pageNumber, Integer numberOfResults,
                                         String userData) {
        this.checkUserAuthorization(userData);
        // build Sort Object
        Pageable page = (sort.equals("")) ?
                PageRequest.of(pageNumber, numberOfResults) :
                PageRequest.of(pageNumber, numberOfResults, Sort.by(ListHandler.getSortOrderList(sort)));

        List<Budget> results;
        Long count = 0L;

        filter = filter.trim().toLowerCase();
        // consider key
        if(filter.equals("")) {
            results = (List) this.budgetRepository.findByDescrizioneContainingIgnoreCase("", page);
            count = this.budgetRepository.countByDescrizioneContainingIgnoreCase("");
        }else if (filter.equals("descrizione")) {
            results = (List)this.budgetRepository.findByDescrizioneContainingIgnoreCase(value, page);
            count = this.budgetRepository.countByDescrizioneContainingIgnoreCase(value);
        }else if (filter.equals("data")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate startingDate = null;
            LocalDate endingDate = null;
            try {
                startingDate = LocalDate.parse(startDate, formatter);
                endingDate = LocalDate.parse(endDate, formatter);
            } catch (DateTimeParseException e) {
                throw new BadRequestException("Formato dei parametri 'startDate' e/o 'endDate' scorretto. Devono essere in formato 'yyyy-MM-dd'");
            }
            results = (List)this.budgetRepository.findByDataBetween(startingDate, endingDate, page);
            count = this.budgetRepository.countByDataBetween(startingDate, endingDate);
        }else if (filter.equals("importo")) {
            Integer minValue = null;
            Integer maxValue = null;
            try {
                minValue = Integer.parseInt(min);
                maxValue = Integer.parseInt(max);
            } catch (NumberFormatException e) {
                throw new BadRequestException("Formato dei parametri 'min' e/o 'max' scorretto. Devono essere solo numeri");
            }
            results = (List)this.budgetRepository.findByImportoBetween(minValue, maxValue, page);
            count = this.budgetRepository.countByImportoBetween(minValue, maxValue);
        }else{
            throw new BadRequestException("Filtro applicato '"+filter+"' non valido");
        }

        return new ResponseList(count, results);
    }

    @Override
    public Budget getOneOperation(Integer id, String userData) {
        this.checkUserAuthorization(userData);
        Optional<Budget> optional = this.budgetRepository.findById(id);
        if(!optional.isPresent()) throw new ItemNotFoundException("Operazione con id "+id+" non trovata");
        return optional.get();
    }

    @Override
    public Budget postOperation(Budget budget, String userData) {
        this.checkUserAuthorization(userData);
        User user = this.retrieveUser(userData);
        budget.setUser(user);
        return this.budgetRepository.save(budget);
    }

    @Override
    public Budget patchOperation(Integer id, Budget patchBudget, String userData) {
        // lo user è chi effettua l'azione
        patchBudget.setUser(null);
        // retrieve budget originale
        Optional<Budget> optional = this.budgetRepository.findById(id);
        if(!optional.isPresent()) throw new ItemNotFoundException("Operazione con id "+id+" non trovata");
        Budget originalBudget = optional.get();
        // se user che effettua l'azione non è userOriginale e non è admin throw error
        this.checkUsers(userData, originalBudget);
        // patch
        ObjectPatcher.patchObject(Budget.class, optional.get(), patchBudget);
        originalBudget.setUser(this.retrieveUser(userData));
        return this.budgetRepository.save(originalBudget);
    }

    @Override
    public String deleteOperation(Integer id, String userData) {
        Optional<Budget> optional = this.budgetRepository.findById(id);
        if(!optional.isPresent()) throw new ItemNotFoundException("Operazione con id "+id+" non trovata");
        Budget budget = optional.get();
        this.checkUsers(userData, budget);
        this.budgetRepository.delete(budget);
        return "Operazione con id "+id+" cancellata con successo";
    }

    private User retrieveUser(String userData){
        User user = UserRetriever.retrieveUser(userData);
        User userFound = this.userDAO.getOneUser(user.getId());
        if(userFound==null) this.userDAO.addUser(user);
        return userFound;
    }

    private void checkUserAuthorization(String userData){
        User user = this.retrieveUser(userData);
        if (!user.getRole().equalsIgnoreCase(UserType.ADMIN.name())){
            throw new ForbiddenException(user.getName()+" non autorizzato ad effettuare l'operazione");
        }
    }

    private void checkUsers(String userData, Budget budget){
        User currentUser = this.retrieveUser(userData);
        User originalUser = budget.getUser();
        if(originalUser!=null &&
                !currentUser.getId().equalsIgnoreCase(originalUser.getId()) &&
                !currentUser.getRole().equalsIgnoreCase(UserType.ADMIN.name())){
            throw new ForbiddenException(currentUser.getName()+" non autorizzato ad effettuare l'operazione");
        }
    }
}
