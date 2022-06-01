package my.service.repositories;

import my.service.entities.Budget;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface BudgetRepositoryDataJPA extends CrudRepository<Budget, Integer> {

    List<Budget> findByDescrizioneContainingIgnoreCase(String descrizione, Pageable pageable);

    Long countByDescrizioneContainingIgnoreCase(String descrizione);

    List<Budget> findByDataBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    Long countByDataBetween(LocalDate startDate, LocalDate endDate);

    List<Budget> findByImportoBetween(Integer min, Integer max, Pageable pageable);

    Long countByImportoBetween(Integer min, Integer max);

}
