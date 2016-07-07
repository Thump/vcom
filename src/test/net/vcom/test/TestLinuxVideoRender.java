
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
import net.vcom.LinuxVideoRender;
import net.vcom.VideoRenderI;
import net.vcom.Video;


/**
 * This class runs tests against the ImageClip class.
 */
public class TestLinuxVideoRender extends TestCase
{
    /**
     * Checks that the ImageClip constructor returns a non-null object
     */
    public void testConstructor()
    {
        LinuxFrameRender fr = null;
        LinuxVideoRender vr = null;

        // check we can create our renderers
        fr = new LinuxFrameRender();
        assertNotNull("null LinuxFrameRender returned", fr);
        vr = new LinuxVideoRender();
        assertNotNull("null LinuxVideoRender returned", vr);
    }


    /**
     * Test encode: checks that the right number of videos are created,
     * and the files exist
     */
    public void testEncode()
    {
        LinuxFrameRender fr = null;
        LinuxVideoRender vr = null;
        Video v = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;
        File f = null;

        try
        {
            // create our renderers
            fr = new LinuxFrameRender();
            assertNotNull("null LinuxFrameRender returned", fr);
            vr = new LinuxVideoRender();
            assertNotNull("null LinuxVideoRender returned", vr);

            // create a video from xml
            String xml = new String("<video name='name1' xsize='320' ysize='240' bgcolor='white' fps='30' encoder='xvid' encoderopts='opt1' workroot='build/test-encode/work' finalroot='build/test-encode/final' > <!-- white background --> <imageclip> <sourcefiles dir='resources/images/'> <include name='white-*.png'/> </sourcefiles> <sourceseq> <seq start='0' stop='29'/> </sourceseq> <targetseq start='0'/> </imageclip> <!-- black dot --> <imageclip> <sourcefiles dir='resources/images'> <include name='dot-*.png'/> </sourcefiles> <sourceseq> <seq start='0' stop='29'/> </sourceseq> <targetseq start='0'/> <xposition> <seq start='0' stop='150' step='5'/> </xposition> <yposition> <seq start='0' stop='150' step='5'/> </yposition> </imageclip> <soundclip> <source file='foo.wav' start='0.0' stop='1.0' /> <target start='0.0'/> </soundclip> </video>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // construct a video out of it
            v = new Video(doc);
            assertNotNull("null video", v);
            v.valid();

            // get the Frames document
            Document frames = v.getFrames();
            assertNotNull("null frames", frames);

            // make a renderer and render the frames and then the video
            fr.renderFrames(frames.getRootElement());
            vr.renderVideo(frames.getRootElement());

            // make sure the right directories were created
            f = new File("build/test-encode");
            assertTrue("test-encode directory doesn't exist", f.isDirectory());
            f = new File("build/test-encode/work");
            assertTrue("test-encode/work directory doesn't exist",
                f.isDirectory());
            f = new File("build/test-encode/final");
            assertTrue("test-encode/final directory doesn't exist",
                f.isDirectory());

            // confirm there is one file in the final dir
            f = new File("build/test-encode/final");
            assertEquals("files in final", 1, f.list().length);

            // and that there are 3 in work/work-videos
            f = new File("build/test-encode/work/work-videos");
            assertEquals("wrong number of files in work", 3, f.list().length);

            // confirm the right number of frames and images were tracked
            assertEquals("wrong number of partial videos", 3,
                vr.partialVideoCount);
            assertEquals("wrong number of skipped partial videos", 0,
                vr.skippedPartialVideoCount);

            // now we re-render: since we haven't changed anything, we
            // shouldn't actually render anything

            // re-render
            fr.renderFrames(frames.getRootElement());
            vr.renderVideo(frames.getRootElement());

            // confirm there is one file in the final dir
            f = new File("build/test-encode/final");
            assertEquals("files in final", 1, f.list().length);

            // and that there are 3 in work/work-videos
            f = new File("build/test-encode/work/work-videos");
            assertEquals("wrong number of files in work", 3, f.list().length);

            // confirm the right number of frames and images were tracked
            assertEquals("wrong number of partial videos", 3,
                vr.partialVideoCount);
            assertEquals("wrong number of skipped partial videos", 3,
                vr.skippedPartialVideoCount);

            // now we touch one file and re-render: because we touched a file,
            // we should re-render that fimage and the partial video for it

            // touch a file
            Thread.sleep(2000);
            File tmpF = new File(
           "build/test-encode/work/work-images/work-frame000000-image0000.png");
            tmpF.setLastModified(System.currentTimeMillis());

            // re-render
            fr.renderFrames(frames.getRootElement());
            vr.renderVideo(frames.getRootElement());

            // confirm there is one file in the final dir
            f = new File("build/test-encode/final");
            assertEquals("files in final", 1, f.list().length);

            // and that there are 3 in work/work-videos
            f = new File("build/test-encode/work/work-videos");
            assertEquals("wrong number of files in work", 3, f.list().length);

            // confirm the right number of frames and images were tracked
            assertEquals("wrong number of partial videos", 3,
                vr.partialVideoCount);
            assertEquals("wrong number of skipped partial videos", 2,
                vr.skippedPartialVideoCount);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            assertTrue("got exception: " + e, false);
        }
    }
}
