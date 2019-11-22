package com.ryan_mtg.servobot.controllers;

import com.ryan_mtg.servobot.controllers.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorController extends AbstractErrorController {
    private static final String ERROR_ENDPOINT = "/error";

    @Autowired
    public ErrorController(final ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @RequestMapping(ERROR_ENDPOINT)
    public String handleError(final HttpServletRequest request) {
        HttpStatus status = getStatus(request);

        if (status == HttpStatus.NOT_FOUND) {
            throw new ResourceNotFoundException(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI).toString());
        }
        throw new ResponseStatusException(status);
    }

    @Override
    public String getErrorPath() {
        return ERROR_ENDPOINT;
    }
}
