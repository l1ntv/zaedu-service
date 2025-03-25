package ru.tbank.zaedu.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.tbank.zaedu.DTO.ErrorResponseDTO;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidData(InvalidDataException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(WrongDataException.class)
    public ResponseEntity<ErrorResponseDTO> handleWrongData(WrongDataException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(ConflictResourceException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflictResource(ConflictResourceException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        ErrorResponseDTO error = new ErrorResponseDTO(errorMessage);
        return ResponseEntity.badRequest().body(error);
    }
}
