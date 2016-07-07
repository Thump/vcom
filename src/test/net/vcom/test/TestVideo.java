
/**************************************************************************
 *
 * VCom: video compositor test
 *
 * source file: TestVideo.java  
 * package: net.vcom
 *
 * Copyright (c) 2005, Denis McLaughlin
 * Released under the GPL license, version 2
 *  
 */ 

package net.vcom.test;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.CharArrayReader;

import org.apache.tools.ant.DirectoryScanner;
import junit.framework.TestCase;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;

import net.vcom.Video;
import net.vcom.ImageClip;
import net.vcom.SoundClip;
import net.vcom.Seq;


/**
 * This class runs tests against the ImageClip class.
 */
public class TestVideo extends TestCase
{
    /**
     * Checks that the Video constructor returns a non-null object
     */
    public void testConstructor()
    {
        Video v = null;

        // create a Video
        v = new Video();
        assertNotNull("null Video returned from constructor",v);
    }


    /**
     * Tests that the attributes of Video can be set and got
     */
    public void testSetAndGet()
    {
        Video v = null;
        String name = "string1";
        int xSize = 1;
        int ySize = 2;
        String bgColor = "black";
        double fps = 3.0;
        String encoder = "string2";
        String encoderOpts1 = "string3";
        String encoderOpts2 = "string4";
        String workRoot = "string6";
        String finalRoot = "string8";

        // create a Video
        v = new Video();
        assertNotNull("null Video returned from constructor",v);

        // check we can set and get each of the attributes
        v.setName(name);
        assertEquals("bad name",name,v.getName());

        v.setXSize(xSize);
        assertEquals("bad xSize", xSize, v.getXSize() );

        v.setYSize(ySize);
        assertEquals("bad ySize", ySize, v.getYSize() );

        v.setFps(fps);
        assertEquals("bad fps", fps, v.getFps() );

        v.setEncoder(encoder);
        assertEquals("bad encoder", encoder, v.getEncoder() );

        v.setEncoderOpts(new String[] {encoderOpts1, encoderOpts2});
        String[] strings = null;
        strings = v.getEncoderOpts();
        assertNotNull("null encoderOpts", strings);
        assertEquals("bad encoderOpts[0]", strings[0], encoderOpts1);
        assertEquals("bad encoderOpts[1]", strings[1], encoderOpts2);

        v.setWorkRoot(workRoot);
        assertEquals("bad workRoot", workRoot, v.getWorkRoot() );

        v.setBgColor(bgColor);
        assertEquals("bad bgColor", bgColor, v.getBgColor() );

        v.setFinalRoot(finalRoot);
        assertEquals("bad finalRoot", finalRoot, v.getFinalRoot() );

        ImageClip ic=new ImageClip();
        v.addImageClip(ic);
        assertEquals("no image clip added", 1, v.getImageClips().size() );

        SoundClip sc=new SoundClip();
        v.addSoundClip(sc);
        assertEquals("no sound clip added", 1, v.getSoundClips().size() );
    }


    /**
     * Tests that we cannot set the workRoot, bgColor, or
     * finalRoot to null values
     */
    public void testBadSet()
    {
        Video v = null;

        // attempt to set the bgColor to null: should get exception,
        // so we fail if we don't
        try
        {
            // create a Video
            v = new Video();
            assertNotNull("null Video returned from constructor",v);

            v.setBgColor(null);
            assertTrue("set bgColor to null, but shouldn't have", false);
        }
        catch (IllegalArgumentException e)
        {}
        catch (Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // attempt to set the workRoot to null: should get exception,
        // so we fail if we don't
        try
        {
            // create a Video
            v = new Video();
            assertNotNull("null Video returned from constructor",v);

            v.setWorkRoot(null);
            assertTrue("set workRoot to null, but shouldn't have", false);
        }
        catch (IllegalArgumentException e)
        {}
        catch (Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // attempt to set the finalRoot to null: should get exception,
        // so we fail if we don't
        try
        {
            // create a Video
            v = new Video();
            assertNotNull("null Video returned from constructor",v);

            v.setFinalRoot(null);
            assertTrue("set finalRoot to null, but shouldn't have", false);
        }
        catch (IllegalArgumentException e)
        {}
        catch (Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // attempt to set negative xSize
        try
        {
            // create a Video
            v = new Video();
            assertNotNull("null Video returned from constructor",v);

            v.setXSize(-1);
            assertTrue("negative xsize succeeded", false);
        }
        catch (IllegalArgumentException e)
        {}
        catch (Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // attempt to set negative ySize
        try
        {
            // create a Video
            v = new Video();
            assertNotNull("null Video returned from constructor",v);

            v.setYSize(-1);
            assertTrue("negative ysize succeeded", false);
        }
        catch (IllegalArgumentException e)
        {}
        catch (Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // attempt to set negative fps
        try
        {
            // create a Video
            v = new Video();
            assertNotNull("null Video returned from constructor",v);

            v.setFps(-1);
            assertTrue("negative fps succeeded", false);
        }
        catch (IllegalArgumentException e)
        {}
        catch (Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * confirm that the default constructor initializes the imageClips
     * and soundClips to null ArrayLists
     */
    public void testClipInitialize()
    {
        Video v = null;
        ArrayList a = null;

        // create a Video
        v = new Video();

        // get the imageClips
        a = null;
        a = v.getImageClips();
        assertNotNull("imageClips not initialized", a);

        // get the soundClips
        a = null;
        a = v.getSoundClips();
        assertNotNull("soundClips not initialized", a);
    }


    /**
     * Check the Document constructor failure paths for Video are working
     */
    public void testBadVideoConstructor()
    {
        Video v = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // check that if the tag name isn't <imageclip> that it fails
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<notvideo/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            v = new Video(doc);

            // if we get here, that's bad
            assertNotNull("non video construction succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // check that if the tag has no attributes, it fails
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<video/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            v = new Video(doc);

            // if we get here, that's bad
            assertNotNull("no attribute video construction succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // check that if the tag has unrecognized attributes, it fails
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<video foo='1'/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            v = new Video(doc);

            // if we get here, that's bad
            assertNotNull("bogus attribute video construction succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // check that if the tag has unrecognized children, it fails
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<video name='foo'> <bar/> </video>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            v = new Video(doc);

            // if we get here, that's bad
            assertNotNull("bogus child video construction succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * Check the Document constructor success paths for Video are working
     */
    public void testGoodVideoConstructor()
    {
        Video v = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // set the name
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<video name='foo'/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            v = new Video(doc);

            // if we get here, that's bad
            assertNotNull("video construction failed", v);

            // check the attribute
            assertEquals("name not set", "foo", v.getName() );
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // set xsize
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<video xsize='720'/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            v = new Video(doc);

            // if we get here, that's bad
            assertNotNull("video construction failed", v);

            // check the attribute
            assertEquals("xsize not set", 720, v.getXSize() );
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // set ysize
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<video ysize='480'/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            v = new Video(doc);

            // if we get here, that's bad
            assertNotNull("video construction failed", v);

            // check the attribute
            assertEquals("ysize not set", 480, v.getYSize() );
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // set fps
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<video fps='29.7'/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            v = new Video(doc);

            // if we get here, that's bad
            assertNotNull("video construction failed", v);

            // check the attribute
            assertEquals("fps not set", 29.7, v.getFps() );
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // set encoder
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<video encoder='xvid'/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            v = new Video(doc);

            // if we get here, that's bad
            assertNotNull("video construction failed", v);

            // check the attribute
            assertEquals("encoder not set", "xvid", v.getEncoder() );
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // set encoderopts
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<video encoderopts='foo;bar'/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            v = new Video(doc);

            // if we get here, that's bad
            assertNotNull("video construction failed", v);

            // check the attribute
            String[] opts = v.getEncoderOpts();
            assertNotNull("encoderopts not set", opts);
            assertEquals("not enough encoderopts", 2, opts.length);
            assertEquals("first encoder opt wrong", "foo", opts[0]);
            assertEquals("second encoder opt wrong", "bar", opts[1]);
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // set bgColor
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<video bgcolor='black'/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            v = new Video(doc);

            // if we get here, that's bad
            assertNotNull("video construction failed", v);

            // check the attribute
            assertEquals("bgColor not set", "black", v.getBgColor() );
        }
        catch(Exception e)
        {
            e.printStackTrace();
            assertTrue("got exception: " + e, false);
        }

        // set workroot
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<video workroot='foo'/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            v = new Video(doc);

            // if we get here, that's bad
            assertNotNull("video construction failed", v);

            // check the attribute
            assertEquals("workroot not set", "foo", v.getWorkRoot() );
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // set finalroot
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<video finalroot='foo'/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            v = new Video(doc);

            // if we get here, that's bad
            assertNotNull("video construction failed", v);

            // check the attribute
            assertEquals("finalroot not set", "foo", v.getFinalRoot() );
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }

        // set an imageclip
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<video name='foo'> <imageclip> <parameter name='opacity'> <seq start='1' stop='2'/> </parameter> </imageclip> </video> ");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            v = new Video(doc);

            // if we get here, that's bad
            assertNotNull("video construction failed", v);

            // check that we have an imageclip
            ArrayList iclips = v.getImageClips();
            assertEquals("wrong number of imageclips", 1, iclips.size() ); 
            ImageClip ic = (ImageClip) iclips.get(0);
            assertNotNull("null imageclip", ic ); 
            Seq opacity = ic.getParameter("opacity").getSeq();
            assertNotNull("null opacity", opacity ); 
            assertEquals("wrong opacity start", 1, opacity.getStart() );
            assertEquals("wrong opacity stop", 2, opacity.getStop() );
        }
        catch(Exception e)
        {
            e.printStackTrace();
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * Check that calling valid on Video calls valid() on the sub-
     * components ImageClip, SoundClip, and Seq
     */
    public void testRecursiveValid()
    {
        Video v = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // set the name
        try
        {
            // create the xml code in a string
            String xml = new String(
            " <video name='foo'> <imageclip> <sourcefiles dir='resources/test'> <include name='**'/> <exclude name='baz'/> <exclude name='quux'/> </sourcefiles> <sourceseq> <seq start='5' stop='10'/> </sourceseq> </imageclip> </video>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            v = new Video(doc);

            // if we get here, that's bad
            assertNotNull("video construction failed", v);

            // make sure we have the right imageclip
            ArrayList ics = v.getImageClips();
            assertNotNull("null ics from video", ics);
            assertEquals("incorrect number of ics in ics", 1, ics.size());
            ImageClip ic = (ImageClip) ics.get(0);
            assertNotNull("null ic in ics", ic);
            DirectoryScanner scanner = ic.getSourceFiles();
            assertNotNull("null scanner in ic", scanner);

            // before we call valid, accessing getIncludedFiles() from
            // the scanner will result in an exception: this is probably
            // a bug in DirectoryScanner
            try
            {
                String[] files = scanner.getIncludedFiles();

                // we shouldn't get here
                assertTrue("bug fixed in directory scanner?", false);
            }
            catch(NullPointerException e)
            {}
            catch(Exception e)
            {
                assertTrue("got exception: " + e, false);
            }

            // now, if recursive validation works, if we call valid()
            // on the video, it should call valid() on the imageclip,
            // which as a side effect will call scan() on the scanner,
            // which will initialize the files, and a subsequent call
            // to getIncludedFiles() will succeed.
            v.valid(); 
            String[] files = scanner.getIncludedFiles();
            assertNotNull("got null files from scanner", files);
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * This checks that the frames document resulting from a simple
     * video document is correct.
     */
    public void testGetFrames()
    {
        Video v = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // set the name
        try
        {
            // create the xml code in a string
            String xml = new String( " <video name='name1' xsize='320' ysize='240' bgcolor='white' fps='30' encoder='xvid' encoderopts='opt1' workroot='build/work' finalroot='build/final' > <!-- white background --> <imageclip> <sourcefiles dir='resources/images/'> <include name='white-*.png'/> </sourcefiles> <sourceseq> <seq start='0' stop='29'/> </sourceseq> <targetseq start='0'/> </imageclip> <!-- black dot --> <imageclip> <sourcefiles dir='resources/images'> <include name='dot-*.png'/> </sourcefiles> <sourceseq> <seq start='0' stop='29'/> </sourceseq> <targetseq start='0'/> <parameter name='xposition'> <seq start='0' stop='150' step='5'/> </parameter> <parameter name='yposition'> <seq start='0' stop='150' step='5'/> </parameter> </imageclip> <soundclip> <source file='foo.wav' start='0.0' stop='1.0' /> <target start='0.0'/> </soundclip> </video>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            v = new Video(doc);

            // make sure we have a valid video document
            assertNotNull("null video", v);
            v.valid();

            // get the frames document
            Document frames = v.getFrames();

            // make sure the root element is <frames>
            Element root = frames.getRootElement();
            assertNotNull("null root element", root);
            assertEquals("wrong root element name", "frames", root.getName() );

            // make sure the root element's attributes are set
            assertEquals("wrong name", "name1", root.getAttributeValue("name"));
            assertEquals("wrong xsize", "320", root.getAttributeValue("xsize"));
            assertEquals("wrong ysize", "240", root.getAttributeValue("ysize"));
            assertEquals("wrong bgcolor", "white",
                root.getAttributeValue("bgcolor"));
            assertEquals("wrong fps", "30.0", root.getAttributeValue("fps"));
            assertEquals("wrong encoder", "xvid",
                root.getAttributeValue("encoder"));
            assertEquals("wrong workroot", "build/work",
                root.getAttributeValue("workroot"));
            assertEquals("wrong finalroot", "build/final",
                root.getAttributeValue("finalroot"));

            // now check we have 30 frame children
            List children = root.getChildren("frame");
            assertEquals("wrong number of frames", 30, children.size());

            // check that the first frame has two image children
            Element frame = (Element) children.get(0);
            List images = frame.getChildren("image");
            assertEquals("wrong number of images", 2, images.size());

            // check the first image has a date attribute
            Element image = (Element) images.get(0);
            assertNotNull("no date property", image.getAttributeValue("date"));
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * This checks that the processing a video file twice preserves
     * date stamps on the images
     */
    public void testPreserveDate()
    {
        Video v = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        // set the name
        try
        {
            // create the xml code in a string
            String xml = new String( "<video name='name1' xsize='320' ysize='240' bgcolor='white' fps='30' encoder='xvid' encoderopts='opt1' workroot='build/test/work' finalroot='build/test/final' > <imageclip> <sourcefiles dir='resources/images/'> <include name='white-*.png'/> </sourcefiles> <sourceseq> <seq start='0' stop='9'/> </sourceseq> <targetseq start='0'/> </imageclip> <imageclip> <sourcefiles dir='resources/images'> <include name='dot-*.png'/> </sourcefiles> <sourceseq> <seq start='0' stop='9'/> </sourceseq> <targetseq start='0'/> <parameter name='xposition'> <seq start='0' stop='10' step='1'/> </parameter> </imageclip> </video>" );

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            v = new Video(doc);

            // make sure we have a valid video document
            assertNotNull("null video", v);
            v.valid();

            // process the video document: this creates the frames.xml file,
            // incidently also setting the first date on the images
            v.process();

            // process the video, returning the results as a frames document:
            // this should pull dates from the frames.xml file
            Document framesDoc1 = v.getFrames();

            // make sure the root element is <frames>
            Element root1 = framesDoc1.getRootElement();
            assertNotNull("null root element", root1);
            assertEquals("wrong root element name", "frames", root1.getName() );

            // now check we have 10 frame children
            List frames1 = root1.getChildren("frame");
            assertEquals("wrong number of frames", 10, frames1.size());

            // get the image children
            List images1 = new ArrayList();
            Iterator i = frames1.iterator();
            while ( i.hasNext() )
            {
                Element frame = (Element) i.next();
                images1.addAll(frame.getChildren("image"));
            }

            // there should be 20 images
            assertEquals("wrong number of images", 20, images1.size());

            // step through the images, and store the associated dates
            String[] dates = new String[20];
            i = images1.iterator();
            int count = 0;
            while ( i.hasNext() )
            {
                Element image = (Element) i.next();
                dates[count] = image.getAttributeValue("date");
                assertNotNull("date at position " + count + " was null",
                    dates[count++]);
            }

            // sleep 2 seconds
            Thread.sleep(2000);


            // now we reprocess the frames, and again look at the dates, 
            // only this time we assert the dates must the same as previous
            // ie, check that the dates were propagated forward

            // reprocess, and compare the frame dates: they should be the same
            Document framesDoc2 = v.getFrames();

            // make sure the root element is <frames>
            Element root2 = framesDoc2.getRootElement();
            assertNotNull("null root element", root2);
            assertEquals("wrong root element name", "frames", root2.getName() );

            // check we have 10 frame children
            List frames2 = root2.getChildren("frame");
            assertEquals("wrong number of frames", 10, frames2.size());

            // get the image children
            List images2 = new ArrayList();
            i = frames2.iterator();
            while ( i.hasNext() )
            {
                Element frame = (Element) i.next();
                images2.addAll(frame.getChildren("image"));
            }

            // there should be 20 images
            assertEquals("wrong number of images", 20, images2.size());

            // step through the images, and compare the associated dates
            i = images2.iterator();
            count = 0;
            while ( i.hasNext() )
            {
                Element image = (Element) i.next();
                String newDate = image.getAttributeValue("date");
                assertNotNull("new date at position " + count + " was null",
                    newDate);
                assertEquals("non-propagated date", dates[count++], newDate);
            }
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * This checks that the processing a video file twice preserves
     * date stamps on the images, unless the image has changed
     */
    public void testChangedDate()
    {
        Video v1, v2 = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc1, doc2 = null;

        // set the name
        try
        {
            // create the xml code in a string
            String xml1 = new String( "<video name='name1' xsize='320' ysize='240' bgcolor='white' fps='30' encoder='xvid' encoderopts='opt1' workroot='build/test/work' finalroot='build/test/final' > <imageclip> <sourcefiles dir='resources/images/'> <include name='white-*.png'/> </sourcefiles> <sourceseq> <seq start='0' stop='9'/> </sourceseq> <targetseq start='0'/> </imageclip> <imageclip> <sourcefiles dir='resources/images'> <include name='dot-*.png'/> </sourcefiles> <sourceseq> <seq start='0' stop='9'/> </sourceseq> <targetseq start='0'/> <parameter name='xposition'> <seq start='0' stop='5' step='1'/> </parameter> </imageclip> </video>" );

            // turn the string into a char[] and build the Document from it
            doc1 = builder.build( new CharArrayReader(xml1.toCharArray()) );

            // create a seq from the document
            v1 = new Video(doc1);

            // make sure we have a valid video document
            assertNotNull("null video", v1);
            v1.valid();

            // process the video document: this creates the frames.xml file,
            // incidently also setting the first date on the images
            v1.process();

            // process the video, returning the results as a frames document:
            // this should pull dates from the frames.xml file
            Document framesDoc1 = v1.getFrames();

            // make sure the root element is <frames>
            Element root1 = framesDoc1.getRootElement();
            assertNotNull("null root element", root1);
            assertEquals("wrong root element name", "frames", root1.getName() );

            // now check we have 10 frame children
            List frames1 = root1.getChildren("frame");
            assertEquals("wrong number of frames", 10, frames1.size());

            // get the image children
            List images1 = new ArrayList();
            Iterator i = frames1.iterator();
            while ( i.hasNext() )
            {
                Element frame = (Element) i.next();
                images1.addAll(frame.getChildren("image"));
            }

            // there should be 20 images
            assertEquals("wrong number of images", 20, images1.size());

            // step through the images, and store the associated dates
            String[] dates1 = new String[20];
            i = images1.iterator();
            int count = 0;
            while ( i.hasNext() )
            {
                Element image = (Element) i.next();
                dates1[count] = image.getAttributeValue("date");
                assertNotNull("date at position " + count + " was null",
                    dates1[count++]);
            }

            // sleep 2 seconds
            Thread.sleep(2000);


            // now we reprocess the frames with a changed xml file: the code
            // to generate the first two images will have changed, and there
            // should therefore be new dates for the second image in the
            // first five frames.

            // create the xml code in a string
            String xml2 = new String( "<video name='name1' xsize='320' ysize='240' bgcolor='white' fps='30' encoder='xvid' encoderopts='opt1' workroot='build/test/work' finalroot='build/test/final' > <imageclip> <sourcefiles dir='resources/images/'> <include name='white-*.png'/> </sourcefiles> <sourceseq> <seq start='0' stop='9'/> </sourceseq> <targetseq start='0'/> </imageclip> <imageclip> <sourcefiles dir='resources/images'> <include name='dot-*.png'/> </sourcefiles> <sourceseq> <seq start='0' stop='9'/> </sourceseq> <targetseq start='0'/> <parameter name='xposition'> <seq start='2' stop='5' step='1'/> </parameter> </imageclip> </video>" );

            // turn the string into a char[] and build the Document from it
            doc2 = builder.build( new CharArrayReader(xml2.toCharArray()) );

            // create a seq from the document
            v2 = new Video(doc2);

            // get the frames for the video document: because the frames.xml
            // file exists from the previous run, it will use those dates
            // for this process run
            Document framesDoc2 = v2.getFrames();

            // make sure the root element is <frames>
            Element root2 = framesDoc2.getRootElement();
            assertNotNull("null root element", root2);
            assertEquals("wrong root element name", "frames", root2.getName() );

            // now check we have 10 frame children
            List frames2 = root2.getChildren("frame");
            assertEquals("wrong number of frames", 10, frames2.size());

            // get the image children
            List images2 = new ArrayList();
            i = frames2.iterator();
            while ( i.hasNext() )
            {
                Element frame = (Element) i.next();
                images2.addAll(frame.getChildren("image"));
            }

            // there should be 20 images
            assertEquals("wrong number of images", 20, images2.size());

            // step through the images, and store the associated dates
            String[] dates2 = new String[20];
            i = images2.iterator();
            count = 0;
            while ( i.hasNext() )
            {
                Element image = (Element) i.next();
                dates2[count] = image.getAttributeValue("date");
                assertNotNull("date at position " + count + " was null",
                    dates2[count++]);
            }

            // now we compare the dates1[] and dates2[]: there are 20 images,
            // 2 per frame.  For the first five frames, the second image date
            // should be changed

            // frame 1
            assertEquals("wrong value", dates1[0], dates2[0]);
            assertFalse("wrong value", dates1[1].equals(dates2[1]) );

            // frame 2
            assertEquals("wrong value", dates1[2], dates2[2]);
            assertFalse("wrong value", dates1[3].equals(dates2[3]) );

            // frame 3
            assertEquals("wrong value", dates1[4], dates2[4]);
            assertFalse("wrong value", dates1[5].equals(dates2[5]) );

            // frame 4
            assertEquals("wrong value", dates1[6], dates2[6]);
            assertFalse("wrong value", dates1[7].equals(dates2[7]) );

            // frame 5
            assertEquals("wrong value", dates1[8], dates2[8]);
            assertFalse("wrong value", dates1[9].equals(dates2[9]) );

            // frame 6
            assertEquals("wrong value", dates1[10], dates2[10]);
            assertEquals("wrong value", dates1[11], dates2[11]);

            // frame 7
            assertEquals("wrong value", dates1[12], dates2[12]);
            assertEquals("wrong value", dates1[13], dates2[13]);

            // frame 8
            assertEquals("wrong value", dates1[14], dates2[14]);
            assertEquals("wrong value", dates1[15], dates2[15]);

            // frame 9
            assertEquals("wrong value", dates1[16], dates2[16]);
            assertEquals("wrong value", dates1[17], dates2[17]);

            // frame 10
            assertEquals("wrong value", dates1[18], dates2[18]);
            assertEquals("wrong value", dates1[19], dates2[19]);
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * This checks that the changing the video's header information
     * causes a full rerender to be performed.
     */
    public void testChangedHeader()
    {
        Video v1, v2 = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc1, doc2 = null;

        // set the name
        try
        {
            // create the xml code in a string
            String xml1 = new String( "<video name='name1' xsize='320' ysize='240' bgcolor='white' fps='30' encoder='xvid' encoderopts='opt1' workroot='build/test/work' finalroot='build/test/final' > <imageclip> <sourcefiles dir='resources/images/'> <include name='white-*.png'/> </sourcefiles> <sourceseq> <seq start='0' stop='9'/> </sourceseq> <targetseq start='0'/> </imageclip> <imageclip> <sourcefiles dir='resources/images'> <include name='dot-*.png'/> </sourcefiles> <sourceseq> <seq start='0' stop='9'/> </sourceseq> <targetseq start='0'/> <parameter name='xposition'> <seq start='0' stop='5' step='1'/> </parameter> </imageclip> </video>" );

            // turn the string into a char[] and build the Document from it
            doc1 = builder.build( new CharArrayReader(xml1.toCharArray()) );

            // create a seq from the document
            v1 = new Video(doc1);

            // make sure we have a valid video document
            assertNotNull("null video", v1);
            v1.valid();

            // process the video document: this creates the frames.xml file,
            // incidently also setting the first date on the images
            v1.process();

            // process the video, returning the results as a frames document:
            // this should pull dates from the frames.xml file
            Document framesDoc1 = v1.getFrames();

            // make sure the root element is <frames>
            Element root1 = framesDoc1.getRootElement();
            assertNotNull("null root element", root1);
            assertEquals("wrong root element name", "frames", root1.getName() );

            // now check we have 10 frame children
            List frames1 = root1.getChildren("frame");
            assertEquals("wrong number of frames", 10, frames1.size());

            // get the image children
            List images1 = new ArrayList();
            Iterator i = frames1.iterator();
            while ( i.hasNext() )
            {
                Element frame = (Element) i.next();
                images1.addAll(frame.getChildren("image"));
            }

            // there should be 20 images
            assertEquals("wrong number of images", 20, images1.size());

            // step through the images, and store the associated dates
            String[] dates1 = new String[20];
            i = images1.iterator();
            int count = 0;
            while ( i.hasNext() )
            {
                Element image = (Element) i.next();
                dates1[count] = image.getAttributeValue("date");
                assertNotNull("date at position " + count + " was null",
                    dates1[count++]);
            }

            // sleep 2 seconds
            Thread.sleep(2000);


            // now we confirm that reprocessing without changes causes all
            // all images to preserve their old dates.

            // create the xml code in a string
            String xml2 = new String( "<video name='name1' xsize='320' ysize='240' bgcolor='white' fps='30' encoder='xvid' encoderopts='opt1' workroot='build/test/work' finalroot='build/test/final' > <imageclip> <sourcefiles dir='resources/images/'> <include name='white-*.png'/> </sourcefiles> <sourceseq> <seq start='0' stop='9'/> </sourceseq> <targetseq start='0'/> </imageclip> <imageclip> <sourcefiles dir='resources/images'> <include name='dot-*.png'/> </sourcefiles> <sourceseq> <seq start='0' stop='9'/> </sourceseq> <targetseq start='0'/> <parameter name='xposition'> <seq start='0' stop='5' step='1'/> </parameter> </imageclip> </video>" );

            // turn the string into a char[] and build the Document from it
            doc2 = builder.build( new CharArrayReader(xml2.toCharArray()) );

            // create a seq from the document
            v2 = new Video(doc2);

            // get the frames for the video document: because the frames.xml
            // file exists from the previous run, it will use those dates
            // for this process run
            Document framesDoc2 = v2.getFrames();

            // make sure the root element is <frames>
            Element root2 = framesDoc2.getRootElement();
            assertNotNull("null root element", root2);
            assertEquals("wrong root element name", "frames", root2.getName() );

            // now check we have 10 frame children
            List frames2 = root2.getChildren("frame");
            assertEquals("wrong number of frames", 10, frames2.size());

            // get the image children
            List images2 = new ArrayList();
            i = frames2.iterator();
            while ( i.hasNext() )
            {
                Element frame = (Element) i.next();
                images2.addAll(frame.getChildren("image"));
            }

            // there should be 20 images
            assertEquals("wrong number of images", 20, images2.size());

            // step through the images, and store the associated dates
            String[] dates2 = new String[20];
            i = images2.iterator();
            count = 0;
            while ( i.hasNext() )
            {
                Element image = (Element) i.next();
                dates2[count] = image.getAttributeValue("date");
                assertNotNull("date at position " + count + " was null",
                    dates2[count++]);
            }

            // now we compare the dates1[] and dates2[]: there are 20 images,
            // 2 per frame.  For the first five frames, the second image date
            // should be changed

            // frame 1
            assertEquals("wrong value", dates1[0], dates2[0]);
            assertEquals("wrong value", dates1[1], dates2[1]);

            // frame 2
            assertEquals("wrong value", dates1[2], dates2[2]);
            assertEquals("wrong value", dates1[3], dates2[3]);

            // frame 3
            assertEquals("wrong value", dates1[4], dates2[4]);
            assertEquals("wrong value", dates1[5], dates2[5]);

            // frame 4
            assertEquals("wrong value", dates1[6], dates2[6]);
            assertEquals("wrong value", dates1[7], dates2[7]);

            // frame 5
            assertEquals("wrong value", dates1[8], dates2[8]);
            assertEquals("wrong value", dates1[9], dates2[9]);

            // frame 6
            assertEquals("wrong value", dates1[10], dates2[10]);
            assertEquals("wrong value", dates1[11], dates2[11]);

            // frame 7
            assertEquals("wrong value", dates1[12], dates2[12]);
            assertEquals("wrong value", dates1[13], dates2[13]);

            // frame 8
            assertEquals("wrong value", dates1[14], dates2[14]);
            assertEquals("wrong value", dates1[15], dates2[15]);

            // frame 9
            assertEquals("wrong value", dates1[16], dates2[16]);
            assertEquals("wrong value", dates1[17], dates2[17]);

            // frame 10
            assertEquals("wrong value", dates1[18], dates2[18]);
            assertEquals("wrong value", dates1[19], dates2[19]);


            // and now we make a change in the background color, and we
            // should see all frames re-rendered

            // create the xml code in a string
            String xml3 = new String( "<video name='name1' xsize='320' ysize='240' bgcolor='black' fps='30' encoder='xvid' encoderopts='opt1' workroot='build/test/work' finalroot='build/test/final' > <imageclip> <sourcefiles dir='resources/images/'> <include name='white-*.png'/> </sourcefiles> <sourceseq> <seq start='0' stop='9'/> </sourceseq> <targetseq start='0'/> </imageclip> <imageclip> <sourcefiles dir='resources/images'> <include name='dot-*.png'/> </sourcefiles> <sourceseq> <seq start='0' stop='9'/> </sourceseq> <targetseq start='0'/> <parameter name='xposition'> <seq start='0' stop='5' step='1'/> </parameter> </imageclip> </video>" );

            // turn the string into a char[] and build the Document from it
            doc2 = builder.build( new CharArrayReader(xml3.toCharArray()) );

            // create a seq from the document
            v2 = new Video(doc2);

            // get the frames for the video document: because the frames.xml
            // file exists from the previous run, it will use those dates
            // for this process run
            framesDoc2 = v2.getFrames();

            // make sure the root element is <frames>
            root2 = framesDoc2.getRootElement();
            assertNotNull("null root element", root2);
            assertEquals("wrong root element name", "frames", root2.getName() );

            // now check we have 10 frame children
            frames2 = root2.getChildren("frame");
            assertEquals("wrong number of frames", 10, frames2.size());

            // get the image children
            images2 = new ArrayList();
            i = frames2.iterator();
            while ( i.hasNext() )
            {
                Element frame = (Element) i.next();
                images2.addAll(frame.getChildren("image"));
            }

            // there should be 20 images
            assertEquals("wrong number of images", 20, images2.size());

            // step through the images, and store the associated dates
            dates2 = new String[20];
            i = images2.iterator();
            count = 0;
            while ( i.hasNext() )
            {
                Element image = (Element) i.next();
                dates2[count] = image.getAttributeValue("date");
                assertNotNull("date at position " + count + " was null",
                    dates2[count++]);
            }

            // now we compare the dates1[] and dates2[]: there are 20 images,
            // 2 per frame.  For the first five frames, the second image date
            // should be changed

            // frame 1
            assertFalse("wrong value", dates1[0].equals(dates2[0]) );
            assertFalse("wrong value", dates1[1].equals(dates2[1]) );

            // frame 2
            assertFalse("wrong value", dates1[2].equals(dates2[2]) );
            assertFalse("wrong value", dates1[3].equals(dates2[3]) );

            // frame 3
            assertFalse("wrong value", dates1[4].equals(dates2[4]) );
            assertFalse("wrong value", dates1[5].equals(dates2[5]) );

            // frame 4
            assertFalse("wrong value", dates1[6].equals(dates2[6]) );
            assertFalse("wrong value", dates1[7].equals(dates2[7]) );

            // frame 5
            assertFalse("wrong value", dates1[8].equals(dates2[8]) );
            assertFalse("wrong value", dates1[9].equals(dates2[9]) );

            // frame 6
            assertFalse("wrong value", dates1[10].equals(dates2[10]) );
            assertFalse("wrong value", dates1[11].equals(dates2[11]) );

            // frame 7
            assertFalse("wrong value", dates1[12].equals(dates2[12]) );
            assertFalse("wrong value", dates1[13].equals(dates2[13]) );

            // frame 8
            assertFalse("wrong value", dates1[14].equals(dates2[14]) );
            assertFalse("wrong value", dates1[15].equals(dates2[15]) );

            // frame 9
            assertFalse("wrong value", dates1[16].equals(dates2[16]) );
            assertFalse("wrong value", dates1[17].equals(dates2[17]) );

            // frame 10
            assertFalse("wrong value", dates1[18].equals(dates2[18]) );
            assertFalse("wrong value", dates1[19].equals(dates2[19]) );
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }
}
