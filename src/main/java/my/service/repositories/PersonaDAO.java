package my.service.repositories;

import my.service.entities.Persona;
import my.service.utilities.QueryClause;

import java.util.List;
import java.util.Map;

public interface PersonaDAO {

    public List<Persona> getListPersone(QueryClause whereClause, String sortClause, Integer page, Integer results);

    public Long getListNumber(QueryClause whereClause);

    public Persona getOnePersona(Integer id);

    public Persona getOnePersona(String whereClause, Map<String, String> fieldsToCheck);

    public Persona addPersona(Persona persona);

    public void deletePersona(Persona persona);

}
