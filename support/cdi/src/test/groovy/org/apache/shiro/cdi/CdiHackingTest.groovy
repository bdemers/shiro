package org.apache.shiro.cdi

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner
import org.apache.shiro.authc.Authenticator
import org.apache.shiro.authc.pam.ModularRealmAuthenticator
import org.apache.shiro.authz.Authorizer
import org.apache.shiro.authz.permission.PermissionResolver
import org.apache.shiro.cache.CacheManager
import org.apache.shiro.event.EventBus
import org.apache.shiro.mgt.DefaultSecurityManager
import org.apache.shiro.mgt.RememberMeManager
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
import javax.enterprise.inject.Produces
import javax.inject.Inject

import static org.junit.Assert.*

/**
 * Hacking around with CDI and Shiro.
 */
@RunWith(CdiTestRunner.class)
@Default
@ApplicationScoped
public class CdiHackingTest {

//    @Inject
//    private SecurityManager securityManager;

//    @Inject
//    private ShiroCdiExtension extension;

//    @Inject
//    private CdiEnvironment cdiEnvironment;

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


//    @Inject
//    private Instance<PermissionResolver> permissionResolver;

    @Test
    public void doSomeStuff() {

        assertNotNull(eventBus)
//        assertNotNull(permissionResolver.get())
        assertNotNull(authenticator)

//        Assert.assertSame cdiEnvironment.securityManager, securityManager
//        assertTrue(cdiEnvironment.securityManager instanceof DefaultSecurityManager)
//        assertSame securityManager.authenticator, authenticator

//        DefaultSecurityManager defaultSecurityManager = cdiEnvironment.getSecurityManager()
//        ModularRealmAuthorizer authorizer = defaultSecurityManager.getAuthorizer()
//        Assert.assertSame authorizer.getPermissionResolver(), permissionResolver

    }

//    @Produces
//    private Realm createTestRealm() {
//        return new TextConfigurationRealm()
//    }



}
