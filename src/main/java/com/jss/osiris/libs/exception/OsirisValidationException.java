package com.jss.osiris.libs.exception;

public class OsirisValidationException extends OsirisException {
    public OsirisValidationException() {
        super();
    }

    public OsirisValidationException(String incorrectField) {
        super(incorrectField);
    }
}