package org.apache.shiro.cdi.web;

import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cdi.ShiroCdiConfiguration;
import org.apache.shiro.cdi.Standard;
import org.apache.shiro.event.EventBus;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.DefaultSubjectFactory;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.SubjectDAO;
import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionFactory;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Destroyable;
import org.apache.shiro.web.filter.mgt.DefaultFilter;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.ServletContainerSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionManager;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.New;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Specializes;
import javax.enterprise.inject.Typed;
import javax.enterprise.util.AnnotationLiteral;

public class ShiroCdiWebConfiguration extends ShiroCdiConfiguration {

    @Web
    @NativeSessions
    @Produces
    @ApplicationScoped
    protected DefaultWebSessionManager nativeSessionManager(@New DefaultWebSessionManager webSessionManager,
                                                  SessionFactory sessionFactory,
                                                  SessionDAO sessionDAO) {

//        webSessionManager.setSessionIdCookieEnabled(sessionIdCookieEnabled);
//        webSessionManager.setSessionIdUrlRewritingEnabled(sessionIdUrlRewritingEnabled);
//        webSessionManager.setSessionIdCookie(sessionCookieTemplate());

        webSessionManager.setSessionFactory(sessionFactory);
        webSessionManager.setSessionDAO(sessionDAO);
//        webSessionManager.setDeleteInvalidSessions(sessionManagerDeleteInvalidSessions);

        return webSessionManager;
    }

//    protected Cookie sessionCookieTemplate() {
//        return buildCookie(
//                sessionIdCookieName,
//                sessionIdCookieMaxAge,
//                sessionIdCookiePath,
//                sessionIdCookieDomain,
//                sessionIdCookieSecure);
//    }
//
//    protected Cookie rememberMeCookieTemplate() {
//        return buildCookie(
//                rememberMeCookieName,
//                rememberMeCookieMaxAge,
//                rememberMeCookiePath,
//                rememberMeCookieDomain,
//                rememberMeCookieSecure);
//    }

    protected Cookie buildCookie(String name, int maxAge, String path, String domain, boolean secure) {
        Cookie cookie = new SimpleCookie(name);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);
        cookie.setDomain(domain);
        cookie.setSecure(secure);

        return cookie;
    }

    @Default
    @Web
    @ServletContainerSessions
    @Produces
    @ApplicationScoped
    @Typed(ServletContainerSessionManager.class)
    protected ServletContainerSessionManager sessionManager(@New ServletContainerSessionManager sessionManager) {
        return sessionManager;
    }

    @Produces
    @Specializes
    @ApplicationScoped
    @Override
    protected WebSessionManager sessionManager(Instance<SessionManager> sessionManagers) {
//        return sessionManagers.select(ServletContainerSessionManager.class).get();
        return sessionManagers.select(ServletContainerSessionManager.class,
                                      new AnnotationLiteral<ServletContainerSessions>() {},
                                      new AnnotationLiteral<Web>() {}).get();
    }


    @Produces
    @ApplicationScoped
    protected RememberMeManager rememberMeManager(@New CookieRememberMeManager rememberMeManager) {
//        rememberMeManager.setCookie(rememberMeCookieTemplate());
        return rememberMeManager;
    }

    @Web
    @Produces
    @ApplicationScoped
    @Typed({
            SecurityManager.class,
            WebSecurityManager.class,
            DefaultSecurityManager.class,
            DefaultWebSecurityManager.class,
            Destroyable.class
    })
    protected DefaultWebSecurityManager securityManager(@New DefaultWebSecurityManager securityManager,
                                                        Instance<Realm> realms,
                                                        EventBus eventBus,
                                                        SessionManager sessionManager,
                                                        Instance<CacheManager> cacheManager,
                                                        SubjectDAO subjectDAO,
                                                        @Web SubjectFactory subjectFactory,
                                                        Instance<RememberMeManager> rememberMeManager,
                                                        Authenticator authenticator,
                                                        Authorizer authorizer) {

        super.securityManager(securityManager,
                              realms,
                              eventBus,
                              sessionManager,
                              cacheManager,
                              subjectDAO,
                              subjectFactory,
                              rememberMeManager,
                              authenticator,
                              authorizer);
        return securityManager;
    }

    @Produces
    @ApplicationScoped
    @Specializes
    @Typed({ SecurityManager.class, DefaultSecurityManager.class, WebSecurityManager.class, DefaultWebSecurityManager.class })
    @Override
    protected DefaultWebSecurityManager securityManager(@Web DefaultSecurityManager securityManager) {
        return (DefaultWebSecurityManager) securityManager;
    }

    @Web
    @Produces
    @ApplicationScoped
    protected DefaultWebSubjectFactory subjectFactory(@New DefaultWebSubjectFactory subjectFactory) {
        return subjectFactory;
    }

    @Specializes
    @Produces
    @ApplicationScoped
    @Override
    protected DefaultWebSubjectFactory subjectFactory(@Web SubjectFactory subjectFactory) {
        return (DefaultWebSubjectFactory) subjectFactory;
    }


    @Produces
    @ApplicationScoped
    protected FilterChainResolver filterChainResolver(@New PathMatchingFilterChainResolver filterChainResolver) {
        return filterChainResolver;
    }
}
