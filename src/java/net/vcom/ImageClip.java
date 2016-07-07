
/**************************************************************************
 *
 * VCom: video compositor
 *
 * source file: imageclip.java
 * package: net.vcom
 *
 * Copyright (c) 2005, Denis McLaughlin
 * Released under the GPL license, version 2
 *
 */

package net.vcom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.Iterator;
import java.io.File;
import java.lang.reflect.Array;

import org.apache.tools.ant.DirectoryScanner;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Attribute;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;


/**
 * This object models the imageclip tag.
 */
public class ImageClip
{
    /**
     * This is the list of files that act as the source of this sequence
     */
    private DirectoryScanner sourceFiles = null;

    /**
     * This is the set of parameters which define this image clip,
     * keyed by parameter name.
     */
    Map parameters = new HashMap();

    /**
     * This is source sequence: the order in which frames are to be
     * taken from the source files
     */
    private Seq sourceSeq = null;

    /**
     * This is the frame at which the image insertion should begin
     */
    private int targetStart = 0;


    /**
     * This is the default constructor
     */
    public ImageClip()
    {}


    /**
     * This is the Document constructor
     */
    public ImageClip(Document doc)
    {
        // check we don't have a null doc
        if ( doc == null )
        {
            throw new IllegalArgumentException(
                "ImageClip created from null document");
        }

        // get the list of the content
        List content = doc.getContent();

        // we better have only a single element to process
        if ( content.size() != 1 )
        {
            throw new IllegalArgumentException(
                "<imageclip> must have a single node");
        }

        // get the element from the document
        Element e = (Element) content.get(0);

        // the name of the element must be <imageclip>
        if ( ! e.getName().equals("imageclip") )
        {
            throw new IllegalArgumentException(
                "imageclip tag must be called <imageclip>");
        }

        // there are no valid attributes
        if ( e.getAttributes().size() > 0 )
        {
            throw new IllegalArgumentException(
                "imageclip tag cannot have attributes");
        }

        // for each child, we process it in the loop
        List children = e.getChildren();
        Iterator i = children.iterator();
        while ( i.hasNext() )
        {
            Element child = (Element) i.next();

            // sourceFiles
            if ( child.getName().equals("sourcefiles") )
            {
                setSourceFiles(child);
                continue;
            }

            // sourceseq
            if ( child.getName().equals("sourceseq") )
            {
                setSourceSeq(child);
                continue;
            }

            // targetSeq
            if ( child.getName().equals("targetseq") )
            {
                setTargetStart(child);
                continue;
            }

            // parameter
            if ( child.getName().equals("parameter") )
            {
                setParameter(child);
                continue;
            }

            // if we get here, then we don't recognize the child
            throw new IllegalArgumentException(
                "unrecognized child node in imageclip: " + child.getName());
        }
    }


    /**
     * This is getter for the sourceFiles
     */
    public DirectoryScanner getSourceFiles()
    {
        return sourceFiles;
    }

    /**
     * This is setter for the sourceFiles
     */
    public void setSourceFiles(DirectoryScanner scanner)
    {
        sourceFiles = scanner;
    }

    /**
     * This sets the value of sourceFiles based on an xml fragment such
     * as:
     *
     *  <sourcefiles dir="/tmp">
     *    <include name="*.png"/>
     *    <include name="*.jpg"/>
     *    <exclude name="*-junk.png"/>
     *    <exclude name="*-junk.jpg"/>
     *  </sourcefiles>
     *
     */
    public void setSourceFiles(Element e)
    {
        // get a new DirectoryScanner object, to initialize as we go
        DirectoryScanner scanner = new DirectoryScanner();

        // these will accumulate the include and exclude patterns
        Collection includes = new HashSet();
        Collection excludes = new HashSet();

        // fail if there isn't exactly one attribute
        List atts = e.getAttributes();
        if ( atts.size() != 1 )
        {
            throw new IllegalArgumentException(
                "<sourcefiles> must have one attribute called dir");
        }

        // fail if the attribute isn't dir
        Attribute att = (Attribute) atts.get(0);
        if ( ! att.getName().equals("dir") )
        {
            throw new IllegalArgumentException(
                "<sourcefiles> must have one attribute called dir, got: "
                + att.getName() );
        }

        // set the base dir in the scanner
        scanner.setBasedir(att.getValue());

        // fail if there aren't any children
        List children = e.getChildren();
        if ( children.size() == 0 )
        {
            throw new IllegalArgumentException(
                "<sourcefiles> must have at least one child");
        }

        // step through children, adding include or exclude patterns, as
        // required
        Iterator i = children.iterator();
        while ( i.hasNext() )
        {
            // get the child element
            Element child = (Element) i.next();

            // if it's an include child
            if ( child.getName().equals("include") )
            {
                // it has to have a single attribute
                List childAtts = child.getAttributes();
                if ( childAtts.size() != 1 )
                {
                    throw new IllegalArgumentException(
                        "<include> tag must have one attribute called name");
                }

                // the attribute has to be called name
                Attribute childAtt = (Attribute) childAtts.get(0);
                if ( ! childAtt.getName().equals("name") )
                {
                    throw new IllegalArgumentException(
                        "<include> tag must have name attribute, got: "
                        + childAtt.getName() );
                }

                // all seems well, add it to the list
                includes.add(childAtt.getValue());
            }

            // if it's an exclude child
            if ( child.getName().equals("exclude") )
            {
                // it has to have a single attribute
                List childAtts = child.getAttributes();
                if ( childAtts.size() != 1 )
                {
                    throw new IllegalArgumentException(
                        "<exclude> tag must have one attribute called name");
                }

                // the attribute has to be called name
                Attribute childAtt = (Attribute) childAtts.get(0);
                if ( ! childAtt.getName().equals("name") )
                {
                    throw new IllegalArgumentException(
                        "<exclude> tag must have name attribute, got: "
                        + childAtt.getName() );
                }

                // all seems well, add it to the list
                excludes.add(childAtt.getValue());
            }
            
            // if the child isn't an include or exclude, we throw an exception
            if ( ! child.getName().equals("include") &&
                 ! child.getName().equals("exclude") )
            {
                throw new IllegalArgumentException(
                    "<sourcefiles> children must be include or exclude, got: "
                    + child.getName() );
            }
        }

        // set the include and exclude patterns
        scanner.setIncludes( (String[]) includes.toArray(new String[]{}) );
        scanner.setExcludes( (String[]) excludes.toArray(new String[]{}) );

        // set the FileSet as the sourceFiles attribute
        setSourceFiles(scanner);
    }


    /**
     * This is getter for the sourceSeq
     */
    public Seq getSourceSeq()
    {
        return sourceSeq;
    }

    /**
     * This is setter for the sourceSeq
     */
    public void setSourceSeq(Seq s)
    {
        sourceSeq = s;
    }

    /**
     * This sets the value of sourceSeq based on an xml fragment such
     * as:
     *
     *  <sourceseq>
     *    <seq start="1" stop="5"/>
     *  </sourceseq>
     *
     */
    public void setSourceSeq(Element e)
    {
        // fail if there are any attributes
        List atts = e.getAttributes();
        if ( atts.size() != 0 )
        {
            throw new IllegalArgumentException(
                "<sourceseq> cannot have any attributes");
        }

        // fail if there isn't exactly one child
        List children = e.getChildren();
        if ( children.size() != 1 )
        {
            throw new IllegalArgumentException(
                "<sourceseq> must have one <seq> child");
        }

        // get the element, turn it into a document, create seq for it
        Element seq = (Element) children.get(0);
        Document d = new Document((Element) seq.clone());
        //setSourceSeq(new Seq(d));
        setSourceSeq(Seq.createSeq(d));
    }


    /**
     * This is getter for the targetStart
     */
    public int getTargetStart()
    {
        return targetStart;
    }

    /**
     * This is setter for the targetStart
     */
    public void setTargetStart(int s)
    {
        // if a negative value is set, throw an exception
        if ( s < 0 )
        {
            throw new IllegalArgumentException(
                "targetstart cannot be negative");
        }

        targetStart = s;
    }

    /**
     * This sets the value of targetStart based on an xml fragment such
     * as:
     *
     *  <targetseq start="#"/>
     *
     */
    public void setTargetStart(Element e)
    {
        // fail if there are any children
        if ( e.getChildren().size() > 0 )
        {
            throw new IllegalArgumentException(
                "<targetseq> must not have any children");
        }

        // fail if there isn't exactly one attribute
        List atts = e.getAttributes();
        if ( atts.size() != 1 )
        {
            throw new IllegalArgumentException(
                "<targetseq> must have a single attribute called start");
        }

        // fail if the attribute isn't called start
        Attribute att = (Attribute) atts.get(0);
        if ( ! att.getName().equals("start") )
        {
            throw new IllegalArgumentException(
                "<targetseq> must have a single attribute called start");
        }

        // set the value
        setTargetStart( Integer.parseInt(att.getValue()) );
    }


    /**
     * This is the getter for a parameter 
     */
    public Parameter getParameter(String name)
    {
        return (Parameter)parameters.get(name);
    }

    /**
     * This is the setter for a parameter
     */
    public void setParameter(String name, Parameter p)
    {
        parameters.put(name,p);
    }

    /**
     * This sets a parameter based on an xml fragment such
     * as:
     *
     *  <parameter name="foo">
     *    <seq start="1" stop="5"/>
     *  </parameter>
     *
     */
    public void setParameter(Element e)
    {
        // turn the element into a document, and create a parameter from it
        Document d = new Document((Element) e.clone());
        Parameter p = new Parameter(d);

        // get the name of the parameter, and push it onto the parameters
        // map keyed on that name
        setParameter(p.getName(),p);
    }


    /**
     * This returns true if the current configuration of this ImageClip
     * is valid, or throws an exception otherwise.  It checks:
     *  - that the sourceFiles FileSet is not null
     */
    public boolean valid()
    {
        // check that the sourceFiles is not null
        if ( sourceFiles == null )
        {
            throw new IllegalArgumentException("sourcefiles must be set");
        }

        // do our best to only scan if we haven't already scanned:
        // getIncludedFiles() throws null pointer exceptions if the scan
        // isn't already done, so this is ugly
        try
        {
            if ( sourceFiles.getIncludedFiles() == null )
            {
                sourceFiles.scan();
            }
        }
        catch(NullPointerException e)
        {
            sourceFiles.scan();
        }

        // check that the sourceSeq is not null
        if ( sourceSeq == null )
        {
            throw new IllegalArgumentException("sourceSeq must be set");
        }
        else
        { sourceSeq.valid(); }

        // and step through the list of parameters, making sure they're
        // all valid too
        Set names = parameters.keySet();
        Iterator i = names.iterator();
        Parameter p = null;
        while ( i.hasNext() )
        {
            p = (Parameter) parameters.get(i.next());
            p.valid();
        }

        return true;
    }


    /**
     * The length of an image clip is the length of the sourceseq, that is,
     * the number of images to be pulled from the source and inserted into
     * the target.  So we compute the length by just computing the length
     * of the sourceSeq.
     */
    public int getLength()
    {
        // check the sequence is valid
        valid();

        return sourceSeq.getLength();
    }


    /**
     * This pretty prints the image clip with an empty prefix.
     */
    public String toString()
    {
        return toString("");
    }


    /**
     * This pretty prints the image, prefixing each line with prefix.
     */
    public String toString(String prefix)
    {
        StringBuffer s = new StringBuffer();

        s.append(prefix + "targetStart: " + targetStart + "\n");
        s.append(prefix + "length: " + getLength() + "\n");

        // we do this inside a try: if the scanner hasn't been
        // initialized, getIncludedFiles will throw an exception
        try
        {
            s.append(prefix + "sourcefiles:" );
            Iterator i =
                Arrays.asList( sourceFiles.getIncludedFiles() ).iterator();
            while ( i.hasNext() )
            {
                s.append(" " + i.next() );
            }
            s.append("\n");
        }
        catch(NullPointerException e)
        {
            s.append("<null>\n");
        }

        // print the source sequence
        s.append(prefix + "sourceSeq: " );
        if ( sourceSeq != null )
        { s.append(sourceSeq.toString(prefix + "  ") ); }
        else
        { s.append(prefix + "<null>\n"); }

        // to step through the parameters, we first get the list of names
        Set names = parameters.keySet();
        Iterator i = names.iterator();
        Parameter p = null;

        // step through the names
        while ( i.hasNext() )
        {
            // get the parameter for this name
            p = (Parameter) parameters.get(i.next());
            s.append(p.toString(prefix));
        }

        return s.toString();
    }


    /**
     * This returns true if the passed frame is in this ImageClip's target
     * sequence.
     */
    public boolean inFrame(int frame)
    {
        // check the sequence is valid
        valid();

        // if the queried frame is equal to or after the start,
        // but before the end, return true
        if ( frame >= targetStart && frame < targetStart+getLength() )
        {
            return true;
        }

        // otherwise return false
        return false;
    }


    /**
     * This returns an element that represents this imageclip's contribution
     * to the specified frame.
     */
    public Element getImage(int frame)
    {
        // check the sequence is valid
        valid();

        // index will be the index into the subsequences
        int index = frame - targetStart;

        // if the target frame is not in this clip, return an IAE
        if ( ! inFrame(frame) )
        {
            throw new IllegalArgumentException(
                "requested frame not inFrame()");
        }

        // create the root <frame> tag
        Element image = new Element("image");

        // get the file list
        String[] files = sourceFiles.getIncludedFiles();
        String dir = sourceFiles.getBasedir().getPath();

        // check that the files array has enough elements
        if ( getSource(index) >= Array.getLength(files) )
        {
            throw new IllegalArgumentException(
                "insufficient source files for:\n" +
                "  frame:" + frame +
                "  directory:" + sourceFiles.getBasedir().getName() );
        }

        // create the attributes
        image.setAttribute(
            new Attribute("file", dir + "/" + files[getSource(index)]) );

        // now process the parameter values
        // to step through the parameters, we first get the list of names
        Set names = parameters.keySet();
        Iterator i = names.iterator();
        Parameter p = null;

        // step through the names
        while ( i.hasNext() )
        {
            // get the parameter for this name
            p = (Parameter) parameters.get(i.next());

            // set an attribute with the name of the parameter and the
            // value of the parameter's seq at this index
            image.setAttribute(
                new Attribute(p.getName(),
                    String.valueOf(p.getSeq().get(index))) );
        }

        // return the element
        return image;
    }


    /**
     * This returns the source value for the specified target frame
     */
    public int getSource(int frame)
    {
        // if sourceSeq is null, throw an exception
        if ( sourceSeq == null )
        {
            throw new IllegalArgumentException("sourceSeq is null!");
        }

        // otherwise return the right value
        return sourceSeq.get(frame);
    }
}
