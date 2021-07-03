package com.user.arb.exception;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

public class ArbException extends RuntimeException {

    private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

    private List<String> messages = new ArrayList<>();

    public ArbException(String message) {
        super(message);
    }

    public <E extends Exception> ArbException(E ex) {
        super(ex);
    }

    public ArbException(HttpStatus handleHttpStatus, String message) {
        super(message);
        this.httpStatus = handleHttpStatus;
    }

    public ArbException(HttpStatus handleHttpStatus, Throwable cause) {
        super(cause);
        this.httpStatus = handleHttpStatus;
    }

    public ArbException(HttpStatus handleHttpStatus, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = handleHttpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }
}