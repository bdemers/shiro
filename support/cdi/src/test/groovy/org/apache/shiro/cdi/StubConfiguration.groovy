package org.apache.shiro.cdi

import org.apache.shiro.authz.Permission
import org.apache.shiro.authz.permission.PermissionResolver
import org.apache.shiro.realm.Realm
import org.apache.shiro.realm.text.TextConfigurationRealm

import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces
import javax.enterprise.inject.Typed

class StubConfiguration {

    @Typed(Realm)
    @Produces
    @ApplicationScoped
    private TextConfigurationRealm createTestRealm() {
        return new TextConfigurationRealm()
    }

    @Produces
    @ApplicationScoped
    private PermissionResolver createPermissionResolver() {
        return new PermissionResolver() {
            @Override
            Permission resolvePermission(String permissionString) {
                return null
            }
        }
    }

}
