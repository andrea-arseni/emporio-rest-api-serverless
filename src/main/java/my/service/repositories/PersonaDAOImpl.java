package my.service.repositories;

import my.service.entities.Persona;
import my.service.utilities.ListHandler;
import my.service.utilities.QueryClause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;

@Repository
public class PersonaDAOImpl implements PersonaDAO {

    @Autowired
    EntityManager entityManager;

    @Override
    public List<Persona> getListPersone(QueryClause whereClause, String sortClause, Integer page, Integer results) {
        Query query = this.entityManager.createQuery("from Persona"+whereClause.getQueryText()+sortClause);
        query = ListHandler.applyQueryClause(query, whereClause.getQueryParams());
        query = ListHandler.implementPagination(query, page, results);
        List<Persona> persone = query.getResultList();
        return persone;
    }

    @Override
    public Long getListNumber(QueryClause whereClause) {
        Query query = this.entityManager.createQuery("select COUNT(*) from Persona "+whereClause.getQueryText());
        query = ListHandler.applyQueryClause(query, whereClause.getQueryParams());
        return (Long) query.getSingleResult();
    }

    @Override
    public Persona getOnePersona(Integer id) {
        return this.entityManager.find(Persona.class, id);
    }

    @Override
    public Persona getOnePersona(String whereClause, Map<String, String> fieldsToCkeck) {
        Query query = this.entityManager.createQuery("from Persona"+whereClause);
        query = ListHandler.applyQueryClause(query, fieldsToCkeck);
        List<Persona> persone = query.getResultList();
        return persone.size()==0 ? null : persone.get(0);
    }

    @Override
    public Persona addPersona(Persona persona) {
        return this.entityManager.merge(persona);
    }

    @Override
    public void deletePersona(Persona persona) {
        this.entityManager.remove(persona);
    }
}
