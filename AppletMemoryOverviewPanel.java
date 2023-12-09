package m6800.applet.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Panel;

//this is a large overview of all memory lcations
//which can quickly be scrolled over
//not finished
public class AppletMemoryOverviewPanel extends Panel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    Dimension size;
    int w, h;
    String[][] dataMemory;
    String[][] programMemory;
    private Font font;
    private FontMetrics fm;
    int strWidth;

    public AppletMemoryOverviewPanel() {
        this.setBackground(Color.white); // color the background
        font = new Font("Helvetica", Font.BOLD, 8);
        fm = getFontMetrics(font);
        dataMemory = new String[5][16];
        programMemory = new String[5][16];
        w = 480; // set specific values for height and width
        h = 150;
        size = new Dimension(w + 1, h + 1); // +1 so that all area
        // up to w can be used
        initialise(); // to reset all values
    }

    // //getPreferredSize() and getMinimumSize() are required by java
    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

    public Dimension getMinimumSize() {
        return size;
    }

    // reset all memory locations to ff
    public void initialise() {
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 16; y++) {
                dataMemory[x][y] = "FF";
                programMemory[x][y] = "FF";
            }
        }
    }

    // //////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////
    public void paint(Graphics g) {
        // g.drawLine(0,h-1,w,h-1);
        g.setColor(Color.lightGray);
        // draw vertical partition lines
        for (int x = 0; x <= w; x += 30) {
            g.drawLine(x, 0, x, h);
        }
        // draw horizontal partition lines
        for (int x = 0; x <= h; x += 15) {
            if (x != 8 * 15) {
                g.drawLine(0, x, w, x);
            } else { // draw heavy partition line
                g.setColor(Color.black);
                g.drawLine(0, x, w, x);
                g.setColor(Color.lightGray);
            }
        }
        g.setColor(Color.black);
        g.setFont(font);
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 16; y++) {
                // drawboth strings at same time??
                // g.drawString(value,w/2-strWidth/2,h-2);
                strWidth = fm.stringWidth(dataMemory[x][y]);
                g.drawString(dataMemory[x][y], (y * 30) + 15 - (strWidth / 2),
                        (x * 15) + 12);
                // g.drawString(programMemory[x][y]);
            }
        }
    }
}