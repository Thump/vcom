
/*****************************************************************************
 *
 * VComFrames: video compositor
 *
 * source file: LinuxFrameRender.java
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
public class LinuxFrameRender implements FrameRenderI
{
    // these are some variables to hold some video-wide values
    private String workRootName = null;
    private String finalRootName = null;
    private int xSize = 0;
    private int ySize = 0;
    private String bgColor = null;

    // these track the stats for rendering images
    public int frameCount = 0;
    public int imageCount = 0;
    public int skippedImageCount = 0;
    private long renderStartTime = 0;
    private long renderStopTime = 0;

    /**
     * This processes a frames document
     */
    public void renderFrames(Element root)
    {
        // initialize our stats
        frameCount = 0;
        imageCount = 0;
        skippedImageCount = 0;

        // get start time
        renderStartTime = System.currentTimeMillis();

        // validate that the frames root we got is correct
        Util.validateFrames(root);

        // parse out some atts and children
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
        Util.mkdir(workRootName + "/work-images");
        Util.mkdir(workRootName + "/final-images");
        Util.mkdir(finalRootName);

        // now, step through and process each frame
        Iterator i = frameChildren.iterator();
        while ( i.hasNext() )
        {
            Element frame = (Element) i.next();
            processFrame(frame);
        }

        // get start time
        renderStopTime = System.currentTimeMillis();
    }


    /**
     * This generates the frame for a specific <frame> tag
     */
    private void processFrame(Element frame)
    {
        // track the number of frames rendered
        frameCount++;

        // the root element must be frame
        if ( ! frame.getName().equals("frame") )
        {
            System.err.println("tag name of frame must be <frame>, not "
                + frame.getName() );
            System.exit(1);
        }

        // must have a single frame number attribute
        int frameNumber = Integer.parseInt(frame.getAttributeValue("number"));
        if ( frameNumber < 0 )
        {
            System.err.println("frame number must be set in frame");
            System.exit(1);
        }

        // the only valid children are images, so the number of children
        // must equal the number image children
        List allChildren = frame.getChildren();
        List imageChildren = frame.getChildren("image");
        if ( allChildren.size() != imageChildren.size() )
        {
            System.err.println("non-image children of frame");
            System.exit(1);
        }

        // Ok, we now process the images
        processImages(frameNumber, allChildren);
    }


    /**
     * This process the images for the specified frame
     */
    private void processImages(int frameNumber, List images)
    {
        // if there are no image children, print an error, since this
        // shouldn't happen
        if ( images.size() < 1 )
        {
            System.err.println("no images for frame number " + frameNumber);
            System.exit(1);
        }

        // this is the prefix for the working image
        String workPrefix = new String( workRootName
            + "/work-images/work-frame" + Util.padLeft(frameNumber) + "-image");

        // make the final name
        String finalFrameName = new String( workRootName
            + "/final-images/final-frame" + Util.padLeft(frameNumber) + ".jpg");

        // get the first image for this frame
        Iterator i = images.iterator();
        Element first = (Element) i.next();

        // get the date from this first image
        long firstDate = Long.parseLong(first.getAttributeValue("date"));

        //for the first image, we composite it with itself
        composite(
            compositeArgs(first),
            compositeFile(first),
            "-size " + xSize + "x" + ySize,
            "xc:" + bgColor,
            workPrefix + Util.padLeft(4,"0") + ".png",
            firstDate);

        // for all subsequent images, we composite the next image with
        // the current work image
        int imageStage = 0;
        while ( i.hasNext() )
        {
            // get the image element
            Element image = (Element) i.next();

            // get the date from the image element
            long imageDate = Long.parseLong(image.getAttributeValue("date"));

            composite(
                compositeArgs(image),
                compositeFile(image),
                "",
                workPrefix + Util.padLeft(4,imageStage) + ".png",
                workPrefix + Util.padLeft(4,++imageStage) + ".png",
                imageDate );
        }

        // when we're all done, copy the final work image to the final image
        //Util.cp(
        //    workPrefix + Util.padLeft(4,imageStage) + ".png",
        //    finalFrameName);

        // well, we would just copy it, except transcode's xvid encoding
        // gets all wonky with images that have alpha channels, so as a
        // final step, we convert to jpg.  bleh.
        File tgt = new File(finalFrameName);
        File src = new File(workPrefix + Util.padLeft(4,imageStage) + ".png");
        if ( tgt.lastModified() < src.lastModified() )
        {
            Util.run("convert "
                + workPrefix + Util.padLeft(4,imageStage) + ".png "
                + finalFrameName);
        }
    }


    /**
     * This takes two composite file strings, and runs them through the
     * composite command, leaving the result in workRootName+"work-"+frameNumber
     */
    private void composite(
        String layerArgs, String layerFile, String baseArgs, String baseFile,
        String targetFile, long imageDate)
    {
        // track the number of images
        imageCount++;

        // create the command
        String cmd = new String(
            "composite "
            + layerArgs + " "
            + layerFile + " "
            + baseArgs + " "
            + baseFile + " "
            + targetFile);

        // if the target image exists, and it is newer than the
        // imageDate, the layer image, and the work image, then we
        // don't need to recreate it

        // get a File for the target, the work image, and the layer
        File targetF = new File(targetFile);
        File layerF = new File(layerFile);
        File baseF = new File(baseFile);

        // if the target exists, then we have the potential to skip making it
        if ( targetF.exists() )
        {
            // get the last modified date
            long lastModified = targetF.lastModified();

            // if it's newer (ie. its lastModified() date is bigger than
            // that of the layer, base, or image
            if ( lastModified >= layerF.lastModified() &&
                 lastModified >= baseF.lastModified() &&
                 lastModified >= imageDate )
            {
                // track the number of images skipped
                skippedImageCount++;
                return;
            }
        }

        // run the command
        Util.run(cmd);
    }


    /**
     * This takes an image element and returns the composite-ready
     * arguments to use
     */
    private String compositeArgs(Element image)
    {
        // the root element must be image
        if ( ! image.getName().equals("image") )
        {
            System.err.println("tag name of image must be <image>, not "
                + image.getName() );
            System.exit(1);
        }

        StringBuffer s = new StringBuffer();

        // image must have an xposition and yposition
        String xposition = image.getAttributeValue("xposition");
        String yposition = image.getAttributeValue("yposition");
        if ( xposition == null || yposition == null )
        {
            System.err.println("image must have xposition and yposition name");
            System.exit(1);
        }
        s.append("-geometry +" + xposition + "+" + yposition + " ");

        // image must have a rotation
        String rotation = image.getAttributeValue("rotation");
        if ( rotation == null )
        {
            System.err.println("image must have rotation");
            System.exit(1);
        }
        s.append("-rotate " + rotation + " ");

        // image must have an opacity
        String opacity = image.getAttributeValue("opacity");
        if ( opacity == null )
        {
            System.err.println("image must have opacity");
            System.exit(1);
        }
        s.append("-dissolve " + opacity + " ");

        return s.toString();
    }


    /**
     * This takes an image element and returns the composite-ready
     * filename to use
     */
    private String compositeFile(Element image)
    {
        // the root element must be image
        if ( ! image.getName().equals("image") )
        {
            System.err.println("tag name of image must be <image>, not "
                + image.getName() );
            System.exit(1);
        }

        // image must have a file name
        String file = image.getAttributeValue("file");
        if ( file == null )
        {
            System.err.println("image must have file name");
            System.exit(1);
        }

        return file;
    }


    /**
     * This prints some stats
     */
    public void printStats()
    {
        long renderDur = (renderStopTime - renderStartTime)/1000;

        // if durations are less then 1, make them 1
        if ( renderDur < 1 ) { renderDur = 1; }

        double imageRenderRate = (imageCount - skippedImageCount)/renderDur;
        double frameRenderRate = frameCount/renderDur;

        // print the stats
        System.out.println("frame count: " + frameCount);
        System.out.println("image count: " + imageCount);
        System.out.println("images skipped: " + skippedImageCount);
        System.out.println("images / frame: "
            + (double)(imageCount/frameCount) );
        System.out.println("image render time: " + renderDur + " seconds");
        System.out.println("image render rate: "
            + imageRenderRate + " images/second");
        System.out.println("frame render rate (effective): "
            + frameRenderRate + " frames/second");
        System.out.println("");
    }
}
