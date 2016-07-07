
/**************************************************************************
 *
 * VCom: video compositor
 *
 * source file: SeqLoop.java
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
public class SeqLoop extends Seq
{
    /**
     * This is the default constructor: it returns an empty SeqLoop object.
     */
    public SeqLoop()
    {}


    /**
     * This is the Document constructor: it is expected to be passed a
     * JDom Document of a <seq> node.
     */
    public SeqLoop(Document doc)
    {
        // check we don't have a null doc
        if ( doc == null )
        {
            throw new IllegalArgumentException(
                "SeqLoop created from null document");
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

        // process the attributes, throw exception on first unrecognized
        List atts = e.getAttributes();
        Iterator i = atts.iterator();
        while ( i.hasNext() )
        {
            Attribute a = (Attribute) i.next();

            // start
            if ( a.getName().equals("start") )
            {
                setStart(a.getValue());
                continue;
            }

            // stop
            if ( a.getName().equals("stop") )
            {
                setStop(a.getValue());
                continue;
            }

            // step
            if ( a.getName().equals("step") )
            {
                setStep(a.getValue());
                continue;
            }

            // loopStart
            if ( a.getName().equals("loopstart") )
            {
                setLoopStart(a.getValue());
                continue;
            }

            // loopStop
            if ( a.getName().equals("loopstop") )
            {
                setLoopStop(a.getValue());
                continue;
            }

            // loopCount
            if ( a.getName().equals("loopcount") )
            {
                setLoopCount(a.getValue());
                continue;
            }

            // loopCount
            if ( a.getName().equals("length") )
            {
                setLength(a.getValue());
                continue;
            }

            // if we get here, it's not recognized
            throw new IllegalArgumentException(
                "unrecognized seq attribute: " + a.getName() );
        }
    }


    /**
     * This is the type-specific validate method: it checks for the type
     * specific validity violations.
     */
    protected boolean typeValid()
    {
        // we calculate
        calculate();

        return true;
    }


    /**
     * This returns the length of the sequence, which will be a number between
     * 0 and infinity.  (Although it would be odd, the length could be zero.)
     * Internally, this is calculated the first time getLength() is called,
     * and then stored.  However, changes to any of the seq parameters
     * will unset the value of length, forcing another recalculation the
     * next time getLength() is called.
     */
    public int getLength()
    {
        // we always call calculate, and it will shortcircuit if the
        // sequence has already been calculated
        calculate();

        // if we have a length parameter defined, return it
        if ( length != -1 )
            return length;

        // otherwise return the length of the sequence array
        return sequence.length;
    }


    /**
     * This method calculates the exact seq values, saving the resulting
     * array in sequence[].  It should only be called once when the
     * sequence is first being accessed, and subsequent calls will return
     * results from the array.  Note that this spends memory to buy CPU
     * time, which may not be the best tradeoff in some situations.
     */
    private void calculate()
    {
        int lcount, lstart, lstop;

        // technically we should never be called when sequence[] is not
        // null, but just in case
        if ( sequence != null )
        {
            return;
        }

        ArrayList s = new ArrayList();

        boolean done = false;

        // calculate the first pass through the sequence
        s.addAll( calculate(start, stop, step) );

        // if loopCount is non-zero, we loop loopCount times
        if ( loopCount > 0 )
        {
            // set the lstart variable: default to start, overridden
            // by loopStart
            lstart = start;
            if ( loopStart != -1 )
            { lstart = loopStart; }

            // set the lstop variable: default to stop, overridden
            // by loopStop
            lstop = stop;
            if ( loopStop != -1 )
            { lstop = loopStop; }

            // loop loopCount times
            for (lcount=0; lcount<loopCount; lcount++)
            {
                s.addAll( calculate(lstart, lstop, step) );
            }
        }

        // and set the final output in the object variables
        sequence = (Integer[]) s.toArray(new Integer[]{});
        index = 0;
    }


    /**
     * This method takes a start, a stop, and a step, and returns an
     * ArrayList containing the elements of the sequence
     */
    private Collection calculate(int start, int stop, double step)
    {
        ArrayList s = new ArrayList();

        // if step is 0, dump the start value into the array and return it
        if ( step == 0 )
        {
            s.add(start);
        }

        // if step is bigger than 0, we start at start and go to stop
        if ( step > 0 )
        {
            // begin at start
            double current = start;

            // continue until we're past stop
            while ( current <= stop )
            {
                s.add((int)current);
                current += step;
            }
        }

        // if step is smaller than 0, we start at stop and go to start
        if ( step < 0 )
        {
            // begin at stop
            double current = start;

            // continue until we're past start
            while ( current >= stop )
            {
                s.add((int)current);
                current += step;
            }
        }

        return(s);
    }
    

    /**
     * This method resets the calculated values of the seq: it is called
     * whenever the operational parameters of the class are changed
     */
    protected void reset()
    {
        sequence = null;
        index = 0;
    }


    /**
     * This returns true of there are further elements of the sequence
     * to return, false otherwise.  This is determined by checking if the
     * current index exceeds the length of the sequence[] array.
     */
    public boolean hasNext()
    {
        // calculate the contents of the sequence
        calculate();

        // if the index is past the length, return false
        if ( index > sequence.length-1 )
        {
            return false;
        }

        // otherwise return true
        return true;
    }


    /**
     * This returns the next element of the sequence.
     */
    public int getNext()
    {
        // return the next element
        return get(index++);
    }


    /**
     * This returns the specified element of the sequence.
     */
    public int get(int i)
    {
        // calculate the contents of the sequence
        calculate();

        // if we're past the end of the sequence, return the hold value
        if ( i >= sequence.length )
        {
            // if we're looping and loopEnd has a value, it's our hold value
            if ( loopCount != 0 && loopStop != -1 )
            {
                return loopStop;
            }
            else
            {
                return stop;
            }
        }

        // otherwise return true
        return sequence[i];
    }


    /**
     * This method does a deep compare of the passed SeqLoop to this SeqLoop,
     * returning true if it is the same, false otherwise.
     */
    public boolean equals(SeqLoop s)
    {
        if ( s.getStart() != getStart() )
        {   return false; }

        if ( s.getStop() != getStop() )
        {   return false; }

        if ( s.getStep() != getStep() )
        {   return false; }

        if ( s.getLoopStart() != getLoopStart() )
        {   return false; }

        if ( s.getLoopStop() != getLoopStop() )
        {   return false; }

        if ( s.getLoopCount() != getLoopCount() )
        {   return false; }

        return true;
    }


    /**
     * This is used to pretty print the sequence with no prefix.
     */
    public String toString()
    {
        return toString("");
    }


    /**
     * This is used to pretty print the sequence, preceding each line
     * with prefix.
     */
    public String toString(String prefix)
    {
        StringBuffer s = new StringBuffer();

        // calculate the sequence
        calculate();

        s.append("\n");
        s.append(prefix + "start: " + start + "\n");
        s.append(prefix + "stop: " + stop + "\n");
        s.append(prefix + "step: " + step + "\n");
        s.append(prefix + "loopCount: " + loopCount + "\n");
        s.append(prefix + "loopStart: " + loopStart + "\n");
        s.append(prefix + "loopStop: " + loopStop + "\n");
        s.append(prefix + "index: " + index + "\n");
        s.append(prefix + "sequence[]:" );

        // print the sequence values
        Iterator i = Arrays.asList(sequence).iterator();
        while ( i.hasNext() )
        {
            s.append(" " + i.next());
        }
        s.append("\n");

        return s.toString();
    }
}
