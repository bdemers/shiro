package org.apache.shiro.cdi;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.cdi.annotations.ProcessShiroAnnotations;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.enterprise.util.AnnotationLiteral;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class ShiroCdiExtension implements Extension {

    <X> void anotherTry(@Observes @WithAnnotations(RequiresAuthentication.class) ProcessAnnotatedType<X> pat, BeanManager beanManager) {

        System.out.println("pat: "+ pat);

        /* wrap this to override the annotations of the class */
        final AnnotatedType<X> at = pat.getAnnotatedType();

        AnnotatedType<X> wrapped = new AnnotatedType<X>() {

            @Override
            public Set<AnnotatedConstructor<X>> getConstructors() {
                return at.getConstructors();
            }

            @Override
            public Set<AnnotatedField<? super X>> getFields() {
                return at.getFields();
            }

            @Override
            public Class<X> getJavaClass() {
                return at.getJavaClass();
            }

            @Override
            public Set<AnnotatedMethod<? super X>> getMethods() {
                return at.getMethods();
            }

            @Override
            public <T extends Annotation> T getAnnotation(final Class<T> annType) {
                return at.getAnnotation(annType);
            }

            @Override
            public Set<Annotation> getAnnotations() {
                Set<Annotation> original = at.getAnnotations();
                Set<Annotation> annotations = new HashSet<Annotation>(original);

                annotations.add(new AnnotationLiteral<ProcessShiroAnnotations>() {});

                return annotations;
            }

            @Override
            public Type getBaseType() {
                return at.getBaseType();
            }

            @Override
            public Set<Type> getTypeClosure() {
                return at.getTypeClosure();
            }

            @Override
            public boolean isAnnotationPresent(Class<? extends Annotation> annType) {
                if (ProcessShiroAnnotations.class.equals(annType)) {
                    return true;
                }
                return at.isAnnotationPresent(annType);
            }
        };

        pat.setAnnotatedType(wrapped);
    }

}
