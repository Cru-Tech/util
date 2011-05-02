package org.ccci.util.mail;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.mail.Session;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Used by client code to create {@link MailMessage}s
 * 
 * @author Matt Drees
 */
@Name("mailMessageFactory")
@Scope(ScopeType.STATELESS)
@AutoCreate
public class MailMessageFactory
{

    @In Session mailSession;

    /**
     * For usage by Seam, where a Seam-managed {@link Session} is configured
     */
    public MailMessageFactory() {}
    
    /**
     * For usage outside of Seam
     */
    public MailMessageFactory(String smtpHost)
    {
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", smtpHost);
        this.mailSession = Session.getInstance(props);
    }

    /**
     * Creates a {@link MailMessage} suitable for application/business email messages.
     * These emails might be indirected by {@link MailIndirection}, if configured.
     */
    public MailMessage createApplicationMessage()
    {
        checkTimeoutProperty();
        return new MailMessage(mailSession, false);
    }
    
    private void checkTimeoutProperty()
    {
        Properties mailProperties = mailSession.getProperties();
        String transport = mailProperties.getProperty("mail.transport.protocol");
        setPropertyIfUnspecified(mailProperties, "mail." + transport + ".connectiontimeout", 5);
        setPropertyIfUnspecified(mailProperties, "mail." + transport + ".timeout", 15);
    }

    private void setPropertyIfUnspecified(Properties mailProperties, String timeoutProperty, long duration)
    {
        if (!mailProperties.containsKey(timeoutProperty))
        {
            mailProperties.setProperty(timeoutProperty, String.valueOf(TimeUnit.SECONDS.toMillis(duration)));
        }
    }

    /**
     * Creates a {@link MailMessage} suitable for system/sysadmin email messages.  For example,
     * used by exception email reports.  These should be guaranteed to be delivered to the emailaddress,
     * even if {@link MailIndirection} is configured.
     */
    public MailMessage createSystemMessage()
    {
        checkTimeoutProperty();
        return new MailMessage(mailSession, true);
    }
}
