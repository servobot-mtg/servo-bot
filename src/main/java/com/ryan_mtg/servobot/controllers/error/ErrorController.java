package com.ryan_mtg.servobot.controllers.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorController extends AbstractErrorController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorController.class);
    private static final String ERROR_ENDPOINT = "/error";

    @Autowired
    public ErrorController(final ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @RequestMapping(ERROR_ENDPOINT)
    @ResponseBody
    public BotError handleError(final HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        String path = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI).toString();

        if (status == HttpStatus.NOT_FOUND) {
            if (path.startsWith("/api/")) {
                return new BotError(String.format("%s not found", path));
            }
            throw new ResourceNotFoundException(path);
        }
        if (status == HttpStatus.FORBIDDEN) {
            throw new AccessDeniedException(path);
        }

        throw new ResponseStatusException(status);
    }

    @Override
    public String getErrorPath() {
        return ERROR_ENDPOINT;
    }
}
