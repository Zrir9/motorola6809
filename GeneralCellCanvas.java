package m6800.canvas;

/*
 * @(#)generalCell.java
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
 * generalCell provides the cell object for memory overview
 * 
 * @author Simon McCaughey
 * @version 1.0, 29 April 1998
 */
public class GeneralCellCanvas extends Canvas {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /*
     * size is the dimension necessary to ensure that this object is drawn on
     * the screen with its requested size
     */
    private Dimension size;
    private int w, h;

    /*
     * active is the variable that defines if the object has a outer or inner
     * bevel <P> false = inner bevel <P> true = outer bevel
     */
    private boolean active;

    /*
     * Colours defined for active and passive states, and also for use in
     * drawing the 3-d bevel effect
     */
    private Color activeColor, passiveColor;
    private Color c1, c2, c3, c4;

    /*
     * variables to do with drawing the strings
     */
    private Font smallFont, medFont, largeFont;
    private int cellAddress;
    private GeneralTextLabel textLabel, textValueLabel, addressLabel;

    /**
     * Constructor arguments :
     * <P>
     * location : location to be created with
     * <P>
     * label : string version of the value to be displayed
     * <P>
     * value : string representing the absolute value
     */
    public GeneralCellCanvas(int location, String label, String Value) {

        /*
         * set up differnet font sizes fot the different display types
         */
        smallFont = new Font("Helvetica", Font.BOLD, 8);
        medFont = new Font("Helvetica", Font.BOLD, 12);
        largeFont = new Font("Helvetica", Font.BOLD, 14);

        /*
         * create labels for display
         */
        textLabel = new GeneralTextLabel(label, largeFont,
                getFontMetrics(largeFont));
        textValueLabel = new GeneralTextLabel(Value, medFont,
                getFontMetrics(medFont));
        addressLabel = new GeneralTextLabel("", smallFont,
                getFontMetrics(smallFont));
        setCellAddress(location);

        active = false; // default to the passive state
        setInActive(); // to initialise colors

        /*
         * intialise colors
         */
        activeColor = Color.white;
        passiveColor = activeColor.darker();

        /* set specific values for height and width */
        w = 60;
        h = 35;
        size = new Dimension(w + 1, h + 1); // +1 so that all area
        // up to w can be used
    }

    /**
     * allows the object to have its address set to a new value
     */
    public void setCellAddress(int newAddress) {

        cellAddress = newAddress;
        /* work out string representation of value */
        String strLocation = java.lang.Integer.toHexString(cellAddress);
        strLocation = strLocation.toUpperCase();

        /* ensure upper case string is 4 digits long eg "C002" */
        switch (strLocation.length()) {
            case 1: {
                strLocation = "000" + strLocation;
                break;
            }
            case 2: {
                strLocation = "00" + strLocation;
                break;
            }
            case 3: {
                strLocation = "0" + strLocation;
                break;
            }
        }
        addressLabel.setLabel(strLocation);
    }

    /**
     * allows the object to have its text label set to a new value
     */
    public void setTextLabel(String newLabel) {
        textLabel.setLabel(newLabel);
    }

    /**
     * allows the object to have its text value set to a new value
     */
    public void setTextValueLabel(String newLabel) {
        textValueLabel.setLabel(newLabel);
    }

    /**
     * returns the current value of the text label
     */
    public String getTextLabel() {
        return textLabel.getLabel();
    }

    /**
     * returns the current value of the text value label
     */
    public String getTextValueLabel() {
        return textValueLabel.getLabel();
    }

    /**
     * returns the current value of the cell's address
     */
    public int getCellAddress() {
        return cellAddress;
    }

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
    public Dimension getMinimumSize() {
        return size;
    }

    /**
     * set the object active by raising the beveled edges
     */
    public void setActive() {
        c1 = Color.white;
        c2 = Color.lightGray;
        c3 = Color.darkGray;
        c4 = Color.black;
        active = true;
        repaint();
    }

    /**
     * set the object inactive (inward bevel)
     */
    public void setInActive() {
        c1 = Color.black;
        c2 = Color.darkGray;
        c3 = Color.lightGray;
        c4 = Color.white;
        active = false;
        repaint();
    }

    /**
     * Overrides the default paint method to implement the necessary drawing
     */
    public void paint(Graphics g) {

        /*
         * determine the colors for line drawing -select active or passive
         * colors
         */
        if (active) {
            g.setColor(activeColor);
        } else {
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

        /* draw strings on box */
        /* draw address label in top corner (small font - 10pts) */
        g.setColor(Color.black);
        g.setFont(addressLabel.getFont());
        g.drawString(addressLabel.getLabel(), 3, addressLabel.getHeight() - 1);

        /* draw large text label in center (large font - 14pts) */
        g.setFont(textLabel.getFont());
        g.drawString(textLabel.getLabel(), w / 2 - textLabel.getWidth() / 2, h
                / 2 + textLabel.getHeight() / 2 - 3);
        /* draw small hex representation at bottom (medium font - 12pts) */
        g.setFont(textValueLabel.getFont());
        g.drawString(textValueLabel.getLabel(), w / 2
                - textValueLabel.getWidth() / 2, h - 2);
    }
}
