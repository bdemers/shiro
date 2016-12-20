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
package org.apache.shiro.el.aop;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.aop.MethodInvocation;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.aop.AuthorizingAnnotationMethodInterceptor;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.el.ParamName;
import org.apache.shiro.el.RequiresExpression;
import org.apache.shiro.subject.Subject;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.StandardELContext;
import javax.el.ValueExpression;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Checks to see if a @{@link org.apache.shiro.el.RequiresExpression RequiresExpression} annotation is declared, and if so, performs
 * a permission check to see if the calling <code>Subject</code> is allowed to call the method.
 * @since 1.4
 */
public class ElAnnotationMethodInterceptor extends AuthorizingAnnotationMethodInterceptor {


    private ExpressionFactory expressionFactory;
    private ELContext elContext;

    /**
     * Default no-argument constructor that ensures this interceptor looks for
     * {@link org.apache.shiro.el.RequiresExpression RequiresExpression} annotations in a method declaration.
     */
    public ElAnnotationMethodInterceptor() {
        super( new ElAnnotationHandler() );

        expressionFactory = ExpressionFactory.newInstance();
        elContext = new StandardELContext(expressionFactory);

        try {
            Method perm = AuthorizerFunctions.class.getMethod("perm", String.class);
            elContext.getFunctionMapper().mapFunction("", "perm", perm);

            Method perms = AuthorizerFunctions.class.getMethod("perms", Collection.class);
            elContext.getFunctionMapper().mapFunction("", "perms", perms);

            Method role = AuthorizerFunctions.class.getMethod("role", String.class);
            elContext.getFunctionMapper().mapFunction("", "role", role);

            Method roles = AuthorizerFunctions.class.getMethod("roles", Collection.class);
            elContext.getFunctionMapper().mapFunction("", "roles", roles);

        } catch (NoSuchMethodException e) {
            throw new ConfigurationException("Could not find method 'AuthorizerFunctions.perm', this is likely due " +
                    "to a classpath or other runtime issue.");
        }
    }


    @Override
    public void assertAuthorized(MethodInvocation mi) throws AuthorizationException {

        Annotation a = getAnnotation(mi);
        if (!(a instanceof RequiresExpression)) return;

        RequiresExpression reAnnotation = (RequiresExpression) a;
        String expressionString = reAnnotation.value();

        ELContext processElContext = new StandardELContext(elContext);

        Annotation[][] annotations = mi.getMethod().getParameterAnnotations();
        Object[] parameters = mi.getArguments();

        if (parameters != null) {
            for (int ii = 0; parameters.length > ii; ii++) {

                // oh... java 1.6...
                Object paramValue = parameters[ii];
                Annotation[] paramAnnotations = annotations[ii];

                for (Annotation annotation : paramAnnotations) {
                    if (ParamName.class.equals(annotation.annotationType())) {
                        processElContext.getELResolver().setValue(processElContext,
                                null,
                                ((ParamName) annotation).value(),
                                paramValue);
                    }
                }
            }
        }

        ValueExpression valueExpression = expressionFactory.createValueExpression(processElContext, expressionString, Boolean.class);
        if (!Boolean.TRUE.equals(valueExpression.getValue(processElContext))) {
            throw new AuthorizationException("Evaluation of expression resulted in a non-true result");
        }
    }

    public static class AuthorizerFunctions {

        private static Subject getSubject() {
            return SecurityUtils.getSubject();
        }

        public static boolean perm(String perm) {
            return getSubject().isPermitted(perm);
        }

        public static boolean perms(Collection<String> perms) {
            return getSubject().isPermittedAll(perms.toArray(new String[perms.size()]));
        }

        public static boolean role(String role) {
            return getSubject().hasRole(role);
        }

        public static boolean roles(Collection<String> roles) {
            return getSubject().hasAllRoles(roles);
        }
    }
}
