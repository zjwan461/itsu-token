package com.itsu.itsutoken.exception;

public class TokenConfigureException extends Exception {

    private static final long serialVersionUID = 6527763755266003951L;

    public TokenConfigureException(String message) {
        super(message);
    }

    public TokenConfigureException(Throwable t) {
        super(t);
    }

    public TokenConfigureException(String message, Throwable t) {
        super(message, t);
    }
}