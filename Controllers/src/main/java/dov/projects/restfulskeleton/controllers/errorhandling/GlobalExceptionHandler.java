package dov.projects.restfulskeleton.controllers.errorhandling;

import dov.projects.restfulskeleton.controllers.errorhandling.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(JpaSystemException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleJpaSystemException(Exception ex) {
        String message = ((JpaSystemException) ex).getRootCause().getMessage();
        if (message.contains("Data too long for column 'password' at row 1")) {
            return createErrorResponse(message, HttpStatus.BAD_REQUEST, ErrorCode.DATA_TOO_LONG_FOR_PASSWORD);
        } else {
            return createErrorResponse(message, HttpStatus.BAD_REQUEST, ErrorCode.UNSPECIFIED_JPA_EXCEPTION);
        }
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
