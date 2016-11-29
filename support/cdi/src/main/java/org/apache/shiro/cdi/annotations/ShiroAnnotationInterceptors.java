package org.apache.shiro.cdi.annotations;

import org.apache.shiro.aop.MethodInterceptor;
import org.apache.shiro.aop.MethodInvocation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.authz.aop.AnnotationsAuthorizingMethodInterceptor;
import org.apache.shiro.authz.aop.AuthenticatedAnnotationMethodInterceptor;
import org.apache.shiro.authz.aop.AuthorizingAnnotationMethodInterceptor;
import org.apache.shiro.authz.aop.GuestAnnotationMethodInterceptor;
import org.apache.shiro.authz.aop.PermissionAnnotationMethodInterceptor;
import org.apache.shiro.authz.aop.RoleAnnotationMethodInterceptor;
import org.apache.shiro.authz.aop.UserAnnotationMethodInterceptor;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;

@ProcessShiroAnnotations
@Interceptor
@Priority(Interceptor.Priority.LIBRARY_BEFORE + 10)
public final class ShiroAnnotationInterceptors extends AnnotationsAuthorizingMethodInterceptor implements MethodInterceptor {

        @AroundInvoke
        public Object invoke(final InvocationContext invocationContext) throws Throwable {

            return invoke(new MethodInvocation() {
                @Override
                public Object proceed() throws Throwable {
                    return invocationContext.proceed();
                }

                @Override
                public Method getMethod() {
                    return invocationContext.getMethod();
                }

                @Override
                public Object[] getArguments() {
                    return invocationContext.getParameters();
                }

                @Override
                public Object getThis() {
                    return invocationContext.getTarget();
                }
            });
        }


//    @RequiresAuthentication
//    @ProcessShiroAnnotations
//    @Interceptor
//    @Priority(Interceptor.Priority.LIBRARY_BEFORE + 10)
//    public static class RequiresAuthenticationInterceptor extends BaseShiroAnnotationInterceptor {
//        public RequiresAuthenticationInterceptor() {
//            super(new AuthenticatedAnnotationMethodInterceptor());
//        }
//    }
//
//    @RequiresUser
//    @ProcessShiroAnnotations
//    @Interceptor
//    @Priority(Interceptor.Priority.LIBRARY_BEFORE + 20)
//    public static class RequiresUserInterceptor extends BaseShiroAnnotationInterceptor {
//        public RequiresUserInterceptor() {
//            super(new UserAnnotationMethodInterceptor());
//        }
//    }
//
//    @RequiresGuest
//    @ProcessShiroAnnotations
//    @Interceptor
//    @Priority(Interceptor.Priority.LIBRARY_BEFORE + 30)
//    public static class RequiresGuestInterceptor extends BaseShiroAnnotationInterceptor {
//        public RequiresGuestInterceptor() {
//            super(new GuestAnnotationMethodInterceptor());
//        }
//    }
//
//    @RequiresRoles("")
//    @ProcessShiroAnnotations
//    @Interceptor
//    @Priority(Interceptor.Priority.LIBRARY_BEFORE + 40)
//    public static class RequiresRolesInterceptor extends BaseShiroAnnotationInterceptor {
//        public RequiresRolesInterceptor() {
//            super(new RoleAnnotationMethodInterceptor());
//        }
//    }
//
//    @RequiresPermissions("")
//    @ProcessShiroAnnotations
//    @Interceptor
//    @Priority(Interceptor.Priority.LIBRARY_BEFORE + 40)
//    public static class RequiresPermissionsInterceptor extends BaseShiroAnnotationInterceptor {
//        public RequiresPermissionsInterceptor() {
//            super(new PermissionAnnotationMethodInterceptor());
//        }
//    }

//    static abstract class BaseShiroAnnotationInterceptor {
//
//        private final AuthorizingAnnotationMethodInterceptor methodInterceptor;
//
//        BaseShiroAnnotationInterceptor(AuthorizingAnnotationMethodInterceptor methodInterceptor) {
//            this.methodInterceptor = methodInterceptor;
//        }
//
//        @AroundInvoke
//        public Object invoke(final InvocationContext invocationContext) throws Throwable {
//            return methodInterceptor.invoke(new MethodInvocation() {
//                @Override
//                public Object proceed() throws Throwable {
//                    return invocationContext.proceed();
//                }
//
//                @Override
//                public Method getMethod() {
//                    return invocationContext.getMethod();
//                }
//
//                @Override
//                public Object[] getArguments() {
//                    return invocationContext.getParameters();
//                }
//
//                @Override
//                public Object getThis() {
//                    return invocationContext.getTarget();
//                }
//            });
//        }
//    }
}
