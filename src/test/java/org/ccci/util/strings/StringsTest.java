package org.ccci.util.strings;

import java.util.Arrays;
import java.util.List;

import org.ccci.util.strings.Strings;
import org.testng.Assert;
import org.testng.annotations.Test;

public class StringsTest
{

    @Test(expectedExceptions = NullPointerException.class)
    public void testRepeatWithNullInput()
    {
        Strings.repeat(null, 3);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRepeatWithBadRepeat()
    {
        Strings.repeat("foo", -1);
    }
    
    @Test
    public void testRepeatWithZeroRepeat()
    {
        Assert.assertEquals(Strings.repeat("foo", 0), "");
    }
    
    @Test
    public void testRepeatWithOneRepeat()
    {
        Assert.assertEquals(Strings.repeat("foo", 1), "foo");
    }
    
    @Test
    public void testRepeatWithTwoRepeats()
    {
        Assert.assertEquals(Strings.repeat("foo", 1), "foo");
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCheckPadArgumentsWithTooLargeRequiredLength()
    {
        Strings.checkPadArguments("foo", 2);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCheckPadArgumentsWithNegativeRequiredLength()
    {
        Strings.checkPadArguments("foo", -1);
    }
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testCheckPadArgumentsWithNullString()
    {
        Strings.checkPadArguments(null, 3);
    }
    
    
    @Test
    public void testRightPad()
    {
        Assert.assertEquals("foo ", Strings.rightPad("foo", 4, ' '));
    }
    
    
    @Test
    public void testLeftPad()
    {
        Assert.assertEquals(" foo", Strings.leftPad("foo", 4, ' '));
    }
    
    @Test
    public void testJoinWithOne()
    {
        List<String> fruit = Arrays.asList("apple"); 
        String result = Strings.join(fruit, ",", " and ");
        Assert.assertEquals("apple", result);
    }

    @Test
    public void testJoinWithTwo()
    {
        List<String> fruit = Arrays.asList("apple", "orange"); 
        String result = Strings.join(fruit, ",", " and ");
        Assert.assertEquals("apple and orange", result);
    }

    @Test
    public void testJoinWithThree()
    {
        List<String> fruit = Arrays.asList("apple", "orange", "pear"); 
        String result = Strings.join(fruit, ", ", " and ");
        Assert.assertEquals("apple, orange and pear", result);
    }
}
