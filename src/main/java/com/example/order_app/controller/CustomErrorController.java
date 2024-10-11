package com.example.order_app.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);

    /**
     * Handles error pages and provides appropriate error information.
     *
     * @param model Spring MVC Model
     * @param request The HTTP request
     * @return The name of the error view
     */
    @RequestMapping("/error")
    public String handleError(Model model, HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Exception exception = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        logger.error("Error occurred: Status={}, Message={}", status, message, exception);

        // Set error information
        model.addAttribute("status", status != null ? status.toString() : "Unknown");
        model.addAttribute("error", getErrorMessage(status));
        model.addAttribute("message", message != null ? message.toString() : "Unexpected error");

        return "error"; // Renders the error.html template
    }

    /**
     * Provides a user-friendly error message based on the HTTP status code.
     *
     * @param status The HTTP status code
     * @return A user-friendly error message
     */
    private String getErrorMessage(Object status) {
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "Page Not Found";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "Internal Server Error";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "Access Denied";
            } else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
                return "Unauthorized Access";
            }
        }
        return "An unexpected error occurred";
    }
}
