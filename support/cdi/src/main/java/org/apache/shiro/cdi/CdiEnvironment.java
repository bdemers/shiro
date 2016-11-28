package org.apache.shiro.cdi;

import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.env.Environment;
import org.apache.shiro.event.EventBus;
import org.apache.shiro.mgt.AuthenticatingSecurityManager;
import org.apache.shiro.mgt.AuthorizingSecurityManager;
import org.apache.shiro.mgt.CachingSecurityManager;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.mgt.SubjectDAO;
import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class CdiEnvironment implements Environment {


//    @Inject
//    private SecurityManager securityManager;

//    @Inject
//    private Instance<SessionManager> sessionManager;
//
//    @Inject
//    private Instance<Realm> realms;
//
//    @Inject
//    private Instance<CacheManager> cacheManager;
//
//    @Inject
//    private EventBus eventBus;
//
//    @Inject
//    private Instance<SubjectDAO> subjectDAO;
//
//    @Inject
//    private Instance<SubjectFactory> subjectFactory;
//
//    @Inject
//    private Instance<RememberMeManager> rememberMeManager;
//
//    @Inject
//    private Instance<Authenticator> authenticator;
//
//    @Inject
//    private Instance<Authorizer> authorizer;


    @PostConstruct
    public void init() {

//        // this will almost always be true.
//        if(securityManager instanceof RealmSecurityManager) {
//            List<Realm> realmList = new ArrayList<Realm>();
//            for (Realm realm : realms) {
//                realmList.add(realm);
//            }
//            ((RealmSecurityManager) securityManager).setRealms(realmList);
//        }
//
//        // Set the cache manager if available
//        if(securityManager instanceof CachingSecurityManager) {
//            if(!cacheManager.isUnsatisfied()) {
//                ((CachingSecurityManager)securityManager).setCacheManager(cacheManager.get());
//            }
//
//            ((CachingSecurityManager)securityManager).setEventBus(eventBus);
//        }
//
//        if(securityManager instanceof AuthenticatingSecurityManager) {
//            ((AuthenticatingSecurityManager) securityManager).setAuthenticator(authenticator.get());
//        }
//
//        if(securityManager instanceof AuthorizingSecurityManager) {
//            ((AuthorizingSecurityManager) securityManager).setAuthorizer(authorizer.get());
//        }
//
//        if(securityManager instanceof SessionsSecurityManager) {
//            ((SessionsSecurityManager) securityManager).setSessionManager(sessionManager.get());
//        }
//
//        if(securityManager instanceof DefaultSecurityManager) {
//
//            DefaultSecurityManager defaultSecurityManager = (DefaultSecurityManager) securityManager;
//
//            if(subjectDAO != null) {
//                defaultSecurityManager.setSubjectDAO(subjectDAO.get());
//            }
//
//            if(subjectFactory != null) {
//                defaultSecurityManager.setSubjectFactory(subjectFactory.get());
//            }
//
//            if (rememberMeManager != null) {
//                defaultSecurityManager.setRememberMeManager(rememberMeManager.get());
//            }
//        }
    }

    @Override
    public SecurityManager getSecurityManager() {
     return null;
//        return securityManager;
    }








}
