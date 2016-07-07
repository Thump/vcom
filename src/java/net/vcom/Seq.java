
/**************************************************************************
 *
 * VCom: video compositor
 *
 * source file: Seq.java
 * package: net.vcom
 *
 * Copyright (c) 2005, Denis McLaughlin
 * Released under the GPL license, version 2
 *
 */

package net.vcom;

import java.util.Collection;
import java.util.List;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ArrayList;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Attribute;

/**
 * This object models the seq tag.
 */
public abstract class Seq
{
    /**
     * This is the start, stop, step, loopcount, loopstart, and
     * loopstop values of the sequence
     */
    protected int start = 0;
    protected int stop = 0;
    protected double step = 1.0;
    protected int loopCount = 0;
    protected int loopStart = -1;
    protected int loopStop = -1;
    protected int length = -1;

    // this is the current index into the sequence: it points to the
    // next value to be returned by getNext()
    protected int index = 0;

    // this is the array of values in the sequence, from the first
    // (index 0), to the last (index length-1)
    protected Integer sequence[] = null;


    /**
     * This static method takes a document and returns the appropriate
     * Seq class.
     */
    static public Seq createSeq(Document doc)
    {
        // check we don't have a null doc
        if ( doc == null )
        {
            throw new IllegalArgumentException(
                "Seq created from null document");
        }

        // get the list of the content
        List content = doc.getContent();

        // there should only be a single node being passed in
        if ( content.size() != 1 )
        {
            throw new IllegalArgumentException(
                "<seq> must have a single node");
        }

        // get the element from the document
        Element e = (Element) content.get(0);

        // the name of the element must be <seq>
        if ( ! e.getName().equals("seq") )
        {
            throw new IllegalArgumentException(
                "seq tag must be called <seq>, got: " + e.getName());
        }

        // there must be no children
        if ( e.getChildren().size() != 0 )
        {
            throw new IllegalArgumentException("seq tag cannot have children");
        }

        // ok, if we get here, we arguably have a real Seq, so create
        // a new one and use it below
        return(new SeqLoop(doc));
    }

    /**
     * This is the getter for the start value
     */
    public int getStart()
    {
        return start;
    }

    /**
     * This is the setter for the start value.  It also resets the
     * operational values of the seq, including sequence and index.
     */
    public void setStart(int i)
    {
        // check that the start isn't negative
        if ( i < 0 )
        {
            throw new IllegalArgumentException(
                "cannot set negative start");
        }

        // since we're futzing with the operation parameters, we reset the seq
        reset();

        // set the start value
        start = i;
    }

    /**
     * This sets the start value from a String
     */
    public void setStart(String s)
    {
        setStart( Integer.parseInt(s) );
    }


    /**
     * This is the getter for the stop value
     */
    public int getStop()
    {
        return stop;
    }

    /**
     * This is the setter for the stop value
     */
    public void setStop(int i)
    {
        // check that the stop isn't negative
        if ( i < 0 )
        {
            throw new IllegalArgumentException(
                "cannot set negative stop");
        }

        // since we're futzing with the operation parameters, we reset the seq
        reset();

        stop = i;
    }

    /**
     * This sets the stop value from a String
     */
    public void setStop(String s)
    {
        setStop( Integer.parseInt(s) );
    }


    /**
     * This is the getter for the step value
     */
    public double getStep()
    {
        return step;
    }

    /**
     * This is the setter for the step value
     */
    public void setStep(double i)
    {
        // since we're futzing with the operation parameters, we reset the seq
        reset();

        step = i;
    }

    /**
     * This sets the step value from a String
     */
    public void setStep(String s)
    {
        setStep( Double.parseDouble(s) );
    }


    /**
     * This is the getter for the loopStart value
     */
    public int getLoopStart()
    {
        return loopStart;
    }

    /**
     * This is the setter for the loopStart value
     */
    public void setLoopStart(int i)
    {
        // check that the loopStart isn't negative
        if ( i < 0 )
        {
            throw new IllegalArgumentException(
                "cannot set negative loopStart");
        }

        // since we're futzing with the operation parameters, we reset the seq
        reset();

        loopStart = i;
    }

    /**
     * This sets the loopStart value from a String
     */
    public void setLoopStart(String s)
    {
        setLoopStart( Integer.parseInt(s) );
    }


    /**
     * This is the getter for the loopStop value
     */
    public int getLoopStop()
    {
        return loopStop;
    }

    /**
     * This is the setter for the loopStop value
     */
    public void setLoopStop(int i)
    {
        // check that the loopStop isn't negative
        if ( i < 0 )
        {
            throw new IllegalArgumentException(
                "cannot set negative loopStop");
        }

        // since we're futzing with the operation parameters, we reset the seq
        reset();

        loopStop = i;
    }

    /**
     * This sets the loopStop value from a String
     */
    public void setLoopStop(String s)
    {
        setLoopStop( Integer.parseInt(s) );
    }


    /**
     * This is the getter for the loopCount value
     */
    public int getLoopCount()
    {
        return loopCount;
    }

    /**
     * This is the setter for the loopCount value
     */
    public void setLoopCount(int i)
    {
        // check that the loopCount isn't negative
        if ( i < 0 )
        {
            throw new IllegalArgumentException(
                "cannot set negative loopCount");
        }

        // since we're futzing with the operation parameters, we reset the seq
        reset();

        loopCount = i;
    }

    /**
     * This sets the loopCount value from a String
     */
    public void setLoopCount(String s)
    {
        setLoopCount( Integer.parseInt(s) );
    }

    /**
     * This sets the length value from a String
     */
    public void setLength(String s)
    {
        setLength( Integer.parseInt(s) );
    }

    /**
     * This sets the length value from an int
     */
    public void setLength(int i)
    {
        length = i;
    }


    /**
     * This method checks that the Seq class as currently configured is
     * valid.
     *
     * Specifically it checks that:
     *  - if loopStart or loopEnd not -1, loopCount must be non-zero
     *  - if step is positive
     *    - start must come before stop
     *    - if loopStart is set and loopStop is set, then loopStart must
     *      come before loopStop
     *    - if loopStart is set and loopStop is not set, then loopStart must
     *      come before stop
     *    - if loopStop is set and loopStart is not set, then loopStop must
     *      come after start
     *  - if step is negative
     *    - start must come after stop
     *    - if loopStart is set and loopStop is set, then loopStart must
     *      come after loopStop
     *    - if loopStart is set and loopStop is not set, then loopStart must
     *      come after stop
     *    - if loopStop is set and loopStart is not set, then loopStop must
     *      come before start
     *  - if step is zero
     *    - start must equal stop
     *    - loopCount must be zero
     *
     * There are no subcomponents below this, so there is no need for
     * SeqLoop's valid() to call valid() on any subcomponents.
     *
     */
    public boolean valid()
    {
        // if loopStart or loopStop is not -1, loopCount must be non-zero
        if ( (loopStart != -1 || loopStop != -1) && loopCount == 0  )
        {
            throw new IllegalArgumentException(
                "loopCount must be non-zero if loopStart or loopStop set");
        }

        // if step is positive
        if ( step > 0 )
        {
            // start can't come after stop
            if ( start > stop )
            {
                throw new IllegalArgumentException(
                    "start cannot be greater than stop when step positive");
            }

            // loopStart can't come after loopStop
            if ( loopStart != -1 && loopStop != -1 && loopStart > loopStop )
            {
                throw new IllegalArgumentException(
                    "loopStart cannot be greater than loopStop " 
                    + "when step positive");
            }

            // loopStart can't come after stop, if loopStop isn't set
            if ( loopStart != -1 && loopStop == -1 && loopStart > stop )
            {
                throw new IllegalArgumentException(
                    "loopStart cannot be greater than stop " 
                    + "when step positive");
            }

            // loopStop can't come before start, if loopStart isn't set
            if ( loopStart == -1 && loopStop != -1 && loopStop < start )
            {
                throw new IllegalArgumentException(
                    "loopStop cannot be less than start " 
                    + "when step positive");
            }
        }

        // if step is negative
        if ( step < 0 )
        {
            // if step is negative, start can't come before stop
            if ( start < stop )
            {
                throw new IllegalArgumentException(
                    "start cannot be greater than stop when step positive");
            }

            // if loopStart and loopStop are both set, then loopStart can't
            // come before loopStop
            if ( loopStart != -1 && loopStop != -1 && loopStart < loopStop )
            {
                throw new IllegalArgumentException(
                    "loopStart cannot be less than loopStop "
                    + "when step is negative");
            }

            // if only loopStart is set, then loopStart can't
            // come before stop
            if ( loopStart != -1 && loopStop == -1 && loopStart < stop )
            {
                throw new IllegalArgumentException(
                    "loopStart cannot be less than stop "
                    + "when step is negative");
            }

            // if only loopStop is set, then loopStop can't
            // come after start
            if ( loopStart == -1 && loopStop != -1 && loopStop > start )
            {
                throw new IllegalArgumentException(
                    "loopStart cannot be less than stop "
                    + "when step is negative");
            }
        }

        // if step is zero
        if ( step == 0 )
        {
            // start and stop must be equal
            if ( start != stop )
            {
                throw new IllegalArgumentException(
                    "if step is 0, start must be equal to stop");
            }

            // loopCount must be zero
            if ( loopCount != 0 )
            {
                throw new IllegalArgumentException(
                    "if step is 0, loopCount must be 0");
            }
        }

        return typeValid();
    }


    protected abstract void reset();
    protected abstract boolean typeValid();
    public abstract boolean hasNext();
    public abstract int getNext();
    public abstract int get(int i);
    public abstract String toString();
    public abstract int getLength();
    public abstract String toString(String prefix);
}
