
/*****************************************************************************
 *
 * VCom: video compositor
 *
 * source file: SoundClip.java
 * package: net.vcom
 *
 * version 0.3
 * 2005-06-01
 * Copyright (c) 2005, Denis McLaughlin
 * Released under the GPL license, version 2
 *
 */

package net.vcom;

import java.util.List;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Attribute;


/**
 * This object models the soundclip tag.
 */
public class SoundClip
{
    /**
     * This is the name of the file to read the sound from
     */
    private String source = null;

    /**
     * This is the begin point in the source file to read the sound from,
     * expressed as seconds
     */
    private double sourceStart = 0.0;

    /**
     * This is the end point in the source file to stop reading at,
     * expressed as seconds
     */
    private double sourceStop = 0.0;

    /**
     * This is the begin point in the target video to insert the sound at
     */
    private double targetStart = 0.0;


    /**
     * This is the default constructor
     */
    public SoundClip()
    {}


    /**
     * This is the Document constructor
     */
    public SoundClip(Document doc)
    {
        // check we don't have a null doc
        if ( doc == null )
        {
            throw new IllegalArgumentException(
                "SoundClip created from null document");
        }

        // get the list of the content
        List content = doc.getContent();

        // we better have only a single element to process
        if ( content.size() != 1 )
        {
            throw new IllegalArgumentException(
                "soundclip must have a single node");
        }

        // get the element from the document
        Element e = (Element) content.get(0);

        // the name of the element must be <soundclip>
        if ( ! e.getName().equals("soundclip") )
        {
            throw new IllegalArgumentException(
                "soundclip tag must be called <soundclip>");
        }

        // there are no valid attributes
        if ( e.getAttributes().size() > 0 )
        {
            throw new IllegalArgumentException(
                "soundclip tag cannot have attributes");
        }

        // for each child, we process it in the loop
        List children = e.getChildren();
        Iterator i = children.iterator();
        while ( i.hasNext() )
        {
            Element child = (Element) i.next();

            // sourceFiles
            if ( child.getName().equals("source") )
            {
                setSource(child);
                continue;
            }

            // targetStart
            if ( child.getName().equals("target") )
            {
                setTargetStart(child);
                continue;
            }

            // if we get here, then we don't recognize the child
            throw new IllegalArgumentException(
                "unrecognized child node in imageclip: " + child.getName() );
        }
    }


    /**
     * This is the getter for source
     */
    public String getSource()
    {
        return source;
    }

    /**
     * This is the setter for source
     */
    public void setSource(String s)
    {
        // can't set a null source
        if ( s == null )
        {
            throw new IllegalArgumentException("can't set null source");
        }

        source = s;
    }

    /**
     * This is the setter for source from an Element
     */
    public void setSource(Element e)
    {
        List atts = e.getAttributes();

        // we must have between one and three attributes
        if ( atts.size() < 1 )
        {
            throw new IllegalArgumentException(
                "<source> must have at least one attribute");
        }

        // can't have more than three attributes
        if ( atts.size() > 3 )
        {
            throw new IllegalArgumentException(
                "<source> can't have more than three attributes");
        }

        // step through attributes, processing each
        Iterator i = atts.iterator();
        while ( i.hasNext() )
        {
            Attribute att = (Attribute) i.next();

            // process the file attribute
            if ( att.getName().equals("file") )
            {
                setSource(att.getValue());
                continue;
            }

            // process the start attribute
            if ( att.getName().equals("start") )
            {
                setSourceStart( Double.parseDouble(att.getValue()) );
                continue;
            }

            // process the stop attribute
            if ( att.getName().equals("stop") )
            {
                setSourceStop( Double.parseDouble(att.getValue()) );
                continue;
            }

            // if we get here, we don't recognize it
            throw new IllegalArgumentException(
                "unrecognized attribute in <source>: " + att.getName() );
        }
    }


    /**
     * This is the getter for sourceStart
     */
    public double getSourceStart()
    {
        return sourceStart;
    }

    /**
     * This is the setter for sourceStart
     */
    public void setSourceStart(double d)
    {
        // can't set a negative sourceStart
        if ( d < 0 )
        {
            throw new IllegalArgumentException("soundStart cannot be negative");
        }

        sourceStart = d;
    }


    /**
     * This is the getter for sourceStop
     */
    public double getSourceStop()
    {
        return sourceStop;
    }

    /**
     * This is the setter for sourceStop
     */
    public void setSourceStop(double d)
    {
        // can't set a negative sourceStop
        if ( d < 0 )
        {
            throw new IllegalArgumentException("soundStop cannot be negative");
        }

        sourceStop = d;
    }


    /**
     * This is the getter for targetStart
     */
    public double getTargetStart()
    {
        return targetStart;
    }

    /**
     * This is the setter for targetStart
     */
    public void setTargetStart(double d)
    {
        // can't set a negative targetStart
        if ( d < 0 )
        {
            throw new IllegalArgumentException(
                "targetStart cannot be negative");
        }

        targetStart = d;
    }

    /**
     * This is the setter for targetStart from an Element
     */
    public void setTargetStart(Element e)
    {
        List atts = e.getAttributes();

        // we must have exactly one attribute
        if ( atts.size() != 1 )
        {
            throw new IllegalArgumentException(
                "<target> must have a start attribute");
        }

        // the attribute must be called start
        Attribute att = (Attribute) atts.get(0);
        if ( ! att.getName().equals("start") )
        {
            throw new IllegalArgumentException(
                "<target> must have a start attribute");
        }

        // set the target start time
        setTargetStart( Double.parseDouble(att.getValue()) );
    }


    /**
     * This returns true if the current configuration is valid, throws
     * an exception otherwise.
     *
     * There are no subcomponents below this, so there is no need for
     * SoundClip's valid() to call valid() on any subcomponents.
     */
    public boolean valid()
    {
        // check that the source is not null
        if ( source == null )
        {
            throw new IllegalArgumentException("source must be set");
        }

        // check that the sourceStart comes after the sourceStop
        if ( sourceStart > sourceStop )
        {
            throw new IllegalArgumentException(
                "sourceStart must come before sourceStop");
        }

        return true;
    }
}
