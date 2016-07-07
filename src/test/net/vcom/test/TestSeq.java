
/*****************************************************************************
 *
 * VCom: video compositor test
 *
 * source file: TestSeq.java  
 * package: net.vcom
 *
 * version 0.3
 * 2005-06-01
 * Copyright (c) 2005, Denis McLaughlin
 * Released under the GPL license, version 2
 *  
 */ 

package net.vcom.test;

import java.lang.IllegalArgumentException;
import java.util.ArrayList;
import java.io.CharArrayReader;

import junit.framework.TestCase;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import net.vcom.Seq;


/**
 * This class runs tests against the Seq class.
 */
public class TestSeq extends TestCase
{
    /**
     * Checks that the Seq constructor returns a non-null object
     */
    public void testDefaultConstructor()
    {
        Seq s = null;

        // create a Video
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);
    }


    /**
     * Tests that the attributes of Seq can be set and got
     */
    public void testSetAndGet()
    {
        int start = 1;
        int stop = 2;
        double step = 3.0;
        int loopCount = 4;
        int loopStart = 5;
        int loopStop = 6;
        Seq s = null;

        // create a seq
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);

        // set and check the stop
        s.setStop(stop);
        assertEquals("stop not set", stop, s.getStop());

        // set and check the start
        s.setStart(start);
        assertEquals("start not set", start, s.getStart());

        // set and check the step
        s.setStep(step);
        assertEquals("step not set", step, s.getStep());

        // set and check the loopCount
        s.setLoopCount(loopCount);
        assertEquals("loopCount not set", loopCount, s.getLoopCount());

        // set and check the loopStop
        s.setLoopStop(loopStop);
        assertEquals("loopStop not set", loopStop, s.getLoopStop());

        // set and check the loopStart
        s.setLoopStart(loopStart);
        assertEquals("loopStart not set", loopStart, s.getLoopStart());
    }


    /**
     * Check that various stop and start ordering is strictly checked:
     *  - positive step, start before stop succeeds
     *  - positive step, start can't come after stop
     *  - positive step, loopStart before loopStop succeeds
     *  - positive step, loopStart can't come after loopStop
     *  - positive step, loopStart before stop succeeds
     *  - positive step, loopStart can't come after stop
     *  - positive step, loopStop after start  succeeds
     *  - positive step, loopStop can't come before start
     *
     *  - negative step, start before stop succeeds
     *  - negative step, start can't come after stop
     */
    public void testStopBeforeStart()
    {
        Seq s = null;
        boolean result = false;

        // positive step, start before stop: should work
        try
        {
            // create a seq
            s = new Seq();
            assertNotNull("null Seq returned from constructor", s);

            // set start to 1 and stop 2: should work
            s.setStart(1);
            s.setStop(2);
            s.setStep(1);

            // check that valid() fails
            result = s.valid();
            assertTrue("got failure with start before stop", result);
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }


        // positive step, start after stop, should fail
        try
        {
            // create a seq
            s = new Seq();
            assertNotNull("null Seq returned from constructor", s);

            // set the bad vals
            s.setStart(2);
            s.setStop(1);
            s.setStep(1);

            // run valid
            result = s.valid();

            // if we get here, that's bad
            assertTrue("start after stop worked", false);
        }
        catch (IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }


        // positive step, loopStart before loopStop: should work
        try
        {
            // create a seq
            s = new Seq();
            assertNotNull("null Seq returned from constructor", s);

            // set start to 1 and stop 2: should work
            s.setStart(1);
            s.setStop(2);
            s.setStep(1);

            s.setLoopStart(0);
            s.setLoopStop(4);
            s.setLoopCount(1);

            // check that valid() fails
            result = s.valid();
            assertTrue("got failure with loopStart before loopStop", result);
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }


        // positive step, loopStart after loopStop, should fail
        try
        {
            // create a seq
            s = new Seq();
            assertNotNull("null Seq returned from constructor", s);

            // set the bad vals
            s.setStart(1);
            s.setStop(2);
            s.setStep(1);

            s.setLoopStart(4);
            s.setLoopStop(3);
            s.setLoopCount(1);

            // run valid
            result = s.valid();

            // if we get here, that's bad
            assertTrue("loopStart after loopStop worked", false);
        }
        catch (IllegalArgumentException e)
        {}


        // positive step, loopStart before stop: should work
        try
        {
            // create a seq
            s = new Seq();
            assertNotNull("null Seq returned from constructor", s);

            // set start to 1 and stop 2: should work
            s.setStart(1);
            s.setStop(2);
            s.setStep(1);

            s.setLoopStart(0);
            s.setLoopCount(1);

            // check that valid() fails
            result = s.valid();
            assertTrue("got failure with loopStart before stop", result);
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }


        // positive step, loopStart after stop, should fail
        try
        {
            // create a seq
            s = new Seq();
            assertNotNull("null Seq returned from constructor", s);

            // set the bad vals
            s.setStart(1);
            s.setStop(2);
            s.setStep(1);

            s.setLoopStart(4);
            s.setLoopCount(1);

            // run valid
            result = s.valid();

            // if we get here, that's bad
            assertTrue("loopStart after stop worked", false);
        }
        catch (IllegalArgumentException e)
        {}


        // positive step, loopStop after start: should work
        try
        {
            // create a seq
            s = new Seq();
            assertNotNull("null Seq returned from constructor", s);

            // set start to 1 and stop 2: should work
            s.setStart(1);
            s.setStop(3);
            s.setStep(1);

            s.setLoopStop(2);
            s.setLoopCount(1);

            // check that valid() fails
            result = s.valid();
            assertTrue("got failure with loopStop after start", result);
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }


        // positive step, loopStop before start, should fail
        try
        {
            // create a seq
            s = new Seq();
            assertNotNull("null Seq returned from constructor", s);

            // set the bad vals
            s.setStart(1);
            s.setStop(3);
            s.setStep(1);

            s.setLoopStop(0);
            s.setLoopCount(1);

            // run valid
            result = s.valid();

            // if we get here, that's bad
            assertTrue("loopStop before start worked", false);
        }
        catch (IllegalArgumentException e)
        {}


        // negative step, start after stop: should work
        try
        {
            // create a seq
            s = new Seq();
            assertNotNull("null Seq returned from constructor", s);

            // set seq params
            s.setStart(3);
            s.setStop(1);
            s.setStep(-1);

            // check that valid() fails
            result = s.valid();
            assertTrue("got failure with start after stop", result);
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }


        // negative step, start before stop, should fail
        try
        {
            // create a seq
            s = new Seq();
            assertNotNull("null Seq returned from constructor", s);

            // set the bad vals
            s.setStart(1);
            s.setStop(3);
            s.setStep(-1);

            // run valid
            result = s.valid();

            // if we get here, that's bad
            assertTrue("start before stop worked", false);
        }
        catch (IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }


        // negative step, loopStart after loopStop: should work
        try
        {
            // create a seq
            s = new Seq();
            assertNotNull("null Seq returned from constructor", s);

            // set start to 1 and stop 2: should work
            s.setStart(3);
            s.setStop(1);
            s.setStep(-1);

            s.setLoopStart(4);
            s.setLoopStop(0);
            s.setLoopCount(1);

            // check that valid() fails
            result = s.valid();
            assertTrue("got failure with loopStart after loopStop", result);
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }


        // negative step, loopStart before loopStop, should fail
        try
        {
            // create a seq
            s = new Seq();
            assertNotNull("null Seq returned from constructor", s);

            // set the bad vals
            s.setStart(3);
            s.setStop(1);
            s.setStep(-1);

            s.setLoopStart(3);
            s.setLoopStop(4);
            s.setLoopCount(1);

            // run valid
            result = s.valid();

            // if we get here, that's bad
            assertTrue("loopStart before loopStop worked", false);
        }
        catch (IllegalArgumentException e)
        {}


        // negative step, loopStart after stop: should work
        try
        {
            // create a seq
            s = new Seq();
            assertNotNull("null Seq returned from constructor", s);

            // set start to 1 and stop 2: should work
            s.setStart(3);
            s.setStop(1);
            s.setStep(-1);

            s.setLoopStart(4);
            s.setLoopCount(1);

            // check that valid() fails
            result = s.valid();
            assertTrue("got failure with loopStart after stop", result);
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }


        // negative step, loopStart before stop, should fail
        try
        {
            // create a seq
            s = new Seq();
            assertNotNull("null Seq returned from constructor", s);

            // set the bad vals
            s.setStart(3);
            s.setStop(1);
            s.setStep(-1);

            s.setLoopStart(0);
            s.setLoopCount(1);

            // run valid
            result = s.valid();

            // if we get here, that's bad
            assertTrue("loopStart before stop worked", false);
        }
        catch (IllegalArgumentException e)
        {}


        // negative step, loopStop before start: should work
        try
        {
            // create a seq
            s = new Seq();
            assertNotNull("null Seq returned from constructor", s);

            // set start to 1 and stop 2: should work
            s.setStart(3);
            s.setStop(1);
            s.setStep(-1);

            s.setLoopStop(0);
            s.setLoopCount(1);

            // check that valid() fails
            result = s.valid();
            assertTrue("got failure with loopStop before start", result);
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }


        // negative step, loopStop after start, should fail
        try
        {
            // create a seq
            s = new Seq();
            assertNotNull("null Seq returned from constructor", s);

            // set the bad vals
            s.setStart(3);
            s.setStop(1);
            s.setStep(-1);

            s.setLoopStop(4);
            s.setLoopCount(1);

            // run valid
            result = s.valid();

            // if we get here, that's bad
            assertTrue("loopStop after start worked", false);
        }
        catch (IllegalArgumentException e)
        {}
    }


    /**
     * Check that start and stop can't be negative
     */
    public void testNegativeStartStopStep()
    {
        Seq s = null;

        // create a seq
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);

        // set start to -1: should fail
        try
        {
            s.setStart(-1);

            // if we get here, that's bad
            assertTrue("negative start worked", false);
        }
        catch (IllegalArgumentException e)
        {}

        // set stop to -1: should fail
        try
        {
            s.setStop(-1);

            // if we get here, that's bad
            assertTrue("negative stop worked", false);
        }
        catch (IllegalArgumentException e)
        {}
    }


    /**
     * Check that loopStart and loopStop can't be negative
     */
    public void testNegativeLoopStartLoopStopLoopCount()
    {
        Seq s = null;

        // create a seq
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);

        // set loopStart to -1: should fail
        try
        {
            s.setLoopStart(-1);

            // if we get here, that's bad
            assertTrue("negative loopStart worked", false);
        }
        catch (IllegalArgumentException e)
        {}

        // set loopStop to -1: should fail
        try
        {
            s.setLoopStop(-1);

            // if we get here, that's bad
            assertTrue("negative loopStop worked", false);
        }
        catch (IllegalArgumentException e)
        {}

        // set loopCount to -1: should fail
        try
        {
            s.setLoopCount(-1);

            // if we get here, that's bad
            assertTrue("negative loopCount worked", false);
        }
        catch (IllegalArgumentException e)
        {}
    }


    /**
     * Check that if step is 0, start and stop must be equal
     */
    public void testStep0StartEqualStop()
    {
        Seq s = null;
        boolean result = false;

        // create a seq
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);

        // set start to 2 and stop to 1: should fail
        try
        {
            // set the bad vals
            s.setLoopStart(2);
            s.setLoopStop(1);
            s.setStep(0);

            // run valid
            result = s.valid();

            // if we get here, that's bad
            assertTrue("zero step with non-equal start/stop", false);
        }
        catch (IllegalArgumentException e)
        {}
    }


    /**
     * Check that if loopStart or loopStop is not -1, loopCount must be set
     */
    public void testLoopCount0LoopStartLoopStopNot()
    {
        Seq s = null;
        boolean result = false;

        // set loopStart to 1 with loopCount set to 0: should fail
        try
        {
            // create a seq
            s = new Seq();
            assertNotNull("null Seq returned from constructor", s);

            // set the bad vals
            s.setLoopStart(1);

            // run valid
            result = s.valid();

            // if we get here, that's bad
            assertTrue("zero loopCount with not -1 loopStart succeeded", false);
        }
        catch (IllegalArgumentException e)
        {}

        // set loopStop to 1 with loopCount set to 0: should fail
        try
        {
            // create a seq
            s = new Seq();
            assertNotNull("null Seq returned from constructor", s);

            // set the bad vals
            s.setLoopStop(1);

            // run valid
            result = s.valid();

            // if we get here, that's bad
            assertTrue("zero loopCount with not -1 loopStop succeeded", false);
        }
        catch (IllegalArgumentException e)
        {}

        // set both loopStart and loopStop to 1 with loopCount set to 0:
        // should fail
        try
        {
            // create a seq
            s = new Seq();
            assertNotNull("null Seq returned from constructor", s);

            // set the bad vals
            s.setLoopStop(1);
            s.setLoopStart(1);

            // run valid
            result = s.valid();

            // if we get here, that's bad
            assertTrue("zero loopCount with not -1 loopStop succeeded", false);
        }
        catch (IllegalArgumentException e)
        {}

        // set loopStart to 1 with loopCount set to 1: should succeed
        try
        {
            // create a seq
            s = new Seq();
            assertNotNull("null Seq returned from constructor", s);

            // set the bad vals
            s.setStart(0);
            s.setStop(3);
            s.setStep(1);

            s.setLoopStart(1);
            s.setLoopCount(1);

            // run valid
            result = s.valid();
            assertTrue("failed valid check", result);
        }
        catch (IllegalArgumentException e)
        {
            assertTrue("non-zero loopCount with not -1 loopStart failed: " + e,
            false);
        }

        // set loopStop to 1 with loopCount set to 1: should succeed
        try
        {
            // create a seq
            s = new Seq();
            assertNotNull("null Seq returned from constructor", s);

            // set the bad vals
            s.setLoopStop(1);
            s.setStep(1);
            s.setLoopCount(1);

            // run valid
            result = s.valid();
            assertTrue("failed valid check", result);
        }
        catch (IllegalArgumentException e)
        {
            assertTrue("non-zero loopCount with not -1 loopStop failed: " + e,
            false);
        }

        // set loopStop and loopStart to 1 with loopCount set to 1:
        // should succeed
        try
        {
            // create a seq
            s = new Seq();
            assertNotNull("null Seq returned from constructor", s);

            // set the bad vals
            s.setLoopStart(1);
            s.setLoopStop(1);
            s.setStep(1);
            s.setLoopCount(1);

            // run valid
            result = s.valid();
            assertTrue("failed valid check", result);
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(
                "non-zero loopCount with not -1 loopStop and loopStart failed",
                false);
        }
    }


    /**
     * This does a basic check of the getLength() method, and through it
     * the Seq internal calculate() method.
     */
    public void testGetLength()
    {
        Seq s = null;
        int length = 0;

        // create a seq
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);

        // set a simple sequence
        s.setStart(1);
        s.setStop(3);
        s.setStep(1);

        // confirm the sequence looks ok
        s.valid();

        // get the length
        length = s.getLength();
        assertEquals("wrong length returned", 3, length);
    } 


    /**
     * This sets up an ascending non-looping sequence and steps
     * through values with the hasNext() and getNext() methods.
     */
    public void testAscendGetNext()
    {
        Seq s = null;
        int length = 0;

        // create a seq
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);

        // set a simple sequence
        s.setStart(1);
        s.setStop(3);
        s.setStep(1);

        // confirm the sequence looks ok
        s.valid();

        // get the length
        length = s.getLength();
        assertEquals("wrong length returned", 3, length);

        // confirm that the numbers in the sequence are 1, 2, and 3
        assertTrue("doesn't have first element", s.hasNext());
        assertEquals("first element not 1", 1, s.getNext());
        assertTrue("doesn't have second element", s.hasNext());
        assertEquals("second element not 2", 2, s.getNext());
        assertTrue("doesn't have third element", s.hasNext());
        assertEquals("third element not 3", 3, s.getNext());
        assertFalse("has fourth element", s.hasNext());
    }


    /**
     * This sets up an ascending non-looping sequence and steps
     * through values with the get() method
     */
    public void testAscendGet()
    {
        Seq s = null;
        int length = 0;

        // create a seq
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);

        // set a simple sequence
        s.setStart(1);
        s.setStop(3);
        s.setStep(1);

        // confirm the sequence looks ok
        s.valid();

        // get the length
        length = s.getLength();
        assertEquals("wrong length returned", 3, length);

        // confirm that the numbers in the sequence are 1, 2, and 3
        assertEquals("first element not 1", 1, s.get(0));
        assertEquals("second element not 2", 2, s.get(1));
        assertEquals("third element not 3", 3, s.get(2));
    }


    /**
     * This sets up a descending non-looping sequence and steps
     * through values with the hasNext() and getNext() methods.
     */
    public void testDescendGetNext()
    {
        Seq s = null;
        int length = 0;

        // create a seq
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);

        // set a simple sequence
        s.setStart(3);
        s.setStop(1);
        s.setStep(-1);

        // confirm the sequence looks ok
        s.valid();

        // get the length
        length = s.getLength();
        assertEquals("wrong length returned", 3, length);

        // confirm that the numbers in the sequence are 1, 2, and 3
        assertTrue("doesn't have first element", s.hasNext());
        assertEquals("first element not 3", 3, s.getNext());
        assertTrue("doesn't have second element", s.hasNext());
        assertEquals("second element not 2", 2, s.getNext());
        assertTrue("doesn't have third element", s.hasNext());
        assertEquals("third element not 1", 1, s.getNext());
        assertFalse("has fourth element", s.hasNext());
    }


    /**
     * This sets up a descending non-looping sequence and steps
     * through values with the get() method.
     */
    public void testDescendGet()
    {
        Seq s = null;
        int length = 0;

        // create a seq
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);

        // set a simple sequence
        s.setStart(3);
        s.setStop(1);
        s.setStep(-1);

        // confirm the sequence looks ok
        s.valid();

        // get the length
        length = s.getLength();
        assertEquals("wrong length returned", 3, length);

        // confirm that the numbers in the sequence are 1, 2, and 3
        assertEquals("first element not 3", 3, s.get(0));
        assertEquals("second element not 2", 2, s.get(1));
        assertEquals("third element not 1", 1, s.get(2));
    }


    /**
     * This sets up an ascending single loop sequence and steps
     * through values with the hasNext() and getNext() methods.
     */
    public void testAscendLoop1GetNext()
    {
        Seq s = null;
        int length = 0;

        // create a seq
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);

        // set a simple sequence
        s.setStart(1);
        s.setStop(3);
        s.setStep(1);
        s.setLoopCount(1);

        // confirm the sequence looks ok
        s.valid();

        // get the length
        length = s.getLength();
        assertEquals("wrong length returned", 6, length);

        // confirm that the numbers in the sequence are 1, 2, and 3
        assertTrue("doesn't have first element", s.hasNext());
        assertEquals("first element not 1", 1, s.getNext());
        assertTrue("doesn't have second element", s.hasNext());
        assertEquals("second element not 2", 2, s.getNext());
        assertTrue("doesn't have third element", s.hasNext());
        assertEquals("third element not 3", 3, s.getNext());

        assertTrue("doesn't have fourth element", s.hasNext());
        assertEquals("fourth element not 1", 1, s.getNext());
        assertTrue("doesn't have fifth element", s.hasNext());
        assertEquals("fifth element not 2", 2, s.getNext());
        assertTrue("doesn't have sixth element", s.hasNext());
        assertEquals("sixth element not 3", 3, s.getNext());

        assertFalse("has seventh element", s.hasNext());
    }


    /**
     * This sets up an ascending single loop sequence and steps
     * through values with the get() method.
     */
    public void testAscendLoop1Get()
    {
        Seq s = null;
        int length = 0;

        // create a seq
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);

        // set a simple sequence
        s.setStart(1);
        s.setStop(3);
        s.setStep(1);
        s.setLoopCount(1);

        // confirm the sequence looks ok
        s.valid();

        // get the length
        length = s.getLength();
        assertEquals("wrong length returned", 6, length);

        // confirm that the numbers in the sequence are 1, 2, and 3
        assertEquals("first element not 1", 1, s.get(0));
        assertEquals("second element not 2", 2, s.get(1));
        assertEquals("third element not 3", 3, s.get(2));

        assertEquals("fourth element not 1", 1, s.get(3));
        assertEquals("fifth element not 2", 2, s.get(4));
        assertEquals("sixth element not 3", 3, s.get(5));
    }


    /**
     * This sets up a simple descending single loop sequence and steps
     * through values with the hasNext() and getNext() methods.
     */
    public void testDescendLoop1GetNext()
    {
        Seq s = null;
        int length = 0;

        // create a seq
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);

        // set a simple sequence
        s.setStart(3);
        s.setStop(1);
        s.setStep(-1);
        s.setLoopCount(1);

        // confirm the sequence looks ok
        s.valid();

        // get the length
        length = s.getLength();
        assertEquals("wrong length returned", 6, length);

        // confirm that the numbers in the sequence are 1, 2, and 3
        assertTrue("doesn't have first element", s.hasNext());
        assertEquals("first element not 3", 3, s.getNext());
        assertTrue("doesn't have second element", s.hasNext());
        assertEquals("second element not 2", 2, s.getNext());
        assertTrue("doesn't have third element", s.hasNext());
        assertEquals("third element not 1", 1, s.getNext());

        assertTrue("doesn't have fourth element", s.hasNext());
        assertEquals("fourth element not 3", 3, s.getNext());
        assertTrue("doesn't have fifth element", s.hasNext());
        assertEquals("fifth element not 2", 2, s.getNext());
        assertTrue("doesn't have sixth element", s.hasNext());
        assertEquals("sixth element not 1", 1, s.getNext());

        assertFalse("has seventh element", s.hasNext());
    }


    /**
     * This sets up a simple descending single loop sequence and steps
     * through values with the get() method.
     */
    public void testDescendLoop1Get()
    {
        Seq s = null;
        int length = 0;

        // create a seq
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);

        // set a simple sequence
        s.setStart(3);
        s.setStop(1);
        s.setStep(-1);
        s.setLoopCount(1);

        // confirm the sequence looks ok
        s.valid();

        // get the length
        length = s.getLength();
        assertEquals("wrong length returned", 6, length);

        // confirm that the numbers in the sequence are 1, 2, and 3
        assertEquals("first element not 3", 3, s.get(0));
        assertEquals("second element not 2", 2, s.get(1));
        assertEquals("third element not 1", 1, s.get(2));

        assertEquals("fourth element not 3", 3, s.get(3));
        assertEquals("fifth element not 2", 2, s.get(4));
        assertEquals("sixth element not 1", 1, s.get(5));
    }


    /**
     * This sets up an ascending triple loop sequence and steps
     * through values with the hasNext() and getNext() methods.
     */
    public void testAscendLoop3GetNext()
    {
        Seq s = null;
        int length = 0;

        // create a seq
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);

        // set a simple sequence
        s.setStart(1);
        s.setStop(3);
        s.setStep(1);
        s.setLoopCount(3);

        // confirm the sequence looks ok
        s.valid();

        // get the length
        length = s.getLength();
        assertEquals("wrong length returned", 12, length);

        // confirm that the numbers in the sequence are 1, 2, and 3
        assertTrue("doesn't have first element", s.hasNext());
        assertEquals("first element not 1", 1, s.getNext());
        assertTrue("doesn't have second element", s.hasNext());
        assertEquals("second element not 2", 2, s.getNext());
        assertTrue("doesn't have third element", s.hasNext());
        assertEquals("third element not 3", 3, s.getNext());

        assertTrue("doesn't have fourth element", s.hasNext());
        assertEquals("fourth element not 1", 1, s.getNext());
        assertTrue("doesn't have fifth element", s.hasNext());
        assertEquals("fifth element not 2", 2, s.getNext());
        assertTrue("doesn't have sixth element", s.hasNext());
        assertEquals("sixth element not 3", 3, s.getNext());

        assertTrue("doesn't have seventh element", s.hasNext());
        assertEquals("seventh element not 1", 1, s.getNext());
        assertTrue("doesn't have eighth element", s.hasNext());
        assertEquals("eighth element not 2", 2, s.getNext());
        assertTrue("doesn't have ninth element", s.hasNext());
        assertEquals("ninth element not 3", 3, s.getNext());

        assertTrue("doesn't have tenth element", s.hasNext());
        assertEquals("tenth element not 1", 1, s.getNext());
        assertTrue("doesn't have eleventh element", s.hasNext());
        assertEquals("eleventh element not 2", 2, s.getNext());
        assertTrue("doesn't have twelfth element", s.hasNext());
        assertEquals("twelfth element not 3", 3, s.getNext());

        assertFalse("has thirteenth element", s.hasNext());
    }


    /**
     * This sets up an ascending triple loop sequence and steps
     * through values with the get() method.
     */
    public void testAscendLoop3Get()
    {
        Seq s = null;
        int length = 0;

        // create a seq
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);

        // set a simple sequence
        s.setStart(1);
        s.setStop(3);
        s.setStep(1);
        s.setLoopCount(3);

        // confirm the sequence looks ok
        s.valid();

        // get the length
        length = s.getLength();
        assertEquals("wrong length returned", 12, length);

        // confirm that the numbers in the sequence are 1, 2, and 3
        assertEquals("first element not 1", 1, s.get(0));
        assertEquals("second element not 2", 2, s.get(1));
        assertEquals("third element not 3", 3, s.get(2));

        assertEquals("fourth element not 1", 1, s.get(3));
        assertEquals("fifth element not 2", 2, s.get(4));
        assertEquals("sixth element not 3", 3, s.get(5));

        assertEquals("seventh element not 1", 1, s.get(6));
        assertEquals("eighth element not 2", 2, s.get(7));
        assertEquals("ninth element not 3", 3, s.get(8));

        assertEquals("tenth element not 1", 1, s.get(9));
        assertEquals("eleventh element not 2", 2, s.get(10));
        assertEquals("twelfth element not 3", 3, s.get(11));
    }


    /**
     * This sets up a simple descending triple loop sequence and steps
     * through values with the hasNext() and getNext() methods.
     */
    public void testDescendLoop3GetNext()
    {
        Seq s = null;
        int length = 0;

        // create a seq
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);

        // set a simple sequence
        s.setStart(3);
        s.setStop(1);
        s.setStep(-1);
        s.setLoopCount(3);

        // confirm the sequence looks ok
        s.valid();

        // get the length
        length = s.getLength();
        assertEquals("wrong length returned", 12, length);

        // confirm that the numbers in the sequence are 1, 2, and 3
        assertTrue("doesn't have first element", s.hasNext());
        assertEquals("first element not 3", 3, s.getNext());
        assertTrue("doesn't have second element", s.hasNext());
        assertEquals("second element not 2", 2, s.getNext());
        assertTrue("doesn't have third element", s.hasNext());
        assertEquals("third element not 1", 1, s.getNext());

        assertTrue("doesn't have fourth element", s.hasNext());
        assertEquals("fourth element not 3", 3, s.getNext());
        assertTrue("doesn't have fifth element", s.hasNext());
        assertEquals("fifth element not 2", 2, s.getNext());
        assertTrue("doesn't have sixth element", s.hasNext());
        assertEquals("sixth element not 1", 1, s.getNext());

        assertTrue("doesn't have seventh element", s.hasNext());
        assertEquals("seventh element not 3", 3, s.getNext());
        assertTrue("doesn't have eighth element", s.hasNext());
        assertEquals("eighth element not 2", 2, s.getNext());
        assertTrue("doesn't have ninth element", s.hasNext());
        assertEquals("ninth element not 1", 1, s.getNext());

        assertTrue("doesn't have tenth element", s.hasNext());
        assertEquals("tenth element not 3", 3, s.getNext());
        assertTrue("doesn't have eleventh element", s.hasNext());
        assertEquals("eleventh element not 2", 2, s.getNext());
        assertTrue("doesn't have twelfth element", s.hasNext());
        assertEquals("twelfth element not 1", 1, s.getNext());

        assertFalse("has thirteenth element", s.hasNext());
    }


    /**
     * This sets up a simple descending triple loop sequence and steps
     * through values with the get() method.
     */
    public void testDescendLoop3Get()
    {
        Seq s = null;
        int length = 0;

        // create a seq
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);

        // set a simple sequence
        s.setStart(3);
        s.setStop(1);
        s.setStep(-1);
        s.setLoopCount(3);

        // confirm the sequence looks ok
        s.valid();

        // get the length
        length = s.getLength();
        assertEquals("wrong length returned", 12, length);

        // confirm that the numbers in the sequence are 1, 2, and 3
        assertEquals("first element not 3", 3, s.get(0));
        assertEquals("second element not 2", 2, s.get(1));
        assertEquals("third element not 1", 1, s.get(2));

        assertEquals("fourth element not 3", 3, s.get(3));
        assertEquals("fifth element not 2", 2, s.get(4));
        assertEquals("sixth element not 1", 1, s.get(5));

        assertEquals("seventh element not 3", 3, s.get(6));
        assertEquals("eighth element not 2", 2, s.get(7));
        assertEquals("ninth element not 1", 1, s.get(8));

        assertEquals("tenth element not 3", 3, s.get(9));
        assertEquals("eleventh element not 2", 2, s.get(10));
        assertEquals("twelfth element not 1", 1, s.get(11));
    }


    /**
     * This sets up an ascending single loop sequence with a specific
     * loopStart value
     */
    public void testAscendLoopStartGetNext()
    {
        Seq s = null;
        int length = 0;

        // create a seq
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);

        // set a simple sequence
        s.setStart(1);
        s.setStop(3);
        s.setStep(1);
        s.setLoopStart(0);
        s.setLoopCount(1);

        // confirm the sequence looks ok
        s.valid();

        // get the length
        length = s.getLength();
        assertEquals("wrong length returned", 7, length);

        // confirm that the numbers in the sequence are 1, 2, and 3
        assertTrue("doesn't have first element", s.hasNext());
        assertEquals("first element not 1", 1, s.getNext());
        assertTrue("doesn't have second element", s.hasNext());
        assertEquals("second element not 2", 2, s.getNext());
        assertTrue("doesn't have third element", s.hasNext());
        assertEquals("third element not 3", 3, s.getNext());

        assertTrue("doesn't have fourth element", s.hasNext());
        assertEquals("fourth element not 0", 0, s.getNext());
        assertTrue("doesn't have fifth element", s.hasNext());
        assertEquals("fifth element not 1", 1, s.getNext());
        assertTrue("doesn't have sixth element", s.hasNext());
        assertEquals("sixth element not 2", 2, s.getNext());
        assertTrue("doesn't have seventh element", s.hasNext());
        assertEquals("seventh element not 3", 3, s.getNext());

        assertFalse("has eighth element", s.hasNext());
    }


    /**
     * This sets up an ascending single loop sequence with a specific
     * loopStart value
     */
    public void testAscendLoopStartGet()
    {
        Seq s = null;
        int length = 0;

        // create a seq
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);

        // set a simple sequence
        s.setStart(1);
        s.setStop(3);
        s.setStep(1);
        s.setLoopStart(0);
        s.setLoopCount(1);

        // confirm the sequence looks ok
        s.valid();

        // get the length
        length = s.getLength();
        assertEquals("wrong length returned", 7, length);

        // confirm that the numbers in the sequence are 1, 2, and 3
        assertEquals("first element not 1", 1, s.get(0));
        assertEquals("second element not 2", 2, s.get(1));
        assertEquals("third element not 3", 3, s.get(2));

        assertEquals("fourth element not 0", 0, s.get(3));
        assertEquals("fifth element not 1", 1, s.get(4));
        assertEquals("sixth element not 2", 2, s.get(5));
        assertEquals("seventh element not 3", 3, s.get(6));
    }


    /**
     * This sets up an ascending single loop sequence with a specific
     * loopStop value
     */
    public void testAscendLoopStopGetNext()
    {
        Seq s = null;
        int length = 0;

        // create a seq
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);

        // set a simple sequence
        s.setStart(1);
        s.setStop(3);
        s.setStep(1);

        s.setLoopStop(4);
        s.setLoopCount(1);

        // confirm the sequence looks ok
        s.valid();

        // get the length
        length = s.getLength();
        assertEquals("wrong length returned", 7, length);

        // confirm that the numbers in the sequence are 1, 2, and 3
        assertTrue("doesn't have first element", s.hasNext());
        assertEquals("first element not 1", 1, s.getNext());
        assertTrue("doesn't have second element", s.hasNext());
        assertEquals("second element not 2", 2, s.getNext());
        assertTrue("doesn't have third element", s.hasNext());
        assertEquals("third element not 3", 3, s.getNext());

        assertTrue("doesn't have fourth element", s.hasNext());
        assertEquals("fourth element not 1", 1, s.getNext());
        assertTrue("doesn't have fifth element", s.hasNext());
        assertEquals("fifth element not 2", 2, s.getNext());
        assertTrue("doesn't have sixth element", s.hasNext());
        assertEquals("sixth element not 3", 3, s.getNext());
        assertTrue("doesn't have seventh element", s.hasNext());
        assertEquals("seventh element not 4", 4, s.getNext());

        assertFalse("has eighth element", s.hasNext());
    }


    /**
     * This sets up an ascending single loop sequence with a specific
     * loopStop value
     */
    public void testAscendLoopStopGet()
    {
        Seq s = null;
        int length = 0;

        // create a seq
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);

        // set a simple sequence
        s.setStart(1);
        s.setStop(3);
        s.setStep(1);

        s.setLoopStop(4);
        s.setLoopCount(1);

        // confirm the sequence looks ok
        s.valid();

        // get the length
        length = s.getLength();
        assertEquals("wrong length returned", 7, length);

        // confirm that the numbers in the sequence are 1, 2, and 3
        assertEquals("first element not 1", 1, s.get(0));
        assertEquals("second element not 2", 2, s.get(1));
        assertEquals("third element not 3", 3, s.get(2));

        assertEquals("fourth element not 1", 1, s.get(3));
        assertEquals("fifth element not 2", 2, s.get(4));
        assertEquals("sixth element not 3", 3, s.get(5));
        assertEquals("seventh element not 4", 4, s.get(6));
    }


    /**
     * This sets up an ascending single loop sequence with a specific
     * loopStart and loopStop value
     */
    public void testAscendLoopStartStopGetNext()
    {
        Seq s = null;
        int length = 0;

        // create a seq
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);

        // set a simple sequence
        s.setStart(1);
        s.setStop(3);
        s.setStep(1);

        s.setLoopStart(0);
        s.setLoopStop(4);
        s.setLoopCount(1);

        // confirm the sequence looks ok
        s.valid();

        // get the length
        length = s.getLength();
        assertEquals("wrong length returned", 8, length);

        // confirm that the numbers in the sequence are 1, 2, and 3
        assertTrue("doesn't have first element", s.hasNext());
        assertEquals("first element not 1", 1, s.getNext());
        assertTrue("doesn't have second element", s.hasNext());
        assertEquals("second element not 2", 2, s.getNext());
        assertTrue("doesn't have third element", s.hasNext());
        assertEquals("third element not 3", 3, s.getNext());

        assertTrue("doesn't have fourth element", s.hasNext());
        assertEquals("fourth element not 0", 0, s.getNext());
        assertTrue("doesn't have fifth element", s.hasNext());
        assertEquals("fifth element not 1", 1, s.getNext());
        assertTrue("doesn't have sixth element", s.hasNext());
        assertEquals("sixth element not 2", 2, s.getNext());
        assertTrue("doesn't have seventh element", s.hasNext());
        assertEquals("seventh element not 3", 3, s.getNext());
        assertTrue("doesn't have eighth element", s.hasNext());
        assertEquals("eighth element not 4", 4, s.getNext());

        assertFalse("has ninth element", s.hasNext());
    }


    /**
     * This sets up an ascending single loop sequence with a specific
     * loopStart and loopStop value
     */
    public void testAscendLoopStartStopGet()
    {
        Seq s = null;
        int length = 0;

        // create a seq
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);

        // set a simple sequence
        s.setStart(1);
        s.setStop(3);
        s.setStep(1);

        s.setLoopStart(0);
        s.setLoopStop(4);
        s.setLoopCount(1);

        // confirm the sequence looks ok
        s.valid();

        // get the length
        length = s.getLength();
        assertEquals("wrong length returned", 8, length);

        // confirm that the numbers in the sequence are 1, 2, and 3
        assertEquals("first element not 1", 1, s.get(0));
        assertEquals("second element not 2", 2, s.get(1));
        assertEquals("third element not 3", 3, s.get(2));

        assertEquals("fourth element not 0", 0, s.get(3));
        assertEquals("fifth element not 1", 1, s.get(4));
        assertEquals("sixth element not 2", 2, s.get(5));
        assertEquals("seventh element not 3", 3, s.get(6));
        assertEquals("eighth element not 4", 4, s.get(7));
    }


    /**
     * This sets up a descending single loop sequence with a specific
     * loopStart value
     */
    public void testDescendLoopStartGetNext()
    {
        Seq s = null;
        int length = 0;

        // create a seq
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);

        // set a simple sequence
        s.setStart(3);
        s.setStop(1);
        s.setStep(-1);

        s.setLoopStart(4);
        s.setLoopCount(1);

        // confirm the sequence looks ok
        s.valid();

        // get the length
        length = s.getLength();
        assertEquals("wrong length returned", 7, length);

        // confirm that the numbers in the sequence are 1, 2, and 3
        assertTrue("doesn't have first element", s.hasNext());
        assertEquals("first element not 3", 3, s.getNext());
        assertTrue("doesn't have second element", s.hasNext());
        assertEquals("second element not 2", 2, s.getNext());
        assertTrue("doesn't have third element", s.hasNext());
        assertEquals("third element not 1", 1, s.getNext());

        assertTrue("doesn't have fourth element", s.hasNext());
        assertEquals("fourth element not 4", 4, s.getNext());
        assertTrue("doesn't have fifth element", s.hasNext());
        assertEquals("fifth element not 3", 3, s.getNext());
        assertTrue("doesn't have sixth element", s.hasNext());
        assertEquals("sixth element not 2", 2, s.getNext());
        assertTrue("doesn't have seventh element", s.hasNext());
        assertEquals("seventh element not 1", 1, s.getNext());

        assertFalse("has eighth element", s.hasNext());
    }


    /**
     * This sets up a descending single loop sequence with a specific
     * loopStart value
     */
    public void testDescendLoopStartGet()
    {
        Seq s = null;
        int length = 0;

        // create a seq
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);

        // set a simple sequence
        s.setStart(3);
        s.setStop(1);
        s.setStep(-1);

        s.setLoopStart(4);
        s.setLoopCount(1);

        // confirm the sequence looks ok
        s.valid();

        // get the length
        length = s.getLength();
        assertEquals("wrong length returned", 7, length);

        // confirm that the numbers in the sequence are 1, 2, and 3
        assertEquals("first element not 3", 3, s.get(0));
        assertEquals("second element not 2", 2, s.get(1));
        assertEquals("third element not 1", 1, s.get(2));

        assertEquals("fourth element not 4", 4, s.get(3));
        assertEquals("fifth element not 3", 3, s.get(4));
        assertEquals("sixth element not 2", 2, s.get(5));
        assertEquals("seventh element not 1", 1, s.get(6));
    }


    /**
     * This sets up a descending single loop sequence with a specific
     * loopStop value
     */
    public void testDescendLoopStopGetNext()
    {
        Seq s = null;
        int length = 0;

        // create a seq
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);

        // set a simple sequence
        s.setStart(3);
        s.setStop(1);
        s.setStep(-1);

        s.setLoopStop(0);
        s.setLoopCount(1);

        // confirm the sequence looks ok
        s.valid();

        // get the length
        length = s.getLength();
        assertEquals("wrong length returned", 7, length);

        // confirm that the numbers in the sequence are 1, 2, and 3
        assertTrue("doesn't have first element", s.hasNext());
        assertEquals("first element not 3", 3, s.getNext());
        assertTrue("doesn't have second element", s.hasNext());
        assertEquals("second element not 2", 2, s.getNext());
        assertTrue("doesn't have third element", s.hasNext());
        assertEquals("third element not 1", 1, s.getNext());

        assertTrue("doesn't have fourth element", s.hasNext());
        assertEquals("fourth element not 3", 3, s.getNext());
        assertTrue("doesn't have fifth element", s.hasNext());
        assertEquals("fifth element not 2", 2, s.getNext());
        assertTrue("doesn't have sixth element", s.hasNext());
        assertEquals("sixth element not 1", 1, s.getNext());
        assertTrue("doesn't have seventh element", s.hasNext());
        assertEquals("seventh element not 0", 0, s.getNext());

        assertFalse("has eighth element", s.hasNext());
    }


    /**
     * This sets up a descending single loop sequence with a specific
     * loopStop value
     */
    public void testDescendLoopStopGet()
    {
        Seq s = null;
        int length = 0;

        // create a seq
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);

        // set a simple sequence
        s.setStart(3);
        s.setStop(1);
        s.setStep(-1);

        s.setLoopStop(0);
        s.setLoopCount(1);

        // confirm the sequence looks ok
        s.valid();

        // get the length
        length = s.getLength();
        assertEquals("wrong length returned", 7, length);

        // confirm that the numbers in the sequence are 1, 2, and 3
        assertEquals("first element not 3", 3, s.get(0));
        assertEquals("second element not 2", 2, s.get(1));
        assertEquals("third element not 1", 1, s.get(2));

        assertEquals("fourth element not 3", 3, s.get(3));
        assertEquals("fifth element not 2", 2, s.get(4));
        assertEquals("sixth element not 1", 1, s.get(5));
        assertEquals("seventh element not 0", 0, s.get(6));
    }


    /**
     * This sets up a descending single loop sequence with a specific
     * loopStart and loopStop value
     */
    public void testDescendLoopStartStopGetNext()
    {
        Seq s = null;
        int length = 0;

        // create a seq
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);

        // set a simple sequence
        s.setStart(3);
        s.setStop(1);
        s.setStep(-1);

        s.setLoopStart(4);
        s.setLoopStop(0);
        s.setLoopCount(1);

        // confirm the sequence looks ok
        s.valid();

        // get the length
        length = s.getLength();
        assertEquals("wrong length returned", 8, length);

        // confirm that the numbers in the sequence are 1, 2, and 3
        assertTrue("doesn't have first element", s.hasNext());
        assertEquals("first element not 3", 3, s.getNext());
        assertTrue("doesn't have second element", s.hasNext());
        assertEquals("second element not 2", 2, s.getNext());
        assertTrue("doesn't have third element", s.hasNext());
        assertEquals("third element not 1", 1, s.getNext());

        assertTrue("doesn't have fourth element", s.hasNext());
        assertEquals("fourth element not 4", 4, s.getNext());
        assertTrue("doesn't have fifth element", s.hasNext());
        assertEquals("fifth element not 3", 3, s.getNext());
        assertTrue("doesn't have sixth element", s.hasNext());
        assertEquals("sixth element not 2", 2, s.getNext());
        assertTrue("doesn't have seventh element", s.hasNext());
        assertEquals("seventh element not 1", 1, s.getNext());
        assertTrue("doesn't have eighth element", s.hasNext());
        assertEquals("eighth element not 0", 0, s.getNext());

        assertFalse("has ninth element", s.hasNext());
    }


    /**
     * This sets up a descending single loop sequence with a specific
     * loopStart and loopStop value
     */
    public void testDescendLoopStartStopGet()
    {
        Seq s = null;
        int length = 0;

        // create a seq
        s = new Seq();
        assertNotNull("null Seq returned from constructor", s);

        // set a simple sequence
        s.setStart(3);
        s.setStop(1);
        s.setStep(-1);

        s.setLoopStart(4);
        s.setLoopStop(0);
        s.setLoopCount(1);

        // confirm the sequence looks ok
        s.valid();

        // get the length
        length = s.getLength();
        assertEquals("wrong length returned", 8, length);

        // confirm that the numbers in the sequence are 1, 2, and 3
        assertEquals("first element not 3", 3, s.get(0));
        assertEquals("second element not 2", 2, s.get(1));
        assertEquals("third element not 1", 1, s.get(2));

        assertEquals("fourth element not 4", 4, s.get(3));
        assertEquals("fifth element not 3", 3, s.get(4));
        assertEquals("sixth element not 2", 2, s.get(5));
        assertEquals("seventh element not 1", 1, s.get(6));
        assertEquals("eighth element not 0", 0, s.get(7));
    }


    /**
     * This tests that the Document constructor failure detects work.
     */
    public void testBadDocumentConstructor()
    {
        Seq s = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;


        // check that if the tag name isn't <seq> that it fails
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<notseq start='1' stop='2'/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            s = new Seq(doc);

            // if we get here, that's bad
            assertNotNull("non seq construction succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }


        // check that if the seq tag has children, it fails
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<seq start='1' stop='2'> <child1/> </seq>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            s = new Seq(doc);

            // if we get here, that's bad
            assertNotNull("seq with child construction succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }


        // check that if the seq tag has bogus fields, it fails
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<seq foo='1'/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            s = new Seq(doc);

            // if we get here, that's bad
            assertNotNull("seq with bad att name construction succeeded", null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }


        // check that if the seq attribute has a bad value, it fails
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<seq start='baz'/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            s = new Seq(doc);

            // if we get here, that's bad
            assertNotNull("seq with bad att value construction succeeded",null);
        }
        catch(IllegalArgumentException e)
        {}
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * This tests that the Document constructor success works.
     */
    public void testGoodDocumentConstructor()
    {
        Seq s = null;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;


        // check that creation with a start value is successful
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<seq start='1'/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            s = new Seq(doc);

            // check that we got a valid seq, with the correct values
            assertNotNull("got null seq", s);
            assertEquals("incorrect start value", 1, s.getStart() ); 
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }


        // check that creation with a stop value is successful
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<seq stop='1'/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            s = new Seq(doc);

            // check that we got a valid seq, with the correct values
            assertNotNull("got null seq", s);
            assertEquals("incorrect stop value", 1, s.getStop() ); 
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }


        // check that creation with a step value is successful
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<seq step='1.0'/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            s = new Seq(doc);

            // check that we got a valid seq, with the correct values
            assertNotNull("got null seq", s);
            assertEquals("incorrect step value", 1.0, s.getStep() ); 
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }


        // check that creation with a loopStart value is successful
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<seq loopstart='1'/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            s = new Seq(doc);

            // check that we got a valid seq, with the correct values
            assertNotNull("got null seq", s);
            assertEquals("incorrect loopstart value", 1, s.getLoopStart() ); 
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }


        // check that creation with a loopStop value is successful
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<seq loopstop='1'/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            s = new Seq(doc);

            // check that we got a valid seq, with the correct values
            assertNotNull("got null seq", s);
            assertEquals("incorrect loopstop value", 1, s.getLoopStop() ); 
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }


        // check that creation with a loopCount value is successful
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<seq loopcount='1'/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            s = new Seq(doc);

            // check that we got a valid seq, with the correct values
            assertNotNull("got null seq", s);
            assertEquals("incorrect loopcount value", 1, s.getLoopCount() ); 
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }


        // check that creation with a start and a stop value is successful
        try
        {
            // create the xml code in a string
            String xml = new String(
            "<seq start='1' stop='2'/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            s = new Seq(doc);

            // check that we got a valid seq, with the correct values
            assertNotNull("got null seq", s);
            assertEquals("incorrect start value", 1, s.getStart() ); 
            assertEquals("incorrect stop value", 2, s.getStop() ); 
        }
        catch(Exception e)
        {
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * This tests the hold behaviour, for a non looping sequence
     */
    public void testNoLoopHold()
    {
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        try
        {
            // create the xml code in a string
            String xml = new String(
            "<seq start='1' stop='2' step='1'/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            Seq s = new Seq(doc);

            // check that we got a valid seq, with the correct values
            assertNotNull("got null seq", s);
            assertEquals("incorrect start value", 1, s.getStart() ); 
            assertEquals("incorrect stop value", 2, s.getStop() ); 

            // check that the sequence is 2 long, and has the correct value
            assertEquals("wrong length", 2, s.getLength() );
            assertEquals("first is wrong", 1, s.getNext() );
            assertEquals("second is wrong", 2, s.getNext() );
            assertEquals("hold 1 is wrong", 2, s.getNext() );
            assertEquals("hold 2 is wrong", 2, s.getNext() );
            assertEquals("hold 3 is wrong", 2, s.getNext() );
            assertEquals("hold 4 is wrong", 2, s.getNext() );
            assertEquals("hold 5 is wrong", 2, s.getNext() );
       }
        catch(Exception e)
        {
            e.printStackTrace();
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * This tests the hold behaviour, for a single loop sequence with no
     * loopStop
     */
    public void testLoopHold()
    {
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        try
        {
            // create the xml code in a string
            String xml = new String(
            "<seq start='1' stop='2' step='1' loopcount='1'/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            Seq s = new Seq(doc);

            // check that we got a valid seq, with the correct values
            assertNotNull("got null seq", s);
            assertEquals("incorrect start value", 1, s.getStart() ); 
            assertEquals("incorrect stop value", 2, s.getStop() ); 
            assertEquals("incorrect loopcount value", 1, s.getLoopCount() ); 

            // check that the sequence is 2 long, and has the correct value
            assertEquals("wrong length", 4, s.getLength() );
            assertEquals("first is wrong", 1, s.getNext() );
            assertEquals("second is wrong", 2, s.getNext() );
            assertEquals("third is wrong", 1, s.getNext() );
            assertEquals("fourth is wrong", 2, s.getNext() );
            assertEquals("hold 1 is wrong", 2, s.getNext() );
            assertEquals("hold 2 is wrong", 2, s.getNext() );
            assertEquals("hold 3 is wrong", 2, s.getNext() );
            assertEquals("hold 4 is wrong", 2, s.getNext() );
            assertEquals("hold 5 is wrong", 2, s.getNext() );
       }
        catch(Exception e)
        {
            e.printStackTrace();
            assertTrue("got exception: " + e, false);
        }
    }


    /**
     * This tests the hold behaviour, for a single loop sequence with an
     * explicit loopStop value
     */
    public void testLoopStopHold()
    {
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        try
        {
            // create the xml code in a string
            String xml = new String(
            "<seq start='1' stop='2' step='1' loopcount='1' loopstop='4'/>");

            // turn the string into a char[] and build the Document from it
            doc = builder.build( new CharArrayReader(xml.toCharArray()) );

            // create a seq from the document
            Seq s = new Seq(doc);

            // check that we got a valid seq, with the correct values
            assertNotNull("got null seq", s);
            assertEquals("incorrect start value", 1, s.getStart() ); 
            assertEquals("incorrect stop value", 2, s.getStop() ); 
            assertEquals("incorrect loopcount value", 1, s.getLoopCount() ); 

            // check that the sequence is 2 long, and has the correct value
            assertEquals("wrong length", 6, s.getLength() );
            assertEquals("first is wrong", 1, s.getNext() );
            assertEquals("second is wrong", 2, s.getNext() );
            assertEquals("third is wrong", 1, s.getNext() );
            assertEquals("fourth is wrong", 2, s.getNext() );
            assertEquals("fifth is wrong", 3, s.getNext() );
            assertEquals("sixth is wrong", 4, s.getNext() );
            assertEquals("hold 1 is wrong", 4, s.getNext() );
            assertEquals("hold 2 is wrong", 4, s.getNext() );
            assertEquals("hold 3 is wrong", 4, s.getNext() );
            assertEquals("hold 4 is wrong", 4, s.getNext() );
            assertEquals("hold 5 is wrong", 4, s.getNext() );
       }
        catch(Exception e)
        {
            e.printStackTrace();
            assertTrue("got exception: " + e, false);
        }
    }

}
