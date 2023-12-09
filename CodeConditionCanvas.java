package m6800.canvas;

/*
 * @(#)animationBubble.java
 *
 *
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import m6800.canvas.components.GeneralTextLabel;

/**
 * Code condition canvas is for use only by the CPU as its special register to
 * display the output results from ALU operations
 * 
 * @author Simon McCaughey
 * @version 1.0, 29 April 1998
 */
public class CodeConditionCanvas extends GeneralCanvas {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /*
     * variables to do with drawing the strings
     */
    private Font font;
    private FontMetrics fm;
    private String outputString;
    private GeneralTextLabel nameLabel;
    private int strLength, strHeight, a;

    /*
     * code condition variables
     */
    private boolean[] flags = new boolean[6]; // array containing the flags
    private boolean[] changed = new boolean[6]; // array retaining change

    /*
     * information to display changed colours
     */
    private String[] labels = new String[6]; // array containing labels

    /**
     * Constructor arguments : initial label, and font
     */
    public CodeConditionCanvas(String label, Font f) {

        /* initialise to top left */
        x = 0;
        y = 0;

        /* initialise array with flag labels */
        labels[0] = "H";
        labels[1] = "I";
        labels[2] = "N";
        labels[3] = "Z";
        labels[4] = "V";
        labels[5] = "C";
        font = f;
        fm = getFontMetrics(font);
        nameLabel = new GeneralTextLabel(label, font, fm);
        clearAllFlags(); // initialise all flags to 0
        clearChanges(); // at the start nothing has changed

        /* set specific values for height and width */
        w = 110;
        h = 50;
        size = new Dimension(w + 1, h + 1); // +1 so that all area
        // up to w can be used
    }

    /**
     * setHalfCarry() sets the Half Carry flag
     */
    public void setHalfCarry() {
        // clearChanges();
        flags[0] = true;
        changed[0] = true;
        repaint();
    }

    /**
     * setInterrupt() sets the Interrupt flag
     */
    public void setInterrupt() {
        // clearChanges();
        flags[1] = true;
        changed[1] = true;
        repaint();
    }

    /**
     * setNegative() sets the Negative flag
     */
    public void setNegative() {
        // clearChanges();
        flags[2] = true;
        changed[2] = true;
        repaint();
    }

    /**
     * setZero() sets the Zero flag
     */
    public void setZero() {
        // clearChanges();
        flags[3] = true;
        changed[3] = true;
        repaint();
    }

    /**
     * setOverflow() sets the Overflow flag
     */
    public void setOverflow() {
        // clearChanges();
        flags[4] = true;
        changed[4] = true;
        repaint();
    }

    /**
     * setCarry() sets the carry flag
     */
    public void setCarry() {
        // clearChanges();
        flags[5] = true;
        changed[5] = true;
        repaint();
    }

    /**
     * clearHalfCarry() clears the Half Carry flag
     */
    public void clearHalfCarry() {
        // clearChanges();
        flags[0] = false;
        changed[0] = true;
        repaint();
    }

    /** clearInterrupt() clears the Interrupt flag */
    public void clearInterrupt() {
        // clearChanges();
        flags[1] = false;
        changed[1] = true;
        repaint();
    }

    /**
     * clearNegative() clears the Negative flag
     */
    public void clearNegative() {
        // clearChanges();
        flags[2] = false;
        changed[2] = true;
        repaint();
    }

    /**
     * clearZero() clears the Zero flag
     */
    public void clearZero() {
        // clearChanges();
        flags[3] = false;
        changed[3] = true;
        repaint();
    }

    /**
     * clearOverflow() clears the Overflow flag
     */
    public void clearOverflow() {
        // clearChanges();
        flags[4] = false;
        changed[4] = true;
        repaint();
    }

    /**
     * clearCarry() clears the carry flag
     */
    public void clearCarry() {
        // clearChanges();
        flags[5] = false;
        changed[5] = true;
        repaint();
    }

    /**
     * returns Half Carry flag
     */
    public boolean halfCarrySet() {
        return flags[0];
    }

    /**
     * returns Interrupt flag
     */
    public boolean interruptSet() {
        return flags[1];
    }

    /**
     * returns Negative flag
     */
    public boolean negativeSet() {
        return flags[2];
    }

    /**
     * returns Zero flag
     */
    public boolean zeroSet() {
        return flags[3];
    }

    /**
     * returns Overflow flag
     */
    public boolean overflowSet() {
        return flags[4];
    }

    /**
     * returns carry flag
     */
    public boolean carrySet() {
        return flags[5];
    }

    /**
     * clearChanges() clears all changes flags
     */
    public void clearChanges() {
        for (a = 0; a != 6; a++) {
            changed[a] = false;
        }
        repaint();
    }

    /**
     * clearAllFlags() clears all flags
     */
    public void clearAllFlags() {
        for (a = 0; a != 6; a++) {
            if (flags[a] == true) {
                changed[a] = true;
            } else {
                changed[a] = false;
            }
        }
        repaint();
    }

    /**
     * Overrides the default paint method to draw the special register
     */
    public void paint(Graphics g) {
        super.paint(g);

        /* draw strings on box */
        /* -labelText */
        g.setColor(Color.white);
        g.setFont(nameLabel.getFont());
        g.drawString(nameLabel.getLabel(), w / 2 - nameLabel.getWidth() / 2,
                nameLabel.getHeight());

        /* draw flag boxes */
        g.setColor(Color.black);
        g.drawLine(10, 30, 100, 30); // draw top line
        g.drawLine(10, 45, 100, 45); // draw bottom line
        // draw dividing lines
        for (a = 0; a < 7; a++)
            g.drawLine(10 + 15 * a, 45, 10 + 15 * a, 30);
        strHeight = nameLabel.getHeight();

        /* now draw flag values and boxes */
        for (a = 0; a != 6; a++) {
            /* first color in boxes */
            if (changed[a]) {
                g.setColor(Color.red);
            } else {
                g.setColor(Color.yellow);
            }
            g.fillRect(11 + 15 * a, 31, 13, 13);
            /* next draw string value in */
            if (flags[a]) {
                outputString = "1";
            } else {
                outputString = "0";
            }
            strLength = fm.stringWidth(outputString);
            g.setColor(Color.black);
            g.drawString(outputString, 17 + 15 * a - strLength / 2,
                    43 - (15 - strHeight) / 2);
            /* draw label above */
            strLength = fm.stringWidth(labels[a]);
            g.drawString(labels[a], 17 + 15 * a - strLength / 2,
                    28 - (15 - strHeight) / 2);
        }
    }
}
