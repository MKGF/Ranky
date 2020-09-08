package com.desierto.LoLRankingMaker.infrastructure.configuration;

import com.desierto.LoLRankingMaker.domain.exception.NotFoundException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WebRestControllerAdvice {

  @ExceptionHandler({Exception.class})
  public void handleGenericException(Exception ex, HttpServletResponse response)
      throws IOException {
    int httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
    if (ex instanceof NotFoundException) {
      httpStatus = HttpStatus.NOT_FOUND.value();
    }

    response.sendError(httpStatus, ex.getMessage());
  }
}
