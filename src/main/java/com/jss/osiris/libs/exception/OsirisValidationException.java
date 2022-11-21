package com.jss.osiris.libs.exception;

public class OsirisValidationException extends Exception {
    public OsirisValidationException() {
        super();
    }

    public OsirisValidationException(String incorrectField) {
        super(incorrectField);
    }
}