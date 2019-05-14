package org.apache.shiro.web.csrf;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.util.Assert;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * <p>
 * Applies
 * <a href="https://www.owasp.org/index.php/Cross-Site_Request_Forgery_(CSRF)" >CSRF</a>
 * protection using a synchronizer token pattern. Developers are required to ensure that
 * {@link CsrfFilter} is invoked for any request that allows state to change. Typically
 * this just means that they should ensure their web application follows proper REST
 * semantics (i.e. do not change state with the HTTP methods GET, HEAD, TRACE, OPTIONS).
 * </p>
 *
 * <p>
 * Typically the {@link CsrfTokenRepository} implementation chooses to store the
 * {@link CsrfToken} in {@link HttpSession} with {@link HttpSessionCsrfTokenRepository}
 * wrapped by a {@link LazyCsrfTokenRepository}. This is preferred to storing the token in
 * a cookie which can be modified by a client application.
 * </p>
 *
 * @see https://github.com/spring-projects/spring-security/blob/master/web/src/main/java/org/springframework/security/web/csrf/CsrfFilter.java
 * @since 1.4
 */
public class CsrfFilter extends AccessControlFilter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String DEFAULT_ERROR_KEY_ATTRIBUTE_NAME = "shiroFailure";

    public static final String DEFAULT_ERROR_URL = "/error.jsp";

    private String errorUrl = DEFAULT_ERROR_URL;

    private CsrfTokenRepository tokenRepository;

    private String failureKeyAttribute = DEFAULT_ERROR_KEY_ATTRIBUTE_NAME;

    private Set<String> allowedHttpMethods = new HashSet<String>(Arrays.asList("GET", "HEAD", "TRACE", "OPTIONS"));

    public CsrfFilter() {
        this(new HttpSessionCsrfTokenRepository());
    }

    public CsrfFilter(CsrfTokenRepository csrfTokenRepository) {
        Assert.notNull(csrfTokenRepository, "csrfTokenRepository cannot be null");
        this.tokenRepository = csrfTokenRepository;
    }

    @Override
    public boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        try {
            return super.onPreHandle(request, response, mappedValue);
        } catch (CsrfException e) {
            logger.debug(e.getMessage());
            setFailureAttribute(request, e);
            return onAccessDenied(request, response);
        }
    }

    private boolean isRequestAllowed(HttpServletRequest request, Object mappedValue) {

        return getAllowedHttpMethods().contains(request.getMethod().toUpperCase(Locale.ENGLISH));
    }


    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object mappedValue) throws Exception {

        HttpServletRequest request = WebUtils.toHttp(servletRequest);
        HttpServletResponse response = WebUtils.toHttp(servletResponse);

//        request.setAttribute(HttpServletResponse.class.getName(), response);

        // loads the expected token by this request
        CsrfToken csrfToken = this.getTokenRepository().loadToken(request);

        // if we are missing the token generate one, so we are not comparing empty/null strings
        final boolean missingToken = csrfToken == null;
        if (missingToken) {
            csrfToken = this.getTokenRepository().generateToken(request);
            this.getTokenRepository().saveToken(csrfToken, request, response);
        }
        request.setAttribute(CsrfToken.class.getName(), csrfToken);
        request.setAttribute(csrfToken.getParameterName(), csrfToken);




        // now that we have generated the token, we can continue for
        // non-state changing requests don't need a csrf check
        if (isRequestAllowed(request, mappedValue)) {
            return true;
        }



        // get the actual token from the request
        String actualToken = request.getHeader(csrfToken.getHeaderName());
        if (actualToken == null) {
            actualToken = request.getParameter(csrfToken.getParameterName());
        }

        // validate the token
        if (!csrfToken.getToken().equals(actualToken)) {

            this.logger.debug("Invalid CSRF token found for {}", request.getPathInfo());
            if (missingToken) {
                throw new MissingCsrfTokenException(actualToken);
            }
            else {
                throw new InvalidCsrfTokenException(csrfToken, actualToken);
            }
        }

        return true;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {

        WebUtils.saveRequest(request);
        request.getRequestDispatcher(getErrorUrl()).forward(request, response);
        return false;
    }


    protected void setFailureAttribute(ServletRequest request, CsrfException e) {

        String className = e.getClass().getName();
        request.setAttribute(getFailureKeyAttribute(), className);
    }


    public Set<String> getAllowedHttpMethods() {
        return allowedHttpMethods;
    }

    public void setAllowedHttpMethods(Set<String> allowedHttpMethods) {
        this.allowedHttpMethods.clear();
        for (String method : allowedHttpMethods) {
            allowedHttpMethods.add(method.toUpperCase(Locale.ENGLISH));
        }
    }

    public String getFailureKeyAttribute() {
        return failureKeyAttribute;
    }

    public void setFailureKeyAttribute(String failureKeyAttribute) {
        this.failureKeyAttribute = failureKeyAttribute;
    }

    public CsrfTokenRepository getTokenRepository() {
        return tokenRepository;
    }

    public void setTokenRepository(CsrfTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public String getErrorUrl() {
        return errorUrl;
    }

    public void setErrorUrl(String errorUrl) {
        this.errorUrl = errorUrl;
    }
}
