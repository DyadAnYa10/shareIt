package ru.practicum.server.exception;


import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Generated
@Slf4j
@RestControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.debug("Ошибка валидации {}", ex.getLocalizedMessage());
        ErrorResponse errorResponse = new ErrorResponse("Ошибка валидации", ex.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        log.debug("Недопустимое значение {}", e.getMessage());
        return new ErrorResponse("Недопустимое значение {}", e.getMessage());
    }

    @ExceptionHandler(DeniedAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleDeniedAccessException(DeniedAccessException e) {
        log.debug("Отказано в доступе {}", e.getMessage());
        return new ErrorResponse("Отказано в доступе", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(Exception e) {
        log.debug("Непредвиденная ошибка {}", e.getMessage());
        return new ErrorResponse("Непредвиденная ошибка {}", e.getMessage());
    }

    @ExceptionHandler(ValidateBookingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnavailableBookingException(ValidateBookingException e) {
        return new ErrorResponse("Ошибка бронирования 400: ", e.getMessage());
    }

    @ExceptionHandler(ValidateCommentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCommentException(ValidateCommentException e) {
        return new ErrorResponse("Невозможно оставить комментарий 400: ", e.getMessage());
    }

    @ExceptionHandler(BookingStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnknownBookingException(BookingStateException e) {
        return new ErrorResponse(e.getMessage());
    }

}
