package my.service.utilities;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.format.DateTimeParseException;

@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<CustomError> itemNotFound(ItemNotFoundException exception){
        CustomError error = new CustomError(exception.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<CustomError> badRequestException(BadRequestException exception){
        CustomError error = new CustomError(exception.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<CustomError> sqlException(ConstraintViolationException exception){
        CustomError error = new CustomError(exception.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<CustomError> forbiddenException(ForbiddenException exception){
        CustomError error = new CustomError(exception.getMessage(), HttpStatus.FORBIDDEN.value());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<CustomError> genericError(Exception exception){
        String message = this.extractMessage(exception.getMessage());
        CustomError error = new CustomError(message, HttpStatus.BAD_REQUEST.value());
        exception.printStackTrace();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    private String extractMessage(String message){
        if(message==null) {
            return "Errore non riconosciuto";
        }else if(message.contains("well-formed email address")) {
            return "Email indicata in formato non valido, operazione annullata";
        }else if(message.contains("immobile.ref_UNIQUE")){
            return "Riferimento indicato già utilizzato, sceglierne un altro";
        }else if(message.contains("Cannot deserialize value of type `java.time.LocalDateTime`")) {
            return "Data indicata con formato scorretto";
        } else if(message.contains("propertyPath=telefono")) {
            return "Numero di telefono scorretto: può contenere solo cifre e nel caso di prefisso iniziare con \"+\"";
        }else {
            return message;
        }
    }

}
