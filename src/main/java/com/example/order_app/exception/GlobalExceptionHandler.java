package com.example.order_app.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        logger.error("Access denied error: ", ex);
        if (isApiRequest(request)) {
            return new ResponseEntity<>("You do not have permission for this action", HttpStatus.FORBIDDEN);
        }
        return createModelAndView(HttpStatus.FORBIDDEN, "Access Denied", ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        logger.error("Resource not found: ", ex);
        if (isApiRequest(request)) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
        return createModelAndView(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage());
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public Object handleAlreadyExistsException(AlreadyExistsException ex, HttpServletRequest request) {
        logger.error("Resource already exists: ", ex);
        if (isApiRequest(request)) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
        }
        return createModelAndView(HttpStatus.CONFLICT, "Resource Already Exists", ex.getMessage());
    }

    @ExceptionHandler(InvalidImageException.class)
    public Object handleInvalidImageException(InvalidImageException ex, HttpServletRequest request) {
        logger.error("Invalid image: ", ex);
        if (isApiRequest(request)) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return createModelAndView(HttpStatus.BAD_REQUEST, "Invalid Image", ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedOperationException.class)
    public Object handleUnauthorizedOperationException(UnauthorizedOperationException ex, HttpServletRequest request) {
        logger.error("Unauthorized operation: ", ex);
        if (isApiRequest(request)) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        }
        return createModelAndView(HttpStatus.UNAUTHORIZED, "Unauthorized Operation", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Object handleAllExceptions(Exception ex, HttpServletRequest request) {
        logger.error("Unexpected error occurred: ", ex);
        if (isApiRequest(request)) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return createModelAndView(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred");
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