package m6800.canvas;

/*
 * @(#)bus.java
 *
 *
 */

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

/**
 * This class is for use by the CPU in conneting its compenent parts together
 * <P>
 * It provides 8 different draw formats allowing any bus part to be made, ie
 * four corners, horizontal and vertical connections, and upwards/downwards T
 * junctions
 * 
 * @author Simon McCaughey
 * @version 1.0, 29 April 1998
 */

public class BusCanvas extends Canvas implements Runnable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /*
     * Manager is the thread that this component runs in
     */
    Thread manager;

    /**
     * x and y specify the (x,y) co-ordinates of this component within its
     * parent container
     */
    public int x, y;

    /*
     * drawingMode is the variable to specify which type of component is drawn :
     * 0 : 1 : 2 : 3 : 4 : 5 : 6 : 7 :
     */
    private int drawingMode;

    /*
     * size is the dimension necessary to ensure that this object is drawn on
     * the screen with its requested size
     */
    Dimension size;
    int w, h;
    // /variables to do with drawing boxes & lines

    /*
     * active is the variable that defines if the object has a outer or inner
     * bevel <P> false = inner bevel <P> true = outer bevel
     */
    private boolean active;

    /*
     * Colors defined for active and passive states, and also for use in drawing
     * the 3-d bevel effect
     */
    private Color activeColor, passiveColor, c1, c2, c3, c4;

    /**
     * Constructor arguments : specify initial settings, such as absolute x
     * position, width/height, and drawing mode
     */
    public BusCanvas(int xOffset, int yOffset, int width, int height, int mode) {

        /* initialise to required position on screen */
        x = xOffset;
        y = yOffset;

        this.setBackground(Color.lightGray);
        setDrawingMode(0);

        // initialise x,y co-ords
        // ox=0;

        // oy=0;
        /* initialisation default to the passive state */
        active = false;
        /* initialise colors */
        activeColor = Color.blue;
        passiveColor = Color.cyan.darker();

        w = width; // set specific values for height and width
        h = height;
        size = new Dimension(w, h); // +1 so that all area
        // up to w can be used
    }

    /* make this a thread */
    public void run() {
    }

    public void start() {
        if (manager == null) {
            manager = new Thread(this);
            manager.start();
        }
    }

    public void stop() {
        if (manager != null) {
            manager = new Thread(this);
            // manager.stop();
            manager = null;
        }
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
     * sets the bevel on the edges of the object
     */
    public void setDrawingMode(int newMode) {
        if (drawingMode != newMode) {
            drawingMode = newMode;
            repaint();
        }
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
     * set the object active by lowering the beveled edges
     */
    public void setInActive() {
        if (active == true) {
            active = false;
            repaint();
        }
    }

    /**
     * Overrides the default paint method to draw the animating area
     * <P>
     * uses the update method to minimise screen flicker
     */
    public void update(Graphics g) {

        /*
         * determine the colors for line drawing -select active or passive
         * colors
         */
        if (active) {
            c1 = Color.white;
            c2 = Color.lightGray;
            c3 = Color.darkGray;
            c4 = Color.black;
            g.setColor(activeColor);
        } else {
            c1 = Color.black;
            c2 = Color.darkGray;
            c3 = Color.lightGray;
            c4 = Color.white;
            g.setColor(passiveColor);
        }

        /* draw it where requested */
        this.setLocation(x, y);

        /* draw inside rectangle */
        g.fillRect(0, 0, w - 0, h - 0);

        /*
         * set up for drawing mode as needed
         */

        switch (drawingMode) {
            case 0: {
                g.setColor(c1); // may be extra pixels drawn here
                // (off edge of canvas)
                g.drawLine(0, 0, w, 0);
                g.drawLine(0, 0, 0, h);
                g.setColor(c2);
                g.drawLine(1, 1, w, 1);
                g.drawLine(1, 1, 1, h);
                g.setColor(c3);
                g.drawLine(w - 2, h - 2, w, h - 2);
                g.drawLine(w - 2, h - 2, w - 2, h);
                g.setColor(c4);
                g.drawLine(w - 1, h - 1, w - 1, h - 1);
                break;
            }
            case 1: {
                g.setColor(c1);
                g.drawLine(0, 0, w - 2, 0);
                g.drawLine(0, h - 1, 0, h - 1);
                g.setColor(c2);
                g.drawLine(0, 1, w - 2, 1);
                g.drawLine(1, h - 2, 1, h);
                g.setColor(c3);
                g.drawLine(0, h - 2, 0, h - 2);
                g.drawLine(w - 2, 1, w - 2, h);
                g.setColor(c4);
                g.drawLine(w - 1, 0, w - 1, h);
                break;
            }
            case 2: {
                g.setColor(c1);
                g.drawLine(w - 1, 0, w - 1, 0);
                g.drawLine(0, 0, 0, h - 2);
                g.setColor(c2);
                g.drawLine(1, 0, 1, h - 3);
                g.drawLine(w - 2, 1, w, 1);
                g.setColor(c3);
                g.drawLine(w - 2, 0, w - 2, 0);
                g.drawLine(1, h - 2, w, h - 2);
                g.setColor(c4);
                g.drawLine(0, h - 1, w, h - 1);
                break;
            }
            case 3: {
                g.setColor(c1);
                g.drawLine(0, 0, 0, 0);
                g.setColor(c2);
                g.drawLine(0, 1, 1, 1);
                g.drawLine(1, 1, 1, 0);
                g.setColor(c3);
                g.drawLine(0, h - 2, w - 2, h - 2);
                g.drawLine(w - 2, 0, w - 2, h - 2);
                g.setColor(c4);
                g.drawLine(0, h - 1, w - 1, h - 1);
                g.drawLine(w - 1, 0, w - 1, h - 1);
                break;
            }
            case 4: {
                g.setColor(c1);
                g.drawLine(w - 1, 0, w - 1, 0);
                g.drawLine(0, 0, 0, 0);
                g.setColor(c2);
                g.drawLine(0, 1, 1, 1);
                g.drawLine(1, 1, 1, 0);
                g.drawLine(w - 2, 1, w, 1);
                g.setColor(c3);
                g.drawLine(w - 2, 0, w - 2, 0);
                g.drawLine(0, h - 2, w, h - 2);
                g.setColor(c4);
                g.drawLine(0, h - 1, w, h - 1);
                break;
            }
            case 5: {
                g.setColor(c1);
                g.drawLine(0, 0, w, 0);
                g.drawLine(0, h - 1, 0, h - 1);
                g.setColor(c2);
                g.drawLine(0, 1, w, 1);
                g.drawLine(1, h - 2, 1, h);
                g.setColor(c3);
                g.drawLine(0, h - 2, 0, h - 2);
                g.drawLine(w - 2, h - 2, w, h - 2);
                g.drawLine(w - 2, h - 2, w - 2, h);
                g.setColor(c4);
                g.drawLine(w - 1, h - 1, w - 1, h - 1);
                break;
            }
            case 6: {
                g.setColor(c1);
                g.drawLine(0, 0, w, 0);
                g.setColor(c2);
                g.drawLine(0, 1, w, 1);
                g.setColor(c3);
                g.drawLine(0, h - 2, w, h - 2);
                g.setColor(c4);
                g.drawLine(0, h - 1, w, h - 1);
                break;
            }
            case 7: {
                g.setColor(c1);
                g.drawLine(0, 0, 0, h);
                g.setColor(c2);
                g.drawLine(1, 0, 1, h);
                g.setColor(c3);
                g.drawLine(w - 2, 0, w - 2, h);
                g.setColor(c4);
                g.drawLine(w - 1, 0, w - 1, h);
                break;
            }
        }
    }

    /**
     * paint simply calls the update method
     */
    public void paint(Graphics g) {
        update(g);
    }
}
