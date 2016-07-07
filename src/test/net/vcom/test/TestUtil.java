
/*****************************************************************************
 *
 * VCom: video compositor test
 *
 * source file: TestUtil.java  
 * package: net.vcom
 *
 * version 0.3
 * 2005-06-01
 * Copyright (c) 2005, Denis McLaughlin
 * Released under the GPL license, version 2
 *  
 */ 

package net.vcom.test;

import java.io.File;
import junit.framework.TestCase;
import org.jdom.Document;
import org.jdom.Element;
import net.vcom.Util;

/**
 * This class runs tests against the ImageClip class.
 */
public class TestUtil extends TestCase
{
    /**
     * Checks that the padding routines work ok
     */
    public void testPadding()
    {
        // test the string padding of specified length
        assertEquals("padleft() 1 failed", "001", Util.padLeft(3,"1"));

        // test the string padding of specified length, string overflow
        assertEquals("padleft() 1 failed", "1234", Util.padLeft(3,"1234"));

        // test the string padding of default length
        assertEquals("padleft() 1 failed", "000001", Util.padLeft("1"));

        // test the string padding of default length, string overflow
        assertEquals("padleft() 1 failed", "1234567", Util.padLeft("1234567"));

        // test the int padding of specified length
        assertEquals("padleft() 1 failed", "001", Util.padLeft(3,1));

        // test the int padding of specified length, string overflow
        assertEquals("padleft() 1 failed", "1234", Util.padLeft(3,1234));

        // test the int padding of default length
        assertEquals("padleft() 1 failed", "000001", Util.padLeft(1));

        // test the int padding of default length, string overflow
        assertEquals("padleft() 1 failed", "1234567", Util.padLeft(1234567));
    }


    /**
     * Checks that the run cmd routine works ok
     */
    public void testRun()
    {
        // get a File for a file
        File file = new File("build/testfile");

        // check the file doesn't exist
        assertFalse("test file exists", file.exists());

        // run the command to touch it
        Util.run("touch build/testfile");

        // check the file exists
        assertTrue("test file doesn't exist", file.exists());
    }


    /**
     * Checks that the cp cmd routine works ok
     */
    public void testCp()
    {
        // get a File for a file
        File file = new File("build/testfile2");

        // check the file doesn't exist
        assertFalse("copied test file exists", file.exists());

        // run the command to touch it
        Util.cp("build/testfile", "build/testfile2");

        // check the file exists
        assertTrue("copied test file doesn't exist", file.exists());
    }


    /**
     * Checks that the print routine works ok
     */
    public void testPrint()
    {
        // get a File for the file
        File file = new File("build/testprint");

        // check the file doesn't exist
        assertFalse("print test file exists", file.exists());

        // create a small xml object
        Element e = new Element("foo");

        // run the command to touch it
        Util.print(new Document(e), "build/testprint");

        // check the file exists
        assertTrue("print test file doesn't exist", file.exists());
    }
}
