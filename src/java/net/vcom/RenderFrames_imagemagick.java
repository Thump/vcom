
/**************************************************************************
 *
 * VComFrames: video compositor
 *
 * source file: RenderFrames_imagemagick.java
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
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
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
 * frames using Image Magick.
 */
public class RenderFrames_imagemagick implements RenderFramesI
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

    // this is the max frame we render: we initialize it below
    private long maxRenderFrame = 0;

    // the output command file
    private BufferedWriter cmds = null;


    /**
     * This processes a frames document
     */
    public void renderFrames(Element root)
    {
        // initialize our stats
        frameCount = 0;
        imageCount = 0;
        skippedImageCount = 0;

        System.out.println("rendering frames with "
            + this.getClass().getName() );

        // get start time
        renderStartTime = System.currentTimeMillis();

        // validate that the frames root we got is correct
        Util.validateFrames(root);

        // parse out some atts and children
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

        finalRootName = "./final";
        if ( root.getAttributeValue("finalroot") != null )
        { finalRootName = root.getAttributeValue("finalroot"); }

        // open the command file if needed
        try
        {
            if ( System.getProperty("vcom.cmd.execute").equals("false") )
            {
                cmds = new BufferedWriter(
                    new FileWriter(workRootName + "/renderframes-cmds"));
            }
        }
        catch (IOException e) { };

        // check for max render frame: if it's set via a property, we
        // use that, otherwise it was initialized to a v large number
        maxRenderFrame = frameChildren.size();
        if ( System.getProperty("vcom.render.frame.maxframe") != null )
        {
            maxRenderFrame = Integer.parseInt(
                System.getProperty("vcom.render.frame.maxframe"));
        }
        System.out.println("setting max render frame to " + maxRenderFrame);

        // create the directories
        Util.mkdir(workRootName + "/work-images");
        Util.mkdir(workRootName + "/final-images");
        Util.mkdir(finalRootName);

        // now, step through and process each frame
        Iterator i = frameChildren.iterator();
        while ( i.hasNext() && frameCount <= maxRenderFrame )
        {
            Element frame = (Element) i.next();
            System.out.print(
                "frame " + frameCount + " / " + frameChildren.size() + "\r");
            processFrame(frame);
        }

        // get stop time
        renderStopTime = System.currentTimeMillis();

        // if we're printing out the commands, close the file
        try
        {
            if ( System.getProperty("vcom.cmd.execute").equals("false") )
            { cmds.close(); }
        } catch (IOException e) { };
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
        int frameNumber =
            Integer.parseInt(frame.getAttributeValue("number"));
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
        if ( System.getProperty("vcom.cmd.execute").equals("false") )
        { try { cmds.write("# batch start\n"); } catch (IOException e) { } }
        processImages(frameNumber, allChildren);
        if ( System.getProperty("vcom.cmd.execute").equals("false") )
        { try { cmds.write("# batch end\n"); } catch (IOException e) { } }
    }


    /**
     * This processes the images for the specified frame
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
            + "/work-images/work-frame" + Util.padLeft(frameNumber)
            + "-image");

        // make the image suffix png by default, but can be overridden
        String imageSuffix = "png";
        if ( System.getProperty("vcom.render.frame.type") != null )
        { imageSuffix = System.getProperty("vcom.render.frame.type"); }

        // compose the final frame name
        String finalFrameName = new String( workRootName
                + "/final-images/final-frame" + Util.padLeft(frameNumber)
                + "." + imageSuffix );

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

        // hmm, I think it's working now, as I seem to have switched back
        // to png without problems, but I'm still doing convert: stupid
        // Denis!

        // compose the file names
        File tgt = new File(finalFrameName);
        File src = new File(
            workPrefix + Util.padLeft(4,imageStage) + "." + imageSuffix);

        if (    ! tgt.exists()
             || ! src.exists()
             || tgt.lastModified() < src.lastModified() )
        {
            // compose the command
            String cmd = new String("convert " + src + " " + tgt + "\n");
            
            // if vcom.cmd.execute is false, we print the commands
            if ( System.getProperty("vcom.cmd.execute").equals("false") )
            { 
                try
                { cmds.write(cmd); }
                catch (IOException e) { }
            }
            // otherwise execute them
            else
            { Util.run(cmd); }
        }
    }


    /**
     * This takes two composite file strings, and runs them through the
     * composite command, leaving the result in
     * workRootName+"work-"+frameNumber
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

        // run the command if execute is not false, otherwise print it
        if ( System.getProperty("vcom.cmd.execute").equals("false") )
        { try { cmds.write(cmd+"\n"); } catch (IOException e) { } }
        else
        { Util.run(cmd); }
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

        // get image's xposition and yposition
        String xposition="0";
        if ( image.getAttributeValue("xposition") != null )
        { xposition = image.getAttributeValue("xposition"); }

        String yposition="0";
        if ( image.getAttributeValue("yposition") != null )
        { yposition = image.getAttributeValue("yposition"); }
        s.append("-geometry +" + xposition + "+" + yposition + " ");

        // get image's rotation
        String rotation = "0";
        if ( image.getAttributeValue("rotation") != null )
        { rotation = image.getAttributeValue("rotation"); }
        s.append("-rotate " + rotation + " ");

        // get image's opacity
        String opacity = "100";
        if ( image.getAttributeValue("opacity") != null )
        { opacity = image.getAttributeValue("opacity"); }
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
