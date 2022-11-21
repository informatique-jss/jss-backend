package com.jss.osiris.libs.exception;

public class OsirisException extends Exception {
    public OsirisException() {
        super();
    }

    public OsirisException(String incorrectField) {
        super(incorrectField);
    }
}