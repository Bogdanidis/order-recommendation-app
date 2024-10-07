package com.example.order_app.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(Model model, HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        // Set error information
        model.addAttribute("status", status != null ? status.toString() : "Unknown");
        model.addAttribute("error", getErrorMessage(status));
        model.addAttribute("message", message != null ? message.toString() : "Unexpected error");

        return "error"; // Render the error.html template
    }

    private String getErrorMessage(Object status) {
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "Page Not Found!";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "Internal Server Error!";
            }
        }
        return "Something went wrong!";
    }
}
