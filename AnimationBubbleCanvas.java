package m6800.canvas;

/*
 * @(#)animationBubble.java
 *
 *
 */

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import m6800.canvas.components.GeneralTextLabel;

/**
 * Animation bubble is a class for the animation of the data movement between
 * registers
 * 
 * @author Simon McCaughey
 * @version 1.0, 29 April 1998
 */

public class AnimationBubbleCanvas extends Canvas {

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
     * variables to do with drawing the strings
     */
    private Font font;
    GeneralTextLabel bubbleLabel;
    long startTime, endTime;

    /**
     * Constructor : no arguments
     */
    public AnimationBubbleCanvas() {

        /* initialise to far off screen */
        x = 1000;
        y = 1000;

        /* create the label for display in the bubble */
        font = new Font("Helvetica", Font.BOLD, 12);
        bubbleLabel = new GeneralTextLabel("", font, getFontMetrics(font));

        w = 40; // set specific values for height and width
        h = 20;
        size = new Dimension(w + 1, h + 1); // +1 so that all area
        // up to w can be used
    }

    /**
     * Gets the size required by this Canvas Object
     */
    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

    /**
     * Gets the size required by this Canvas Object
     * <P>
     * Simply returns getMinimumSize()
     */
    public Dimension getMinimumSize() {
        return size;
    }

    /**
     * setLabel() sets the label to be displayed insude the bubble
     */
    public void setLabel(String lab) {
        bubbleLabel.setLabel(lab);
    }

    /**
     * Overrides the default paint method to draw the animating area
     * <P>
     * uses the update method to minimise screen flicker
     */
    public void update(Graphics g) {

        this.setLocation(x, y); // draw it where requested

        /* draw inside rectangle */
        g.setColor(Color.yellow);
        g.fillRect(2, 2, w - 3, h - 3);

        /* draw lines all round */
        g.setColor(Color.white);
        g.drawLine(0, 0, w - 1, 0);
        g.drawLine(0, 0, 0, h - 1);
        g.setColor(Color.lightGray);
        g.drawLine(1, 1, w - 2, 1);
        g.drawLine(1, 1, 1, h - 2);
        g.setColor(Color.darkGray);
        g.drawLine(1, h - 1, w - 1, h - 1);
        g.drawLine(w - 1, 1, w - 1, h - 1);
        g.setColor(Color.black);
        g.drawLine(0, h, w, h);
        g.drawLine(w, 0, w, h);

        /* draw string in box */
        g.setColor(Color.black);
        g.setFont(bubbleLabel.getFont());
        g.drawString(bubbleLabel.getLabel(),
                w / 2 - bubbleLabel.getWidth() / 2, bubbleLabel.getHeight());
    }

    /**
     * simply calls the update method
     */
    public void paint(Graphics g) {
        update(g);
    }
}
