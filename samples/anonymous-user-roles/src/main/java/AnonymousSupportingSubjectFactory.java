import org.apache.shiro.mgt.DefaultSubjectFactory;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;

/**
 * A subject factory that will sets the default principals to an 'anonymous' user.
 */
public class AnonymousSupportingSubjectFactory extends DefaultSubjectFactory {

    @Override
    public Subject createSubject( SubjectContext context )
    {
        PrincipalCollection principals = context.resolvePrincipals();
        if (principals == null || principals.isEmpty()) { // or check for a flag in the context
            context.setPrincipals( new SimplePrincipalCollection( "anonymous", "n/a" ) );
        }

        return super.createSubject( context );
    }
}
