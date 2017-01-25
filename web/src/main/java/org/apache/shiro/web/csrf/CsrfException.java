package org.apache.shiro.web.csrf;

import org.apache.shiro.ShiroException;

/**
 * Created by briandemers on 1/23/17.
 */
public class CsrfException extends ShiroException {

    public CsrfException(String message) {
        super(message);
    }

    public CsrfException(String message, Throwable t) {
        super(message, t);
    }
}
