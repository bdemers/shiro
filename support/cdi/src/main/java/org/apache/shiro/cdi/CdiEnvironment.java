package org.apache.shiro.cdi;

import org.apache.shiro.env.Environment;
import org.apache.shiro.mgt.SecurityManager;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class CdiEnvironment implements Environment {

    @Inject
    private SecurityManager securityManager;

    @Override
    public SecurityManager getSecurityManager() {
        return securityManager;
    }
}
