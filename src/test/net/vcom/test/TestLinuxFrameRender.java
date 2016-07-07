
/*****************************************************************************
 *
 * VCom: video compositor test
 *
 * source file: TestVComLinuxRender.java  
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
import java.util.List;
import java.util.Iterator;
import java.io.File;
import java.io.CharArrayReader;

import junit.framework.TestCase;
import org.apache.tools.ant.DirectoryScanner;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import net.vcom.LinuxFrameRender;
import net.vcom.FrameRenderI;
import net.vcom.Video;


/**
 * This class runs tests against the ImageClip class.
 */
public class TestLinuxFrameRender extends TestCase
{
    /**
     * Checks that the ImageClip constructor returns a non-null object
     */
    public void testConstructor()
    {
        LinuxFrameRender fr = null;

        // check we can create our renderers
        fr = new LinuxFrameRender();
        assertNotNull("null LinuxFrameRender returned", fr);
    }


    /**
     * Test render: checks that the right number of frames and images
     * are rendered, and the files exist
     */
    public void testRender()
    {
        LinuxFrameRender fr = null;
        Video v = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;
        File f = null;

        try
        {
            // create our renderers
            fr = new LinuxFrameRender();
            assertNotNull("null LinuxFrameRender returned from constructor",
                fr);

            // create a video from xml
            String xml = new String("<video name='name1' xsize='320' ysize='240' bgcolor='white' fps='30' encoder='xvid' encoderopts='opt1' workroot='build/test-render/work' finalroot='build/test-render/final' > <!-- white background --> <imageclip> <sourcefiles dir='resources/images/'> <include name='white-*.png'/> </sourcefiles> <sourceseq> <seq start='0' stop='29'/> </sourceseq> <targetseq start='0'/> </imageclip> <!-- black dot --> <imageclip> <sourcefiles dir='resources/images'> <include name='dot-*.png'/> </sourcefiles> <sourceseq> <seq start='0' stop='29'/> </sourceseq> <targetseq start='0'/> <xposition> <seq start='0' stop='150' step='5'/> </xposition> <yposition> <seq start='0' stop='150' step='5'/> </yposition> </imageclip> <soundclip> <source file='foo.wav' start='0.0' stop='1.0' /> <target start='0.0'/> </soundclip> </video>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // construct a video out of it
            v = new Video(doc);
            assertNotNull("null video", v);
            v.valid();

            // get the Frames document
            Document frames = v.getFrames();
            assertNotNull("null frames", frames);

            // render the frames
            fr.renderFrames(frames.getRootElement());

            // make sure the right directories were created
            f = new File("build/test-render");
            assertTrue("test-render directory doesn't exist", f.isDirectory());
            f = new File("build/test-render/work");
            assertTrue("test-render/work directory doesn't exist",
                f.isDirectory());
            f = new File("build/test-render/final");
            assertTrue("test-render/final directory doesn't exist",
                f.isDirectory());

            // confirm there are no files in the final dir
            f = new File("build/test-render/final");
            assertEquals("files in final", 0, f.list().length);

            // and that there are 60 in work/work-images
            f = new File("build/test-render/work/work-images");
            assertEquals("wrong number of files in work", 60, f.list().length);

            // and that there are 30 in work/work-images
            f = new File("build/test-render/work/final-images");
            assertEquals("wrong number of files in work", 30, f.list().length);

            // confirm the right number of frames and images were tracked
            assertEquals("wrong number of frames", 30,
                fr.frameCount);
            assertEquals("wrong number of processed images", 60,
                fr.imageCount - fr.skippedImageCount);
            assertEquals("wrong number of skipped images", 0,
                fr.skippedImageCount);

            // re-render
            fr.renderFrames(frames.getRootElement());

            // make sure the right directories still present
            f = new File("build/test-render");
            assertTrue("test-render directory doesn't exist", f.isDirectory());
            f = new File("build/test-render/work");
            assertTrue("test-render/work directory doesn't exist",
                f.isDirectory());
            f = new File("build/test-render/final");
            assertTrue("test-render/final directory doesn't exist",
                f.isDirectory());

            // confirm there are still no files in the final dir
            f = new File("build/test-render/final");
            assertEquals("files in final", 0, f.list().length);

            // and that there are still 60 in work/work-images
            f = new File("build/test-render/work/work-images");
            assertEquals("wrong number of files in work", 60, f.list().length);

            // and that there are still 30 in work/work-images
            f = new File("build/test-render/work/final-images");
            assertEquals("wrong number of files in work", 30, f.list().length);

            // confirm the right number of frames and images were tracked
            assertEquals("wrong number of frames", 30,
                fr.frameCount);

            // at least some images should have been skipped
            assertEquals("wrong number of processed images", 0,
                fr.imageCount - fr.skippedImageCount);
            assertEquals("wrong number of skipped images", 60,
                fr.skippedImageCount);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            assertTrue("got exception: " + e, false);
        }
    }
}
