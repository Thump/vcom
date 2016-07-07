
/*****************************************************************************
 *
 * VCom: video compositor
 *
 * source file: VComProcess.java
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
import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Properties;

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
 * This program takes a video xml file, describing a series of video and
 * sound clips to be composited and translates it into a per-frame xml file.
 */
public class VComProcess
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

            // if the root element is <video>, process the video
            if ( root.getName().equals("video") )
            {
                processVideo(doc);
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
     * This processes a video document
     */
    public static void processVideo(Document doc)
    {
        System.out.println("processing video document");

        // attempt to create a video object from the document
        Video v = new Video(doc);

        // validate it
        v.valid();

        // process the video document
        v.process();
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
