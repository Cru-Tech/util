package org.ccci.util.seam;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.ccci.util.ValueLatch;
import org.ccci.util.contract.Preconditions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.log.Log;

@Scope(ScopeType.APPLICATION)
@AutoCreate
@Install(precedence = Install.FRAMEWORK)
@Startup
public class JndiSeamBridge
{
    private String sourceJndiName;
    
    private boolean cache;
    
    private final ValueLatch<Object> cached = new ValueLatch<Object>();
    
    @Logger Log log;
    
    @Create
    public void init(Component component) throws NamingException
    {
        Preconditions.checkState(sourceJndiName != null, "sourceJndiName has not been configured");
        log.info("mapping {0} to jndi name {1}", component.getName(), sourceJndiName);
        if (cache)
        {
            cached.set(doJndiLookup());
        }
    }
    
    @Unwrap
    public Object lookup() throws NamingException
    {
        if (cache)
        {
            return cached.get();
        }
        else
        {
            return doJndiLookup();
        }
    }

    private Object doJndiLookup() throws NamingException
    {
        return InitialContext.doLookup(sourceJndiName);
    }

    public String getSourceJndiName()
    {
        return sourceJndiName;
    }

    public void setSourceJndiName(String sourceJndiName)
    {
        log.debug("Setting sourceJndiName to " + sourceJndiName);
        this.sourceJndiName = sourceJndiName;
    }

    public boolean isCache()
    {
        return cache;
    }

    public void setCache(boolean cache)
    {
        this.cache = cache;
    }
    
    
}
