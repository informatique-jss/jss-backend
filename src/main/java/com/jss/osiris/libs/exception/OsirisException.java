package com.jss.osiris.libs.exception;

public class OsirisException extends Exception {

    private Exception causeException;

    public OsirisException() {
        super();
    }

    public OsirisException(Exception e, String incorrectField) {
        super(incorrectField);
        if (e != null)
            causeException = e;
    }

    public Exception getCauseException() {
        return causeException;
    }
}