package com.example.ecom.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    //! Entity Not Found Exception
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(EntityNotFoundException e) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("message", e.getMessage());
        errorDetails.put("status", HttpStatus.NOT_FOUND.value());
        errorDetails.put("error", "Not Found");

        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    //! IO Exception
    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, Object>> handleIOException(IOException e) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("message", "Error processing request: " + e.getMessage());
        errorDetails.put("status", HttpStatus.BAD_REQUEST.value());
        errorDetails.put("error", "Bad Request");

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    //! Illegal Argument Exception
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("message", e.getMessage());
        errorDetails.put("status", HttpStatus.BAD_REQUEST.value());
        errorDetails.put("error", "Bad Request");
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    //! Runtime Exception
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException e) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());

        // Check if it's an image upload error
        if (e.getMessage().contains("upload") || e.getMessage().contains("image")) {
            errorDetails.put("message", e.getMessage());
            errorDetails.put("status", HttpStatus.BAD_REQUEST.value());
            errorDetails.put("error", "Bad Request");
            return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
        } else {
            // Generic runtime exception
            errorDetails.put("message", "An unexpected error occurred");
            errorDetails.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorDetails.put("error", "Internal Server Error");

            System.err.println("Runtime error: " + e.getMessage());
//            e.printStackTrace();

            return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //! Generic Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("message", "An unexpected error occurred: " + e.getMessage());
        errorDetails.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorDetails.put("error", "Internal Server Error");

        System.err.println("Unexpected error: " + e.getMessage());
//        e.printStackTrace();

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
