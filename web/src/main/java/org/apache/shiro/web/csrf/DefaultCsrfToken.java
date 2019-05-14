package org.apache.shiro.web.csrf;

import org.apache.shiro.util.Assert;

/**
 * A CSRF token that is used to protect against CSRF attacks.
 *
 * @author Rob Winch
 * @since 3.2
 */
@SuppressWarnings("serial")
public final class DefaultCsrfToken implements CsrfToken {

    private final String token;

    private final String parameterName;

    private final String headerName;

    /**
     * Creates a new instance
     * @param headerName the HTTP header name to use
     * @param parameterName the HTTP parameter name to use
     * @param token the value of the token (i.e. expected value of the HTTP parameter of
     * parametername).
     */
    public DefaultCsrfToken(String headerName, String parameterName, String token) {
        Assert.hasLength(headerName, "headerName cannot be null or empty");
        Assert.hasLength(parameterName, "parameterName cannot be null or empty");
        Assert.hasLength(token, "token cannot be null or empty");
        this.headerName = headerName;
        this.parameterName = parameterName;
        this.token = token;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.security.web.csrf.CsrfToken#getHeaderName()
     */
    public String getHeaderName() {
        return this.headerName;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.security.web.csrf.CsrfToken#getParameterName()
     */
    public String getParameterName() {
        return this.parameterName;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.security.web.csrf.CsrfToken#getToken()
     */
    public String getToken() {
        return this.token;
    }

}
