
/**************************************************************************
 *
 * VComFrames: video compositor
 *
 * source file: RenderSoundI.java
 * package: net.vcom
 *
 * Copyright (c) 2005, Denis McLaughlin
 * Released under the GPL license, version 2
 *
 */

package net.vcom;

import org.jdom.Element;

/**
 * This program takes a per-frame xml file and generates the corresponding
 * video.
 */
public interface RenderSoundI
{
    /**
     * This processes the images and creates the video
     */
    public void renderSounds(Element root);

    /**
     * This prints some stats
     */
    public void printStats();
}
