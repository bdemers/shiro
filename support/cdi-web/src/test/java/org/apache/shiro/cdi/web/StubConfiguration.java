package org.apache.shiro.cdi.web;

import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.TextConfigurationRealm;
import org.apache.shiro.util.Initializable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;

@ApplicationScoped
class StubConfiguration {

    @Typed({Realm.class, Initializable.class})
    @Produces
    @ApplicationScoped
    private TextConfigurationRealm createTestRealm() {
        return new TextConfigurationRealm();
    }
}
