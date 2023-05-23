package app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomErrorExceptionAdvice {

    @ExceptionHandler(value = CustomErrorException.class)
    public ResponseEntity<String> exceptionHandler(CustomErrorException e){
        String errorMessage = "Error: " + e.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

}
