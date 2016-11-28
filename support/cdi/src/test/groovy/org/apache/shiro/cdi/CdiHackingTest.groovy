package org.apache.shiro.cdi

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner
import org.apache.shiro.authc.Authenticator
import org.apache.shiro.authc.pam.ModularRealmAuthenticator
import org.apache.shiro.authz.Authorizer
import org.apache.shiro.authz.ModularRealmAuthorizer
import org.apache.shiro.authz.permission.PermissionResolver
import org.apache.shiro.cache.CacheManager
import org.apache.shiro.event.EventBus
import org.apache.shiro.mgt.DefaultSecurityManager
import org.apache.shiro.mgt.RememberMeManager
import org.apache.shiro.mgt.SecurityManager
import org.apache.shiro.mgt.SubjectDAO
import org.apache.shiro.mgt.SubjectFactory
import org.apache.shiro.realm.Realm
import org.apache.shiro.session.mgt.SessionManager
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Default
import javax.enterprise.inject.Instance
import javax.inject.Inject

import static org.hamcrest.Matchers.*

import static org.junit.Assert.*

/**
 * Hacking around with CDI and Shiro.
 */
@RunWith(CdiTestRunner.class)
@Default
@ApplicationScoped
public class CdiHackingTest {


    @Inject
    private SecurityManager securityManager;

//    @Inject
//    private ShiroCdiExtension extension;

    @Inject
    private CdiEnvironment cdiEnvironment;

    @Inject
    private EventBus eventBus;

    @Inject
    private Instance<Realm> realms;

    @Inject
    private SessionManager sessionManager

    @Inject
    private Instance<CacheManager> cacheManager;

    @Inject
    private SubjectDAO subjectDAO;

    @Inject
    private SubjectFactory subjectFactory;

    @Inject
    private Instance<RememberMeManager> rememberMeManager;

    @Inject
    private Authenticator authenticator;

    @Inject
    private Authorizer authorizer;

    @Inject
    private Instance<PermissionResolver> permissionResolver;

    @Inject
    private PermissionResolver wtf;

    @Test
    public void doSomeStuff() {

        assertNotNull(eventBus)
        assertNotNull(authenticator)
        assertNotNull(authorizer)
        assertNotNull(subjectFactory)
        assertNotNull(subjectDAO)
        assertNotNull(sessionManager)

        assertTrue(rememberMeManager.isUnsatisfied())
        assertTrue(cacheManager.isUnsatisfied())

        assertNotNull(permissionResolver.get())

        assertSame cdiEnvironment.securityManager, securityManager
        assertThat securityManager, instanceOf(DefaultSecurityManager)
        assertSame securityManager.authenticator, authenticator
        assertSame securityManager.authorizer.getPermissionResolver(), permissionResolver.get()

    }
}
