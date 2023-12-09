package m6800.panels;

/*
 * @(#)bus.java
 *
 *
 */

import java.awt.Dimension;
import java.util.Vector;

import m6800.canvas.GeneralCellCanvas;

/**
 * memoryInspectorPanel() provides a panel object which contains a data
 * structure (vector) of memory overview cell objects (generalCell()) and lays
 * them out in a row, allowing the appropriate movement
 * 
 * @author Simon McCaughey
 * @version 1.0, 29 April 1998
 */

public class MemoryInspectorPanel extends GeneralPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /*
     * variable for the first element displayed
     */
    private int currentStart;

    /*
     * records value of currently active cell
     */
    private int activeCell;

    /*
     * vector to contain all cells
     */
    private Vector<GeneralCellCanvas> cells;

    /*
     * string to be added as the offset for each memory location
     */
    int origon;

    /**
     * constructor method memoryInspectorPanel() (no arguments)
     */
    public MemoryInspectorPanel() {

        cells = new Vector<GeneralCellCanvas>(); /*
                                                  * to be of type generalCell to
                                                  * contain all memory elements
                                                  */

        this.setBackground(getCenterColor()); // Colour the background

        panelWidth = 500; // set specific values for height and width
        panelHeight = 45;
        size = new Dimension(panelWidth + 1, panelHeight + 1); // +1 so that all
        // area
        // up to w can be used
        initialise(0); // simply to reset values

    }

    /**
     * initialise(origin) deletes all previous data ready for new data
     */
    public void initialise(int org) {
        origon = org;
        System.out.println("Initialising Memory Inspector");
        cells.removeAllElements(); // clear previous vector data
        removeAll(); // clear all components from the screen
    }

    /**
     * addCell() adds cells to vector (from start)
     */
    public void addCell(int location, String label, String Value) {
        // now added later
        GeneralCellCanvas c = new GeneralCellCanvas(location, label, Value);
        // generalCell c=new generalCell(location+origon, label, Value);
        System.out.println("new cell constructed at " + location + origon);
        cells.addElement(c);
        if (c == null)
            System.out.println("added null cell");
        // need to throw exception here
    }

    /**
     * addCell() adds cells to vector (from start)
     */
    public void addCell(GeneralCellCanvas c) {
        /*
         * this is the only place that cell addition is allowed to happen
         */
        if (c == null) {
            System.err.println("added null cell");
            // need to throw exception here??
        }
        c.setCellAddress(origon + c.getCellAddress());
        cells.addElement(c);
    }

    /**
     * execute() called to tell the object (this) that all data has been
     * received, and a first draw is to be completed and execution is about to
     * begin - initialise currentStart to -1??
     */
    public void execute() {
        GeneralCellCanvas generalCellCanvas;
        int offset; // for counting 7 elements across screen
        int x; // for general iteration functions

        /*
         * firstly add 7 extra blank cells at end of vector for display
         */
        for (x = 0; x < 7; x++) {
            /* create new blank cell */
            generalCellCanvas = new GeneralCellCanvas((cells.lastElement())
                    .getCellAddress()
                    + 1 - origon, "FF", "FF");
            /* add the new blank cell to the end */
            addCell(generalCellCanvas);
        }

        /* add first 7 elements as components to (this) display */
        currentStart = 0;
        offset = 0;
        generalCellCanvas = cells.elementAt(0);
        activeCell = -1; // none active at start
        System.out.println("Drawing Memory Inspector");

        /* actually add the components to the screen */
        while ((getComponentCount() < 7)
                && (currentStart + offset < cells.size())) {
            generalCellCanvas = cells.elementAt(currentStart + offset);
            if (generalCellCanvas != null) {
                add(generalCellCanvas);
                // repaint();
            } else {
                System.err.println("bad array element " + offset);
                // throw exception here??
            }
            offset++;
        }
        doLayout();
    }

    /**
     * for use by cpu setting the value during a store instruction de-activates
     * the cell at activeCell
     */
    public void setCellValue(String value) {
        GeneralCellCanvas g = (GeneralCellCanvas) cells.elementAt(activeCell);
        g.setTextLabel(value);
        g.setTextValueLabel(value);
        g.repaint();
    }

    /**
     * for use by cpu in selecting which panel needs to be active de-activates
     * the cell at activeCell
     */
    public void deActivate() {
        // de-activate the active cell
        if (activeCell != -1) { // if not first time
            GeneralCellCanvas g = (GeneralCellCanvas) cells
                    .elementAt(activeCell);
            g.setInActive();
        }
    }

    /**
     * moves the memory inspector to the specified location
     */
    public void gotoLocation(int location) {
        // here change so that it copes with the first time
        if (activeCell == -1) { // first time - just call forward to activate
            // first
            // cell
            forward();
        }
        if (location < activeCell) {
            back(activeCell - location);
        }
        if (location > activeCell) {
            forward(location - activeCell);
        }
    }

    /**
     * can be called using an int to go more than 1 step
     */
    private void forward(int numberOfSteps) {
        for (int i = 0; i < numberOfSteps; i++) {
            forward();
        }
    }

    /**
     * can be called using an int to go more than 1 step
     */
    private void back(int numberOfSteps) {
        for (int i = 0; i < numberOfSteps; i++) {
            back();
        }
    }

    /**
     * take one cell from start and add one to end
     */
    private void forward() {

        GeneralCellCanvas g;

        if (currentStart + 7 < cells.size()) { // stop at end (after last
            // element)
            // in-activate the old active cell
            if (activeCell != -1) { // if not first time
                g = (GeneralCellCanvas) cells.elementAt(activeCell);
                g.setInActive();
            }
            activeCell++;
            // check for "special" Condition of the first few elements
            if (activeCell > 2) { // first 2 steps complete
                remove(0);
                add((GeneralCellCanvas) cells.elementAt(currentStart + 7));
                currentStart++;
            }
        }
        /* activate the new cell */
        g = (GeneralCellCanvas) cells.elementAt(activeCell);
        g.setActive();
        doLayout();

    }

    /*
     * take one cell from end and add one to start
     */
    private void back() {
        GeneralCellCanvas g;
        System.out.println("Going back..");
        if (activeCell != 0) { // stop at start
            // in-activate the old active cell
            if (activeCell != -1) { // if not first time
                g = (GeneralCellCanvas) cells.elementAt(activeCell);
                g.setInActive();
            }
            activeCell--;
            // check for "special" Condition of the first few elements
            // where the end element is not removed
            if (activeCell > 1) {
                remove(6);
                currentStart--;
                add((GeneralCellCanvas) cells.elementAt(currentStart), 0);
            }
        }
        // activate the new cell
        g = (GeneralCellCanvas) cells.elementAt(activeCell);
        g.setActive();
        doLayout();
        System.out.println("Active cell= " + activeCell + " Size= "
                + cells.size() + "Strat" + currentStart);
        System.out.println("Gone back..");
    }

    /**
     * returns the value of the active cell
     */
    public String getCellValue() {
        GeneralCellCanvas g;
        g = (GeneralCellCanvas) cells.elementAt(activeCell);
        return g.getTextValueLabel();
    }

    /**
     * returns the label text of the active cell
     */

    public String getCellHexValue() {
        GeneralCellCanvas g;
        g = (GeneralCellCanvas) cells.elementAt(activeCell);
        return g.getTextLabel();
    }

    /**
     * returns the address of the active cell
     */
    public int getCellAddress() {
        GeneralCellCanvas g;
        if (activeCell != -1) { // check for first time
            g = (GeneralCellCanvas) cells.elementAt(activeCell);
            return g.getCellAddress();
        } else {
            return -1; // for first time
        }
    }
}
