package org.apache.shiro.cdi.web

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner
import org.apache.shiro.authc.Authenticator
import org.apache.shiro.authz.Authorizer
import org.apache.shiro.cache.CacheManager
import org.apache.shiro.cdi.CdiEnvironment
import org.apache.shiro.event.EventBus
import org.apache.shiro.mgt.DefaultSecurityManager
import org.apache.shiro.mgt.RememberMeManager
import org.apache.shiro.mgt.SecurityManager
import org.apache.shiro.mgt.SubjectDAO
import org.apache.shiro.mgt.SubjectFactory
import org.apache.shiro.realm.Realm
import org.apache.shiro.session.mgt.NativeSessionManager
import org.apache.shiro.session.mgt.SessionManager
import org.apache.shiro.web.mgt.DefaultWebSecurityManager
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory
import org.apache.shiro.web.session.mgt.ServletContainerSessionManager
import org.apache.shiro.web.session.mgt.WebSessionManager
import org.junit.Test
import org.junit.runner.RunWith

import javax.enterprise.inject.Instance
import javax.inject.Inject

import static org.hamcrest.Matchers.instanceOf
import static org.junit.Assert.*

@RunWith(CdiTestRunner.class)
public class MoreHackingTest {


    @Inject
    private SecurityManager securityManager;

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

    @Test
    public void doStuff() {
        assertNotNull(eventBus)
        assertNotNull(authenticator)
        assertNotNull(authorizer)
        assertNotNull(subjectFactory)
        assertNotNull(subjectDAO)
        assertNotNull(sessionManager)

        assertFalse(rememberMeManager.isUnsatisfied())
        assertTrue(cacheManager.isUnsatisfied())

        assertSame cdiEnvironment.securityManager, securityManager
        assertThat securityManager, instanceOf(DefaultWebSecurityManager)
        assertSame securityManager.authenticator, authenticator

        assertEquals subjectFactory, ((DefaultSecurityManager)securityManager).getSubjectFactory()
        assertThat subjectFactory, instanceOf(DefaultWebSubjectFactory)

        assertEquals sessionManager, ((DefaultSecurityManager)securityManager).getSessionManager()

        assertThat sessionManager, instanceOf(WebSessionManager)
    }

}
