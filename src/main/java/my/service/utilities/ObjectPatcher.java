package my.service.utilities;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class ObjectPatcher {

    private static Object erasePatchObjectId(Class<?> objectClass, Object updatedObject){
        try {
            objectClass.getMethod("setId", Integer.class).invoke(updatedObject, 0);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e.getMessage());
        }
        return updatedObject;
    }

    private static boolean isValidType(Class<?> type){
        return type == Integer.class || type==String.class || type==Double.class || type==Boolean.class
                || type==LocalDate.class || type==LocalDateTime.class;
    }

    private static Class<?> getClass(String className){
        switch (className){
            case "java.lang.Integer": return Integer.class;
            case "java.lang.String": return String.class;
            case "java.lang.Boolean": return Boolean.class;
            case "java.time.LocalDate": return LocalDate.class;
            case "java.time.LocalDateTime": return LocalDateTime.class;
            case "java.lang.Double": return Double.class;
            default: throw new RuntimeException(className+" non gestisto nella classe");
        }
    }

    private static HashMap<String, Method> getFieldMethods(Class<?> objectClass, Field field){

        // get ogni metodo della classe
        List<Method> methodList = Arrays.asList(objectClass.getDeclaredMethods());

        // creare la mappa per getter e setter del field interessato
        HashMap<String, Method> fieldMethods = new HashMap<String, Method>();

        // capitalize first letter of name
        String fieldName = field.getName().substring(0, 1).toUpperCase()+field.getName().substring(1, field.getName().length());

        // trova i metodi corrispondenti all'attributo
        for(Method method : methodList){
            if(method.getName().contains(fieldName)){
                if(method.getName().startsWith("get")){
                    fieldMethods.put("getter", method);
                }else{
                    fieldMethods.put("setter", method);
                }
            }
        }
        if(fieldMethods.size()!=2) throw new RuntimeException(fieldName+" non ha getter e setter corretti");
        return fieldMethods;
    }

    public static Object patchObject(Class<?> objectClass, Object originalObject, Object updatedObject) {

        // scansionare ogni field della classe
        for(Field field : objectClass.getDeclaredFields()){

            if(field.getName().equalsIgnoreCase("id")) continue;

            if(!isValidType(field.getType())) continue;

            // retrieve i metodi per il field
            HashMap<String, Method> fieldMethods = getFieldMethods(objectClass, field);

            // dichiara getter e setter
            Method getter = fieldMethods.get("getter");
            Method setter = fieldMethods.get("setter");

            Object getterValue = null;
            try {
                getterValue = objectClass.getMethod(getter.getName()).invoke(updatedObject, null);

                // se getterValue!=null invocare setter dell'originale con value
                if(getterValue!=null){
                    objectClass.getMethod(setter.getName(),getClass(getter.getReturnType().getName())).invoke(originalObject, getterValue);
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        // ritornare originale modificato
        return originalObject;
    }

    public static List<String> getListOfDifferentFields(Class<?> objectClass, Object originalObject, Object updatedObject){

        // get List of fields
        List<Field> originalFields = Arrays.asList(objectClass.getDeclaredFields());

        List<String> changedFields = new ArrayList<>();

        // se è un oggetto oppure id skip producing getter
        for(Field field : originalFields) {
            if (field.getName().equalsIgnoreCase("id") || !isValidType(field.getType())) continue;

            // find getter methods
            Method getter = getFieldMethods(objectClass, field).get("getter");

            // invoke getter su original e su other
            Object getterOriginalValue = null;
            Object getterPatchedValue = null;
            try {
                getterOriginalValue = objectClass.getMethod(getter.getName()).invoke(originalObject, null);
                getterPatchedValue = objectClass.getMethod(getter.getName()).invoke(updatedObject, null);
                // if getter su other is null skip
                if(getterPatchedValue==null) continue;
                // if values are different add string "fields"
                if(getterOriginalValue==null || !getterOriginalValue.equals(getterPatchedValue)){
                    String name = field.getName();
                    // check tutti chars
                    Integer capitalLetterIndex = -1;
                    for(Character letter : name.toCharArray()){
                        // if char uppercase get index
                        if(letter >= 'A' && letter <= 'Z'){
                            capitalLetterIndex = name.indexOf(letter);
                        }
                    }
                    // inject blank space and get all lower case
                    if(capitalLetterIndex!=-1){
                        name = name.substring(0, capitalLetterIndex)+" "+name.substring(capitalLetterIndex).toLowerCase();
                    }
                    changedFields.add(name);
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e.getMessage());
            }

        }
        return changedFields;
    }

}
