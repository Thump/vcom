
/*****************************************************************************
 *
 * VCom: video compositor
 *
 * source file: Video.java
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
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.io.FileOutputStream;
import java.io.File;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Attribute;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;


/**
 * This object models the Video tag.
 */
public class Video
{
    /**
     * This is the name (prefix) for the directories, temporary files,
     * frame files, and final video
     */
    private String name = null;

    /**
     * This is the x- and y-dimension of the frames and video
     */
    private int xSize = 720;
    private int ySize = 480;

    /**
     * This is the default background image color
     */
    private String bgColor = "white";

    /**
     * This is the frame rate of the final video
     */
    private double fps = 30;

    /**
     * This is the name of the encoder to use when generating the final
     * video.
     */
    private String encoder = "xvid";

    /**
     * This is an array of options to the encoder
     */
    private String[] encoderOpts = null;

    /**
     * This is the base directory below which source, work, tmp, and
     * final files will be read
     */
    private String workRoot = "./";
    private String finalRoot = "./";

    /**
     * This holds the imageclips, in order of being processed
     */
    private ArrayList imageClips;

    /**
     * This holds the soundclips, in order of being processed
     */
    private ArrayList soundClips;

    /**
     * oldFrames holds the old frames document.
     */
    private Element oldFrames = null;

    /**
     * This holds the map of the old frames and their dates
     */
    private Map oldImages = null;

    /**
     * This is true if the video header is changed, and therefore all
     * images must be re-rendered.
     */
    private boolean renderAll = false;


    /**
     * Default constructor: assigns an arraylist to imageClips and
     * soundClips
     */
    public Video()
    {
        imageClips = new ArrayList();
        soundClips = new ArrayList();
    }


    /**
     * This is the Document constructor
     */
    public Video(Document doc)
    {
        // check we don't have a null doc
        if ( doc == null )
        {
            throw new IllegalArgumentException(
                "Video created from null document");
        }

        // initialize the clips
        imageClips = new ArrayList();
        soundClips = new ArrayList();

        // get the list of the content
        List content = doc.getContent();

        // we better have only a single element to process
        if ( content.size() != 1 )
        {
            throw new IllegalArgumentException(
                "<video> must have a single node");
        }

        // get the element from the document
        Element e = (Element) content.get(0);

        // the name of the element must be <video>
        if ( ! e.getName().equals("video") )
        {
            throw new IllegalArgumentException(
                "video tag must be called <video>");
        }

        // get the attributes
        List atts = e.getAttributes();

        // there must be some attributes
        if ( atts.size() == 0 )
        {
            throw new IllegalArgumentException(
                "video tag must have attributes");
        }

        // process the attributes
        Iterator i = atts.iterator();
        while ( i.hasNext() )
        {
            Attribute att = (Attribute) i.next();

            // process the name
            if ( att.getName().equals("name") )
            {
                setName(att.getValue());
                continue;
            }

            // process xsize
            if ( att.getName().equals("xsize") )
            {
                setXSize( Integer.parseInt(att.getValue()) );
                continue;
            }

            // process ysize
            if ( att.getName().equals("ysize") )
            {
                setYSize( Integer.parseInt(att.getValue()) );
                continue;
            }

            // process fps
            if ( att.getName().equals("fps") )
            {
                setFps( Double.parseDouble(att.getValue()) );
                continue;
            }

            // process encoder
            if ( att.getName().equals("encoder") )
            {
                setEncoder(att.getValue());
                continue;
            }

            // process encoderopts
            if ( att.getName().equals("encoderopts") )
            {
                setEncoderOpts( att.getValue().split(";") );
                continue;
            }

            // process bgcolor
            if ( att.getName().equals("bgcolor") )
            {
                setBgColor(att.getValue());
                continue;
            }

            // process workroot
            if ( att.getName().equals("workroot") )
            {
                setWorkRoot(att.getValue());
                continue;
            }

            // process finalroot
            if ( att.getName().equals("finalroot") )
            {
                setFinalRoot(att.getValue());
                continue;
            }

            throw new IllegalArgumentException(
                "unrecognized video attribute: " + att.getName() );
        }

        // process the children
        List children = e.getChildren();
        i = children.iterator();
        while ( i.hasNext() )
        {
            Element child = (Element) i.next();

            // process an image clip
            if ( child.getName().equals("imageclip") )
            {
                // clone the child, make a new doc from it, and create
                // an image clip from that doc
                ImageClip ic = new ImageClip(
                     new Document((Element) child.clone()) );
                addImageClip(ic);
                continue;
            }

            // process a sound clip
            if ( child.getName().equals("soundclip") )
            {
                // clone the child, make a new doc from it, and create
                // a sound clip from that doc
                SoundClip sc = new SoundClip(
                     new Document((Element) child.clone()) );
                addSoundClip(sc);
                continue;
            }

            throw new IllegalArgumentException(
                "unrecognized video child tag: " + child.getName() );
        }
    }


    /**
     * This is the getter for name
     */
    public String getName()
    { return name; }

    /**
     * This is the setter for name
     */
    public void setName(String name)
    { this.name = name; }


    /**
     * This is the getter for xSize
     */
    public int getXSize()
    { return xSize; }

    /**
     * This is the setter for xSize
     */
    public void setXSize(int xSize)
    {
        // can't set to negative
        if ( xSize < 0 )
        {
            throw new IllegalArgumentException("can't set negative xSize");
        }

        this.xSize = xSize;
    }


    /**
     * This is the getter for ySize
     */
    public int getYSize()
    { return ySize; }

    /**
     * This is the setter for ySize
     */
    public void setYSize(int ySize)
    {
        // can't set to negative
        if ( ySize < 0 )
        {
            throw new IllegalArgumentException("can't set negative ySize");
        }

        this.ySize = ySize;
    }


    /**
     * This is the getter for fps
     */
    public double getFps()
    { return fps; }

    /**
     * This is the setter for fps
     */
    public void setFps(double fps)
    {
        // can't set to negative
        if ( fps < 0 )
        {
            throw new IllegalArgumentException("can't set negative fps");
        }

        this.fps = fps;
    }


    /**
     * This is the getter for encoder
     */
    public String getEncoder()
    { return encoder; }

    /**
     * This is the setter for encoder
     */
    public void setEncoder(String encoder)
    { this.encoder = encoder; }


    /**
     * This is the getter for encoderOpts
     */
    public String[] getEncoderOpts()
    { return encoderOpts; }

    /**
     * This is the setter for encoderOpts
     */
    public void setEncoderOpts(String[] encoderOpts)
    { this.encoderOpts = encoderOpts; }


    /**
     * This is the getter for workRoot
     */
    public String getWorkRoot()
    { return workRoot; }

    /**
     * This is the setter for workRoot
     */
    public void setWorkRoot(String workRoot)
    {
        // can't set it to null
        if ( workRoot == null )
        {
            throw new IllegalArgumentException("cannot set workRoot to null");
        }

        this.workRoot = workRoot;
    }


    /**
     * This is the getter for bgColor
     */
    public String getBgColor()
    { return bgColor; }

    /**
     * This is the setter for bgColor
     */
    public void setBgColor(String bgColor)
    {
        // can't set it to null
        if ( bgColor == null )
        {
            throw new IllegalArgumentException("cannot set bgColor to null");
        }
        this.bgColor = bgColor;
    }


    /**
     * This is the getter for finalRoot
     */
    public String getFinalRoot()
    { return finalRoot; }

    /**
     * This is the setter for finalRoot
     */
    public void setFinalRoot(String finalRoot)
    {
        // can't set it to null
        if ( finalRoot == null )
        {
            throw new IllegalArgumentException("cannot set finalRoot to null");
        }
        this.finalRoot = finalRoot;
    }


    /**
     * This is the getter for soundclips
     */
    public ArrayList getImageClips()
    { return imageClips; }

    /**
     * This is the setter for soundclips
     */
    public void setImageClips(ArrayList imageClips)
    {
        this.imageClips = imageClips;
    }

    /**
     * This adds an image clip to the imageClips array list
     */
    public void addImageClip(ImageClip clip)
    {
        // can't set it to null
        if ( clip == null )
        {
            throw new IllegalArgumentException("cannot add null image clip");
        }

        imageClips.add(clip);
    }


    /**
     * This is the getter for soundClips
     */
    public ArrayList getSoundClips()
    { return soundClips; }

    /**
     * This is the setter for soundClips
     */
    public void setSoundClips(ArrayList soundClips)
    {
        this.soundClips = soundClips;
    }

    /**
     * This adds a sound clip to the soundClips array list
     */
    public void addSoundClip(SoundClip clip)
    {
        // can't set it to null
        if ( clip == null )
        {
            throw new IllegalArgumentException("cannot add null sound clip");
        }

        soundClips.add(clip);
    }


    /**
     * This returns true if the Video object is valid to be processed,
     * and IllegalArgumentException otherwise
     */
    public boolean valid()
    {
        Iterator i = null;

        // iterate through all image clips, processing each
        i = imageClips.iterator();
        while ( i.hasNext() )
        {
            ((ImageClip)i.next()).valid();
        }

        // iterate through all sound clips, processing each
        i = soundClips.iterator();
        while ( i.hasNext() )
        {
            ((SoundClip)i.next()).valid();
        }

        return true;
    }


    /**
     * This returns the first target frame number of all imageclips in this
     * video object
     */
    public int firstFrame()
    {
        // we set the default beginning to -1
        int frame = -1;

        // we iterate across all image clips, tracking the smallest
        // targetStart
        Iterator i = imageClips.iterator();
        while ( i.hasNext() )
        {
            // get the image clip
            ImageClip ic = (ImageClip) i.next();

            // if the image clip starts earlier than the current lowest,
            // or if this is the first image clip processed, store the start
            if ( frame > ic.getTargetStart() || frame == -1 )
            {
                frame = ic.getTargetStart();
            }
        }

        return frame;
    }


    /**
     * This returns the last target frame number of all imageclips in this
     * video object
     */
    public int lastFrame()
    {
        // we set the default beginning to -1
        int frame = -1;

        // we iterate across all image clips, tracking the smallest
        // targetStart
        Iterator i = imageClips.iterator();
        while ( i.hasNext() )
        {
            // get the image clip
            ImageClip ic = (ImageClip) i.next();

            // the end target frame is the targetStart plus the length
            // less one, since we're zero-based
            int icEndFrame = ic.getTargetStart() + ic.getLength() - 1;

            // if the image clip starts earlier than the current lowest,
            // or if this is the first image clip processed, store the start
            if ( frame < icEndFrame || frame == -1 )
            {
                frame = icEndFrame;
            }
        }

        return frame;
    }


    /**
     * This is intended to print the contents of the video to screen
     */
    public String toString()
    {
        StringBuffer s = new StringBuffer();

        s.append("\n");
        s.append("video:\n");
        s.append("  name: " + name + "\n");
        s.append("  xsize: " + xSize + "\n");
        s.append("  ysize: " + ySize + "\n");
        s.append("  bgcolor: " + bgColor + "\n");
        s.append("  fps: " + fps + "\n");
        s.append("  encoder: " + encoder + "\n");
        s.append("  workroot: " + workRoot + "\n");
        s.append("  finalroot: " + finalRoot + "\n");

        s.append("\n");
        s.append("number of ImageClips: " + imageClips.size());
        s.append("\n");

        Iterator i = imageClips.iterator();
        int count = 0;
        while ( i.hasNext() )
        {
            s.append("\n");
            s.append("imageclip " + count + ":\n");
            ImageClip ic = (ImageClip) i.next();
            s.append(ic.toString("  "));
            count++;
        }

        return s.toString();
    }


    /**
     * This is the normal high level method: it will process the video
     * and generate the frames xml file
     */
    public void process()
    {
        // get the frames document
        Document frames = getFrames();

        // make the work directory
        File workRoot = new File(getWorkRoot());
        workRoot.mkdirs();

        // construct the destination frames file name, and generate it
        String framesFile = new String(getWorkRoot() + "/frames.xml");
        Util.print(frames, framesFile);
    }


    /**
     * This method returns a frames document, for all frames in its
     * image clips
     */
    public Document getFrames()
    {
        // create the root <frames> tag
        Element frames = new Element("frames");

        // add the attributes
        frames.setAttribute( new Attribute("name", getName()) );
        frames.setAttribute(
            new Attribute("xsize", String.valueOf(getXSize())) );
        frames.setAttribute(
            new Attribute("ysize", String.valueOf(getYSize())) );
        frames.setAttribute( new Attribute("bgcolor", getBgColor()) );
        frames.setAttribute(
            new Attribute("fps", String.valueOf(getFps())) );
        frames.setAttribute( new Attribute("encoder", getEncoder()) );

        // expand the encoderOpts and ; separate them
        StringBuffer s = new StringBuffer();
        String sep = new String("");
        Iterator i = Arrays.asList(encoderOpts).iterator();
        while ( i.hasNext() )
        {
            s.append( (String) i.next() + sep );
            sep = ";";
        }

        frames.setAttribute( new Attribute("workroot", getWorkRoot()) );
        frames.setAttribute( new Attribute("finalroot", getFinalRoot()) );

        // get the old frames document, if one exists
        setOldFrames();

        // set the date on the frames video header
        frames.setAttribute(
            new Attribute("date", getElementDate(frames, oldFrames)) );

        // if we have a old frames, and the old frames are different than
        // the new ones, then set renderAll to true
        if ( oldFrames != null &&
             (! compareElementAttributes(frames, oldFrames)) )
        { renderAll = true; }

        // this sets the old frames map, for use with forward propagating
        // image dates
        setOldFrames();
        setOldImages();

        // step through all the frames
        for (int frame=firstFrame(); frame<=lastFrame(); frame++)
        {
            // get the frame
            frames.addContent( getFrame(frame) );
        }

        // step through all sound clips
        i = soundClips.iterator();
        while ( i.hasNext() )
        {
            SoundClip sc = (SoundClip) i.next();
            frames.addContent( getSound(sc) );
        }

        // make a document from the frames and return it
        return new Document(frames);
    }


    /**
     * This routine takes a soundclip and returns a <sound> tag for
     * the frames.xml file.
     */
    private Element getSound(SoundClip sc)
    {
        // create the element
        Element s = new Element("sound");

        // set the attributes
        s.setAttribute( new Attribute("file", sc.getSource()) );
        s.setAttribute( new Attribute("srcstart",
            String.valueOf(sc.getSourceStart())) );
        s.setAttribute( new Attribute("srcstop",
            String.valueOf(sc.getSourceStop())) );
        s.setAttribute( new Attribute("tgtstart",
            String.valueOf(sc.getTargetStart())) );

        return s;
    }


    /**
     * This method returns a frame element for the specified frame
     */
    public Element getFrame(int frameNumber)
    {
        // create the root <frame> tag
        Element f = new Element("frame");
        f.setAttribute( new Attribute("number", String.valueOf(frameNumber)) );

        // and now step through all clips
        Iterator i = imageClips.iterator();
        int imageNumber = 0;
        while ( i.hasNext() )
        {
            // get the specific image clip
            ImageClip ic = (ImageClip) i.next();

            // if the clip is in that frame, process it
            if ( ic.inFrame(frameNumber) )
            {
                // get the image from the image clip
                Element image = ic.getImage(frameNumber);

                // compare this image with the old one
                String oldImageKey = new String(""+frameNumber+":"+imageNumber);
                Element oldImage = (Element) oldImages.get(oldImageKey);

                // add the date stamp
                image.setAttribute(
                    new Attribute("date", getElementDate(image, oldImage)) );
                f.addContent(image);

                // increment the image count
                imageNumber++;
            }
        }

        return f;
    }


    /**
     * This routine reads the old frames file (if it exists), and
     * sets oldFrames to be equal to the <frames> tag from it.
     */
    private void setOldFrames()
    {
        // make a file for the old frames.xml
        File file = new File( getWorkRoot() + "/frames.xml" );

        // if there is no old frames file, return null
        if ( ! file.exists() )
        {
            return;
        }

        // otherwise, suck it up
        Document oldFramesDoc = Util.jellyReadDoc(file);

        // get the root element, and check its correct: return null if not
        Element root = oldFramesDoc.getRootElement(); 
        if ( ! root.getName().equals("frames") )
        {
            System.out.println("old frames file has bad root element: "
                + root.getName() + ", skipping");
            return;
        }

        oldFrames = root;
    }


    /**
     * This looks for the old frames file, loads it up, and creates
     * a hash map from it, which maps each frame:image number (ie 1:2
     * would be frame 2, image 1; both frame and image counting are
     * zero-based) to the image element describing that
     * image.
     */
    private void setOldImages()
    {
        oldImages = new HashMap();

        // if there is a null oldFrames doc, there are no old images
        if ( oldFrames == null )
        { return; }

        // otherwise get the frame objects from the old frames document
        List frames = oldFrames.getChildren("frame");

        // and step through each frame
        Iterator i1 = frames.iterator();
        while ( i1.hasNext() )
        {
            // get the frame number: if there isn't one, the file is bad,
            // and we abort
            Element frame = (Element) i1.next();
            String frameNumber = frame.getAttributeValue("number");
            if ( frameNumber == null )
            {
                return;
            }

            // get the image children of this frame
            List images = frame.getChildren("image");

            // and step through the image children of this frame, storing
            // each frame:image pair in the map, valued with the date of
            // the old image
            int imageNumber = 0;
            Iterator i2 = images.iterator();
            while ( i2.hasNext() )
            {
                // get the old date: if there isn't one, the file is corrupt
                // and we return null
                Element image = (Element) i2.next();
                String oldDate = image.getAttributeValue("date");
                if ( oldDate == null )
                {
                    return;
                }

                // otherwise we store the date against the frame:image pair
                oldImages.put("" + frameNumber + ":" + imageNumber, image);

                // increment our image count
                imageNumber++;
            }
        }
    }


    /**
     * This returns the date of an new element: its set to the date of the
     * old element, if the elements attributes are unchanged, or set to the
     * current date if it has changed.  If renderAll is true, the current
     * date is always returned.
     */
    private String getElementDate(Element elem, Element oldElem)
    {
        // we round new dates down to seconds, because filesystem access time
        // is usually in seconds, and this avoids spurious newer-than-dates
        String newDate =
            String.valueOf(1000*((long)(System.currentTimeMillis()/1000)));

        // if renderAll is set, set the date to the latest
        if ( renderAll )
        { return newDate; }

        // if either is null, return the current date
        if ( elem == null || oldElem == null )
        { return newDate; }

        // if there is no date attribute in the oldElem, return the current
        String oldDate = oldElem.getAttributeValue("date");
        if ( oldDate == null )
        { return newDate; }

        if ( compareElementAttributes(elem, oldElem) )
        { return oldDate; }

        // otherwise return the new date
        return newDate;
    }


    /**
     * This takes two elements and returns true if the elements have the
     * same attribute names and values, not including "date".
     */
    public boolean compareElementAttributes(Element elem, Element oldElem)
    {
        // check for nullness
        if ( elem == null || oldElem == null )
        { return false; }

        // get the list of attributes from both
        List attrs = elem.getAttributes();
        List oldAttrs = oldElem.getAttributes();

        // go through the new elem, make sure all its values are in the old
        // (except for date, which we don't care about)
        Iterator i = attrs.iterator();
        while ( i.hasNext() )
        {
            // get the attr
            Attribute att = (Attribute) i.next();
            String name = att.getName();
            String value = att.getValue();
            String oldValue = oldElem.getAttributeValue(name);

            // if the attr name is date, skip it
            if ( name.equals("date") )
            { continue; }

            // compare based on nullness
            if ( (oldValue == null && value != null) ||
                 (oldValue != null && value == null) )
            { return false; };

            // compare based on real value
            if ( ! oldValue.equals(value) )
            { return false; };
        }

        // if we got here, we know the new elem has the same attrs as the
        // old, but we also need to check that the old has no additional
        // attrs, so we do the converse checks too

        // go through the new elem, make sure all its values in the old
        // (except for date, which we don't care about)
        i = oldAttrs.iterator();
        while ( i.hasNext() )
        {
            // get the attr
            Attribute oldAtt = (Attribute) i.next();
            String oldName = oldAtt.getName();
            String oldValue = oldAtt.getValue();
            String value = elem.getAttributeValue(oldName);

            // if the attr name is date, skip it
            if ( oldName.equals("date") )
            { continue; }

            // compare based on nullness
            if ( (oldValue == null && value != null) ||
                 (oldValue != null && value == null) )
            { return false; };

            // compare based on real value
            if ( ! oldValue.equals(value) )
            { return false; };
        }

        // if we get here, they're the same, so return the old date
        return true;
    }
}
