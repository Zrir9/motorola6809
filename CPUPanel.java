package m6800.panels;

/*
 * @(#)CPUpanel.java
 *
 *
 */

import java.awt.Button;
import java.awt.Dimension;
import java.awt.Font;

import m6800.canvas.ALUCanvas;
import m6800.canvas.AnimationBubbleCanvas;
import m6800.canvas.BusCanvas;
import m6800.canvas.CodeConditionCanvas;
import m6800.canvas.GeneralRegisterCanvas;
import m6800.canvas.IRCanvas;

/**
 * CPUPanel() provides a panel object which contains and lays out all the object
 * components of the CPU.
 * 
 * @author Simon McCaughey
 * @version 1.0, 15 April 1998
 */

public class CPUPanel extends GeneralPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public GeneralRegisterCanvas accumulatorA, dataBuffer, addressBuffer,
            programCounter;
    public ALUCanvas ALU;
    public CodeConditionCanvas CCR;
    public IRCanvas IR;
    int currentAnimationPath[][];
    final int[][] pathPCToAdd = { {490, 490, 380, 380}, {100, 60, 60, 20}};
    final int[][] pathDataToAcc = { {150, 150, 40, 40}, {20, 60, 60, 100}};
    final int[][] pathAccToALU = { {40, 40}, {100, 160}};
    final int[][] pathALUToAcc = { {85, 85, 185, 185, 40, 40},
            {195, 220, 220, 60, 60, 100}};
    final int[][] pathDataToIR = { {150, 150, 265, 265}, {20, 60, 60, 100}};
    final int[][] pathDataToALU = { {150, 150, 135, 135}, {20, 60, 60, 160}};
    final int[][] pathDataToAdd = { {150, 150, 380, 380}, {20, 60, 60, 20}};
    final int[][] pathALUToData = { {85, 85, 185, 185, 150, 150},
            {195, 220, 220, 60, 60, 20}};
    final int[][] pathDataToPC = { {150, 150, 490, 490}, {20, 60, 60, 100}};

    int currentAnimationLocation;

    Button b1, b2;
    public AnimationBubbleCanvas bubble;
    boolean forwardDirection;
    Font font;
    String bubbleText = "";

    BusCanvas accLink;
    BusCanvas accAlu;
    BusCanvas dataLink;
    BusCanvas addressLink;
    BusCanvas pcLink;
    BusCanvas irLink;
    BusCanvas aluLink;
    BusCanvas l1;
    BusCanvas l2;
    BusCanvas l3;
    BusCanvas l4;
    BusCanvas l5;
    BusCanvas l6;
    BusCanvas l7;
    BusCanvas l8;
    BusCanvas l9;
    BusCanvas j1;
    BusCanvas j2;
    BusCanvas j3;
    BusCanvas j4;
    BusCanvas j5;
    BusCanvas j6;
    BusCanvas j7;
    BusCanvas j8;
    BusCanvas j9;

    public boolean animationRequest = false;

    /**
     * constructor method CPUpanel() (no arguments)
     */
    public CPUPanel() {
        this.setBackground(getCenterColor()); // color the background
        bubble = new AnimationBubbleCanvas();
        bubble.setLabel("");
        add(bubble);
        /*
         * create the font to be used in all CPU components & font for bubble
         */
        font = new Font("Helvetica", Font.BOLD, 12);

        // add all registers
        dataBuffer = new GeneralRegisterCanvas("Data Buffer", font);
        addressBuffer = new GeneralRegisterCanvas("Address Buffer", font);
        accumulatorA = new GeneralRegisterCanvas("Accumulator A", font);
        programCounter = new GeneralRegisterCanvas("Program  Counter", font);
        ALU = new ALUCanvas("ALU", font, 15);
        CCR = new CodeConditionCanvas("CCR", font);
        IR = new IRCanvas("Instruction Register", font);

        /*
         * determine positioning of all components
         * 
         * Components are laid out relative to accumulator A
         */
        accumulatorA.x = 5; // everything ablsolute positioning
        accumulatorA.y = 85; // relative to acc a
        IR.x = accumulatorA.x + 220;
        IR.y = accumulatorA.y;
        programCounter.x = IR.x + 230;
        programCounter.y = accumulatorA.y;
        ALU.x = accumulatorA.x + 25;
        ALU.y = accumulatorA.y + 70;
        CCR.x = IR.x + 5;
        CCR.y = ALU.y;
        dataBuffer.x = (accumulatorA.x + IR.x) / 2;
        dataBuffer.y = accumulatorA.y - 80;
        addressBuffer.x = dataBuffer.x + 230;
        addressBuffer.y = dataBuffer.y;
        // make all drawing relative to accumulator a
        ox = accumulatorA.x; // for paint operations (not yet implemented)
        oy = accumulatorA.y;
        System.out.println("adding CPU Components");
        add(dataBuffer);
        add(addressBuffer);
        add(accumulatorA);
        add(programCounter);
        add(ALU);
        add(CCR);
        add(IR);
        panelWidth = programCounter.x + 115; // set specific values for height
        // and width
        // programcounter is 110 wide
        panelHeight = ALU.y + 95;
        // ALU is 60 deep, and allow 30 for bus below
        size = new Dimension(panelWidth + 1, panelHeight + 1); // +1 so that all
        // area
        // up to w can be used
        // draw all bus components (names in appendix)
        System.out.print("adding CPU bus components");
        System.out.print(".");
        accLink = new BusCanvas(55, 75, 10, 12, 7);
        accAlu = new BusCanvas(55, 133, 10, 34, 7);
        dataLink = new BusCanvas(165, 55, 10, 12, 7);
        addressLink = new BusCanvas(panelWidth - 175, 55, 10, 12, 7); // ??
        pcLink = new BusCanvas(panelWidth - 65, 75, 10, 12, 7);
        irLink = new BusCanvas(panelWidth / 2 - 5, 75, 10, 12, 7);
        aluLink = new BusCanvas(145, 75, 10, 112, 7);
        l1 = new BusCanvas(65, 65, 80, 10, 6);
        l2 = new BusCanvas(155, 65, 10, 10, 6);
        l3 = new BusCanvas(175, 65, 25, 10, 6);
        l4 = new BusCanvas(210, 65, panelWidth / 2 - 5 - 205, 10, 6);
        l5 = new BusCanvas(irLink.x + 10, 65, addressLink.x - irLink.x - 10,
                10, 6);
        l6 = new BusCanvas(addressLink.x + 10, 65, pcLink.x - addressLink.x
                - 10, 10, 6);
        l7 = new BusCanvas(200, 75, 10, 150, 7);
        l8 = new BusCanvas(100, 215, 10, 10, 7);
        l9 = new BusCanvas(110, 225, 90, 10, 6);
        j1 = new BusCanvas(55, 65, 10, 10, 0);
        j2 = new BusCanvas(145, 65, 10, 10, 5);
        j3 = new BusCanvas(165, 65, 10, 10, 4);
        j4 = new BusCanvas(200, 65, 10, 10, 5);
        j5 = new BusCanvas(panelWidth / 2 - 5, 65, 10, 10, 5);
        j6 = new BusCanvas(panelWidth - 175, 65, 10, 10, 4);
        j7 = new BusCanvas(panelWidth - 65, 65, 10, 10, 1);
        j8 = new BusCanvas(100, 225, 10, 10, 2);
        j9 = new BusCanvas(200, 225, 10, 10, 3);

        /*
         * NOTE : the order of these add() functions is important because all
         * the components must fit on the panel before they call setLocation()
         * otherwise they are not drawn at all
         */
        add(j1);
        add(accLink);
        add(accAlu);
        add(j7);
        add(dataLink);
        add(addressLink);
        add(pcLink);
        add(irLink);
        add(aluLink);
        add(l7);
        add(l8);
        add(l2);
        add(l3);
        add(j5);
        add(j3);
        add(j6);
        add(l9);
        add(j2);
        add(j4);
        add(l4);
        add(l5);
        add(l6);
        add(l1);
        add(j8);
        add(j9);

    }

    /*
     * resets all values inside the cpu
     */
    public void resetAllValues() {
        accumulatorA.setValue("00");
        accumulatorA.setValue("00");
        dataBuffer.setValue("00");
        dataBuffer.setValue("00");
        addressBuffer.setValue("0000");
        addressBuffer.setValue("0000");
        programCounter.setValue("0000");
        programCounter.setValue("0000");
        CCR.clearChanges();
        CCR.clearAllFlags();
        IR.setInstruction("NOP", "00");
    }

    /**
     * showConnectionPath() raises the bevel on the path between 2 registers
     */
    public void showConnectionPath(int settings[][]) {

        // check for a deviation from the norm, and change if necessary
        for (int i = 0; i < 25; i++) {

            switch (i) {
                // find the element that needs changed, and apply the change
                case 0: {
                    accLink.setDrawingMode(settings[0][i]);
                    if (settings[1][i] == 0) {
                        accLink.setInActive();
                    } else {
                        accLink.setActive();
                    }
                    break;
                }
                case 1: {
                    accAlu.setDrawingMode(settings[0][i]);
                    if (settings[1][i] == 0) {
                        accAlu.setInActive();
                    } else {
                        accAlu.setActive();
                    }
                    break;
                }
                case 2: {
                    dataLink.setDrawingMode(settings[0][i]);
                    if (settings[1][i] == 0) {
                        dataLink.setInActive();
                    } else {
                        dataLink.setActive();
                    }
                    break;
                }
                case 3: {
                    addressLink.setDrawingMode(settings[0][i]);
                    if (settings[1][i] == 0) {
                        addressLink.setInActive();
                    } else {
                        addressLink.setActive();
                    }
                    break;
                }
                case 4: {
                    pcLink.setDrawingMode(settings[0][i]);
                    if (settings[1][i] == 0) {
                        pcLink.setInActive();
                    } else {
                        pcLink.setActive();
                    }
                    break;
                }
                case 5: {
                    irLink.setDrawingMode(settings[0][i]);
                    if (settings[1][i] == 0) {
                        irLink.setInActive();
                    } else {
                        irLink.setActive();
                    }
                    break;
                }
                case 6: {
                    aluLink.setDrawingMode(settings[0][i]);
                    if (settings[1][i] == 0) {
                        aluLink.setInActive();
                    } else {
                        aluLink.setActive();
                    }
                    break;
                }
                case 7: {
                    l1.setDrawingMode(settings[0][i]);
                    if (settings[1][i] == 0) {
                        l1.setInActive();
                    } else {
                        l1.setActive();
                    }
                    break;
                }
                case 8: {
                    l2.setDrawingMode(settings[0][i]);
                    if (settings[1][i] == 0) {
                        l2.setInActive();
                    } else {
                        l2.setActive();
                    }
                    break;
                }
                case 9: {
                    l3.setDrawingMode(settings[0][i]);
                    if (settings[1][i] == 0) {
                        l3.setInActive();
                    } else {
                        l3.setActive();
                    }
                    break;
                }
                case 10: {
                    l4.setDrawingMode(settings[0][i]);
                    if (settings[1][i] == 0) {
                        l4.setInActive();
                    } else {
                        l4.setActive();
                    }
                    break;
                }
                case 11: {
                    l5.setDrawingMode(settings[0][i]);
                    if (settings[1][i] == 0) {
                        l5.setInActive();
                    } else {
                        l5.setActive();
                    }
                    break;
                }
                case 12: {
                    l6.setDrawingMode(settings[0][i]);
                    if (settings[1][i] == 0) {
                        l6.setInActive();
                    } else {
                        l6.setActive();
                    }
                    break;
                }
                case 13: {
                    l7.setDrawingMode(settings[0][i]);
                    if (settings[1][i] == 0) {
                        l7.setInActive();
                    } else {
                        l7.setActive();
                    }
                    break;
                }
                case 14: {
                    l8.setDrawingMode(settings[0][i]);
                    if (settings[1][i] == 0) {
                        l8.setInActive();
                    } else {
                        l8.setActive();
                    }
                    break;
                }
                case 15: {
                    l9.setDrawingMode(settings[0][i]);
                    if (settings[1][i] == 0) {
                        l9.setInActive();
                    } else {
                        l9.setActive();
                    }
                    break;
                }
                case 16: {
                    j1.setDrawingMode(settings[0][i]);
                    if (settings[1][i] == 0) {
                        j1.setInActive();
                    } else {
                        j1.setActive();
                    }
                    break;
                }
                case 17: {
                    j2.setDrawingMode(settings[0][i]);
                    if (settings[1][i] == 0) {
                        j2.setInActive();
                    } else {
                        j2.setActive();
                    }
                    break;
                }
                case 18: {
                    j3.setDrawingMode(settings[0][i]);
                    if (settings[1][i] == 0) {
                        j3.setInActive();
                    } else {
                        j3.setActive();
                    }
                    break;
                }
                case 19: {
                    j4.setDrawingMode(settings[0][i]);
                    if (settings[1][i] == 0) {
                        j4.setInActive();
                    } else {
                        j4.setActive();
                    }
                    break;
                }
                case 20: {
                    j5.setDrawingMode(settings[0][i]);
                    if (settings[1][i] == 0) {
                        j5.setInActive();
                    } else {
                        j5.setActive();
                    }
                    break;
                }
                case 21: {
                    j6.setDrawingMode(settings[0][i]);
                    if (settings[1][i] == 0) {
                        j6.setInActive();
                    } else {
                        j6.setActive();
                    }
                    break;
                }
                case 22: {
                    j7.setDrawingMode(settings[0][i]);
                    if (settings[1][i] == 0) {
                        j7.setInActive();
                    } else {
                        j7.setActive();
                    }
                    break;
                }
                case 23: {
                    j8.setDrawingMode(settings[0][i]);
                    if (settings[1][i] == 0) {
                        j8.setInActive();
                    } else {
                        j8.setActive();
                    }
                    break;
                }
                case 24: {
                    j9.setDrawingMode(settings[0][i]);
                    if (settings[1][i] == 0) {
                        j9.setInActive();
                    } else {
                        j9.setActive();
                    }
                    break;
                }
            }// end switch
        } // end for
    } // end showConnectionPath

    /*
     * a function to animate the movement of data betweeen registers called from
     * main program with and argument specifying the path
     */
    public void animateDataMove(int path, boolean direction, String label) {

        bubble.setLabel(label);
        forwardDirection = direction;
        switch (path) {
            case 0: {
                currentAnimationPath = pathPCToAdd;
                break;
            }
            case 1: {
                currentAnimationPath = pathDataToAcc;
                break;
            }
            case 2: {
                currentAnimationPath = pathAccToALU;
                break;
            }
            case 3: {
                currentAnimationPath = pathALUToAcc;
                break;
            }
            case 4: {
                currentAnimationPath = pathDataToIR;
                break;
            }
            case 5: {
                currentAnimationPath = pathDataToALU;
                break;
            }
            case 6: {
                currentAnimationPath = pathDataToAdd;
                break;
            }
            case 7: {
                currentAnimationPath = pathALUToData;
                break;
            }
            case 8: {
                currentAnimationPath = pathDataToPC;
                break;
            }
        }
        if (forwardDirection) {
            // initialise to start of array
            currentAnimationLocation = 0;
        } else { // will this ever be used???
            // initialise to end of array
            currentAnimationLocation = java.lang.reflect.Array
                    .getLength(currentAnimationPath[0]) - 1;
            // 0 based
        }
        bubble.x = currentAnimationPath[0][currentAnimationLocation];
        bubble.y = currentAnimationPath[1][currentAnimationLocation];

    } // end animate method

    /*
     * called after calling animateDataPath() to set the path this function
     * moves to the next step of the data path
     */
    public boolean nextMove(int moveSize) {
        // check to ensure not passed end of array - use try and
        // catch exception if over the size limit
        try {
            if (bubble.x < currentAnimationPath[0][currentAnimationLocation]) { // move
                // to
                // right
                if (bubble.x + moveSize >= currentAnimationPath[0][currentAnimationLocation]) { // cater
                    // for
                    // overshoot
                    bubble.x = currentAnimationPath[0][currentAnimationLocation]; // make
                    // x
                    // exactly
                    // the
                    // limit
                } else {
                    bubble.x += moveSize; // simply increment
                }
            }
            if (bubble.x > currentAnimationPath[0][currentAnimationLocation]) { // move
                // to
                // left
                if (bubble.x - moveSize <= currentAnimationPath[0][currentAnimationLocation]) { // cater
                    // for
                    // overshoot
                    bubble.x = currentAnimationPath[0][currentAnimationLocation]; // make
                    // x
                    // exactly
                    // the
                    // limit
                } else {
                    bubble.x -= moveSize; // simply increment
                }
            }
            if (bubble.y < currentAnimationPath[1][currentAnimationLocation]) { // move
                // down
                if (bubble.y + moveSize >= currentAnimationPath[1][currentAnimationLocation]) { // cater
                    // for
                    // overshoot
                    bubble.y = currentAnimationPath[1][currentAnimationLocation]; // make
                    // x
                    // exactly
                    // the
                    // limit
                } else {
                    bubble.y += moveSize; // simply increment
                }
            }
            if (bubble.y > currentAnimationPath[1][currentAnimationLocation]) { // move
                // up for overshoot
                if (bubble.y - moveSize <= currentAnimationPath[1][currentAnimationLocation]) { // cater
                    // x exactly the limit
                    bubble.y = currentAnimationPath[1][currentAnimationLocation]; // make
                } else {
                    // simply increment
                    bubble.y -= moveSize;
                }
            }
            // now the 4 directions have been serviced
            // check if this point has been reached
            if ((bubble.x == currentAnimationPath[0][currentAnimationLocation])
                    && (bubble.y == currentAnimationPath[1][currentAnimationLocation])) {
                if (forwardDirection) {
                    currentAnimationLocation++; // move to next location
                } else {
                    currentAnimationLocation--; // move to previous location
                }
            }
            long minTime = 4;
            bubble.repaint(minTime);
            return true; // tell the caller to try again
        } catch (ArrayIndexOutOfBoundsException ex) {
            // hide the bubble
            bubble.x = 1000;
            bubble.y = 1000;
            bubble.repaint();
            // tell the caller not to try again
            return false;
        }
    }

}// end class
