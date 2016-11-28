package org.apache.shiro.cdi;

import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.util.Destroyable;
import org.apache.shiro.util.Initializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.util.AnnotationLiteral;
import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ShiroCdiExtension implements Extension {


//    void afterBeanDiscovery(@Observes AfterBeanDiscovery afterBeanDiscovery, BeanManager beanManager) throws NoSuchMethodException {


//        afterBeanDiscovery.addBean(createBean(SecurityManager.class, DefaultSecurityManager.class, beanManager));
//    }
//
//    private static <T, I extends T> Bean createBean(Class<T> a, Class<I> b, BeanManager beanManager) {
//        AnnotatedType<I> at = beanManager.createAnnotatedType(b);
//        return new ShiroBean<T, I>(a, b, beanManager.createInjectionTarget(at));
//    }
//
//    static class ShiroBean<T, I extends T> implements Bean<I> {
//
//        private final Logger log = LoggerFactory.getLogger(ShiroBean.class);
//
//        private final Class<T> typeClass;
//        private final Class<I> beanClass;
//        private final InjectionTarget<I> injectionTarget;
//
//        public ShiroBean(Class<T> typeClass, Class<I> beanClass, InjectionTarget<I> injectionTarget) {
//            this.typeClass = typeClass;
//            this.beanClass = beanClass;
//            this.injectionTarget = injectionTarget;
//        }
//
//        @Override
//        public Class<?> getBeanClass() {
//            return beanClass;
//        }
//
//        @Override
//        public Set<InjectionPoint> getInjectionPoints() {
//            return injectionTarget.getInjectionPoints();
//        }
//
//        @Override
//        public String getName() {
//            return "shiro." + Introspector.decapitalize(typeClass.getSimpleName());
//        }
//
//        @Override
//        public Set<Annotation> getQualifiers() {
//
//            Set<Annotation> qualifiers = new HashSet<Annotation>();
//            qualifiers.add( new AnnotationLiteral<Default>() {} );
//            qualifiers.add( new AnnotationLiteral<Any>() {} );
//
//            return qualifiers;
//        }
//
//        @Override
//        public Class<? extends Annotation> getScope() {
//            return ApplicationScoped.class;
//        }
//
//        @Override
//        public Set<Class<? extends Annotation>> getStereotypes() {
//            return Collections.emptySet();
//        }
//
//        @Override
//        public Set<Type> getTypes() {
//
//            Set<Type> types = new HashSet<Type>();
//            types.add(typeClass);
//            return types;
//        }
//
//        @Override
//        public boolean isAlternative() {
//            return false;
//        }
//
//        @Override
//        public boolean isNullable() {
//            return false;
//        }
//
//        @Override
//        public I create(CreationalContext<I> ctx) {
//
//            I instance = injectionTarget.produce(ctx);
//            injectionTarget.inject(instance, ctx);
//            injectionTarget.postConstruct(instance);
//
//            if(instance instanceof Initializable) {
//                ((Initializable) instance).init();
//            }
//
//            return instance;
//        }
//
//        @Override
//        public void destroy(I instance, CreationalContext<I> ctx) {
//
//            if(instance instanceof Destroyable) {
//                try {
//                    ((Destroyable) instance).destroy();
//                } catch (Exception e) {
//                    log.error("Exception thrown while destroying object.", e);
//                }
//            }
//
//            injectionTarget.preDestroy(instance);
//            injectionTarget.dispose(instance);
//            ctx.release();
//        }
//    }

}
