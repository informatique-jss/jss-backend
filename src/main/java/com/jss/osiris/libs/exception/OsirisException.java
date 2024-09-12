package com.jss.osiris.libs.exception;

public class OsirisException extends Exception {

    private Exception causeException;

    public OsirisException() {
        super();
    }

    public OsirisException(String message) {
        super(message);
    }

    public OsirisException(Exception e, String incorrectField) {
        super(incorrectField);
        if (e != null)
            causeException = e;
        // Get and use root cause
        if (e instanceof OsirisException && ((OsirisException) e).getCauseException() != null)
            causeException = ((OsirisException) e).getCauseException();

    }

    public Exception getCauseException() {
        return causeException;
    }
}