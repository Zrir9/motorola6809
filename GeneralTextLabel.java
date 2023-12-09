/**
 * @(#)GeneralTextLabel.java
 *
 *
 */

package m6800.canvas.components;

import java.awt.Font;
import java.awt.FontMetrics;

/**
 * A class designed to be instantiated inside each instance of any class
 * requiring label attributes to be calculated such as height width etc. This
 * does it all for you!
 * 
 * @author Simon McCaughey
 * @version 1.0, 29 April 1998
 */
public class GeneralTextLabel {

    /*
 
  */

    /**
     * the label text.
     */
    private String label;
    /**
     * the label font.
     */
    private Font font;
    /**
     * the label height.
     */
    private int height;
    /**
     * the label width.
     */
    private int width;
    /**
     * Fontmetrics object.
     */
    private FontMetrics fm;

    /**
     * constructor arguments.
     * <P>
     * labelText : the label to be operated on
     * <P>
     * f : the font to be used to display the string
     * <P>
     * fontMet : the fontMetrics object from the specified font
     */
    public GeneralTextLabel(String labelText, Font f, FontMetrics fontMet) {
        setFont(f);
        fm = fontMet;
        setLabel(labelText);
        setHeight(fm.getMaxAscent() + fm.getMaxDescent());
    }

    /**
     * this method must be used to set the label, so that new values are
     * calculated for width and height.
     */
    public void setLabel(String labelText) {
        label = labelText;
        setWidth(fm.stringWidth(labelText));
    }

    /**
     * Get the label
     * 
     * @return - returns the label
     */
    public String getLabel() {
        return label;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Font getFont() {
        return font;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }
}
