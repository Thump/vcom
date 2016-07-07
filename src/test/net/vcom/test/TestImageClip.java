
/**************************************************************************
 *
 * VCom: video compositor test
 *
 * source file: TestImageClip.java  
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
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import net.vcom.ImageClip;
import net.vcom.Seq;
import net.vcom.SeqLoop;
import net.vcom.Parameter;


/**
 * This class runs tests against the ImageClip class.
 */
public class TestImageClip extends TestCase
{
    /**
     * Checks that the ImageClip constructor returns a non-null object
     */
    public void testConstructor()
    {
        ImageClip ic = null;

        // create an ImageClip
        ic = new ImageClip();
        assertNotNull("null ImageClip returned from constructor", ic);
    }


    /**
     * Tests that the attributes of ImageClip can be set and got
     */
    public void testSetAndGet()
    {
        ImageClip ic = null;
        DirectoryScanner scanner = new DirectoryScanner();
        Seq sourceSeq = new SeqLoop();
        int targetStart = 1;
        Parameter p1 = new Parameter();
        Parameter p2 = new Parameter("bar");

        // check we can construct a new ImageClip
        ic = new ImageClip();
        assertNotNull("null ImageClip returned from constructor", ic);

        // set and check the sourceFiles
        ic.setSourceFiles(scanner);
        assertEquals("sourceFiles not set", scanner, ic.getSourceFiles());

        // set and check the sourceSeq
        ic.setSourceSeq(sourceSeq);
        assertEquals("sourceSeq not set", sourceSeq, ic.getSourceSeq());

        // set and check the targetStart
        ic.setTargetStart(targetStart);
        assertEquals("targetStart not set",targetStart,ic.getTargetStart());

        // set and check anonymous parameter
        ic.setParameter("foo",p1);
        assertEquals("parameter not set", p1, ic.getParameter("foo"));

        // set and check non-anonymous parameter
        ic.setParameter("bar",p2);
        assertEquals("parameter not set", p2, ic.getParameter("bar"));
    }


    /**
     * Check that we can't set bad values
     */
    public void testBadSet()
    {
        // attempt to set a negative targetStart: should fail
        try
        {
            // check we can construct a new ImageClip
            ImageClip ic = new ImageClip();
            assertNotNull("null ImageClip returned from constructor", ic);

            // set the negative target start
            ic.setTargetStart(-1);

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
        // attempt to validate with a null sourceFiles attribute
        try
        {
            // check we can construct a new ImageClip
            ImageClip ic = new ImageClip();
            assertNotNull("null ImageClip returned from constructor", ic);

            // call valid()
            ic.valid();

            // we shouldn't get to here
            assertTrue("null sourceFiles succeeded", false);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * Check the Document constructor failure paths for imageClip are working
     */
    public void testBadImageClipConstructor()
    {
        ImageClip ic = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // check that if the tag name isn't <imageclip> that it fails
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<notimageclip/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("non imageclip construction succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // check that if the tag has attributes, that it fails
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip att1='foo'/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("imageclip with atts construction succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * Check the Document constructor failure paths for sourceFiles are
     * working
     */
    public void testBadSourceFiles()
    {
        ImageClip ic = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // should fail if <sourcefiles> has no attributes
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <sourcefiles> </sourcefiles> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("sourcefiles with no attributes succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <sourcefiles> doesn't have an attribute named dir
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <sourcefiles foo='bar'> </sourcefiles> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("sourcefiles with no dir attribute succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <sourcefiles> doesn't have any children
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <sourcefiles dir='bar'> </sourcefiles> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("sourcefiles with no children succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <sourcefiles> has children other than include/exclude
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <sourcefiles dir='bar'> <foo/> </sourcefiles> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull(
                "sourcefiles with no include exclude children succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <sourcefiles> include child has no attribute
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <sourcefiles dir='bar'> <include/> </sourcefiles> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull(
                "sourcefiles with no attribute include child succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <sourcefiles> include child non-name attribute
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <sourcefiles dir='bar'> <include foo='bar'/> </sourcefiles> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull(
                "sourcefiles with non-name attribute child succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <sourcefiles> exclude child has no attribute
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <sourcefiles dir='bar'> <exclude/> </sourcefiles> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull(
                "sourcefiles with no attribute exclude child succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <sourcefiles> exclude child non-name attribute
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <sourcefiles dir='bar'> <exclude foo='bar'/> </sourcefiles> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull(
                "sourcefiles with non-name attribute child succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * Check the Document constructor success paths for sourceFiles
     * are working
     */
    public void testGoodSourceFiles()
    {
        ImageClip ic = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // should work
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <sourcefiles dir='resources/test'> <include name='**'/> <exclude name='baz'/> <exclude name='quux'/> </sourcefiles> <sourceseq> <seq start='0'/> </sourceseq> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("simple sourcefiles create failed", ic);

            // get the sourcefiles
            DirectoryScanner scanner = ic.getSourceFiles();
            assertNotNull("got null scanner", scanner);

            // check the base dir is set right
            File f = scanner.getBasedir();
            assertNotNull("got null basedir", f);
            assertEquals("got wrong dir", "test", f.getName());

            // scan the files
            ic.valid();

            // check the included files are right
            String[] included = scanner.getIncludedFiles();
            assertEquals("wrong number of includes", 2, included.length);
            assertTrue("wrong file included: " + included[0],
                (included[0].equals("foo") || included[0].equals("bar")) ); 
            assertTrue("wrong file included: " + included[1],
                (included[1].equals("foo") || included[1].equals("bar")) ); 
        }
        catch(Exception e)
        {
            e.printStackTrace();
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * Check the Document constructor failure paths for sourceSeq are working
     */
    public void testBadSourceSeqConstructor()
    {
        ImageClip ic = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // should fail if <sourceseq> has an attribute
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <sourceseq foo='bar'> </sourceseq> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("sourceseq with attribute succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <sourceseq> has no children
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <sourceseq> </sourceseq> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("sourceseq with no children succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <sourceseq> has more than one child
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <sourceseq> <foo/> <bar/> </sourceseq> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("sourceseq with multiple children succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <sourceseq> has non seq child
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <sourceseq> <foo/> </sourceseq> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("sourceseq with non-seq child succeeded", null);
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
    public void testGoodSourceSeqConstructor()
    {
        ImageClip ic = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // should work if <sourceseq> has an attribute
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <sourceseq> <seq start='1' stop='2'/> </sourceseq> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("simple sourceseq create failed", ic);

            // get the sourceSeq
            Seq ss = ic.getSourceSeq();
            assertEquals("wrong start in sourceSeq", 1, ss.getStart());
            assertEquals("wrong stop in sourceSeq", 2, ss.getStop());
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * Check the Document constructor failure paths for targetSeq are working
     */
    public void testBadTargetSeqConstructor()
    {
        ImageClip ic = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // should fail if <targetseq> has children
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <targetseq> <foo/> </targetseq> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("targetseq with child succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <targetseq> has no attributes
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <targetseq> </targetseq> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("targetseq with no attributes succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <targetseq> has an attribute not called start
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <targetseq foo='bar'> </targetseq> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("targetseq with wrong attribute name succeeded",null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * Check the Document constructor success paths for targetSeq are working
     */
    public void testGoodTargetSeqConstructor()
    {
        ImageClip ic = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // should fail if <targetseq> has an attribute not called start
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <targetseq start='1'> </targetseq> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("targetseq with correct attribute name failed",ic);

            // check that targetstart value is correct
            assertEquals("wrong targetStart value", 1, ic.getTargetStart() );
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * Check the Document constructor failure paths for sourceSeq are working
     */
    public void testBadParameterConstructor()
    {
        ImageClip ic = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // should fail if <parameter> has more than one attribute
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <parameter name='foo' att2='bar'>  </parameter> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("parameter with multiple attributes succeeded",
                null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <parameter> has no children
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <parameter name='foo'> </parameter> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("parameter with no children succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <parameter> has more than one child
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <parameter name='foo'> <seq start='1' stop='2'/> <seq start='1' stop='2'/> </parameter> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("parameter with multiple children succeeded",
                null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <parameter> has non seq child
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <parameter name='foo'> <foo start='1' stop='2'/> </parameter> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("parameter with non-seq child succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * Check the Document constructor success path for the parameters works
     */
    public void testGoodParameterConstructor()
    {
        ImageClip ic = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // should work if <parameter> has a name and subseq
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <parameter name='foo'> <seq start='1' stop='2'/> </parameter> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // check we got a valid ic
            assertNotNull("simple parameter create failed", ic);

            // get the parameter we defined
            Parameter p = ic.getParameter("foo");
            assertNotNull("got null parameter", p);

            // check the parameters sequence is defined
            Seq s = p.getSeq();
            assertNotNull("got null seq", s);

            // check the sequence's start and stop are correct
            assertEquals("wrong start in parameter", 1, s.getStart());
            assertEquals("wrong stop in parameter", 2, s.getStop());
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // check that we can override sequences
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <parameter name='foo'> <seq start='1' stop='2'/> </parameter> <parameter name='foo'> <seq start='9' stop='99'/> </parameter> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // check we got a valid ic
            assertNotNull("simple parameter create failed", ic);

            // get the parameter we defined
            Parameter p = ic.getParameter("foo");
            assertNotNull("got null parameter", p);

            // check the parameters sequence is defined
            Seq s = p.getSeq();
            assertNotNull("got null seq", s);

            // check the sequence's start and stop are correct
            assertEquals("wrong start in parameter override", 9,
                s.getStart());
            assertEquals("wrong stop in parameter override", 99,
                s.getStop());
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }

    /**
     * Check the Document constructor flags a parameter with an invalid
     * seq correctly
     */
    public void testRecursiveValid()
    {
        ImageClip ic = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // create an IC with a parameter with an invalid seq: this should
        // only be caught when valid() is called, so this try should
        // succeed
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <sourcefiles dir='resources/images'> <include name='**'/> </sourcefiles> <sourceseq> <seq start='5' stop='10'/> </sourceseq> <parameter name='foo'> <seq start='2' stop='1'/> </parameter> </imageclip>");


            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // now call valid: this should fail
        try
        {
            // call valid()
            ic.valid();

            // if we get here, that's bad
            assertNotNull("parameter with bad seq succeeded",
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
     * Check the inFrame() calls work
     */
    public void testInFrame()
    {
        ImageClip ic = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // should work
        try
        {
            // create the xml code in a string: this clip is 6 images long
            // (from frame 5 to frame 10) and the target starts at 10,
            // so we should be inframe from frame 10 to 15
            String xml = new String(
            "<imageclip> <sourcefiles dir='resources/test'> <include name='**'/> <exclude name='baz'/> <exclude name='quux'/> </sourcefiles> <sourceseq> <seq start='5' stop='10'/> </sourceseq> <targetseq start='10'/> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("simple sourcefiles create failed", ic);

            // these should be outframe
            for (int i=-1 ; i<10; i++)
            {
                assertFalse("incorrectly inframe " + i, ic.inFrame(i));
            }

            // these should be inframe
            for (int i=10 ; i<16; i++)
            {
                assertTrue("incorrectly outframe " + i, ic.inFrame(i));
            }

            // these should be outframe again
            for (int i=16 ; i<20; i++)
            {
                assertFalse("incorrectly inframe " + i, ic.inFrame(i));
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * Check the getImage() calls work, using no defaults
     */
    public void testGetImage()
    {
        ImageClip ic = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;
        Element image = null;

        // pre-parameter getimage test: should work
        try
        {
            // create the xml code in a string: this clip is 6 images long
            // (from frame 5 to frame 10) and the target starts at 10,
            // so we should be inframe from frame 10 to 15
            String xml = new String(
 "<imageclip>"

+"  <sourcefiles dir='resources/images'><include name='*'/></sourcefiles>"
+"  <sourceseq> <seq start='5' stop='10'/> </sourceseq>"
+"  <targetseq start='5'/>"

+"  <parameter name='xposition'> <seq start='100' stop='200'/> </parameter>"
+"  <parameter name='yposition'>"
+"    <seq start='50' stop='10' step='-1'/>"
+"  </parameter>"
+"  <parameter name='rotation'> <seq start='15' stop='25'/> </parameter>"
+"  <parameter name='opacity'> <seq start='10' stop='100'/> </parameter>"
+"  <parameter name='xsize'> <seq start='3' stop='6'/> </parameter>"
+"  <parameter name='ysize'> <seq start='120' stop='180'/> </parameter>"

+"</imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("simple sourcefiles create failed", ic);

            // get the first image element
            image = ic.getImage(7);
            assertNotNull("got null image", image);

            // make sure there's no children below the node
            assertEquals("children in image", 0, image.getContentSize());

            // make sure the tag is <image>
            assertEquals("wrong tag name", "image", image.getName());

            // make sure the attributes are correct
            assertEquals("wrong file name", "resources/images/dot-000007.png",
                image.getAttributeValue("file") );
            assertEquals("wrong xposition", "102",
                image.getAttributeValue("xposition") );
            assertEquals("wrong yposition", "48",
                image.getAttributeValue("yposition") );
            assertEquals("wrong rotation", "17",
                image.getAttributeValue("rotation") );
            assertEquals("wrong opacity", "12",
                image.getAttributeValue("opacity") );
            assertEquals("has an xSize", "5",
                image.getAttributeValue("xsize") );
            assertEquals("has an ySize", "122",
                image.getAttributeValue("ysize") );
        }
        catch(Exception e)
        {
            e.printStackTrace();
            assertTrue("got exception: " + e, false);
        }
    }
}
