package org.ccci.transaction;

import javax.transaction.Status;
import javax.transaction.Synchronization;

/**
 * A convenience class for methods that need special rollback logic.
 * 
 * @author Matt Drees
 *
 */
public abstract class RollbackSynchronization implements Synchronization
{

    public abstract void afterRollback();
    
    
    @Override
    public void afterCompletion(int status)
    {
        if (status == Status.STATUS_ROLLEDBACK)
        {
            afterRollback();
        }
    }

    @Override
    public void beforeCompletion()
    {
    }

}
