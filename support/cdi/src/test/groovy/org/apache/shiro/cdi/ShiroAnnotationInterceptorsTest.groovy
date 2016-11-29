package org.apache.shiro.cdi

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner
import org.apache.shiro.authz.UnauthenticatedException
import org.apache.shiro.subject.Subject
import org.apache.shiro.util.ThreadContext
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

import javax.inject.Inject

import static org.easymock.EasyMock.*
import static org.junit.Assert.*

/**
 * Tests for {@link org.apache.shiro.cdi.annotations.ShiroAnnotationInterceptors}.
 */
@RunWith(CdiTestRunner.class)
public class ShiroAnnotationInterceptorsTest {

    @Inject
    private AnnotationTestStub stub

    @Test
    def void doRequiresAuthenticationTrue() {

        def subject = mock(Subject)

        expect(subject.isAuthenticated()).andReturn(true);
        replay subject

        ThreadContext.bind(subject)

        assertEquals("Test Me", stub.doRequiresAuthentication("Test Me"))

        verify subject
    }

    @Test
    def void doRequiresAuthenticationFalse() {

        def subject = mock(Subject)

        expect(subject.isAuthenticated()).andReturn(false);
        replay subject

        ThreadContext.bind(subject)

        try {
            stub.doRequiresAuthentication("Test Me")
            fail("expected UnauthenticatedException")
        }
        catch(UnauthenticatedException e) {
            // expected
        }

        verify subject
    }

    @After
    void cleanupThread() {
        ThreadContext.unbindSubject();
    }

}
