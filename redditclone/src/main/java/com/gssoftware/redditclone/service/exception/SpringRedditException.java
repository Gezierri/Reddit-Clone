package com.gssoftware.redditclone.service.exception;

import org.springframework.mail.MailException;

public class SpringRedditException extends RuntimeException {
    public SpringRedditException(String message, MailException error) {
        super(message, error);
    }
}
