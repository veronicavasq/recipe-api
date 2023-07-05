package com.recipes.demo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ControllerAdvice
public class RecipeExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {RecipeNotFoundException.class, IngredientNotFoundException.class})
    protected ResponseEntity<Object> handlerNotFoundException(RecipeException ex, WebRequest request) {
        this.logRecipeException(ex.getAdditionalData());
        return handleExceptionInternal(ex, ex.getAdditionalData(),
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = {InvalidInputException.class})
    protected ResponseEntity<Object> handlerBadRequestException(RecipeException ex, WebRequest request) {
        this.logRecipeException(ex.getAdditionalData());
        return handleExceptionInternal(ex, ex.getAdditionalData(),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {RuntimeException.class})
    protected ResponseEntity<Object> handlerGeneralException(RuntimeException ex, WebRequest request) {
        this.logger.warn(ex.toString());
        return handleExceptionInternal(ex, new ErrorInfo("Internal Error", ex.getMessage()),
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> details = new ArrayList<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            details.add(error.getDefaultMessage());
        }
        return new ResponseEntity<>(new ErrorInfo("Failed Validation", details), HttpStatus.BAD_REQUEST);
    }

    private void logRecipeException(Object additionalData) {
        ErrorInfo info = (ErrorInfo) additionalData;
        logger.warn(info.toString());
    }


}
