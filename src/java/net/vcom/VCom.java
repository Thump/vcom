
/**************************************************************************
 *
 * VCom: video compositor
 *
 * source file: VCom.java
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
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;

import org.jdom.*;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;

import org.jdom.input.SAXBuilder;

import java.io.OutputStream;
import java.io.FileOutputStream;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.JellyException;

import gnu.getopt.Getopt;

/**
 * This program takes a per-frame xml file and generates the corresponding
 * video.
 */
public class VCom
{
    // some global variables
    static Boolean process = true;
    static Boolean render = true;
    static Boolean encode = true;

    static Boolean renderFrames = true;
    static Boolean renderSound = true;
    static Boolean encodeVideo = true;

    static Boolean generateCommands = false;


    // the mainline
    public static void main(String[] args)
    {
        Element videoElement = null;
        Document videoDoc = null;

        try
        {
            // load properties: this is a utility function provided by me,
            // it sets some defaults, then reads in vcom.properties
            Util.loadProperties();

            // process the arguments with getopt
            Getopt g = new Getopt("vcom", args, "prefsc");
            int c;
            String arg;
            while ((c = g.getopt()) != -1)
            {
                switch(c)
                {
                    // process only
                    case 'p':
                        process=true;
                        render=false;
                        encode=false;
                        break;

                    // render only
                    case 'r':
                        process=false;
                        render=true;
                        encode=false;
                        break;

                    // encode only
                    case 'e':
                        process=false;
                        render=false;
                        encode=true;
                        break;

                    // act on frames only
                    case 'f':
                        renderFrames=true;
                        renderSound=false;
                        break;

                    // act on sound only
                    case 's':
                        renderFrames=false;
                        renderSound=true;
                        break;

                    // If -c is passed, we do not execute commands, we
                    // just print them into the file <workdir>/cmds.
                    // Because of dependencies between rendering and
                    // encoding, you can only use with -c with either
                    // -r or -e (ie. you can only generate the commands to
                    // render or encode, but not both).
                    case 'c':
                        if ( render && encode )
                        {
                            System.err.println(
                                "Cannot generate commands for both rendering"
                                + "and encoding:\n"
                                + "use -c with either -r or -v");
                            usage();
                            System.exit(1);
                        }

                        System.out.println("command execution disabled");
                        System.out.println(
                            "commands written to <work>/cmds");
                        System.setProperty("vcom.cmd.execute", "false");
                        break;

                    default:
                }
            }

            // check we have enough arguments
            if ( args.length < 1 )
            { usage(); }

            // process the specified xml file and get the root element
            videoDoc = Util.jellyReadDoc(args[g.getOptind()]);
            videoElement = videoDoc.getRootElement();

            // if the root is null, that's bad
            if ( videoElement == null )
            {
                System.err.println("got null root for " + args[0]);
                System.exit(1);
            }

            // check the name of the root element: should be <video>
            if ( ! videoElement.getName().equals("video") )
            {
                // we didn't recognize the root element
                System.err.println(
                    "unrecognized root: " + videoElement.getName());
                System.exit(1);
            }

            // ok, it seems we have a valid video xml file, yay!

            // if we're processing the file, pass in the doc, as that's
            // it uses
            if ( process ) { process(videoDoc); }

            // if we're either rendering or encoding, we need to turn
            // the videoElement into a framesElement
            Element framesElement = null;
            if ( render || encode )
            { framesElement=Util.prepareFramesElement(videoElement); }

            // if we're rendering the file, pass in the element, since
            // that's most convenient for it
            if ( render ) { render(framesElement); }

            // if we're encoding the file, pass in the element, since
            // that's most convenient for it
            if ( encode ) { encode(framesElement); }
        }
        catch (Exception e)
        {
            System.out.println("\nException in mainline:\n"
                + e.getClass().getName() + "\n"
                + e.getMessage() + "\n" );
            e.printStackTrace();
        }
    }


    /**
     * This processes a video document: the processing is based on the
     * video.xml file, so we directly process doc.
     */
    public static void process(Document videoDoc)
    {
        System.out.println("processing video document");

        // attempt to create a video object from the document
        Video v = new Video(videoDoc);

        // validate it
        v.valid();

        // process the video document
        v.process();
    }


    /**
     * This renders the frames and sound files: it actually only uses
     * the video.xml file to locate the frames.xml file (should be in
     * the root of the work directory).
     */
    public static void render(Element framesElement)
    {
        // ok, in theory we have the frames file, let's get down to it
        try
        {
            // get the default frame renderer
            if ( renderFrames )
            {
                System.out.println("rendering frames");

                // we default to using net.vcom.RenderFrames-imagemagick,
                // unless overridden by the property
                String frameRenderClass ="net.vcom.RenderFrames_imagemagick";
                if ( System.getProperty("vcom.render.frame.class") != null )
                {
                    frameRenderClass =
                        System.getProperty("vcom.render.frame.class");
                }

                // use reflection to instantiate the requisite class
                System.out.println("instantiating " + frameRenderClass +
                    " to render frames");
                RenderFramesI fr =
                    (RenderFramesI)
                        Class.forName(frameRenderClass).newInstance();

                // now that we have the class, we use it
                fr.renderFrames(framesElement);
                fr.printStats();
            }

            // get the default sound renderer
            if ( renderSound )
            {
                System.out.println("rendering sound");
                RenderSoundI sr = new RenderSound_ecasound_lame();
                sr.renderSounds(framesElement);
                sr.printStats();
            }
        }
        catch (Exception e)
        {
            System.out.println("\nException in mainline:\n"
                + e.getClass().getName() + "\n"
                + e.getMessage() + "\n" );
            e.printStackTrace();
        }
    }


    /**
     * This encodes the rendered frames and sound into a video.  It
     * actually only uses the video.xml file to locate the frames.xml
     * file (should be in the root of the work directory).
     */
    public static void encode(Element framesElement)
    {
        // ok, in theory we have the frames file, let's get down to it
        try
        {
            // get the default video rendere
            System.out.println("rendering video");
            RenderVideoI vr = new RenderVideo_transcode();
            vr.renderVideo(framesElement);
            vr.printStats();
        }
        catch (Exception e)
        {
            System.out.println("\nException in mainline:\n"
                + e.getClass().getName() + "\n"
                + e.getMessage() + "\n" );
            e.printStackTrace();
        }
    }


    /**
     * This method just prints out a usage string to stderr.
     */
    private static void usage()
    {
        System.err.println( "\n"
+ "vcom [-p | -r | -e] [-f | -s | -v] [<vcom.xml>\n"
+ "  where <vcom.xml> is the vcom xml file\n"
+ "  -p: means process only, do not render or encode\n"
+ "  -r: means render only, assume processing has already been performed\n"
+ "  -e: means encode only, assume rendering has already been performed\n"
+ "  -f: means render frames only, not sound or video\n"
+ "  -s: means render sound only, not frames or video\n"
+ "  -c: means don't execute commands to render or encode, just print them\n"
+ "      this is useful for generating commands that will be later execute\n"
+ "      in parallel\n"
+ "\n");

        System.exit(-1);
    }
}
