package org.bots.lfbot.exception;

import java.text.ParseException;

public class ParseErrorException extends RuntimeException {

    public ParseErrorException(String errorText, ParseException e) {
        super(errorText, e);
    }

    public ParseErrorException(String errorParsingReleaseDate) {
        super(errorParsingReleaseDate);
    }
}
