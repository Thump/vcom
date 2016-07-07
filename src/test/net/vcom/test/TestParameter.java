
/*****************************************************************************
 *
 * VCom: video compositor test
 *
 * source file: TestParameter.java  
 * package: net.vcom
 *
 * Copyright (c) 2007, Denis McLaughlin
 * Released under the GPL license, version 2
 *  
 */ 

package net.vcom.test;

import java.io.CharArrayReader;

import junit.framework.TestCase;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import net.vcom.Parameter;
import net.vcom.Seq;
import net.vcom.SeqLoop;


/**
 * This class runs tests against the Parameter class.
 */
public class TestParameter extends TestCase
{
    /**
     * Checks that the Parameter constructor returns a non-null object
     */
    public void testConstructor()
    {
        Parameter p = null;

        // create a Parameter and check it's not null
        p = new Parameter();
        assertNotNull("null Parameter returned from default constructor", p);
    }


    /**
     * Tests that the attributes of Parameter can be set and got
     */
    public void testSetAndGet()
    {
        Parameter p = null;
        String n = new String("name1");
        Seq s = new SeqLoop();

        // check we can construct a new Parameter
        p = new Parameter();
        assertNotNull("null Parameter returned from default constructor", p);

        // set and check the name
        p.setName(n);
        assertEquals("name not set", n, p.getName());

        // set and check the seq
        p.setSeq(s);
        assertEquals("seq not set", s, p.getSeq());
    }


    /**
     * Check that various valid() conditions are caught
     */
    public void testValid()
    {
        Parameter p = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // attempt to validate with a null Parameter
        try
        {
            // check we can construct a new ImageClip
            p = new Parameter();
            assertNotNull("null Parameter returned from constructor", p);

            // call valid()
            p.valid();

            // we shouldn't get to here
            assertTrue("null name and seq succeeded", false);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }


        // attempt to validate with an invalid Seq: should fail
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<parameter name='foo'> <seq start='1' stop='0'/> </parameter>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a Parameter from the document
            p = new Parameter(doc);

            // check we got a Parameter back
            assertNotNull("good parameter constructor failed", p);

            // call valid(): this should fail
            p.valid();

            // we shouldn't get to here
            assertTrue("call to valid with invalid seq succeeded", false);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // attempt to validate with an invalid Seq: should succeed
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<parameter name='foo'> <seq start='1' stop='2'/> </parameter>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a Parameter from the document
            p = new Parameter(doc);

            // check we got a Parameter back
            assertNotNull("good parameter constructor failed", p);

            // call valid(): this should succeed
            p.valid();
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * Check the Document constructor failure paths for Parameter are working
     */
    public void testBadParameterDocConstructor()
    {
        Parameter p = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // check that if the tag name isn't <parameter> that it fails
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<notparameter/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            p = new Parameter(doc);

            // if we get here, that's bad
            assertNotNull(
                "parameter document construction with bad tag succeeded",
                null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // check that if the tag has more than one attribute, that it fails
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<parameter att1='foo' att2='foo'> <seq/> </parameter>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            p = new Parameter(doc);

            // if we get here, that's bad
            assertNotNull(
               "parameter document construction with multiple atts succeeded",
                null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // check that if the tag has no attributes, that it fails
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<parameter> <seq/> </parameter>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            p = new Parameter(doc);

            // if we get here, that's bad
            assertNotNull(
               "parameter document construction with no atts succeeded",
                null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // check that if the tag has one attribute not called 'name', that
        // it fails
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<parameter att1='foo'> <seq/> </parameter>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            p = new Parameter(doc);

            // if we get here, that's bad
            assertNotNull(
               "parameter document construction with no name att succeeded",
                null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // check that if the tag has more than one child, that it fails
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<parameter name='foo'> <seq/> <seq/> </parameter>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            p = new Parameter(doc);

            // if we get here, that's bad
            assertNotNull(
                "parameter construction with multiple children succeeded",
                null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // check that if the tag has no children, that it fails
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<parameter name='foo'/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            p = new Parameter(doc);

            // if we get here, that's bad
            assertNotNull(
               "parameter document construction with no children succeeded",
                null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // check that if the tag has one child not called 'seq', that it fails
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<parameter name='foo'> <notseq/> </parameter>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            p = new Parameter(doc);

            // if we get here, that's bad
            assertNotNull(
               "parameter document construction with non-seq child succeeded",
                null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }



    /**
     * Check the Document constructor success path for sourceSeq works
     */
    public void testGoodParameterConstructor()
    {
        Parameter p = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // should work
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<parameter name='foo'> <seq start='1' stop='2'/> </parameter>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            p = new Parameter(doc);

            // if we get here, that's bad
            assertNotNull("good parameter constructor failed", p);

            // check the name
            String n = p.getName();
            assertEquals("wrong name in parameter", "foo", n);

            // check the seq
            Seq s = p.getSeq();
            assertEquals("wrong start in parameter seq", 1, s.getStart());
            assertEquals("wrong stop in parameter seq", 2, s.getStop());
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }
}
