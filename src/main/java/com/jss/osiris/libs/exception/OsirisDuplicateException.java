package com.jss.osiris.libs.exception;

import java.util.List;
import java.util.stream.Collectors;

public class OsirisDuplicateException extends OsirisException {
    public OsirisDuplicateException() {
        super();
    }

    public OsirisDuplicateException(List<Integer> duplicateIds) {
        super(String.join(",", duplicateIds.stream().map(String::valueOf).collect(Collectors.joining(","))));
    }
}