package org.ccci.util.contract;

public class Postconditions
{

    public static void postcondition(boolean condition, String string, Object... args)
    {
        if (!condition) { throw new AssertionError(String.format(string, args)); }
    }
}
