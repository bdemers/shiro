package org.apache.shiro.cdi

import org.apache.shiro.authc.Authenticator
import org.apache.shiro.authc.pam.ModularRealmAuthenticator
import org.apache.shiro.authz.Permission
import org.apache.shiro.authz.permission.PermissionResolver
import org.apache.shiro.realm.Realm
import org.apache.shiro.realm.text.TextConfigurationRealm

import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces

class StubConfiguration {

    @Produces
    @ApplicationScoped
    private Realm createTestRealm() {
        return new TextConfigurationRealm()
    }

    static class StubPermissionResolver implements PermissionResolver {
        @Override
        Permission resolvePermission(String permissionString) {
            return null
        }
    }


}
