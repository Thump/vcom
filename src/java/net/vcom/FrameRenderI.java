
/*****************************************************************************
 *
 * VComFrames: video compositor
 *
 * source file: FrameRenderI.java
 * package: net.vcom
 *
 * version 0.3
 * 2005-06-01
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
public interface FrameRenderI
{
    /**
     * This processes a frames document and creates the images
     */
    public void renderFrames(Element root);

    /**
     * This prints some stats
     */
    public void printStats();
}
