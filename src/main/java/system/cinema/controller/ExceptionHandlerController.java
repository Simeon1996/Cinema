package system.cinema.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import system.cinema.exception.CinemaEntityInvalidParameterException;
import system.cinema.exception.CinemaEntityNotFoundException;
import system.cinema.exception.ErrorMessageResponse;

// @TODO fix this exception handler

@ControllerAdvice
@RestController
public class ExceptionHandlerController {

    @ExceptionHandler({CinemaEntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleCinemaEntityNotFound(RuntimeException ex)
    {
        return ex.getMessage();
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMethodArgumentNotValid()
    {
        return ErrorMessageResponse.METHOD_ARGUMENT_NOT_VALID.getMessage();
    }

    @ExceptionHandler(CinemaEntityInvalidParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidParameter(RuntimeException ex)
    {
        return ex.getMessage();
    }

//    @ExceptionHandler()
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public String handleInvalidParameter(RuntimeException ex)
//    {
//        return ex.getMessage();
//    }

//    @ExceptionHandler({Exception.class})
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public String handleAllExceptions()
//    {
//        return "Something went wrong";
//    }
}
