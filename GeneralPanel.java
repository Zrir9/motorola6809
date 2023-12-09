/**
 * generalCell provides the cell object for memory overview
 * 
 * @author Simon McCaughey
 * @version 1.0, 29 April 1998
 */

package m6800.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Panel;

import m6800.applet.AppletColors;

/**
 * abstract class containing variables common to each panel
 */

public abstract class GeneralPanel extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /*
     * Colours defined for use in drawing the 3-d bevel effect
     */
    private Color lightBorderColor = AppletColors.lightBorderColor;
    private Color centerColor = AppletColors.centerColor;
    private Color darkBorderColor = AppletColors.darkBorderColor;

    /*
     * size is the dimension necessary to ensure that this object is drawn on
     * the screen with its requested size
     */
    protected Dimension size;
    protected int panelHeight;
    protected int panelWidth;

    // TODO determine if these two variables are required
    protected int ox;
    protected int oy;

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
     * Overrides the default paint method to draw the animating area
     * <P>
     * uses the update method to minimise screen flicker
     */
    public void update(Graphics g) {
        g.setColor(lightBorderColor);
        g.drawLine(0, 0, panelWidth, 0);
        g.drawLine(0, 1, panelWidth, 1);
        g.drawLine(0, 2, 0, panelHeight - 2);
        g.drawLine(1, 2, 1, panelHeight - 2);
        g.setColor(darkBorderColor);
        g.drawLine(0, panelHeight, panelWidth, panelHeight);
        g.drawLine(0, panelHeight - 1, panelWidth, panelHeight - 1);
        g.drawLine(panelWidth, 2, panelWidth, panelHeight);
        g.drawLine(panelWidth - 1, 2, panelWidth - 1, panelHeight);
    }

    /**
     * simply calls the update method
     */
    public void paint(Graphics g) {
        update(g);
    }

    public void setCenterColor(Color centerColor) {
        this.centerColor = centerColor;
    }

    public Color getCenterColor() {
        return centerColor;
    }
}
