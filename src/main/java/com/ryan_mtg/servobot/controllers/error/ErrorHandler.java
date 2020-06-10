package com.ryan_mtg.servobot.controllers.error;

import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.SystemError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.security.WebsiteUserFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;

@ControllerAdvice
public class ErrorHandler {
    private final WebsiteUserFactory websiteUserFactory;

    public ErrorHandler(final WebsiteUserFactory websiteUserFactory) {
        this.websiteUserFactory = websiteUserFactory;
    }

    @ExceptionHandler(BotHomeError.class)
    public ModelAndView renderBotHomeError(final BotHomeError botHomeError) {
        return renderException(botHomeError);
    }

    @ExceptionHandler(SystemError.class)
    public ModelAndView renderSystemError(final SystemError systemError) {
        return renderException(systemError);
    }

    @ExceptionHandler(UserError.class)
    public ModelAndView renderUserError(final UserError userError) {
        return renderException(userError);
    }

    @ExceptionHandler(Throwable.class)
    public ModelAndView renderErrorPage(final HttpServletRequest httpRequest, final Throwable exception,
                                  final OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        int httpErrorCode = HttpURLConnection.HTTP_BAD_REQUEST;
        if (httpRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE) != null) {
            httpErrorCode = (Integer) httpRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        }

        exception.printStackTrace();

        HttpStatus status = HttpStatus.resolve(httpErrorCode);
        String errorMessage = "Http Error Code: " + httpErrorCode + ". " + status;
        ModelAndView modelAndView = setupModelAndView("error/error", status, exception, oAuth2AuthenticationToken);
        modelAndView.addObject("error_message", errorMessage);

        return modelAndView;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ModelAndView defaultExceptionHandler(final ResponseStatusException exception,
                               final OAuth2AuthenticationToken oAuth2AuthenticationToken) {
       return setupModelAndView("error/error", exception.getStatus(), exception, oAuth2AuthenticationToken);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView resourceNotFoundHandler(final ResourceNotFoundException exception,
                                                final OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        return setupModelAndView("error/resource_not_found",
                exception.getStatus(), exception, oAuth2AuthenticationToken);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView accessDeniedHandler(final AccessDeniedException exception,
                                            final OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        return setupModelAndView("error/access_denied",
                exception.getStatus(), exception, oAuth2AuthenticationToken);
    }

    public ModelAndView renderException(final Exception exception) {
        exception.printStackTrace();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        modelAndView.setView(new MappingJackson2JsonView());
        modelAndView.addObject("message", exception.getMessage());
        return modelAndView;
    }

    private ModelAndView setupModelAndView(final String viewName, final HttpStatus status, final Throwable exception,
                                           final OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        ModelAndView modelAndView = new ModelAndView(viewName);
        modelAndView.setStatus(status);
        modelAndView.addObject("resource", exception.getMessage());
        modelAndView.addObject("stack_trace", extractStackTrace(exception));
        modelAndView.addObject("user", websiteUserFactory.createWebsiteUser(oAuth2AuthenticationToken));

        return modelAndView;
    }

    private String extractStackTrace(final Throwable exception) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);

        return stringWriter.toString();
    }
}
