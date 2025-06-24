package com.mscursosv3.mscursosv3.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CursoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleCursoNoEncontrado(CursoNoEncontradoException ex){
        Map<String, String> error = new HashMap<>();
        error.put("detalle", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

}
