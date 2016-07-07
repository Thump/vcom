
/*****************************************************************************
 *
 * VComFrames: video compositor
 *
 * source file: VComRender.java
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

import org.jdom.*;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;

import org.jdom.input.SAXBuilder;

import java.io.OutputStream;
import java.io.FileOutputStream;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.JellyException;

/**
 * This program takes a per-frame xml file and generates the corresponding
 * video.
 */
public class VComRender
{
    static String workRootName = null;
    static String finalRootName = null;

    public static void main(String[] args)
    {
        try
        {
            Document doc = null;

            // load properties
            Util.loadProperties();

            // check we have enough arguments
            if ( args.length != 1 )
            { usage(); }

            // process the specified xml file and get the root element
            doc = Util.jellyReadDoc(args[0]);
            Element root = doc.getRootElement();

            // if the root is null, that's bad
            if ( root == null )
            {
                System.err.println("got null root for " + args[0]);
                System.exit(1);
            }

            // if the root element is <frames>, process the video
            if ( root.getName().equals("frames") )
            {
                // get the default frame renderer
                FrameRenderI fr = new LinuxFrameRender();
                fr.renderFrames(root);
                fr.printStats();

                // get the default sound renderer
                SoundRenderI sr = new LinuxSoundRender();
                sr.renderSounds(root);
                sr.printStats();

                // get the default video rendere
                VideoRenderI vr = new LinuxVideoRender();
                vr.renderVideo(root);
                vr.printStats();

                System.exit(0);
            }

            // if we get here, we didn't recognize the root element
            System.err.println("unrecognized root: " + root.getName());
            System.exit(1);
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
        System.err.println("error: must provide argument\n");
        System.err.println("vcom <vcom.xml>\n");
        System.err.println("  where <vcom.xml> is the vcom xml file\n");
        System.err.println("\n");
        System.exit(-1);
    }
}
