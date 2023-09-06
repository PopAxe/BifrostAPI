package dev.gtech.bifrost.bifrostapi.config.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import dev.gtech.bifrost.bifrostapi.models.common.GenericResult;
import dev.gtech.bifrost.bifrostapi.models.exceptions.BadRequestException;
import dev.gtech.bifrost.bifrostapi.models.exceptions.InternalServerErrorException;

@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public GenericResult<String> badRequestException(BadRequestException exception) {
        return new GenericResult<>(exception.getLocalizedMessage());
    }

    @ExceptionHandler(InternalServerErrorException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GenericResult<String> internalServerErrorException(InternalServerErrorException exception) {
        return new GenericResult<>(exception.getLocalizedMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public GenericResult<String> noHandlerFoundException(NoHandlerFoundException exception) {
        return new GenericResult<>("Not Found");
    }
}
