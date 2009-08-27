package org.ccci.auth;

import java.util.Map;

import org.ccci.util.Exceptions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.security.Identity;

@Name("org.jboss.seam.security.identity")
@Scope(ScopeType.SESSION)
@BypassInterceptors
@Startup
public class CcciIdentity extends Identity
{
    private static final long serialVersionUID = 1L;
    
    private Map<String,String> attributes;

    private Exception authenticationSystemException;
    
    public Map<String, String> getAttributes()
    {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes)
    {
        this.attributes = attributes;
    }

    /**
     * see {@link #login()}
     * @param loginException
     */
    public void notifyAuthenticationSystemException(Exception loginException)
    {
        this.authenticationSystemException = loginException;
    }

    /**
     * by default, Seam logs & swallows system exceptions that occur during login, and
     * shows the user a "login failed" message.  I'd rather them see an error page.
     * 
     * The authentication method should call {@link #notifyAuthenticationSystemException(Exception)}
     * if a system exception occurs.
     */
    @Override
    public String login()
    {
        authenticationSystemException = null;
        String login = super.login();
        if (authenticationSystemException != null)
        {
            throw Exceptions.wrap(authenticationSystemException);
        }
        return login;
    }
}
