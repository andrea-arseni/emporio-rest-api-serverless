package my.service.utilities;

import org.springframework.data.domain.Sort;

import javax.persistence.Query;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class ListHandler {

    public static String getSortClause(Class<?> objectFactory, String sort) {

        if (sort.equals("")) return "";

        String sortClause = "";
        Boolean firstSorting = true;

        sortClause = " ORDER BY ";
        String[] sortParameters = sort.split(",");
        for (String sortParam : sortParameters) {
            String sortParamName = sortParam.split("-")[0];
            String sortParamType = sortParam.split("-").length > 1 ? sortParam.split("-")[1] : "ASC";
            if (!sortParamType.equalsIgnoreCase("ASC") && !sortParamType.equalsIgnoreCase("DESC")) {
                throw new BadRequestException(sortParamName + " può essere ordinato solo ASC o DESC, non " + sortParamType);
            }
            if (!firstSorting) sortClause = sortClause.concat(",");
            Boolean sortFound = false;
            for (Field field : objectFactory.getDeclaredFields()) {
                if (field.getName().equalsIgnoreCase(sortParamName)) {
                    sortFound = true;
                    firstSorting = false;
                    sortClause = sortClause.concat(sortParamName + " " + sortParamType);
                    break;
                }
            }
            if (!sortFound) throw new BadRequestException("Parametro per ordinare " + sortParam + " non trovato");
        }
        sortClause = sortClause.concat(" ");

        return sortClause;

    }

    public static List<Sort.Order> getSortOrderList(String sort){
        List<Sort.Order> sortOrders = new ArrayList<>();
        String[] sortParameters = sort.split(",");
        for (String sortParam : sortParameters) {
            String sortParamName = sortParam.split("-")[0];
            String sortParamType = sortParam.split("-").length > 1 ? sortParam.split("-")[1] : "ASC";
            if (!sortParamType.equalsIgnoreCase("ASC") && !sortParamType.equalsIgnoreCase("DESC")) {
                throw new BadRequestException(sortParamName + " può essere ordinato solo ASC o DESC, non " + sortParamType);
            }
            Sort.Order order = sortParamType.equalsIgnoreCase("ASC") ? Sort.Order.asc(sortParamName) : Sort.Order.desc(sortParamName);
            sortOrders.add(order);
        }
        return sortOrders;
    }

    public static Query implementPagination(Query query, Integer pageNumber, Integer numberOfResults){
        Integer offset = (pageNumber - 1)*numberOfResults;
        query.setFirstResult(offset);
        query.setMaxResults(numberOfResults);
        return query;
    }

    public static Query applyQueryClause(Query query, Map<String, String> params){
        for(String key : params.keySet()){
                query.setParameter(key, params.get(key));
        }
        return query;
    }

    public static QueryClause getQueryWhereClause(Class<?> objectFactory, String filter, String parentName,
                                                  Integer parentId, String value, String startDate, String endDate,
                                                  String min, String max) {

        // se filtro = "" e parentName = "" vai via
        if (filter.equals("") && parentName.equals("")) return new QueryClause("", new HashMap<>());

        String whereClause = "";
        Map<String,String> whereClauseParams = new HashMap<>();

        // se c'è parentName e parentValue è valido crea e ritorna WHERE CLAUSE
        if (filter.equals("")){
            if(parentId.intValue()<=0) throw new BadRequestException("Id indicato non valido");
            whereClause = " WHERE "+parentName+" = "+parentId;
            return new QueryClause(whereClause, whereClauseParams);
        }

        // se c'è il filtro retrieve il type e crea la where clause
        Boolean filterFound = false;
        for (Field field : objectFactory.getDeclaredFields()) {
            if (field.getName().equalsIgnoreCase(filter)) {
                filterFound = true;
                String type = field.getType().getSimpleName().toLowerCase();
                switch (type) {
                    case "integer":
                        try {
                            whereClause = " WHERE " + filter + " BETWEEN " + Integer.parseInt(min) + " AND " + Integer.parseInt(max) + " ";
                        } catch (NumberFormatException e) {
                            throw new BadRequestException("Formato dei parametri 'min' e/o 'max' scorretto. Devono essere solo numeri");
                        }
                        break;
                    case "localdate":
                    case "localdatetime":
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        try {
                            whereClause = " WHERE " + filter + " BETWEEN '" + LocalDate.parse(startDate, formatter) + "' AND '" + LocalDate.parse(endDate, formatter) + "' ";
                        } catch (DateTimeParseException e) {
                            throw new BadRequestException("Formato dei parametri 'startDate' e/o 'endDate' scorretto. Devono essere in formato 'yyyy-MM-dd'");
                        }
                        break;
                    case "boolean":
                        if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
                            throw new BadRequestException("Formato del parametro "+filter+" scorretto. Può essere solo 'true' o 'false'");
                        }
                        whereClause = " WHERE " + filter + " = " + Boolean.parseBoolean(value) + " ";
                        break;
                    case "string":
                        whereClause = " WHERE "+filter+" LIKE :value ";
                        whereClauseParams.put("value", "%"+value+"%");
                        break;
                    default:
                        throw new RuntimeException(type + " non gestito dalla classe");
                }
                break;
            }
        }
        // non hai trovato il filtro throw error
        if (!filterFound) throw new BadRequestException("Filtro applicato '"+filter+"' non valido");

        // se c'è anche il parent aggiungi AND alla WHERE clause
        if(!parentName.equals("")){
            whereClause = whereClause+" AND "+parentName+" = "+parentId;
        }
        return new QueryClause(whereClause, whereClauseParams);
    }
}
