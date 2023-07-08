package ru.practicum.server.booking;

import lombok.Generated;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.server.booking.exception.*;
import ru.practicum.server.item.exception.ItemExistsException;
import ru.practicum.server.user.exception.UserConflictException;
import ru.practicum.server.user.exception.UserExistsException;

@Generated
@RestControllerAdvice("ru.practicum.shareit.booking")
public class ErrorBookingHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorBookingResponse handlerBookingCreateException(final BookingCreateException e) {
        return new ErrorBookingResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorBookingResponse handlerBookingStatusUpdateException(final BookingStatusUpdateException e) {
        return new ErrorBookingResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorBookingResponse handlerBookingExistsException(final BookingExistsException e) {
        return new ErrorBookingResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorBookingResponse handlerItemExistsException(final ItemExistsException e) {
        return new ErrorBookingResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorBookingResponse handlerUserExistsException(final UserExistsException e) {
        return new ErrorBookingResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorBookingResponse handlerUserConflictException(final UserConflictException e) {
        return new ErrorBookingResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorBookingResponse handlerBookingStateException(final BookingStateException e) {
        return new ErrorBookingResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorBookingResponse handlerWrongStateException(final WrongStateException e) {
        return new ErrorBookingResponse(e.getMessage());
    }
}
