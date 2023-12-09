package m6800.canvas;

/*
 * @(#)numberConverter.java
 *
 *
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import m6800.canvas.components.GeneralTextLabel;

/**
 * IRcanvas provides the Instruction Register object
 * 
 * 
 * @author Simon McCaughey
 * @version 1.0, 15 April 1998
 */

public class IRCanvas extends GeneralCanvas implements Runnable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    Thread manager;

    // variables to do with drawing the strings
    private GeneralTextLabel nameLabel, textIns, hexIns;
    private int totalHeight;

    // make this a thread
    public void run() {
    }

    public void start() {
        if (manager == null) {
            manager = new Thread(this);
            manager.start();
        }
    }

    @SuppressWarnings("deprecation")
    public void stop() {
        if (manager != null) {
            manager = new Thread(this);
            manager.stop();
            manager = null;
        }
    }

    /**
     * Constructor : initialise label
     */
    public IRCanvas(String label, Font f) {

        x = 0; // initialise to top left
        y = 0;
        nameLabel = new GeneralTextLabel(label, f, getFontMetrics(f));
        textIns = new GeneralTextLabel("", f, getFontMetrics(f));
        hexIns = new GeneralTextLabel("", f, getFontMetrics(f));
        // initialise x,y co-ords
        ox = 0;
        oy = 0;
        w = 120; // set specific values for height and width
        h = 50;
        size = new Dimension(w + 1, h + 1); // +1 so that all area
        // up to w can be used
    }

    /**
     * setInstruction() sets the current instruction to be displayed arguements
     * are the string version of the instruction and the HEX version of the
     * instruction
     */

    public void setInstruction(String instr, String hexInstr) {
        textIns.setLabel(instr);
        hexIns.setLabel(hexInstr);
        repaint();
    }

    /*
     * implement the necessary drawing
     */
    public void paint(Graphics g) {
        super.paint(g);
        // draw strings on box
        // labelText
        g.setColor(Color.white);
        g.setFont(nameLabel.getFont());
        g.drawString(nameLabel.getLabel(), w / 2 - nameLabel.getWidth() / 2,
                nameLabel.getHeight());
        totalHeight = nameLabel.getHeight();
        // draw text instruction
        // draw box around
        g.setColor(Color.white);
        g.fillRect(w / 2 - textIns.getWidth() / 2 - 3, totalHeight + 3, textIns
                .getWidth() + 6, textIns.getHeight());
        // draw actual text
        g.setColor(Color.black);
        g.drawString(textIns.getLabel(), w / 2 - textIns.getWidth() / 2,
                textIns.getHeight() + totalHeight);
        totalHeight += textIns.getHeight();
        // draw hex version
        // draw box around
        g.setColor(Color.yellow);
        g.fillRect(w / 2 - hexIns.getWidth() / 2 - 3, totalHeight + 5, hexIns
                .getWidth() + 6, hexIns.getHeight() - 4);
        // draw actual text
        g.setColor(Color.black);
        g.drawString(hexIns.getLabel(), w / 2 - hexIns.getWidth() / 2, hexIns
                .getHeight()
                + totalHeight);
    }
}
