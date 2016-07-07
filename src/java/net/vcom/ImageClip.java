
/*****************************************************************************
 *
 * VCom: video compositor
 *
 * source file: imageclip.java
 * package: net.vcom
 *
 * version 0.3
 * 2005-06-01
 * Copyright (c) 2005, Denis McLaughlin
 * Released under the GPL license, version 2
 *
 */

package net.vcom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
     * These are the default values
     */
    public static int TARGETSTART_DEFAULT = 0;
    public static int XPOSITION_DEFAULT = 0;
    public static int YPOSITION_DEFAULT = 0;
    public static int ROTATION_DEFAULT = 0;
    public static int OPACITY_DEFAULT = 100;


    /**
     * This is the list of files that act as the source of this sequence
     */
    private DirectoryScanner sourceFiles = null;

    /**
     * This is source sequence: the order in which frames are to be
     * taken from the source files
     */
    private Seq sourceSeq = null;

    /**
     * This is the frame at which the image insertion should begin
     */
    private int targetStart = TARGETSTART_DEFAULT;

    /**
     * These are the various image parameters
     */
    private Seq xPosition = null;
    private Seq yPosition = null;
    private Seq rotation = null;
    private Seq opacity = null;
    private Seq xSize = null;
    private Seq ySize = null;


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

            // targetSeq
            if ( child.getName().equals("targetseq") )
            {
                setTargetStart(child);
                continue;
            }

            // sourceseq
            if ( child.getName().equals("sourceseq") )
            {
                setSourceSeq(child);
                continue;
            }

            // xposition
            if ( child.getName().equals("xposition") )
            {
                setXPosition(child);
                continue;
            }

            // yposition
            if ( child.getName().equals("yposition") )
            {
                setYPosition(child);
                continue;
            }

            // rotation
            if ( child.getName().equals("rotation") )
            {
                setRotation(child);
                continue;
            }

            // opacity
            if ( child.getName().equals("opacity") )
            {
                setOpacity(child);
                continue;
            }

            // xsize
            if ( child.getName().equals("xsize") )
            {
                setXSize(child);
                continue;
            }

            // ysize
            if ( child.getName().equals("ysize") )
            {
                setYSize(child);
                continue;
            }

            // if we get here, then we don't recognize the child
            throw new IllegalArgumentException(
                "unrecognized child node in imageclip: " + child.getName() );
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
     * This is getter for the xPosition
     */
    public Seq getXPosition()
    {
        return xPosition;
    }

    /**
     * This is setter for the xPosition
     */
    public void setXPosition(Seq s)
    {
        xPosition = s;
    }

    /**
     * This sets the value of xPosition based on an xml fragment such
     * as:
     *
     *  <xposition>
     *    <seq start="1" stop="5"/>
     *  </xposition>
     *
     */
    public void setXPosition(Element e)
    {
        // fail if there are any attributes
        List atts = e.getAttributes();
        if ( atts.size() != 0 )
        {
            throw new IllegalArgumentException(
                "<xposition> cannot have any attributes");
        }

        // fail if there isn't exactly one child
        List children = e.getChildren();
        if ( children.size() != 1 )
        {
            throw new IllegalArgumentException(
                "<xposition> must have one <seq> child");
        }

        // get the element, turn it into a document, create seq for it
        Element seq = (Element) children.get(0);
        Document d = new Document((Element) seq.clone());
        //setXPosition(new Seq(d));
        setXPosition(Seq.createSeq(d));
    }


    /**
     * This is getter for the yPosition
     */
    public Seq getYPosition()
    {
        return yPosition;
    }

    /**
     * This is setter for the yPosition
     */
    public void setYPosition(Seq s)
    {
        yPosition = s;
    }

    /**
     * This sets the value of yPosition based on an xml fragment such
     * as:
     *
     *  <yposition>
     *    <seq start="1" stop="5"/>
     *  </yposition>
     *
     */
    public void setYPosition(Element e)
    {
        // fail if there are any attributes
        List atts = e.getAttributes();
        if ( atts.size() != 0 )
        {
            throw new IllegalArgumentException(
                "<yposition> cannot have any attributes");
        }

        // fail if there isn't exactly one child
        List children = e.getChildren();
        if ( children.size() != 1 )
        {
            throw new IllegalArgumentException(
                "<yposition> must have one <seq> child");
        }

        // get the element, turn it into a document, create seq for it
        Element seq = (Element) children.get(0);
        Document d = new Document((Element) seq.clone());
        //setYPosition(new Seq(d));
        setYPosition(Seq.createSeq(d));
    }


    /**
     * This is getter for the xSize
     */
    public Seq getXSize()
    {
        return xSize;
    }

    /**
     * This is setter for the xSize
     */
    public void setXSize(Seq s)
    {
        xSize = s;
    }

    /**
     * This sets the value of xSize based on an xml fragment such
     * as:
     *
     *  <xsize>
     *    <seq start="1" stop="5"/>
     *  </xsize>
     *
     */
    public void setXSize(Element e)
    {
        // fail if there are any attributes
        List atts = e.getAttributes();
        if ( atts.size() != 0 )
        {
            throw new IllegalArgumentException(
                "<xsize> cannot have any attributes");
        }

        // fail if there isn't exactly one child
        List children = e.getChildren();
        if ( children.size() != 1 )
        {
            throw new IllegalArgumentException(
                "<xsize> must have one <seq> child");
        }

        // get the element, turn it into a document, create seq for it
        Element seq = (Element) children.get(0);
        Document d = new Document((Element) seq.clone());
        //setXSize(new Seq(d));
        setXSize(Seq.createSeq(d));
    }


    /**
     * This is getter for the ySize
     */
    public Seq getYSize()
    {
        return ySize;
    }

    /**
     * This is setter for the ySize
     */
    public void setYSize(Seq s)
    {
        ySize = s;
    }

    /**
     * This sets the value of ySize based on an xml fragment such
     * as:
     *
     *  <ysize>
     *    <seq start="1" stop="5"/>
     *  </ysize>
     *
     */
    public void setYSize(Element e)
    {
        // fail if there are any attributes
        List atts = e.getAttributes();
        if ( atts.size() != 0 )
        {
            throw new IllegalArgumentException(
                "<ysize> cannot have any attributes");
        }

        // fail if there isn't exactly one child
        List children = e.getChildren();
        if ( children.size() != 1 )
        {
            throw new IllegalArgumentException(
                "<ysize> must have one <seq> child");
        }

        // get the element, turn it into a document, create seq for it
        Element seq = (Element) children.get(0);
        Document d = new Document((Element) seq.clone());
        //setYSize(new Seq(d));
        setYSize(Seq.createSeq(d));
    }


    /**
     * This is getter for the opacity
     */
    public Seq getOpacity()
    {
        return opacity;
    }

    /**
     * This is setter for the opacity
     */
    public void setOpacity(Seq s)
    {
        opacity = s;
    }

    /**
     * This sets the value of opacity based on an xml fragment such
     * as:
     *
     *  <opacity>
     *    <seq start="1" stop="5"/>
     *  </opacity>
     *
     */
    public void setOpacity(Element e)
    {
        // fail if there are any attributes
        List atts = e.getAttributes();
        if ( atts.size() != 0 )
        {
            throw new IllegalArgumentException(
                "<opacity> cannot have any attributes");
        }

        // fail if there isn't exactly one child
        List children = e.getChildren();
        if ( children.size() != 1 )
        {
            throw new IllegalArgumentException(
                "<opacity> must have one <seq> child");
        }

        // get the element, turn it into a document, create seq for it
        Element seq = (Element) children.get(0);
        Document d = new Document((Element) seq.clone());
        //setOpacity(new Seq(d));
        setOpacity(Seq.createSeq(d));
    }


    /**
     * This is getter for the rotation
     */
    public Seq getRotation()
    {
        return rotation;
    }

    /**
     * This is setter for the rotation
     */
    public void setRotation(Seq s)
    {
        rotation = s;
    }

    /**
     * This sets the value of rotation based on an xml fragment such
     * as:
     *
     *  <rotation>
     *    <seq start="1" stop="5"/>
     *  </rotation>
     *
     */
    public void setRotation(Element e)
    {
        // fail if there are any attributes
        List atts = e.getAttributes();
        if ( atts.size() != 0 )
        {
            throw new IllegalArgumentException(
                "<rotation> cannot have any attributes");
        }

        // fail if there isn't exactly one child
        List children = e.getChildren();
        if ( children.size() != 1 )
        {
            throw new IllegalArgumentException(
                "<rotation> must have one <seq> child");
        }

        // get the element, turn it into a document, create seq for it
        Element seq = (Element) children.get(0);
        Document d = new Document((Element) seq.clone());
        //setRotation(new Seq(d));
        setRotation(Seq.createSeq(d));
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

        // now we call valid on each subcomponent
        if ( sourceSeq != null )
        { sourceSeq.valid(); }

        if ( xPosition != null )
        { xPosition.valid(); }

        if ( yPosition != null )
        { yPosition.valid(); }

        if ( rotation != null )
        { rotation.valid(); }

        if ( opacity != null )
        { opacity.valid(); }

        if ( xSize != null )
        { xSize.valid(); }

        if ( ySize != null )
        { ySize.valid(); }

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

        // print the x position sequence
        s.append(prefix + "xPosition: " );
        if ( xPosition != null )
        { s.append(xPosition.toString(prefix + "  ") ); }
        else
        { s.append(prefix + "<null>\n"); }

        // print the y position sequence
        s.append(prefix + "yPosition: " );
        if ( yPosition != null )
        { s.append(yPosition.toString(prefix + "  ") ); }
        else
        { s.append(prefix + "<null>\n"); }

        // print the rotation sequence
        s.append(prefix + "rotation: " );
        if ( rotation != null )
        { s.append(rotation.toString(prefix + "  ") ); }
        else
        { s.append(prefix + "<null>\n"); }

        // print the opacity sequence
        s.append(prefix + "opacity: " );
        if ( opacity != null )
        { s.append(opacity.toString(prefix + "  ") ); }
        else
        { s.append(prefix + "<null>\n"); }

        // print the x size sequence
        s.append(prefix + "xSize: " );
        if ( xSize != null )
        { s.append(xSize.toString(prefix + "  ") ); }
        else
        { s.append(prefix + "<null>\n"); }

        // print the y size sequence
        s.append(prefix + "ySize: " );
        if ( ySize != null )
        { s.append(ySize.toString(prefix + "  ") ); }
        else
        { s.append(prefix + "<null>\n"); }

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
     * This returns an element that represents this imageclips contribution
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
            throw new IllegalArgumentException("requested frame not inFrame()");
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
        image.setAttribute(
            new Attribute("xposition", String.valueOf(getXPosition(index))) );
        image.setAttribute(
            new Attribute("yposition", String.valueOf(getYPosition(index))) );
        image.setAttribute(
            new Attribute("rotation", String.valueOf(getRotation(index))) );
        image.setAttribute(
            new Attribute("opacity", String.valueOf(getOpacity(index))) );

        // no defaults for these, so we check first
        if ( xSize != null )
        {
            image.setAttribute(
                new Attribute("xsize", String.valueOf(getXSize(index))) );
        }
        if ( ySize != null )
        {
            image.setAttribute(
                new Attribute("ysize", String.valueOf(getYSize(index))) );
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


    /**
     * This returns the xposition value for the specified target frame,
     * including a default value
     */
    public int getXPosition(int frame)
    {
        // if xposition is null, return the default value
        if ( xPosition == null )
        {
            return XPOSITION_DEFAULT;
        }

        // otherwise return the right value
        return xPosition.get(frame);
    }


    /**
     * This returns the yposition value for the specified target frame,
     * including a default value
     */
    public int getYPosition(int frame)
    {
        // if yposition is null, return the default value
        if ( yPosition == null )
        {
            return YPOSITION_DEFAULT;
        }

        // otherwise return the right value
        return yPosition.get(frame);
    }


    /**
     * This returns the rotation value for the specified target frame,
     * including a default value
     */
    public int getRotation(int frame)
    {
        // if rotation is null, return the default value
        if ( rotation == null )
        {
            return ROTATION_DEFAULT;
        }

        // otherwise return the right value
        return rotation.get(frame);
    }


    /**
     * This returns the opacity value for the specified target frame,
     * including a default value
     */
    public int getOpacity(int frame)
    {
        // if opacity is null, return the default value
        if ( opacity == null )
        {
            return OPACITY_DEFAULT;
        }

        // otherwise return the right value
        return opacity.get(frame);
    }


    /**
     * This returns the xsize value for the specified target frame,
     * including a default value
     */
    public int getXSize(int frame)
    {
        // there is no default xSize, so if we don't have one, throw IAE
        if ( xSize == null )
        {
            throw new IllegalArgumentException("xSize is null!");
        }

        // otherwise return the right value
        return xSize.get(frame);
    }


    /**
     * This returns the ySize value for the specified target frame,
     * including a default value
     */
    public int getYSize(int frame)
    {
        // there is no default ySize, so if we don't have one, throw IAE
        if ( ySize == null )
        {
            throw new IllegalArgumentException("ySize is null!");
        }

        // otherwise return the right value
        return ySize.get(frame);
    }
}
