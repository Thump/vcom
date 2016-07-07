
/**************************************************************************
 *
 * VCom: video compositor test
 *
 * source file: TestSoundClip.java  
 * package: net.vcom
 *
 * Copyright (c) 2005, Denis McLaughlin
 * Released under the GPL license, version 2
 *  
 */ 

package net.vcom.test;

import java.util.ArrayList;
import java.io.File;
import java.io.CharArrayReader;

import junit.framework.TestCase;
import org.apache.tools.ant.DirectoryScanner;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import net.vcom.SoundClip;


/**
 * This class runs tests against the ImageClip class.
 */
public class TestSoundClip extends TestCase
{
    /**
     * Checks that the ImageClip constructor returns a non-null object
     */
    public void testConstructor()
    {
        SoundClip sc = null;

        // create an ImageClip
        sc = new SoundClip();
        assertNotNull("null SoundClip returned from constructor", sc);
    }


    /**
     * Tests that the attributes of SoundClip can be set and got
     */
    public void testSetAndGet()
    {
        SoundClip sc = null;
        String source = "foo";
        double sourceStart = 1.0;
        double sourceStop = 1.0;
        double targetStart = 1.0;

        // check we can construct a new ImageClip
        sc = new SoundClip();
        assertNotNull("null SoundClip returned from constructor", sc);

        // set and check the sourceStart
        sc.setSource(source);
        assertEquals("source not set", source, sc.getSource());

        // set and check the sourceStart
        sc.setSourceStart(sourceStart);
        assertEquals("sourceStart not set", sourceStart, sc.getSourceStart());

        // set and check the sourceStop
        sc.setSourceStop(sourceStop);
        assertEquals("sourceStop not set", sourceStop, sc.getSourceStop());

        // set and check the targetStart
        sc.setTargetStart(targetStart);
        assertEquals("targetStart not set", targetStart, sc.getTargetStart());
    }


    /**
     * Check that we can't set bad values
     */
    public void testBadSet()
    {
        // attempt to set a null source: should fail
        try
        {
            // check we can construct a new SoundClip
            SoundClip sc = new SoundClip();
            assertNotNull("null SoundClip returned from constructor", sc);

            // set the null source
            sc.setSource((String) null);

            // we shouldn't get to here
            assertTrue("set a null source", false);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // attempt to set a negative sourceStart: should fail
        try
        {
            // check we can construct a new SoundClip
            SoundClip sc = new SoundClip();
            assertNotNull("null SoundClip returned from constructor", sc);

            // set the negative source start
            sc.setSourceStart(-1);

            // we shouldn't get to here
            assertTrue("set a negative sourceStart", false);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // attempt to set a negative sourceStop: should fail
        try
        {
            // check we can construct a new SoundClip
            SoundClip sc = new SoundClip();
            assertNotNull("null SoundClip returned from constructor", sc);

            // set the negative source stop
            sc.setSourceStop(-1);

            // we shouldn't get to here
            assertTrue("set a negative sourceStop", false);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // attempt to set a negative targetStart: should fail
        try
        {
            // check we can construct a new SoundClip
            SoundClip sc = new SoundClip();
            assertNotNull("null SoundClip returned from constructor", sc);

            // set the negative target start
            sc.setTargetStart(-1);

            // we shouldn't get to here
            assertTrue("set a negative targetStart", false);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * Check that various valid() conditions are caught
     */
    public void testValid()
    {
        // attempt to validate with a null source
        try
        {
            // check we can construct a new ImageClip
            SoundClip sc = new SoundClip();
            assertNotNull("null SoundClip returned from constructor", sc);

            // call valid()
            sc.valid();

            // we shouldn't get to here
            assertTrue("null source succeeded", false);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // attempt to validate with a sourceStart after sourceStop
        try
        {
            // check we can construct a new ImageClip
            SoundClip sc = new SoundClip();
            assertNotNull("null SoundClip returned from constructor", sc);

            // set the sourceStart after sourceStop
            sc.setSourceStart(2.0);
            sc.setSourceStop(1.0);

            // call valid()
            sc.valid();

            // we shouldn't get to here
            assertTrue("sourceStart after sourceStop succeeded", false);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * This tests the failure paths of the Document constructor
     */
    public void testBadSoundClipConstructor()
    {
        SoundClip sc = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // check that if the tag name isn't <soundclip> that it fails
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<notsoundclip/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            sc = new SoundClip(doc);

            // if we get here, that's bad
            assertNotNull("non soundclip construction succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // check that if we give soundclip attributes, that it fails
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<soundclip foo='bar'/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            sc = new SoundClip(doc);

            // if we get here, that's bad
            assertNotNull("soundclip with attributes succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // check that if we give soundclip bogus children, that it fails
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<soundclip> <foo/> </soundclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            sc = new SoundClip(doc);

            // if we get here, that's bad
            assertNotNull("soundclip with attributes succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // check that if we give soundclip a source child with no atts, it fails
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<soundclip> <source/> </soundclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            sc = new SoundClip(doc);

            // if we get here, that's bad
            assertNotNull("source with no attributes succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // check that if we give soundclip a source child with too many
        // atts, it fails
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<soundclip> <source foo='1' bar='2' baz='3' quux='4'/> </soundclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            sc = new SoundClip(doc);

            // if we get here, that's bad
            assertNotNull("source with too many attributes succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // check that if we give soundclip a bogus att, it fails
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<soundclip> <source foo='1'/> </soundclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            sc = new SoundClip(doc);

            // if we get here, that's bad
            assertNotNull("source with bogus attribute succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // check that if we give target the wrong number of atts, it fails
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<soundclip> <target/> </soundclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            sc = new SoundClip(doc);

            // if we get here, that's bad
            assertNotNull("target with no attributes succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * This tests the success paths of the Document constructor
     */
    public void testGoodSoundClipConstructor()
    {
        SoundClip sc = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // check that we can set the source name
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<soundclip> <source file='foo' start='1.0' stop='2.0'/> <target start='3.0'/> </soundclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            sc = new SoundClip(doc);

            // we should have gotten a SoundClip
            assertNotNull("got null soundclip on Document construction", sc);

            // check the attributes are right
            assertEquals("bad source", "foo", sc.getSource() );
            assertEquals("bad source start", 1.0, sc.getSourceStart() );
            assertEquals("bad source stop", 2.0, sc.getSourceStop() );
            assertEquals("bad target start", 3.0, sc.getTargetStart() );
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }
}
