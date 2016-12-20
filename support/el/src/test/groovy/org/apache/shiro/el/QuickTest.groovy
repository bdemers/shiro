/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shiro.el

import org.apache.shiro.aop.MethodInvocation
import org.apache.shiro.authz.AuthorizationException
import org.apache.shiro.el.aop.ElAnnotationMethodInterceptor
import org.apache.shiro.subject.Subject
import org.apache.shiro.util.ThreadContext
import org.junit.After
import org.junit.Test

import java.lang.reflect.Method

import static org.easymock.EasyMock.*
import static org.junit.Assert.*

/**
 * Quick and dirty test for {@link org.apache.shiro.el.aop.ElAnnotationMethodInterceptor}.
 */
class QuickTest {

    @Test
    void requiresPerm() {

        def subject = mock(Subject)
        expect(subject.isPermitted("simple")).andReturn(true)
        expect(subject.isPermitted("simple")).andReturn(false)

        replay subject
        ThreadContext.bind(subject)
        assertEquals "yes", doCall("requiresPerm", null, null)

        try {
            doCall("requiresPerm", null, null)
            fail("AuthorizationException expected")
        }
        catch (AuthorizationException e) {
            // expected
        }

        verify subject
    }

    @Test
    void requiresPerms() {

        def subject = mock(Subject)
        def perms = new ArrayList();
        perms.add("require:perms1")
        perms.add("require:perms2")
        expect(subject.isPermittedAll(perms.toArray(new String[2]))).andReturn(true)
        expect(subject.isPermittedAll(perms.toArray(new String[2]))).andReturn(false)

        replay subject
        ThreadContext.bind(subject)
        doCall("requiresPerms", null, null)

        try {
            doCall("requiresPerms", null, null)
            fail("AuthorizationException expected")
        }
        catch (AuthorizationException e) {
            // expected
        }

        verify subject
    }

    @Test
    void concatPerm() {

        def subject = mock(Subject)
        expect(subject.isPermitted("simple:foobar")).andReturn(true)
        expect(subject.isPermitted("simple:foobar")).andReturn(false)

        replay subject
        ThreadContext.bind(subject)
        assertTrue doCall("concatPerm", [String] as Class[], ["foobar"] as Object[])

        try {
            doCall("concatPerm", [String] as Class[], ["foobar"] as Object[])
            fail("AuthorizationException expected")
        }
        catch (AuthorizationException e) {
            // expected
        }

        verify subject
    }


    @Test
    void requiresRole() {

        def subject = mock(Subject)
        expect(subject.hasRole("simple")).andReturn(true)
        expect(subject.hasRole("simple")).andReturn(false)

        replay subject
        ThreadContext.bind(subject)
        assertEquals "yes", doCall("requiresRole", null, null)

        try {
            doCall("requiresRole", null, null)
            fail("AuthorizationException expected")
        }
        catch (AuthorizationException e) {
            // expected
        }

        verify subject
    }

    @Test
    void requiresRoles() {

        def subject = mock(Subject)
        def roles = new ArrayList();
        roles.add("role1")
        roles.add("role2")
        expect(subject.hasAllRoles(roles)).andReturn(true)
        expect(subject.hasAllRoles(roles)).andReturn(false)

        replay subject
        ThreadContext.bind(subject)
        doCall("requiresRoles", null, null)

        try {
            doCall("requiresRoles", null, null)
            fail("AuthorizationException expected")
        }
        catch (AuthorizationException e) {
            // expected
        }

        verify subject
    }

    @Test
    void concatRole() {

        def subject = mock(Subject)
        expect(subject.hasRole("simple-foobar")).andReturn(true)
        expect(subject.hasRole("simple-foobar")).andReturn(false)

        replay subject
        ThreadContext.bind(subject)
        assertTrue doCall("concatRole", [String] as Class[], ["foobar"] as Object[])

        try {
            doCall("concatRole", [String] as Class[], ["foobar"] as Object[])
            fail("AuthorizationException expected")
        }
        catch (AuthorizationException e) {
            // expected
        }

        verify subject
    }

    @Test
    void complexExpression() {

        def subject = mock(Subject)

        // 1st
        expect(subject.hasRole("admin")).andReturn(true)

        // 2nd
        expect(subject.hasRole("admin")).andReturn(false)
        expect(subject.isPermitted("printer:queue:foobar")).andReturn(true)

        // 3rd
        expect(subject.hasRole("admin")).andReturn(false)
        expect(subject.isPermitted("printer:queue:foobar")).andReturn(false)


        replay subject
        ThreadContext.bind(subject)

        // 1st
        assertEquals "OK", doCall("complexExpression", [String] as Class[], ["foobar"] as Object[])

        // 2nd
        assertEquals "OK", doCall("complexExpression", [String] as Class[], ["foobar"] as Object[])

        try {
            // 3rd
            doCall("complexExpression", [String] as Class[], ["foobar"] as Object[])
            fail("AuthorizationException expected")
        }
        catch (AuthorizationException e) {
            // expected
        }

        verify subject


    }

    @After
    public void unbindSubject() {
        ThreadContext.unbindSubject();
        ThreadContext.unbindSecurityManager()
    }

    private Object doCall(String methodName, Class[] argTypes, Object[] args) {
        def stub = new AnnotatedStub()

        def method = stub.class.getMethod(methodName, argTypes)

        def mi = new MethodInvocation() {
            @Override
            Object proceed() throws Throwable {
                return method.invoke(getThis(), getArguments())
            }

            @Override
            Method getMethod() {
                return method
            }

            @Override
            Object[] getArguments() {
                return args
            }

            @Override
            Object getThis() {
                return stub
            }
        }

        def interceptor = new ElAnnotationMethodInterceptor()
        return interceptor.invoke(mi);
    }

}
