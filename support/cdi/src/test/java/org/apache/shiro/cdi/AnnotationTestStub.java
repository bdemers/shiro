package org.apache.shiro.cdi;

import org.apache.shiro.authz.annotation.RequiresAuthentication;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AnnotationTestStub {

    @RequiresAuthentication
    public String doRequiresAuthentication(String param) {
        return param;
    }
}
