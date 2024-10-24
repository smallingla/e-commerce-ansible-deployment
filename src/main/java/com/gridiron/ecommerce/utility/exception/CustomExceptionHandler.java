package com.gridiron.ecommerce.utility.exception;

import com.gridiron.ecommerce.utility.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

    /**
     * This method handles the processing of a duplicate resource existing exception.
     * It throws a bad request status code to the client
     */
    @ExceptionHandler(ResourceExistsException.class)
    public final ResponseEntity<ApiResponse> handleResourceExistsException(ResourceExistsException e){
        return new ResponseEntity<>(new ApiResponse(false,e.getMessage(),null), HttpStatus.CONFLICT);
    }

    /**
     * This method handles the processing of a resource not found exception
     * It throws a not found  status code to the client
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException e){
        return new ResponseEntity<>(new ApiResponse(false,e.getMessage(),null), HttpStatus.NOT_FOUND);
    }

    /**
     * This method handles the processing of a not authorized exception
     * It throws an unauthorized status code to the client
     */
    @ExceptionHandler(UnauthorizedException.class)
    public final ResponseEntity<ApiResponse> handleUnauthorizedException(UnauthorizedException e){
        return new ResponseEntity<>(new ApiResponse(false,e.getMessage(),null), HttpStatus.UNAUTHORIZED);
    }

    /**
     * This method handles the processing of an invalid input exception
     * It throws a bad request status code to the client
     */
    @ExceptionHandler(InvalidInputException.class)
    public final ResponseEntity<ApiResponse> handleInvalidInputException(InvalidInputException e){
        return new ResponseEntity<>(new ApiResponse(false,e.getMessage(),null), HttpStatus.BAD_REQUEST);
    }



}
