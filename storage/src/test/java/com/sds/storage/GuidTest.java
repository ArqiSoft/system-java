package com.sds.storage;

import org.junit.Assert;
import org.junit.Test;

import junit.framework.TestCase;

public class GuidTest  extends TestCase {

    @Test
    public void testFromByteArray() {
        String guidString = "11223344-5566-7788-9900-aabbccddeeff";
        byte[] binary = new byte[] { 68, 51, 34, 17, 102, 85, -120, 119, 
                -103, 0, -86, -69, -52, -35, -18, -1 };

        assertEquals(guidString, Guid.fromByteArray(binary).toString());
    }

    @Test
    public void testToByteArray() {
        String guidString = "11223344-5566-7788-9900-aabbccddeeff";
        byte[] expected = new byte[] { 68, 51, 34, 17, 102, 85, -120, 119, 
                -103, 0, -86, -69, -52, -35, -18, -1 };

        Assert.assertArrayEquals(expected, Guid.fromString(guidString).toByteArray());
    }
    

    @Test
    public void testConvert() {
        String guidString = "11223344-5566-7788-9900-aabbccddeeff";
        byte[] guidBytes = new byte[] { 68, 51, 34, 17, 102, 85, -120, 119, 
                -103, 0, -86, -69, -52, -35, -18, -1 };

        Assert.assertArrayEquals(guidBytes, 
                Guid.fromString(Guid.fromByteArray(guidBytes).toString()).toByteArray());
        
        assertEquals(guidString, 
                Guid.fromByteArray(Guid.fromString(guidString).toByteArray()).toString());
    }

}
