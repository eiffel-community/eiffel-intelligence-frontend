package com.ericsson.ei.frontend.exception;

public class OSNotSupportedException extends Exception {

    private static final long serialVersionUID = 1L;

    public OSNotSupportedException() {
        super();
    }

    public OSNotSupportedException(String message) {
        super(message);
    }

    public OSNotSupportedException(String message, Throwable e) {
        super(message, e);
    }

}
