
/**************************************************************************
 *
 * VCom: video compositor
 *
 * source file: Parameter.java
 * package: net.vcom
 *
 * Copyright (c) 2007, Denis McLaughlin
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
 * This object models the parameter tag.
 */
public class Parameter
{
    /**
     * This is the name of the parameter
     */
    private String name = null;

    /**
     * This is the sequence of the parameter
     */
    private Seq seq = null;


    /**
     * This is the default constructor
     */
    public Parameter()
    {}


    /**
     * This is the constructor that takes a name
     */
    public Parameter(String name)
    {
        this.name = name;
    }


    /**
     * This is the Document constructor
     */
    public Parameter(Document doc)
    {
        // check we don't have a null doc
        if ( doc == null )
        {
            throw new IllegalArgumentException(
                "<parameter> created from null document");
        }

        // get the list of the content
        List content = doc.getContent();

        // we better have only a single element to process
        if ( content.size() != 1 )
        {
            throw new IllegalArgumentException(
                "<parameter> must have a single node");
        }

        // get the element from the document
        Element e = (Element) content.get(0);

        // the name of the element must be <parameter>
        if ( ! e.getName().equals("parameter") )
        {
            throw new IllegalArgumentException(
                "parameter tag must be called <parameter>");
        }

        // there must be exactly one attributes
        if ( e.getAttributes().size() != 1 )
        {
            throw new IllegalArgumentException(
                "parameter tag must have a name attributes");
        }

        // process the attribute
        List atts = e.getAttributes();
        Iterator i = atts.iterator();
        while ( i.hasNext() )
        {
            Attribute att = (Attribute) i.next();

            // if the sub-element is not called seq, that's bad
            if ( ! att.getName().equals("name") )
            {
                throw new IllegalArgumentException(
                    "parameter attribute must be 'name ");
            }

            // call setSeq() to set the sequence into this parameter
            setName(att.getValue());
        }

        // there must be exactly one child
        if ( e.getChildren().size() != 1 )
        {
            throw new IllegalArgumentException(
                "parameter tag must have a single seq sub-element");
        }

        // process the child
        List children = e.getChildren();
        i = children.iterator();
        while ( i.hasNext() )
        {
            Element child = (Element) i.next();

            // if the sub-element is not called seq, that's bad
            if ( ! child.getName().equals("seq") )
            {
                throw new IllegalArgumentException(
                    "parameter tag must have a seq sub-element");
            }

            // call setSeq() to set the sequence into this parameter
            setSeq(child);
        }
    }

    /**
     * This is setter for the name
     */
    public void setName(String s)
    {
        name = s;
    }

    /**
     * This is getter for the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * This is setter for the seq
     */
    public void setSeq(Seq s)
    {
        seq = s;
    }

    /**
     * This sets the value of seq based on an xml fragment such
     * as:
     *
     *    <seq start="1" stop="5"/>
     *
     */
    public void setSeq(Element e)
    {
        Document d = new Document((Element) e.clone());
        //setSourceSeq(new Seq(d));
        seq = Seq.createSeq(d);
    }


    /**
     * This is getter for the seq
     */
    public Seq getSeq()
    {
        return seq;
    }


    /**
     * This returns true if the current configuration of this Parameter
     * is valid, or throws an exception otherwise.  It checks:
     *  - that the name is not null
     *  - that the seq is not null
     */
    public boolean valid()
    {
        // check that the name is not null
        if ( name == null )
        {
            throw new IllegalArgumentException(
                "parameter name must be set");
        }

        // check that the seq is not null
        if ( seq == null )
        {
            throw new IllegalArgumentException("parameter seq must be set");
        }

        // now we call valid on each subcomponent
        seq.valid();

        return true;
    }


    /**
     * This pretty prints the parameter with an empty prefix.
     */
    public String toString()
    {
        return toString("");
    }


    /**
     * To stringify this Parameter, we just print the name, and then
     * call toString() on the Seq
     */
    public String toString(String prefix)
    {
        StringBuffer s = new StringBuffer();

        s.append(prefix + name + ": " );
        s.append(seq.toString(prefix + "  "));

        return s.toString();
    }
}
