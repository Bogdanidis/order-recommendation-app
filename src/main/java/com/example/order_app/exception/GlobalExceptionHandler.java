package com.example.order_app.exception;

import com.example.order_app.dto.ApiError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        logger.error("Access denied error: {}", ex.getMessage());
        if (isApiRequest(request)) {
            ApiError apiError = new ApiError(
                    HttpStatus.FORBIDDEN.value(),
                    "Access Denied",
                    "You do not have permission for this action",
                    request.getRequestURI()
            );
            return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
        }
        return createModelAndView(HttpStatus.FORBIDDEN, "Access Denied", ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        logger.error("Resource not found: {}", ex.getMessage());
        if (isApiRequest(request)) {
            ApiError apiError = new ApiError(
                    HttpStatus.NOT_FOUND.value(),
                    "Resource Not Found",
                    ex.getMessage(),
                    request.getRequestURI()
            );
            return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
        }
        return createModelAndView(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage());
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public Object handleAlreadyExistsException(AlreadyExistsException ex, HttpServletRequest request) {
        logger.error("Resource already exists: {}", ex.getMessage());
        if (isApiRequest(request)) {
            ApiError apiError = new ApiError(
                    HttpStatus.CONFLICT.value(),
                    "Resource Already Exists",
                    ex.getMessage(),
                    request.getRequestURI()
            );
            return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
        }
        return createModelAndView(HttpStatus.CONFLICT, "Resource Already Exists", ex.getMessage());
    }

    @ExceptionHandler(InvalidImageException.class)
    public Object handleInvalidImageException(InvalidImageException ex, HttpServletRequest request) {
        logger.error("Invalid image: {}", ex.getMessage());
        if (isApiRequest(request)) {
            ApiError apiError = new ApiError(
                    HttpStatus.BAD_REQUEST.value(),
                    "Invalid Image",
                    ex.getMessage(),
                    request.getRequestURI()
            );
            return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
        }
        return createModelAndView(HttpStatus.BAD_REQUEST, "Invalid Image", ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedOperationException.class)
    public Object handleUnauthorizedOperationException(UnauthorizedOperationException ex, HttpServletRequest request) {
        logger.error("Unauthorized operation: {}", ex.getMessage());
        if (isApiRequest(request)) {
            ApiError apiError = new ApiError(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Unauthorized Operation",
                    ex.getMessage(),
                    request.getRequestURI()
            );
            return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
        }
        return createModelAndView(HttpStatus.UNAUTHORIZED, "Unauthorized Operation", ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Object handleValidationExceptions(Exception ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();

        if (ex instanceof MethodArgumentNotValidException) {
            ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
        } else if (ex instanceof BindException) {
            ((BindException) ex).getBindingResult().getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
        }

        String errorMessage = errors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));

        logger.error("Validation error: {}", errorMessage);

        if (isApiRequest(request)) {
            ApiError apiError = new ApiError(
                    HttpStatus.BAD_REQUEST.value(),
                    "Validation Error",
                    errorMessage,
                    request.getRequestURI()
            );
            return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
        }
        return createModelAndView(HttpStatus.BAD_REQUEST, "Validation Error", errorMessage);
    }

    @ExceptionHandler(BusinessException.class)
    public Object handleBusinessException(BusinessException ex, HttpServletRequest request) {
        logger.error("Business exception: {}", ex.getMessage());
        if (isApiRequest(request)) {
            ApiError apiError = new ApiError(
                    HttpStatus.BAD_REQUEST.value(),
                    "Business Error",
                    ex.getMessage(),
                    request.getRequestURI()
            );
            return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
        }
        return createModelAndView(HttpStatus.BAD_REQUEST, "Business Error", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Object handleAllExceptions(Exception ex, HttpServletRequest request) {
        logger.error("Unexpected error occurred: ", ex);
        if (isApiRequest(request)) {
            ApiError apiError = new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    "An unexpected error occurred. Please try again later.",
                    request.getRequestURI()
            );
            return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return createModelAndView(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred. Please try again later.");
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return requestURI.startsWith("/api/") || requestURI.startsWith("/order-api/");
    }

    private ModelAndView createModelAndView(HttpStatus status, String error, String message) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("status", status.value());
        modelAndView.addObject("error", error);
        modelAndView.addObject("message", message);
        modelAndView.setStatus(status);
        return modelAndView;
    }
}