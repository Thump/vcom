
/**************************************************************************
 *
 * VComFrames: video compositor
 *
 * source file: RenderVideo_transcode.java
 * package: net.vcom
 *
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
import java.io.BufferedWriter;
import java.io.FileWriter;
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
 * video using transcode.
 */
public class RenderVideo_transcode implements RenderVideoI
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

    // the output command file
    private BufferedWriter cmds = null;

    // list of partial avis
    List partialVideos = null;


    /**
     * This reads the final images from the work dir, and makes a video
     * from them.
     */
    public void renderVideo(Element root)
    {
        // reset the stats
        partialVideoCount = 0;
        skippedPartialVideoCount = 0;

        // null out the partialVideos list, in case we're being called twice
        partialVideos = new ArrayList();

        // track how much time we spend encoding
        encodeStartTime = System.currentTimeMillis();

        // validate the frames document
        Util.validateFrames(root);

        // parse out the atts and children
        xSize = 720;
        if ( root.getAttributeValue("xsize") != null )
        { xSize = Integer.parseInt(root.getAttributeValue("xsize")); }

        ySize = 480;
        if ( root.getAttributeValue("ysize") != null )
        { ySize = Integer.parseInt(root.getAttributeValue("ysize")); }

        bgColor = "white";
        if ( root.getAttributeValue("bgcolor") != null )
        { bgColor = root.getAttributeValue("bgcolor"); }

        List frameChildren = root.getChildren("frame");
        List soundChildren = root.getChildren("sound");

        // parse out the final and work directory from the frames attributes
        workRootName = "./work";
        if ( root.getAttributeValue("workroot") != null )
        { workRootName = root.getAttributeValue("workroot"); }

        { finalRootName = "./final"; }
        if ( root.getAttributeValue("finalroot") != null )
        { finalRootName = root.getAttributeValue("finalroot"); }

        // create the directories
        Util.mkdir(workRootName + "/work-videos");
        Util.mkdir(finalRootName);

        // if we're not executing commands, but instead printing them
        try
        {
            if ( System.getProperty("vcom.cmd.execute").equals("false") )
            {
                cmds = new BufferedWriter(
                    new FileWriter(workRootName + "/encode-cmds"));
            }
        }
        catch (IOException e) { };

        // make a directory scanner for our files
        DirectoryScanner scanner = new DirectoryScanner();
        String[] includes = {"final-frame*"};
        scanner.setIncludes(includes);
        scanner.setBasedir(workRootName + "/final-images");
        scanner.scan();

        // and call the render interface with the files
        renderVideo(scanner.getIncludedFiles(), frameChildren.size());

        // record the stop time
        encodeStopTime = System.currentTimeMillis();

        // if we're printing out the commands, close the file
        try
        {
            if ( System.getProperty("vcom.cmd.execute").equals("false") )
            { cmds.close(); }
        } catch (IOException e) { };
    }


    /**
     * This takes an array of file names and creates a partial videos from
     * them in batches of 10.
     */
    private void renderVideo(String[] files, int maxEncodeFrame)
    {
        // default to 10, override with property
        int batchSize = 10;
        if ( System.getProperty("vcom.jelly.video.batchsize") != null )
        {
            batchSize = Integer.parseInt(
                System.getProperty("vcom.jelly.video.batchsize"));
        }
        ArrayList fileBatch = new ArrayList(batchSize);

        // maxEncodeFrame is passed in as the number of frames in the video:
        // we allow it to be overridden here in two ways: if
        // vcom.encode.maxframe is set, we use that, else if 
        // vcom.render.maxframe is set, we use that
        if ( System.getProperty("vcom.encode.video.maxframe") != null )
        {
            maxEncodeFrame = Integer.parseInt(
                System.getProperty("vcom.encode.video.maxframe"));
        }
        else if ( System.getProperty("vcom.render.frame.maxframe") != null )
        {
            maxEncodeFrame = Integer.parseInt(
                System.getProperty("vcom.render.frame.maxframe"));
        }
        System.out.println("setting max encode frame to " + maxEncodeFrame);


        // step through the file name array batchSize at a time
        List fileList = Arrays.asList(files);
        Collections.sort(fileList);
        Iterator i = fileList.iterator();
        frameCount = 0;
        partialVideoCount = 0;
        while ( i.hasNext() && frameCount <= maxEncodeFrame )
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
     * This takes an array of file names and creates a partial video from
     * them
     */
    private void renderPartialVideo(int batch, List fileBatch)
    {
        // get a File on the to-be-created partial video
        String partialVideoName = new String(
            workRootName + "/work-videos/video-batch"
            + Util.padLeft(batch) + ".avi" );
        File partialVideo = new File(partialVideoName);

        // add the name of this partial Avi to the list
        partialVideos.add(partialVideoName);

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
                File image = new File(
                    workRootName + "/final-images/" +(String)i.next());

                if ( image.lastModified() > partialVideo.lastModified() )
                { allOlder = false; }
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
                fileNames.write( (workRootName + "/final-images/"
                    + (String)i.next() + "\n")
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

        // prepare the command to encode to video
        String cmd = "transcode --use_rgb -g " + xSize + "x" + ySize
                + " -i "+workRootName+"/filenames-batch"+Util.padLeft(batch)
                + " -x imlist,null -y xvid,null -f 15 "
                + " -o " + partialVideo + " -H 0";

        // and either print the command to cmds, or execute it
        if ( System.getProperty("vcom.cmd.execute").equals("false") )
        { try { cmds.write(cmd + "\n"); } catch (IOException e) { } }
        else
        { Util.run(cmd); }

        return;
    }


    /**
     * This takes all the videos in the work-videos directory, and stitches
     * them into a single video in finalRoot
     */
    private void stitchVideo()
    {
        // store the names of the videos in a file
        try
        {
            // make a file to hold the file names
            BufferedWriter videoNames = new BufferedWriter(
                new FileWriter(workRootName + "/videonames"));


            // dunk the video names into a file
            Iterator i = partialVideos.iterator();
            while ( i.hasNext() )
            {
                // store the filename
                videoNames.write( (String)i.next()+"\n" );
            }

            // close the file
            videoNames.close();
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

        // convert the list to an array and run or print it
        String[] cmdArray = (String[]) cmd.toArray(new String[0]);
        System.out.println("stitching video");
        if ( System.getProperty("vcom.cmd.execute").equals("false") )
        {
            StringBuffer s = new StringBuffer("");
            for ( int i=0; i<cmdArray.length; i++)
            { s.append(cmdArray[i]+" "); }
            try { cmds.write(s + "\n"); } catch (IOException e) { };
        }
        else
        { Util.run(cmdArray); }
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
