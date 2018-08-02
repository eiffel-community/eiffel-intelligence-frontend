package com.ericsson.ei.frontend.exception;

public class PropertiesNotLoadedException extends Exception {

    private static final long serialVersionUID = 1L;

    public PropertiesNotLoadedException() {
        super();
    }

    public PropertiesNotLoadedException(String message) {
        super(message);
    }

    public PropertiesNotLoadedException(String message, Throwable e) {
        super(message, e);
    }

}
