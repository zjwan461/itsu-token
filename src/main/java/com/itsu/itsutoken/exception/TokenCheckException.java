package com.itsu.itsutoken.exception;

public class TokenCheckException extends Exception {

    private static final long serialVersionUID = 1858323711681488180L;

    public TokenCheckException(String message) {
        super(message);
    }

    public TokenCheckException(Throwable t) {
        super(t);
    }

    public TokenCheckException(String message, Throwable t) {
        super(message, t);
    }

}