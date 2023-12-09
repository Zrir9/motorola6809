package m6800.applet.components;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;

import m6800.applet.AppletColors;

/**
 * provides a resizing object which contains one image not my code - got it from
 * the Internet somewhere.
 */
public class AppletImageCanvas extends Panel {
    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    private Container parentContainer;
    private Image image;
    private Color centerColor = AppletColors.centerColor;
    private boolean trueSizeKnown = false;
    private Dimension minSize;
    private int w, h;

    public AppletImageCanvas(final Image image, final Container parent,
            final int initialWidth, final int initialHeight) {
        if (image == null) {
            System.err.println("Canvas got invalid image object!");
            centerColor = AppletColors.centerColor;
            this.setBackground(centerColor); // color the background
            return;
        }
        this.image = image;
        parentContainer = parent;
        w = initialWidth; // set initial height
        h = initialHeight;
        minSize = new Dimension(w, h);
    }

    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

    public synchronized Dimension getMinimumSize() {
        return minSize;
    }

    public void setMinimumSize(final Dimension minimumSize) {
        super.setMinimumSize(minimumSize);
        this.minSize = minimumSize;
    }

    /**
     * Paint method.
     */
    public void paint(final Graphics g) {
        if (image != null) {
            g.setColor(centerColor);
            g.drawImage(image, 0, 0, centerColor, this);
            if (!trueSizeKnown) {
                int imageWidth = image.getWidth(this);
                int imageHeight = image.getHeight(this);
                if ((imageWidth > 0) && (imageHeight > 0)) {
                    trueSizeKnown = true;
                    if ((w != imageWidth) && (h != imageHeight)) {
                        // only validate if different size to suggested
                        // Component-initiated resizing.
                        w = imageWidth;
                        h = imageHeight;
                        setMinimumSize(new Dimension(w, h));
                        parentContainer.validate();
                    }
                }
            }
        }
    }
}