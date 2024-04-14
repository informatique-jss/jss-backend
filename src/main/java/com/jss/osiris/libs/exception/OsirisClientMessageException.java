package com.jss.osiris.libs.exception;

/**
 * Used to send a message to client on frontend for outside JSS erroers
 */
public class OsirisClientMessageException extends OsirisException {
    public OsirisClientMessageException() {
        super();
    }

    public OsirisClientMessageException(String message) {
        super(message);
    }
}