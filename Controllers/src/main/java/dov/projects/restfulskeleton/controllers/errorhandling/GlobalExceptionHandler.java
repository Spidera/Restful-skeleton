package dov.projects.restfulskeleton.controllers.errorhandling;

import com.fasterxml.jackson.databind.ObjectMapper;
import dov.projects.restfulskeleton.controllers.errorhandling.exception.UserNotFoundException;
import dov.projects.restfulskeleton.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {


    @ExceptionHandler(JpaSystemException.class)
    public ResponseEntity<ErrorResponse> handleJpaSystemException(Exception ex, HttpServletRequest httpServletRequest) {
        String rootCauseMessage = ((JpaSystemException) ex).getRootCause().getMessage();
        if (rootCauseMessage.contains("violation: unique") && rootCauseMessage.contains("table: USER")) {
            try {
                String message = "User with email '" +
                        new ObjectMapper().readValue(httpServletRequest.getReader().readLine(), User.class).getEmail() +
                        "' already exists";
                return new ResponseEntity<ErrorResponse>(
                        createErrorResponse(message, HttpStatus.CONFLICT, ErrorCode.USER_EXISTS),
                        HttpStatus.CONFLICT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity<ErrorResponse>(
                createErrorResponse(rootCauseMessage, HttpStatus.BAD_REQUEST, ErrorCode.JPA_EXCEPTION),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGlobalException(Exception ex) {
        return createErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.UNSPECIFIED_GENERIC_EXCEPTION);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(Exception ex) {
        return createErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND);
    }

    private ErrorResponse createErrorResponse(String message, HttpStatus httpStatus, ErrorCode errorCode) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(httpStatus.value());
        errorResponse.setMessage(message);
        errorResponse.setCode(errorCode.getValue());
        return errorResponse;
    }


}
