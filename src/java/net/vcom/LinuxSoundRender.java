
/*****************************************************************************
 *
 * VComFrames: video compositor
 *
 * source file: LinuxSoundRender.java
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
import java.io.FileWriter;
import java.io.BufferedWriter;
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
 * sound clips.
 */
public class LinuxSoundRender implements SoundRenderI
{
    // these are some variables to hold some video-wide values
    private String workRootName = null;
    private String finalRootName = null;

    // these track the stats for encoding videos
    public int soundCount = 0;
    public int skippedSoundCount = 0;
    private long renderStartTime = 0;
    private long renderStopTime = 0;


    /**
     * This reads the sounds from the work dir, and makes a video
     * from them.
     */
    public void renderSounds(Element root)
    {
        // reset the stats
        soundCount = 0;
        skippedSoundCount = 0;

        // track how much time we spend encoding
        renderStartTime = System.currentTimeMillis();

        // validate the frames document
        Util.validateFrames(root);

        // parse out the atts and children
        List soundChildren = root.getChildren("sound");

        // parse out the final and work directory from the frames attributes
        workRootName = root.getAttributeValue("workroot");
        finalRootName = root.getAttributeValue("finalroot");

        // default the workRootName to ./work and finalRootName to ./final
        if ( workRootName == null )
        { workRootName = "./work"; }
        if ( finalRootName == null )
        { finalRootName = "./final"; }

        // create the directories
        Util.mkdir(workRootName + "/work-sounds");
        Util.mkdir(workRootName + "/final-sound");
        Util.mkdir(finalRootName);

        // generate the .ewf files for the sounds
        makeEWF(soundChildren);

        // record the stop time
        renderStopTime = System.currentTimeMillis();
    }


    /**
     * This takes a list of sound tags and generates .ewf files for each
     */
    private void makeEWF(List soundChildren)
    {
        Iterator i = soundChildren.iterator();

        // this will hold the ecasound chainsetup
        StringBuffer chain = new StringBuffer();
        String chainName = workRootName+"/work-sounds/"+"chain.ecs";

        while ( i.hasNext() )
        {
            // get the element
            Element elem = (Element) i.next();

            // get the attributes
            String fileName = elem.getAttributeValue("file");
            String srcStart = elem.getAttributeValue("srcstart");
            String srcStop = elem.getAttributeValue("srcstop");
            String tgtStart = elem.getAttributeValue("tgtstart");

            // compute a length from the srcStart and srcStop
            double start = Double.parseDouble(srcStart);
            double stop = Double.parseDouble(srcStop);
            String length = String.valueOf(stop - start);

            // get a file on the source
            File src = new File(fileName);

            // if we can't find that src, that's bad
            if ( ! src.exists() || ! src.canRead() )
            {
                System.out.print("can't read " + fileName);
                System.exit(1);
            }

            // the name of the ewf file to create
            String ewfName = workRootName + "/work-sounds/" + src.getName() + "-" + soundCount + ".ewf";

            // write the ewf file
            try
            {
                // create the file
                BufferedWriter ewf =
                    new BufferedWriter(new FileWriter(ewfName));

                // write it
                ewf.write("-- " + fileName + " --\n");
                ewf.write("source = " + fileName + "\n");
                ewf.write("offset = " + tgtStart + "\n");
                ewf.write("start-position = " + srcStart + "\n");
                ewf.write("length = " + length + "\n");
                ewf.write("looping = false\n");

                // close it
                ewf.close();
            }
            catch (IOException e)
            {}

            // keep track of how many we process
            soundCount++;

            // add to the chain setup
            chain.append(" -a:" + soundCount + " -i " + ewfName);
        }
        

        // write out the chain file
        try
        {
            BufferedWriter chainWriter =
                new BufferedWriter(new FileWriter(chainName));
            chainWriter.write(chain.toString());
            chainWriter.write(
                " -a:all -o " + workRootName + "/final-sound/final-sound.wav");
            chainWriter.close();
        }
        catch(IOException e)
        {}

        // finally, run ecasound on the chain file
        Util.run("ecasound-new -z:mixmode,sum -s:" + chainName);
        Util.run("lame "
            + workRootName + "/final-sound/final-sound.wav "
            + workRootName + "/final-sound/final-sound.mp3"
            );
    }


    /**
     * This prints some stats
     */
    public void printStats()
    {
        long renderDur = (renderStopTime - renderStartTime)/1000;

        // if durations are less then 1, make them 1
        if ( renderDur < 1 ) { renderDur = 1; }

        // print the stats
        System.out.println("sound count: " + soundCount);
        System.out.println("sounds skipped: " + skippedSoundCount);
        System.out.println("encode time: " + renderDur + " seconds");
        System.out.println("");
    }
}
