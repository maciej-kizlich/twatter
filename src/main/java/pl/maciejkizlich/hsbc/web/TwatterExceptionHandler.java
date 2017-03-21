package pl.maciejkizlich.hsbc.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.google.common.collect.Iterables;

import pl.maciejkizlich.hsbc.exception.ServiceException;


@ControllerAdvice
public class TwatterExceptionHandler {

  @ExceptionHandler({
      MissingServletRequestParameterException.class,
      ServiceException.class})
  public ResponseEntity<Object> badRequest(HttpServletRequest req, Exception ex) {
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("path", req.getRequestURL().toString());
    responseBody.put("message", ex.getMessage());
    return new ResponseEntity<Object>(responseBody, HttpStatus.BAD_REQUEST);
  }
  
  @ExceptionHandler({ConstraintViolationException.class})
  public ResponseEntity<Object> constraintViolation(HttpServletRequest req,
      ConstraintViolationException ex) {
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("path", req.getRequestURL().toString());
    responseBody.put("message",
        Iterables.getFirst(ex.getConstraintViolations(), null).getMessage());
    return new ResponseEntity<Object>(responseBody, HttpStatus.BAD_REQUEST);
  }

}
