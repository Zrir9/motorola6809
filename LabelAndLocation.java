package m6800.data;

/*
 * @(#)labelAndLocation.java
 *
 *
 */

/**
 * A class used by simulatorData class to hold information in the assembly task
 * : a simple data structure for each label
 * 
 * @author Simon McCaughey
 * @version 1.0, 28 April 1998
 */
public class LabelAndLocation extends Object {

    /**
     * field for any labels in the source code
     */
    private String label;
    /**
     * field for the label location in the source code
     */
    private int location;

    public void setLocation(int location) {
        this.location = location;
    }

    public int getLocation() {
        return location;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
