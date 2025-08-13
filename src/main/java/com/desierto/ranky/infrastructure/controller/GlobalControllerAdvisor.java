package com.desierto.ranky.infrastructure.controller;

import com.desierto.ranky.domain.exception.NotFoundException;
import com.desierto.ranky.infrastructure.exceptions.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalControllerAdvisor {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
    ErrorResponse errorResponse = new ErrorResponse(
        HttpStatus.NOT_FOUND.value(),
        ex.getMessage()
    );
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
    ErrorResponse errorResponse = new ErrorResponse(
        HttpStatus.UNAUTHORIZED.value(),
        ex.getMessage()
    );
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  public static class ErrorResponse {

    private int status;
    private String message;

    public ErrorResponse(int status, String message) {
      this.status = status;
      this.message = message;
    }

    public int getStatus() {
      return status;
    }

    public String getMessage() {
      return message;
    }
  }
}

