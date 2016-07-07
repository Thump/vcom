
/*****************************************************************************
 *
 * VCom: video compositor test
 *
 * source file: TestImageClip.java  
 * package: net.vcom
 *
 * version 0.3
 * 2005-06-01
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
        Seq sourceSeq = new Seq();
        int targetStart = 1;
        Seq xPosition = new Seq();
        Seq yPosition = new Seq();
        Seq rotation = new Seq();
        Seq opacity = new Seq();
        Seq xSize = new Seq();
        Seq ySize = new Seq();

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
        assertEquals("targetStart not set", targetStart, ic.getTargetStart());

        // set and check the xPosition
        ic.setXPosition(xPosition);
        assertEquals("xPosition not set", xPosition, ic.getXPosition());

        // set and check the yPosition
        ic.setYPosition(yPosition);
        assertEquals("yPosition not set", yPosition, ic.getYPosition());

        // set and check the rotation
        ic.setRotation(rotation);
        assertEquals("rotation not set", rotation, ic.getRotation());

        // set and check the opacity
        ic.setOpacity(opacity);
        assertEquals("opacity not set", opacity, ic.getOpacity());

        // set and check the xSize
        ic.setXSize(xSize);
        assertEquals("xSize not set", xSize, ic.getXSize());

        // set and check the ySize
        ic.setYSize(ySize);
        assertEquals("ySize not set", ySize, ic.getYSize());
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
     * Check the Document constructor failure paths for sourceFiles are working
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
     * Check the Document constructor success paths for sourceFiles are working
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
            "<imageclip> <sourcefiles dir='resources/test'> <include name='**'/> <exclude name='baz'/> <exclude name='quux'/> </sourcefiles> </imageclip>");

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
     * Check the Document constructor failure paths for xposition are working
     */
    public void testBadXPositionConstructor()
    {
        ImageClip ic = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // should fail if <xposition> has an attribute
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <xposition foo='bar'> </xposition> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("xposition with attribute succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <xposition> has no children
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <xposition> </xposition> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("xposition with no children succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <xposition> has more than one child
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <xposition> <foo/> <bar/> </xposition> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("xposition with multiple children succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <xposition> has non seq child
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <xposition> <foo/> </xposition> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("xposition with non-seq child succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * Check the Document constructor success path for xposition works
     */
    public void testGoodXPositionConstructor()
    {
        ImageClip ic = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // should work if <xposition> has an attribute
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <xposition> <seq start='1' stop='2'/> </xposition> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("simple xposition create failed", ic);

            // get the sourceSeq
            Seq ss = ic.getXPosition();
            assertEquals("wrong start in xposition", 1, ss.getStart());
            assertEquals("wrong stop in xposition", 2, ss.getStop());
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * Check the Document constructor failure paths for yposition are working
     */
    public void testBadYPositionConstructor()
    {
        ImageClip ic = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // should fail if <yposition> has an attribute
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <yposition foo='bar'> </yposition> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("yposition with attribute succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <yposition> has no children
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <yposition> </yposition> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("yposition with no children succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <yposition> has more than one child
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <yposition> <foo/> <bar/> </yposition> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("yposition with multiple children succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <yposition> has non seq child
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <yposition> <foo/> </yposition> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("yposition with non-seq child succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * Check the Document constructor success path for yposition works
     */
    public void testGoodYPositionConstructor()
    {
        ImageClip ic = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // should work if <yposition> has an attribute
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <yposition> <seq start='1' stop='2'/> </yposition> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("simple yposition create failed", ic);

            // get the sourceSeq
            Seq ss = ic.getYPosition();
            assertEquals("wrong start in yposition", 1, ss.getStart());
            assertEquals("wrong stop in yposition", 2, ss.getStop());
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * Check the Document constructor failure paths for xsize are working
     */
    public void testBadXSizeConstructor()
    {
        ImageClip ic = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // should fail if <xsize> has an attribute
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <xsize foo='bar'> </xsize> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("xsize with attribute succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <xsize> has no children
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <xsize> </xsize> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("xsize with no children succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <xsize> has more than one child
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <xsize> <foo/> <bar/> </xsize> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("xsize with multiple children succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <xsize> has non seq child
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <xsize> <foo/> </xsize> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("xsize with non-seq child succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * Check the Document constructor success path for xsize works
     */
    public void testGoodXSizeConstructor()
    {
        ImageClip ic = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // should work if <xsize> has an attribute
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <xsize> <seq start='1' stop='2'/> </xsize> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("simple xsize create failed", ic);

            // get the sourceSeq
            Seq ss = ic.getXSize();
            assertEquals("wrong start in xsize", 1, ss.getStart());
            assertEquals("wrong stop in xsize", 2, ss.getStop());
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * Check the Document constructor failure paths for ysize are working
     */
    public void testBadYSizeConstructor()
    {
        ImageClip ic = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // should fail if <ysize> has an attribute
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <ysize foo='bar'> </ysize> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("ysize with attribute succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <ysize> has no children
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <ysize> </ysize> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("ysize with no children succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <ysize> has more than one child
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <ysize> <foo/> <bar/> </ysize> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("ysize with multiple children succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <ysize> has non seq child
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <ysize> <foo/> </ysize> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("ysize with non-seq child succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * Check the Document constructor success path for ysize works
     */
    public void testGoodYSizeConstructor()
    {
        ImageClip ic = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // should work if <ysize> has an attribute
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <ysize> <seq start='1' stop='2'/> </ysize> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("simple ysize create failed", ic);

            // get the sourceSeq
            Seq ss = ic.getYSize();
            assertEquals("wrong start in ysize", 1, ss.getStart());
            assertEquals("wrong stop in ysize", 2, ss.getStop());
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * Check the Document constructor failure paths for rotation are working
     */
    public void testBadRotationConstructor()
    {
        ImageClip ic = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // should fail if <rotation> has an attribute
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <rotation foo='bar'> </rotation> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("rotation with attribute succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <rotation> has no children
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <rotation> </rotation> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("rotation with no children succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <rotation> has more than one child
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <rotation> <foo/> <bar/> </rotation> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("rotation with multiple children succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <rotation> has non seq child
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <rotation> <foo/> </rotation> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("rotation with non-seq child succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * Check the Document constructor success path for rotation works
     */
    public void testGoodRotationConstructor()
    {
        ImageClip ic = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // should work if <rotation> has an attribute
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <rotation> <seq start='1' stop='2'/> </rotation> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("simple rotation create failed", ic);

            // get the sourceSeq
            Seq ss = ic.getRotation();
            assertEquals("wrong start in rotation", 1, ss.getStart());
            assertEquals("wrong stop in rotation", 2, ss.getStop());
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * Check the Document constructor failure paths for opacity are working
     */
    public void testBadOpacityConstructor()
    {
        ImageClip ic = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // should fail if <opacity> has an attribute
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <opacity foo='bar'> </opacity> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("opacity with attribute succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <opacity> has no children
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <opacity> </opacity> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("opacity with no children succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <opacity> has more than one child
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <opacity> <foo/> <bar/> </opacity> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("opacity with multiple children succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // should fail if <opacity> has non seq child
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <opacity> <foo/> </opacity> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("opacity with non-seq child succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * Check the Document constructor success path for opacity works
     */
    public void testGoodOpacityConstructor()
    {
        ImageClip ic = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // should work if <opacity> has an attribute
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<imageclip> <opacity> <seq start='1' stop='2'/> </opacity> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("simple opacity create failed", ic);

            // get the sourceSeq
            Seq ss = ic.getOpacity();
            assertEquals("wrong start in opacity", 1, ss.getStart());
            assertEquals("wrong stop in opacity", 2, ss.getStop());
        }
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
     * Check the getImage() calls work, using all defaults
     */
    public void testGetImageDefault()
    {
        ImageClip ic = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;
        Element image = null;

        // should work
        try
        {
            // create the xml code in a string: this clip is 6 images long
            // (from frame 5 to frame 10) and the target starts at 10,
            // so we should be inframe from frame 10 to 15
            String xml = new String(
            "<imageclip> <sourcefiles dir='resources/images'> <include name='**'/> </sourcefiles> <sourceseq> <seq start='0' stop='30'/> </sourceseq> <targetseq start='0'/> </imageclip>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            ic = new ImageClip(doc);

            // if we get here, that's bad
            assertNotNull("simple sourcefiles create failed", ic);

            // get the first image element
            image = ic.getImage(0);
            assertNotNull("got null image", image);

            // make sure there's no children below the node
            assertEquals("children in image", 0, image.getContentSize());

            // make sure the tag is <image>
            assertEquals("wrong tag name", "image", image.getName());

            // make sure the attributes are correct
            assertEquals("wrong file name", "resources/images/dot-000000.png",
                image.getAttributeValue("file") );
            assertEquals("wrong xposition", "0",
                image.getAttributeValue("xposition") );
            assertEquals("wrong yposition", "0",
                image.getAttributeValue("yposition") );
            assertEquals("wrong rotation",
                String.valueOf(ImageClip.ROTATION_DEFAULT),
                image.getAttributeValue("rotation") );
            assertEquals("wrong opacity",   
                String.valueOf(ImageClip.OPACITY_DEFAULT),
                image.getAttributeValue("opacity") );

            // there are no default xSize or ySize, so they shouldn't
            // be set
            assertEquals("has an xSize", null,
                image.getAttributeValue("xsize") );
            assertEquals("has an ySize", null,
                image.getAttributeValue("ysize") );
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
    public void testGetImageNoDefault()
    {
        ImageClip ic = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;
        Element image = null;

        // should work
        try
        {
            // create the xml code in a string: this clip is 6 images long
            // (from frame 5 to frame 10) and the target starts at 10,
            // so we should be inframe from frame 10 to 15
            String xml = new String(
            "<imageclip> <sourcefiles dir='resources/images'> <include name='**'/> </sourcefiles> <sourceseq> <seq start='5' stop='10'/> </sourceseq> <xposition> <seq start='100' stop='200'/> </xposition> <yposition> <seq start='50' stop='10' step='-1'/> </yposition> <rotation> <seq start='15' stop='25'/> </rotation> <opacity> <seq start='10' stop='100'/> </opacity> <xsize> <seq start='3' stop='6'/> </xsize> <ysize> <seq start='120' stop='180'/> </ysize> <targetseq start='5'/> </imageclip>");

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
