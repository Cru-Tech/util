package org.ccci.util;

import org.apache.log4j.Logger;

import com.google.common.base.Throwables;


public class Exceptions
{

    private static Logger log = Logger.getLogger(Exceptions.class);
    
    
    /**
     * If {@code t} is a RuntimeException, then return it. <br/>
     * If {@code t} is a checked exception, then return a new RuntimeException with {@code t} as its cause. <br/>
     * If {@code t} is an Error, throw it. <br/>
     * 
     * <p>
     * Intended to be used as such:  <pre>
     * try
     * {
     *    something();
     * }
     * catch (Exception e)
     * {
     *    throw Exceptions.wrap(e);
     * }</pre>
     * </p>
     * Useful for bailing when we aren't interested in handling checked exceptions that {@code something()}
     * might throw.
     * 
     * @param t
     * @return a RuntimeException that can be thrown
     * @deprecated use {@link Throwables#propagate(Throwable)} instead
     */
    @Deprecated
    public static RuntimeException wrap(Throwable t)
    {
        throw Throwables.propagate(t);
    }

    public static IllegalArgumentException newIllegalArgumentException(String message, Object... args)
    {
        return new IllegalArgumentException(String.format(message, args));
    }

    public static IllegalArgumentException newIllegalArgumentException(Exception e, String message, Object... args)
    {
        return new IllegalArgumentException(String.format(message, args), e);
    }

    /**
     * A shortcut for {@code new IllegalStateException(String.format(message, args))} 
     * 
     * Maybe this isn't worth having, but I like not having an extra "String.format(" everywhere
     * @param message
     * @param args
     * @return
     */
    public static IllegalStateException newIllegalStateException(String message, Object... args)
    {
        return new IllegalStateException(String.format(message, args));
    }

    /**
     * A shortcut for {@code new IllegalStateException(String.format(message, args), e)} 
     * 
     * see {@link #newIllegalStateException(String, Object...)}
     * @param message
     * @param args
     * @return
     */
    public static IllegalStateException newIllegalStateException(Exception e, String message, Object... args)
    {
        return new IllegalStateException(String.format(message, args), e);
    }

    /**
     * log an exception that must not be thrown.
     * 
     * Will not throw an Exception, so can be safely used in catch & finally blocks.
     * 
     * @param exception
     * @param message
     * @param args
     */
    public static void swallow(Exception exception, String message, Object... args)
    {
        final String logMessage;
        try
        {
            logMessage = String.format(message, args) + "; swallowing exception:";
        }
        catch (Exception e)
        {
            carefulLogError(exception, "swallowing exception:");
            return;
        }
        carefulLogError(exception, logMessage);
    }

    /**
     * Log an error message using the logging framework, and if an exception occurs, 
     * try to print the message via System.err
     * 
     * Must not throw an exception
     */
    private static void carefulLogError(Exception exception, final String logMessage)
    {
        try
        {
            log.error(logMessage, exception);
        }
        catch (Exception e)
        {
            try
            {
                System.err.println(logMessage);
                e.printStackTrace();
            }
            catch (Exception ignored)
            {
                // Highly unlikely to happen.
                // could only happen if a runtime exception is generated by System.err, which I believe
                // can only happen if either the JRE has a bug, or someone replaced the System.err PrintStream
                // (see System.setErr(OutputStream() ) with a runtime-exception-throwing PrintStream
            }
        }
    }

}
