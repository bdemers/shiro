package org.apache.shiro.web.mgt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.util.StringUtils;

import java.util.Date;

/**
 * JWT based cookie RememberMeManager.  Stores the encrypted subject value as the 'sub' field in a JWT.
 * Uses the cookie expiration date as the JWT expiration.
 */
public class JwtCookieRememberMeManager extends CookieRememberMeManager {


    @Override
    protected String encodeCookieValue(byte[] serialized) {

        if (serialized != null && serialized.length > 0) {

            String sub = Base64.encodeToString(serialized);

            return Jwts.builder()
                    .setSubject(sub)
                    // cookie Max Age to mill + current time == expiration date
                    .setExpiration(new Date(System.currentTimeMillis() + (getCookie().getMaxAge() * 1000) ))
                    .compressWith(CompressionCodecs.DEFLATE)
                    .signWith(SignatureAlgorithm.HS512, getEncryptionCipherKey())
                    .compact();
            }

        return null;
    }

    @Override
    protected String decodeCookieValue(String cookieValue) {

        if (!StringUtils.hasText(cookieValue)) {
            return null;
        }

        Jws<Claims> claims = Jwts.parser()
                .setSigningKey(getDecryptionCipherKey())
                .parseClaimsJws(cookieValue);

        return claims.getBody().getSubject();
    }
}
