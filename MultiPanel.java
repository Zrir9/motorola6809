package m6800.panels;

/*
 * @(#)numberConverter.java
 *
 *
 */

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import m6800.exceptions.BadProgramLineException;

/**
 * this is a large overview of all memory lcations which can quickly be scrolled
 * over never finished - ran out of time, only error display working
 * 
 * @author Simon McCaughey
 * @version 1.0, 15 April 1998
 */

public class MultiPanel extends Panel implements ItemListener {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    Dimension size, s;

    /*
     * uses cardLayout to hold as many items as necessary
     */
    CardLayout cLayout;
    int w, h;
    private Vector<BadProgramLineException> errors;
    int errorStart = 0, errorEnd = 0;
    public boolean errorToDisplay = false;

    // memory overview not finished
    // ScrollPane mo;
    // memoryOverviewPanel memOverview; //panel for inside mo

    java.awt.List errorList;

    /**
     * Constructor.
     */
    public MultiPanel() {

        errors = new Vector<BadProgramLineException>();
        this.setBackground(Color.white); // color the background

        w = 250; // set specific values for height and width
        h = 100;
        size = new Dimension(w + 1, h + 1); // +1 so that all area
        // up to w can be used

        // memory overview not finished
        // mo = new ScrollPane(ScrollPane.SCROLLBARS_ALWAYS);
        // memOverview = new memoryOverviewPanel();
        // mo.add(memOverview); //mo only contains one item

        cLayout = new CardLayout();
        setLayout(cLayout);
        errorList = new java.awt.List(6);
        errorList.addItemListener(this);
        // cLayout.addLayoutComponent("",new Label("hello"));
        add("23", errorList);
        add("23", new Label("hello"));
        // memory overview not finished
        // add("42", mo);
        cLayout.first(this);
        doLayout();
        errorList.add("No Errors To Display");

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

    public void externInput() {

        // this method would have accepted control from the control panel
        cLayout.next(this);
        doLayout();

    }

    public void clearAllErrors() {
        errorList.removeAll();
        errors.removeAllElements();
    }

    public void addError(BadProgramLineException bple) {

        // add to list
        errorList
                .add("Error [" + bple.getLineNumber() + "] " + bple.getError());

        // record in vector
        errors.addElement(bple);

        System.out
                .println("Adding error " + bple.getLineNumber() + " "
                        + bple.getError() + " " + bple.getStart() + " "
                        + bple.getEnd());

    }

    public void itemStateChanged(ItemEvent e) {

        BadProgramLineException bple;

        bple = (BadProgramLineException) errors.elementAt(((Integer) e
                .getItem()).intValue());

        errorStart = bple.getStart();
        errorEnd = bple.getEnd();

        System.out.println(e.getItem() + " " + bple.getError() + " "
                + bple.getStart());

        try {
            errorToDisplay = true;

            notify();
        } catch (Exception ex) {
            System.out.println("Exception notify caught");
        }

    }

    public int getErrorStart() {

        return errorStart;
    }

    public int getErrorEnd() {

        return errorEnd;
    }

}
