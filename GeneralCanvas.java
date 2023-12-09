package m6800.canvas;

/*
 * @(#)generalCanvas.java
 *
 *
 */

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import m6800.applet.AppletColors;

/**
 * A class designed to be instantiated inside each instance of any class
 * requiring the colour / activity attributes associated with all elements
 * inside the CPU
 * 
 * @author Simon McCaughey
 * @version 1.0, 29 April 1998
 */

/*
 * NOTE: No Constructor : abstract class which must be sub-classed and cannot be
 * Instantiated on its own
 */

public abstract class GeneralCanvas extends Canvas {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * x and y specify the (x,y) co-ordinates of this component within its
     * parent container
     */

    public int x, y;

    /*
     * size is the dimension necessary to ensure that this object is drawn on
     * the screen with its requested size
     */
    Dimension size;
    int ox, oy, w, h;

    /*
     * active is the variable that defines if the object has a outer or inner
     * bevel <P> false = inner bevel <P> true = outer bevel
     */
    boolean active = false; // set by methods

    /*
     * Colors defined for active and passive states, and also for use in drawing
     * the 3-d bevel effect
     */

    Color centerColor = AppletColors.centerColor;
    Color activeColor = AppletColors.activeColor;
    Color passiveColor = AppletColors.passiveColor;
    Color c1, c2, c3, c4;

    /**
     * Gets the size required by this Canvas Object
     */
    // //getPreferredSize() and getMinimumSize() are required by java
    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

    /**
     * Gets the size required by this Canvas Object
     * <P>
     * Simply returns getMinimumSize()
     */
    // //getPreferredSize() and getMinimumSize() are required by java
    public Dimension getMinimumSize() {
        return size;
    }

    /**
     * set the object active by raising the beveled edges
     */
    public void setActive() {
        if (active == false) {
            active = true;
            repaint();
        }
    }

    /**
     * set the object inactive (inward bevel)
     */
    public void setInActive() {
        if (active == true) {
            active = false;
            repaint();
        }
    }

    /**
     * Overrides the default paint method to draw the beveled edge
     */
    public void paint(Graphics g) {
        this.setLocation(x, y); // draw it where requested by container

        /*
         * determine the colors for line drawing -select active or passive
         * colors
         */
        if (active) {
            c1 = AppletColors.activeC1;
            c2 = AppletColors.activeC2;
            c3 = AppletColors.activeC3;
            c4 = AppletColors.activeC4;
            g.setColor(activeColor);
        } else {
            c1 = AppletColors.passiveC1;
            c2 = AppletColors.passiveC2;
            c3 = AppletColors.passiveC3;
            c4 = AppletColors.passiveC4;
            g.setColor(passiveColor);
        }
        /* draw inside rectangle */
        g.fillRect(2, 2, w - 3, h - 3);
        /* draw lines all round */
        g.setColor(c1);
        g.drawLine(0, 0, w - 1, 0);
        g.drawLine(0, 0, 0, h - 1);
        g.setColor(c2);
        g.drawLine(1, 1, w - 2, 1);
        g.drawLine(1, 1, 1, h - 2);
        g.setColor(c3);
        g.drawLine(1, h - 1, w - 1, h - 1);
        g.drawLine(w - 1, 1, w - 1, h - 1);
        g.setColor(c4);
        g.drawLine(0, h, w, h);
        g.drawLine(w, 0, w, h);
    }
}
