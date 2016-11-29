package org.apache.shiro.cdi;

import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.AuthenticationStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.authz.permission.RolePermissionResolver;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.event.EventBus;
import org.apache.shiro.event.support.DefaultEventBus;
import org.apache.shiro.mgt.CachingSecurityManager;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.DefaultSubjectFactory;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.SessionStorageEvaluator;
import org.apache.shiro.mgt.SubjectDAO;
import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SessionFactory;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.SimpleSessionFactory;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.util.Destroyable;
import org.apache.shiro.util.Initializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.New;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ShiroCdiConfiguration {

    @Inject
    private Instance<Initializable> initializables;

    @Inject
    private Instance<Destroyable> destroyables;

    @Default
    @Produces
    @Typed({
        SecurityManager.class,
        DefaultSecurityManager.class,
        Destroyable.class // We need to set all of the possible types here,
                          // as this will conflict with Authorizer/Authenticator
                          // TODO: I'm not sure this is the best approach
    })
    @ApplicationScoped
    protected DefaultSecurityManager securityManager(@New DefaultSecurityManager securityManager,
                                                     Instance<Realm> realms,
                                                     EventBus eventBus,
                                                     SessionManager sessionManager,
                                                     Instance<CacheManager> cacheManager,
                                                     SubjectDAO subjectDAO,
                                                     SubjectFactory subjectFactory,
                                                     Instance<RememberMeManager> rememberMeManager,
                                                     Authenticator authenticator,
                                                     Authorizer authorizer ) {

        List<Realm> realmList = new ArrayList<Realm>();
        for (Realm realm : realms) {
            realmList.add(realm);
        }
        securityManager.setRealms(realmList);

        // Set the cache manager if available
        if(!cacheManager.isUnsatisfied()) {
            securityManager.setCacheManager(cacheManager.get());
        }

        if(!rememberMeManager.isUnsatisfied()) {
            securityManager.setRememberMeManager(rememberMeManager.get());
        }

        securityManager.setEventBus(eventBus);
        securityManager.setAuthenticator(authenticator);
        securityManager.setAuthorizer(authorizer);
        securityManager.setSessionManager(sessionManager);
        securityManager.setSubjectDAO(subjectDAO);
        securityManager.setSubjectFactory(subjectFactory);

        return securityManager;
    }

    @Produces
    @ApplicationScoped
    protected DefaultSessionManager sessionManager(@New DefaultSessionManager sessionManager,
                                            SessionDAO sessionDAO,
                                            SessionFactory sessionFactory) {

        sessionManager.setSessionDAO(sessionDAO);
        sessionManager.setSessionFactory(sessionFactory);
        sessionManager.setDeleteInvalidSessions(true); // TODO: add configuration for this.
        return sessionManager;
    }

//    @Produces
//    @ApplicationScoped
//    @Named("shiro.sessionManager.deleteInvalidSessions")
//    protected Boolean sessionManagerDeleteInvalidSessions() {
//        return true;
//    }

    @Default
    @Produces
    @ApplicationScoped
    public DefaultEventBus getEventBus(@New DefaultEventBus eventBus) {
        return eventBus;
    }

    @Produces
    @ApplicationScoped
    protected DefaultSubjectDAO subjectDAO(@New DefaultSubjectDAO subjectDAO, SessionStorageEvaluator sessionStorageEvaluator) {
        subjectDAO.setSessionStorageEvaluator(sessionStorageEvaluator);
        return subjectDAO;
    }

    @Produces
    @ApplicationScoped
    protected DefaultSessionStorageEvaluator sessionStorageEvaluator(@New DefaultSessionStorageEvaluator sessionStorageEvaluator) {
        return sessionStorageEvaluator;
    }

    @Produces
    @ApplicationScoped
    protected DefaultSubjectFactory subjectFactory(@New DefaultSubjectFactory subjectFactory) {
        return subjectFactory;
    }

    @Produces
    @ApplicationScoped
    protected SimpleSessionFactory sessionFactory(@New SimpleSessionFactory sessionFactory) {
        return sessionFactory;
    }

    @Produces
    @ApplicationScoped
    protected MemorySessionDAO sessionDAO(@New MemorySessionDAO memorySessionDAO) {
        return memorySessionDAO;
    }

    @Produces
    @ApplicationScoped
    protected ModularRealmAuthorizer authorizer(@New ModularRealmAuthorizer authorizer,
                                    Instance<PermissionResolver> permissionResolver,
                                    Instance<RolePermissionResolver> rolePermissionResolver) {

        if (permissionResolver != null && !permissionResolver.isUnsatisfied()) {
            authorizer.setPermissionResolver(permissionResolver.get());
        }

        if (rolePermissionResolver != null && !rolePermissionResolver.isUnsatisfied()) {
            authorizer.setRolePermissionResolver(rolePermissionResolver.get());
        }

        return authorizer;
    }

    @Produces
    @ApplicationScoped
    protected AtLeastOneSuccessfulStrategy authenticationStrategy(@New AtLeastOneSuccessfulStrategy authenticationStrategy) {
        return authenticationStrategy;
    }

    @Produces
    @ApplicationScoped
    protected ModularRealmAuthenticator authenticator(@New ModularRealmAuthenticator authenticator,
                                          AuthenticationStrategy authenticationStrategy) {
        authenticator.setAuthenticationStrategy(authenticationStrategy);
        return authenticator;
    }

    @PostConstruct
    void init() {
        for(Initializable initializable : initializables) {
            initializable.init();
        }
    }

    @PreDestroy
    void destory() throws Exception {
        for(Destroyable destroyable : destroyables) {
            System.out.println("Destroying: "+ destroyable);
            destroyable.destroy();
        }
    }
}
