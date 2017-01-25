package org.apache.shiro.web.csrf;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * Provides the information about an expected CSRF token.
 *
 * @see DefaultCsrfToken
 *
 * @author Rob Winch
 * @since 3.2
 *
 */
public interface CsrfToken extends Serializable {

    /**
     * Gets the HTTP header that the CSRF is populated on the response and can be placed
     * on requests instead of the parameter. Cannot be null.
     *
     * @return the HTTP header that the CSRF is populated on the response and can be
     * placed on requests instead of the parameter
     */
    String getHeaderName();

    /**
     * Gets the HTTP parameter name that should contain the token. Cannot be null.
     * @return the HTTP parameter name that should contain the token.
     */
    String getParameterName();

    /**
     * Gets the token value. Cannot be null.
     * @return the token value
     */
    String getToken();

}
