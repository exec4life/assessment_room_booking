package com.user.arb.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ArbHandlerExceptionResolver extends AbstractHandlerExceptionResolver {

    private static Logger LOG = LoggerFactory.getLogger(ArbHandlerExceptionResolver.class);

    @Autowired
    protected MessageSource messageSource;

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response,
                                              Object o, Exception e) {
        try {
            LOG.error("UN-HANDLE EXCEPTION: " + e.getMessage(), e);

            String accept = request.getHeader(HttpHeaders.ACCEPT);
            response.setHeader(HttpHeaders.ACCEPT, accept);
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    messageSource.getMessage("system.convert.error",
                            null,
                            "Has an internal error",
                            LocaleContextHolder.getLocale()));

            return new ModelAndView();
        } catch (IOException ex) {
            LOG.error("Handle Exception error", ex);
        }
        return null;
    }

    @ResponseBody
    @ExceptionHandler(ArbException.class)
    public ResponseEntity<Error> arbException(HttpServletRequest request, ArbException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", ex.getHttpStatus().value());
        body.put("path", request.getRequestURI());
        body.put("message", ex.getMessage());
        body.put("messages", ex.getMessages());

        return new ResponseEntity(body, ex.getHttpStatus());
    }

    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler({ConstraintViolationException.class, javax.validation.ConstraintViolationException.class})
    public ResponseEntity<Error> constraintViolationException(HttpServletRequest request,
                                                                 Exception ex) {
        List<String> messages;
        if (ex instanceof javax.validation.ConstraintViolationException) {
            javax.validation.ConstraintViolationException except = (javax.validation.ConstraintViolationException) ex;
            messages = except.getConstraintViolations().stream().map(e -> {
                String msgTemp = e.getMessage().replace("{", "").replace("}", "");
                return messageSource.getMessage(msgTemp, null, msgTemp, LocaleContextHolder.getLocale());
            }).collect(Collectors.toList());
        } else {
            messages = Arrays.asList(ex.getMessage());
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", BAD_REQUEST.value());
        body.put("messages", messages);
        body.put("path", request.getRequestURI());
        body.put("message",
                messageSource.getMessage("system.validation.fields.failed",
                        null,
                        "Some fields are failed in validation rules",
                        LocaleContextHolder.getLocale()));

        return new ResponseEntity(body, BAD_REQUEST);
    }

    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> methodArgumentNotValidException(HttpServletRequest request,
                                                                 MethodArgumentNotValidException ex) {
        List<String> messages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getDefaultMessage())
                .collect(Collectors.toList());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", BAD_REQUEST.value());
        body.put("messages", messages);
        body.put("path", request.getRequestURI());
        body.put("message",
                messageSource.getMessage("system.validation.fields.failed",
                        null,
                        "Some fields are failed in validation rules",
                        LocaleContextHolder.getLocale()));

        return new ResponseEntity(body, BAD_REQUEST);
    }

}
