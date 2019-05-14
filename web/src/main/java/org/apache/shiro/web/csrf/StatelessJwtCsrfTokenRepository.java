package org.apache.shiro.web.csrf;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.crypto.CipherService;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by briandemers on 1/24/17.
 */
public class StatelessJwtCsrfTokenRepository implements CsrfTokenRepository {


    private static final Logger log = LoggerFactory.getLogger(StatelessJwtCsrfTokenRepository.class);

    private static final String DEFAULT_CSRF_PARAMETER_NAME = "_csrf";

    private static final String DEFAULT_CSRF_HEADER_NAME = "X-CSRF-TOKEN";

    private static final String DEFAULT_CSRF_COOKIE_NAME = "XSRF-TOKEN";

    /**
     * The number of seconds in one hour (= 60 * 60).
     */
    private static final int ONE_HOUR = 60 * 60;

    private String parameterName = DEFAULT_CSRF_PARAMETER_NAME;

    private String headerName = DEFAULT_CSRF_HEADER_NAME;


    /**
     * Cipher to use for encrypting/decrypting serialized byte arrays for added security
     */
    private CipherService cipherService;

    /**
     * Cipher encryption key to use with the Cipher when encrypting data
     */
    private byte[] encryptionCipherKey;

    /**
     * Cipher decryption key to use with the Cipher when decrypting data
     */
    private byte[] decryptionCipherKey;



    private Cookie cookie;

    public StatelessJwtCsrfTokenRepository() {
        Cookie cookie = new SimpleCookie(DEFAULT_CSRF_COOKIE_NAME);
        cookie.setHttpOnly(false);
        cookie.setMaxAge(ONE_HOUR);
        this.cookie = cookie;

        AesCipherService cipherService = new AesCipherService();
        this.cipherService = cipherService;
        setCipherKey(cipherService.generateNewKey().getEncoded());
    }


    @Override
    public CsrfToken generateToken(HttpServletRequest request) {

        Subject subject = SecurityUtils.getSubject();

        PrincipalCollection principals = subject.getPrincipals();

        Object principal = "guest";
        if (principals != null && !principals.isEmpty()) {
            principal = principals.getPrimaryPrincipal();
        }

        String jwt = Jwts.builder()
            .setSubject(principal.toString())
            // cookie Max Age to mill + current time == expiration date
            .setExpiration(new Date(System.currentTimeMillis() + (getCookie().getMaxAge() * 1000) ))
            .compressWith(CompressionCodecs.DEFLATE)
            .signWith(SignatureAlgorithm.HS512, getEncryptionCipherKey())
            .compact();

        return new DefaultCsrfToken(getHeaderName(), getParameterName(), jwt);
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {

        Cookie template = getCookie(); //the class attribute is really a template for the outgoing cookies
        Cookie cookie = new SimpleCookie(template);
        cookie.setValue(token.getToken());
        cookie.saveTo(request, response);

    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {

        // get the actual token from the request
        String jwtString = request.getHeader(getHeaderName());
        if (jwtString == null) {
            jwtString = request.getParameter(getParameterName());
        }

        if (!StringUtils.hasText(jwtString)) {
            return null;
        }

        // if we have a token it better be valid!
        try {

            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(getDecryptionCipherKey())
                    .parseClaimsJws(jwtString);

            Subject subject = SecurityUtils.getSubject();
            PrincipalCollection principals = subject.getPrincipals();

            Object principal = "guest";
            if (principals != null && !principals.isEmpty()) {
                principal = principals.getPrimaryPrincipal();
            }

            if (principal.equals(claims.getBody().getSubject())) {
                return new DefaultCsrfToken(getHeaderName(), getParameterName(), jwtString);
            }
            else {
                throw new InvalidCsrfTokenException("JWT claims subject does NOT match current subject");
            }
        }
        catch (JwtException e) {
            throw new InvalidCsrfTokenException("CSRF JWT was invalid", e);

        }
    }

    public Cookie getCookie() {
        return cookie;
    }

    public void setCookie(Cookie cookie) {
        this.cookie = cookie;
    }











    /**
     * Returns the {@code CipherService} to use for encrypting and decrypting serialized identity data to prevent easy
     * inspection of Subject identity data.
     * <p/>
     * Unless overridden by the {@link #setCipherService} method, the default instance is an {@link AesCipherService}.
     *
     * @return the {@code Cipher} to use for encrypting and decrypting serialized identity data to prevent easy
     *         inspection of Subject identity data
     */
    public CipherService getCipherService() {
        return cipherService;
    }

    /**
     * Sets the {@code CipherService} to use for encrypting and decrypting serialized identity data to prevent easy
     * inspection of Subject identity data.
     * <p/>
     * If the CipherService is a symmetric CipherService (using the same key for both encryption and decryption), you
     * should set your key via the {@link #setCipherKey(byte[])} method.
     * <p/>
     * If the CipherService is an asymmetric CipherService (different keys for encryption and decryption, such as
     * public/private key pairs), you should set your encryption and decryption key via the respective
     * {@link #setEncryptionCipherKey(byte[])} and {@link #setDecryptionCipherKey(byte[])} methods.
     * <p/>
     * <b>N.B.</b> Unless overridden by this method, the default CipherService instance is an
     * {@link AesCipherService}.  This {@code RememberMeManager} implementation already has a configured symmetric key
     * to use for encryption and decryption, but it is recommended to provide your own for added security.  See the
     * class-level JavaDoc for more information and why it might be good to provide your own.
     *
     * @param cipherService the {@code CipherService} to use for encrypting and decrypting serialized identity data to
     *                      prevent easy inspection of Subject identity data.
     */
    public void setCipherService(CipherService cipherService) {
        this.cipherService = cipherService;
    }

    /**
     * Returns the cipher key to use for encryption operations.
     *
     * @return the cipher key to use for encryption operations.
     * @see #setCipherService for a description of the various {@code get/set*Key} methods.
     */
    public byte[] getEncryptionCipherKey() {
        return encryptionCipherKey;
    }

    /**
     * Sets the encryption key to use for encryption operations.
     *
     * @param encryptionCipherKey the encryption key to use for encryption operations.
     * @see #setCipherService for a description of the various {@code get/set*Key} methods.
     */
    public void setEncryptionCipherKey(byte[] encryptionCipherKey) {
        this.encryptionCipherKey = encryptionCipherKey;
    }

    /**
     * Returns the decryption cipher key to use for decryption operations.
     *
     * @return the cipher key to use for decryption operations.
     * @see #setCipherService for a description of the various {@code get/set*Key} methods.
     */
    public byte[] getDecryptionCipherKey() {
        return decryptionCipherKey;
    }

    /**
     * Sets the decryption key to use for decryption operations.
     *
     * @param decryptionCipherKey the decryption key to use for decryption operations.
     * @see #setCipherService for a description of the various {@code get/set*Key} methods.
     */
    public void setDecryptionCipherKey(byte[] decryptionCipherKey) {
        this.decryptionCipherKey = decryptionCipherKey;
    }

    /**
     * Convenience method that returns the cipher key to use for <em>both</em> encryption and decryption.
     * <p/>
     * <b>N.B.</b> This method can only be called if the underlying {@link #getCipherService() cipherService} is a symmetric
     * CipherService which by definition uses the same key for both encryption and decryption.  If using an asymmetric
     * CipherService public/private key pair, you cannot use this method, and should instead use the
     * {@link #getEncryptionCipherKey()} and {@link #getDecryptionCipherKey()} methods individually.
     * <p/>
     * The default {@link AesCipherService} instance is a symmetric cipher service, so this method can be used if you are
     * using the default.
     *
     * @return the symmetric cipher key used for both encryption and decryption.
     */
    public byte[] getCipherKey() {
        //Since this method should only be used with symmetric ciphers
        //(where the enc and dec keys are the same), either is fine, just return one of them:
        return getEncryptionCipherKey();
    }

    /**
     * Convenience method that sets the cipher key to use for <em>both</em> encryption and decryption.
     * <p/>
     * <b>N.B.</b> This method can only be called if the underlying {@link #getCipherService() cipherService} is a
     * symmetric CipherService?which by definition uses the same key for both encryption and decryption.  If using an
     * asymmetric CipherService?(such as a public/private key pair), you cannot use this method, and should instead use
     * the {@link #setEncryptionCipherKey(byte[])} and {@link #setDecryptionCipherKey(byte[])} methods individually.
     * <p/>
     * The default {@link AesCipherService} instance is a symmetric CipherService, so this method can be used if you
     * are using the default.
     *
     * @param cipherKey the symmetric cipher key to use for both encryption and decryption.
     */
    public void setCipherKey(byte[] cipherKey) {
        //Since this method should only be used in symmetric ciphers
        //(where the enc and dec keys are the same), set it on both:
        setEncryptionCipherKey(cipherKey);
        setDecryptionCipherKey(cipherKey);
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }
}
