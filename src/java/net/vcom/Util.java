
/*****************************************************************************
 *
 * VCom: video compositor
 *
 * source file: Util.java
 * package: net.vcom
 *
 * version 0.3
 * 2005-06-01
 * Copyright (c) 2005, Denis McLaughlin
 * Released under the GPL license, version 2
 *
 *****************************************************************************/

package net.vcom;

import java.io.IOException;
import java.io.CharArrayWriter;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
 * This class contains a number of static utility methods used in other
 * classes.
 */
public class Util
{
    /**
     * This method takes a vcom xml file name as an argument and reads
     * the xml, returning the data in a Document.  As part of reading
     * the xml, it is filtered through a jelly processor.  The method
     * calls the jelly runScript() to produce the jelly-processed
     * xml into a char array, and then processes the xml in
     * the char array via jdom, returning the resulting Document.
     */
    public static Document jellyReadDoc(String fileName)
    {
        // check that we can see the file
        File file = new File(fileName);
        if ( ! file.canRead() )
        {
            System.out.println("unable to read " + fileName);
            System.exit(1);
        }

        return jellyReadDoc(file);
    }


    /**
     * This method takes a vcom xml file name as an argument and reads
     * the xml, returning the data in a Document.  As part of reading
     * the xml, it is filtered through a jelly processor.  The method
     * calls the jelly runScript() to produce the jelly-processed
     * xml into a char array, and then processes the xml in
     * the char array via jdom, returning the resulting Document.
     */
     public static Document jellyReadDoc(File file)
     {
        Document doc = null;
        SAXBuilder builder = new SAXBuilder();

        try
        {
            CharArrayWriter output = new CharArrayWriter();
            XMLOutput xmlOutput = XMLOutput.createXMLOutput(output);

            // process the xml in the file using jelly into a char array
            JellyContext context = new JellyContext();
            context.runScript(file, xmlOutput);
            xmlOutput.flush();

            // if we want to debug the jelly output, we write it out now
            if ( System.getProperty("vcom.jelly.debug") != null &&
                 System.getProperty("vcom.jelly.debug").equals("true") )
            {
                FileWriter debug = new FileWriter(file.getName() + ".jelly");
                output.writeTo(debug);
                debug.close();
            }


            // process the xml in the char array into a Document
            doc = builder.build(new CharArrayReader(output.toCharArray()));
        }
        // indicates a well-formedness error
        catch (JDOMException e)
        { 
            System.out.println(file.getName() + " is not well-formed.");
            System.out.println(e.getMessage());
            System.exit(1);
        }  
        catch (IOException e)
        { 
            System.out.println("Could not check " + file.getName());
            System.out.println(" because " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        catch (JellyException e)
        {
            System.out.println("Jelly error: " + e);
            e.printStackTrace();
            System.exit(1);
        }
        catch(Exception e)
        {
            System.out.println("general exception: " + e);
            e.printStackTrace();
            System.exit(1);
        }

        return doc;
    }


    /**
     * This is mostly a utility function: it reads an xml file without
     * first filtering it through jelly.
     */
    public static Document jdomReadDoc(String file)
    {
        Document doc = null;
        SAXBuilder builder = new SAXBuilder();

        // command line should offer URIs or file names
        try
        {
            doc = builder.build(file);
            // If there are no well-formedness errors, 
            // then no exception is thrown
            System.out.println(file + " is well-formed.");
        }
        // indicates a well-formedness error
        catch (JDOMException e)
        { 
            System.out.println(file + " is not well-formed.");
            System.out.println(e.getMessage());
        }  
        catch (IOException e)
        { 
            System.out.println("Could not check " + file);
            System.out.println(" because " + e.getMessage());
        }  

        return doc;
    }


    /**
     * This pretty prints a Document to the specified file
     */
    public static void print(Document doc, String file)
    {
        try
        {
            // open the file
            FileOutputStream f = new FileOutputStream(file);

            // print it to stdout
            XMLOutputter serializer = new XMLOutputter();
            serializer.setFormat(Format.getPrettyFormat());
            serializer.output(doc, f);
        }
        catch (Exception e)
        {
            System.out.println("Exception in print: " + e);
            e.printStackTrace();
        }
    }


    /**
     * This utility takes an int and a string and returns the string left
     * padded with zeroes such that the overall string is int characters long.
     * If the passed string is longer then 6 characters already, then it
     * is returned.
     */
    public static String padLeft(int i, String string)
    {
        StringBuffer s = new StringBuffer(i);

        // otherwise append zeroes to s until the aggregate length is 6
        while ( (s.length() + string.length()) < i )
        {
            s.append("0");
        }

        return(s.toString()+string);
    }


    /**
     * This utility takes a String and returns it as a String left padded
     * with zeroes such that the overall string is 6 characters long.
     */
    public static String padLeft(String s)
    {
        return padLeft(6, s);
    }


    /**
     * This utility takes an int and returns it as a String left padded
     * with zeroes such that the overall string is 6 characters long.
     * If the passed string is longer then 6 characters already, then it
     * is returned.
     */
    public static String padLeft(int i)
    {
        return padLeft(6, String.valueOf(i));
    }


    /**
     * This utility takes an int length and an int value, and returns the
     * value as a String left padded with zeroes such that the overall
     * string is length characters long.
     */
    public static String padLeft(int length, int i)
    {
        return padLeft(length, String.valueOf(i));
    }


    /**
     * This utility method copies one file to another
     */
    public static void cp(String src, String tgt)
    {
        try
        {
            // if the tgt exists and is newer than the src, we skip the copy
            File srcF = new File(src);
            File tgtF = new File(tgt);
            if ( tgtF.exists() && (tgtF.lastModified() >= srcF.lastModified()) )
            { return; }

            // get streams for the files
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(tgt);
    
            // copy one to the other
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0)
            { out.write(buf, 0, len); }
            in.close();
            out.close();
        }
        catch(FileNotFoundException e)
        {
            System.out.println("file copy: can't find " + src);
            System.exit(1);
        }
        catch(IOException e)
        {
            System.out.println("file copy: io exception:");
            e.printStackTrace();
            System.exit(1);
        }
    }


    /**
     * This utility routine takes a string and attempts to create it
     * as a directory
     */
    public static void mkdir(String dirName)
    {
        File dir = new File(dirName);
        if ( dir.exists() )
        {
            if ( ! dir.isDirectory() )
            {
                System.out.println(
                    dirName + " already exists, but isn't a directory");
                System.exit(1);
            }
        }
        else
        {
            dir.mkdirs();
        }
    }


    /**
     * This utility routine takes a command string and runs it
     */
    public static void run(String cmd)
    {
        // Execute the command and then wait for it to complete
        try
        {
            if ( System.getProperty("vcom.cmd.debug") != null &&
                 System.getProperty("vcom.cmd.debug").equals("true") )
            {
                System.out.println("running: " + cmd);
            }

            Process child = Runtime.getRuntime().exec(cmd);
            child.waitFor();
        }
        catch (InterruptedException e)
        {
            System.out.println("running command -- interrupt exception:" + e);
            System.exit(1);
        }
        catch (IOException e)
        {
            System.out.println("running command -- io exception:" + e);
            System.exit(1);
        }
    }


    /**
     * This utility routine takes a command string array and runs it
     */
    public static void run(String[] cmd)
    {
        // Execute the command and then wait for it to complete
        try
        {
            if ( System.getProperty("vcom.cmd.debug") != null &&
                 System.getProperty("vcom.cmd.debug").equals("true") )
            {
                StringBuffer s = new StringBuffer("running: ");
                for ( int i=0; i<cmd.length; i++)
                { s.append(cmd[i]+" "); }
                System.out.println(s);
            }

            Process child = Runtime.getRuntime().exec(cmd);
            child.waitFor();
        }
        catch (InterruptedException e)
        {
            System.out.println("running command -- interrupt exception:" + e);
            System.exit(1);
        }
        catch (IOException e)
        {
            System.out.println("running command -- io exception:" + e);
            System.exit(1);
        }
    }


    /**
     * This initializes the default properties, and then loads properties
     * from ./vcom.properties.
     */
    public static void loadProperties()
    {
        // set some default properties
        System.setProperty("vcom.cmd.debug", "false");

        try
        {
            // load up our system properties: ./vcom.properties
            System.getProperties();
            Properties p = new Properties(System.getProperties());
            p.load(new FileInputStream("vcom.properties"));
            System.setProperties(p);
        }
        // we don't care about this
        catch (FileNotFoundException e)
        {}
        catch (IOException e)
        {
            System.out.println("\nException in Util:\n"
                + e.getClass().getName() + "\n"
                + e.getMessage() + "\n" );
            e.printStackTrace();
        }
    }


    /**
     * This routine validates a <frames> document
     */
    public static void validateFrames(Element root)
    {
        // the root element must be frames
        if ( ! root.getName().equals("frames") )
        {
            System.err.println("tag name of frames must be <frames>, not "
                + root.getName() );
            System.exit(1);
        }

        // the number of children should equal the number of <frame>
        // tags, since every child should be a frame
        // attempt to create a video object from the document
        List allChildren = root.getChildren();
        List frameChildren = root.getChildren("frame");
        List soundChildren = root.getChildren("sound");
        if (allChildren.size() != ( frameChildren.size()+soundChildren.size()))
        {
            System.err.println("non-sound/frame children of frames");
            System.exit(1);
        }
    }
}
