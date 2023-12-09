package m6800.canvas;

/*
 * @(#)generalRegister.java
 *
 *
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import m6800.util.NumberConverter;

/**
 * generalRegister() provides a canvas for those registers which need only show
 * two values: p current and a previous
 * 
 * @author Simon McCaughey
 * @version 1.0, 15 April 1998
 */
public class GeneralRegisterCanvas extends GeneralCanvas {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    NumberConverter numberConverter;

    // variables to do with drawing the strings
    private Font largeFont, smallFont;
    private FontMetrics largeFM, smallFM;
    private String labelText; // for name of register - set at startup
    private int strWidth, strHeight, totalHeight;

    // disallow int input - only strings will be accepted & returned!!
    private String currentString, previousString;
    private String currentBinaryString, previousBinaryString;
    private boolean inHexMode;

    /**
     * Constructor : no arguments, nothing to initialise
     */
    public GeneralRegisterCanvas(String label, Font f) {
        x = 0; // initialise to top left
        y = 0;
        currentString = "0"; // initialise strings to 0
        previousString = "0";
        numberConverter = new NumberConverter();
        inHexMode = true; // default to hex mode (works in BIN)
        setValue("0"); // initialise to 0

        largeFont = f;
        // //////// make "smallFont" (for previous value display) 2 points
        // smaller
        smallFont = new Font(largeFont.getName(), largeFont.getStyle(),
                largeFont.getSize() - 2);
        largeFM = getFontMetrics(largeFont);
        smallFM = getFontMetrics(smallFont);
        labelText = label; // set the label
        // initialise x,y co-ords
        ox = 0;
        oy = 0;
        active = false; // default to the passive state
        w = 110; // set specific values for height and width
        h = 50;
        size = new Dimension(w + 1, h + 1); // +1 so that all area
        // up to w can be used
    }

    /**
     * allow the output display mode (HEX or BINARY) to be set true = HEX ////
     * false = BINARY
     */
    public void setHexMode(boolean x) {
        inHexMode = x;
        updateValues();
        repaint();
    }

    public boolean getHexMode() {
        return inHexMode;
    }

    /**
     * setValue() takes a HEX string in sets the value while moving the old
     * value down
     */
    public void setValue(String n) {
        // ////animation needed here??
        // previousValue=currentValue; //change values
        previousString = currentString; // change values
        currentString = n;
        updateValues();
        repaint();
    }

    /*
     * allow external querying of current value
     */
    public String getValue() {
        return currentString;
    }

    /*
     * generic private method necessary for both setValue() and Hex->BIN Mode
     * changes (setHexMode()) updates on-scereen values
     */
    private void updateValues() {
        if (inHexMode) {
            // simple - just display the string
            // add leading zeros to odd numbers
            if (currentString.length() == 1)
                currentString = "0" + currentString;
            if (currentString.length() == 3)
                currentString = "0" + currentString;
            if (previousString.length() == 1)
                previousString = "0" + previousString;
            if (previousString.length() == 3)
                previousString = "0" + previousString;
            currentString = currentString.toUpperCase(); // ensure all chars are
            // upper
            previousString = previousString.toUpperCase();
        } else { // not In Hex Mode but BINARY mode
            // convert to integer - then to BIN
            currentBinaryString = Integer.toBinaryString(numberConverter
                    .hexToInt(currentString));
            // add leading zeros to odd numbers
            if (currentBinaryString.length() < 8) {
                while (currentBinaryString.length() != 8) {
                    currentBinaryString = "0" + currentBinaryString;
                }
            }
            previousBinaryString = Integer.toBinaryString(numberConverter
                    .hexToInt(previousString));
            if (previousBinaryString.length() < 8) {
                while (previousBinaryString.length() != 8) {
                    previousBinaryString = "0" + previousBinaryString;
                }
            }
            // ensure all chars are upper
            currentBinaryString = currentBinaryString.toUpperCase();
            previousBinaryString = previousBinaryString.toUpperCase();
        }
    }

    /**
     * implement the necessary drawing
     */
    public void paint(Graphics g) {
        super.paint(g);

        // draw strings on box
        // labelText
        g.setColor(Color.white);
        g.setFont(largeFont);
        strWidth = largeFM.stringWidth(labelText);
        strHeight = largeFM.getMaxAscent() + largeFM.getMaxDescent();
        g.drawString(labelText, w / 2 - strWidth / 2, strHeight);
        totalHeight = strHeight;

        // draw present value
        // do calculations
        if (inHexMode) {
            strWidth = largeFM.stringWidth(currentString);
        } else {
            strWidth = largeFM.stringWidth(currentBinaryString);
        }
        strHeight = largeFM.getMaxAscent() + largeFM.getMaxDescent();

        // draw box around
        g.setColor(Color.white);
        g.fillRect(ox + w / 2 - strWidth / 2 - 3, oy + totalHeight + 3,
                strWidth + 6, strHeight);
        // draw actual text
        g.setFont(largeFont);
        g.setColor(Color.black);
        if (inHexMode) {
            g.drawString(currentString, w / 2 - strWidth / 2, strHeight
                    + totalHeight);
        } else {
            g.drawString(currentBinaryString, w / 2 - strWidth / 2, strHeight
                    + totalHeight);
        }
        totalHeight += strHeight;

        // draw previous value
        // do calculations
        if (inHexMode) {
            strWidth = smallFM.stringWidth(previousString);
        } else {
            strWidth = smallFM.stringWidth(previousBinaryString);
        }
        strHeight = smallFM.getMaxAscent() + largeFM.getMaxDescent();
        // draw box around
        g.setColor(Color.white);
        g.fillRect(ox + w / 2 - strWidth / 2 - 2, oy + totalHeight + 4,
                strWidth + 4, strHeight);
        // draw actual text
        g.setFont(smallFont);
        g.setColor(Color.black);
        if (inHexMode) {
            g.drawString(previousString, w / 2 - strWidth / 2, strHeight
                    + totalHeight + 1);
        } else {
            g.drawString(previousBinaryString, w / 2 - strWidth / 2, strHeight
                    + totalHeight + 1);
        }
    }
}
