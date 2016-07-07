
/*****************************************************************************
 *
 * VComFrames: video compositor
 *
 * source file: LinuxVideoRender.java
 * package: net.vcom
 *
 * version 0.3
 * 2005-06-01
 * Copyright (c) 2005, Denis McLaughlin
 * Released under the GPL license, version 2
 *
 */

package net.vcom;

import java.io.IOException;
import java.io.CharArrayWriter;
import java.io.CharArrayReader;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.Arrays;

import org.jdom.*;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;

import org.jdom.input.SAXBuilder;

import java.io.OutputStream;
import java.io.FileOutputStream;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.JellyException;
import org.apache.tools.ant.DirectoryScanner;


/**
 * This program takes a per-frame xml file and generates the corresponding
 * video.
 */
public class LinuxVideoRender implements VideoRenderI
{
    // these are some variables to hold some video-wide values
    private String workRootName = null;
    private String finalRootName = null;
    private int xSize = 0;
    private int ySize = 0;
    private String bgColor = null;

    // these track the stats for encoding videos
    public int frameCount = 0;
    public int partialVideoCount = 0;
    public int skippedPartialVideoCount = 0;
    private long encodeStartTime = 0;
    private long encodeStopTime = 0;


    /**
     * This reads the final images from the work dir, and makes a video
     * from them.
     */
    public void renderVideo(Element root)
    {
        // reset the stats
        partialVideoCount = 0;
        skippedPartialVideoCount = 0;

        // track how much time we spend encoding
        encodeStartTime = System.currentTimeMillis();

        // validate the frames document
        Util.validateFrames(root);

        // parse out the atts and children
        xSize = Integer.parseInt(root.getAttributeValue("xsize"));
        ySize = Integer.parseInt(root.getAttributeValue("ysize"));
        bgColor = root.getAttributeValue("bgcolor");
        List frameChildren = root.getChildren("frame");
        List soundChildren = root.getChildren("sound");

        // set some default values
        if ( xSize < 1 )
        { xSize = 720; }
        if ( ySize < 1 )
        { ySize = 480; }
        if ( bgColor == null )
        { bgColor = "white"; }

        // parse out the final and work directory from the frames attributes
        workRootName = root.getAttributeValue("workroot");
        finalRootName = root.getAttributeValue("finalroot");

        // default the workRootName to ./work and finalRootName to ./final
        if ( workRootName == null )
        { workRootName = "./work"; }
        if ( finalRootName == null )
        { finalRootName = "./final"; }

        // create the directories
        Util.mkdir(workRootName + "/work-videos");
        Util.mkdir(finalRootName);

        // make a directory scanner for our files
        DirectoryScanner scanner = new DirectoryScanner();
        String[] includes = {"final-frame*"};
        scanner.setIncludes(includes);
        scanner.setBasedir(workRootName + "/final-images");
        scanner.scan();

        // and call the render interface with the files
        renderVideo(scanner.getIncludedFiles());

        // record the stop time
        encodeStopTime = System.currentTimeMillis();
    }


    /**
     * This takes an array of file names and creates a partial videos from
     * them in batches of 10.
     */
    private void renderVideo(String[] files)
    {
        // default to 10, override with property
        int batchSize = 10;
        if ( System.getProperty("vcom.jelly.video.batchsize") != null )
        {
            batchSize = Integer.parseInt(
                System.getProperty("vcom.jelly.video.batchsize"));
        }
        ArrayList fileBatch = new ArrayList(batchSize);

        // step through the file name array batchSize at a time
        List fileList = Arrays.asList(files);
        Collections.sort(fileList);
        Iterator i = fileList.iterator();
        frameCount = 0;
        partialVideoCount = 0;
        while ( i.hasNext() )
        {
            fileBatch.add(i.next());
            frameCount++;
            if ( fileBatch.size() == batchSize )
            {
                renderPartialVideo(partialVideoCount, fileBatch);
                fileBatch.clear();
                partialVideoCount++;
            }
        }

        // if we have unprocessed frames, render them
        if ( fileBatch.size() != 0 )
        {
            renderPartialVideo(partialVideoCount, fileBatch);
            partialVideoCount++;
        }

        // stitch together the final video
        stitchVideo();
    }


    /**
     * This takes an array of file names and creates a partial video from them
     */
    private void renderPartialVideo(int batch, List fileBatch)
    {
        // get a File on the to-be-created partial video
        File partialVideo = new File( workRootName + "/work-videos/video-batch"
            + Util.padLeft(batch) + ".avi" );

        // if the partial video exists
        if ( partialVideo.exists() )
        {
            // set the older flag to true
            boolean allOlder = true;

            // step through all the images, setting allOlder to false if
            // one is newer than the partial video
            Iterator i = fileBatch.iterator();
            while ( i.hasNext() )
            {
                File image =
                    new File(workRootName + "/final-images/" +(String)i.next());

                if ( image.lastModified() > partialVideo.lastModified() )
                {
                    allOlder = false;
                }
            }

            // if all of the images are older than the video, skip creating
            // the video
            if ( allOlder )
            {
                skippedPartialVideoCount++;
                return;
            }
        }

        // if we get here, then we need to recreate it
        try
        {
            // make a file to hold the file names
            FileOutputStream fileNames = new FileOutputStream(
                new File(workRootName + "/filenames-batch"
                    + Util.padLeft(batch)) );

            // dunk the file names into a file
            Iterator i = fileBatch.iterator();
            while ( i.hasNext() )
            {
                // store the filenames, preceded by the work path
                fileNames.write(
                    (workRootName + "/final-images/" + (String)i.next() + "\n")
                        .getBytes() );
            }
        }
        catch(FileNotFoundException e)
        {
            System.out.println("got exception: " + e);
            e.printStackTrace();
            System.exit(1);
        }
        catch(IOException e)
        {
            System.out.println("got exception: " + e);
            e.printStackTrace();
            System.exit(1);
        }

        // now we run the video transcode
        Util.run("transcode -z --use_rgb -g " + xSize + "x" + ySize
            + " -i " + workRootName + "/filenames-batch" + Util.padLeft(batch)
            + " -x imlist,null -y xvid,null -f 30 "
            + " -o " + workRootName + "/work-videos/video-batch"
            + Util.padLeft(batch)
            + ".avi -H 0");

        return;
    }


    /**
     * This takes all the videos in the work-videos directory, and stitches
     * them into a single video in finalRoot
     */
    private void stitchVideo()
    {
        // get the list of videos to stitch
        File dir = new File(workRootName + "/work-videos");
        List videoFiles = Arrays.asList(dir.list());
        Collections.sort(videoFiles);

        // store the names of the videos in a file
        try
        {
            // make a file to hold the file names
            FileOutputStream videoNames =
                new FileOutputStream(
                    new File(workRootName + "/videonames") );

            // dunk the video names into a file
            Iterator i = videoFiles.iterator();
            while ( i.hasNext() )
            {
                // store the filenames, preceded by the work path
                videoNames.write(
                    (workRootName + "/work-videos/" + (String)i.next() + "\n")
                        .getBytes() );
            }
        }
        catch(FileNotFoundException e)
        {
            System.out.println("got exception: " + e);
            e.printStackTrace();
            System.exit(1);
        }
        catch(IOException e)
        {
            System.out.println("got exception: " + e);
            e.printStackTrace();
            System.exit(1);
        }

        // make a new list to hold the command arguments
        List cmd = new ArrayList();
        cmd.add("avimerge-new");
        cmd.add("-o");
        cmd.add(finalRootName + "/final-video.avi");

        // if there's a sound file in workRootName/final-sound, merge it too
        File sound = new File(workRootName + "/final-sound/final-sound.mp3");
        if ( sound.exists() )
        {
            cmd.add("-p");
            cmd.add(workRootName + "/final-sound/final-sound.mp3");
        }

        cmd.add("-I");
        cmd.add(workRootName + "/videonames");

        // convert the list to an array and run it
        System.out.println("stitching video");
        Util.run((String[]) cmd.toArray(new String[0]));
    }


    /**
     * This prints some stats
     */
    public void printStats()
    {
        long encodeDur = (encodeStopTime - encodeStartTime)/1000;

        // if durations are less then 1, make them 1
        if ( encodeDur < 1 ) { encodeDur = 1; }

        double frameEncodeRate = frameCount/encodeDur;

        // print the stats
        System.out.println("partial video count: " + partialVideoCount);
        System.out.println("partial videos skipped: "
            + skippedPartialVideoCount);
        System.out.println("encode time: " + encodeDur + " seconds");
        System.out.println("frame encode rate (effective): "
            + frameEncodeRate + " frames/second");
        System.out.println("");
    }
}
