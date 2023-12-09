package m6800.canvas;

/*
 * @(#)ALUcanvas.java
 *
 *
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;

import m6800.applet.enums.AluMode;
import m6800.canvas.components.GeneralTextLabel;
import m6800.data.ALUOutput;
import m6800.util.NumberConverter;

/**
 * This class provides the ALU object for the CPU Implements all necessary
 * drawing, and all ALU calculations.
 * 
 * @author Simon McCaughey
 * @version $Id 1.0, 15 April 1998 $
 */

public class ALUCanvas extends GeneralCanvas {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * variables to do with painting the scalable object.
     */
    private int inputw, inputh, vwidth, vheight, toph, bottomh, outputw;

    /**
     * nameLabel.
     */
    private GeneralTextLabel nameLabel;

    /**
     * modeLabel.
     */
    private GeneralTextLabel modeLabel;

    /**
     * number converter.
     */
    private NumberConverter nc;

    /**
     * Constructor : initialised with a label and a size.
     * 
     * 
     * 
     */
    public ALUCanvas(String label, Font f, int scaleFactor) {

        x = 0; // initialise to top left
        y = 0;

        /*
         * because there is area outside the paint area
         */
        this.setBackground(centerColor);

        nc = new NumberConverter();

        nameLabel = new GeneralTextLabel(label, f, getFontMetrics(f));
        modeLabel = new GeneralTextLabel("", f, getFontMetrics(f));
        setMode("NO MODE"); // initialise to nothing

        // initialise x,y co-ords
        ox = 0;
        oy = 0;

        // initialise all widths and heights
        inputw = 4 * scaleFactor;
        vwidth = 2 * scaleFactor;
        vheight = 1 * scaleFactor;
        toph = 3 * scaleFactor;
        bottomh = 1 * scaleFactor;
        outputw = 4 * scaleFactor;

        w = 2 * inputw + vwidth;
        h = toph + bottomh;
        size = new Dimension(w + 1, h + 1); // +1 so that all area
        // up to w can be used
    }

    /**
     * returns the output for a specified input.
     * 
     * over riding functions for different operations
     */
    public ALUOutput getOutput(AluMode ALUMode, int accInput, ALUOutput a) {
        return CalculateALUOutput(ALUMode, accInput, -1, a); // -1 means no
        // input
    }

    public ALUOutput getOutput(AluMode ALUMode, int accInput, int otherInput,
            ALUOutput a) {
        return CalculateALUOutput(ALUMode, accInput, otherInput, a);
    }

    private ALUOutput CalculateALUOutput(AluMode ALUMode, int accInput,
            int otherInput, ALUOutput aluOutput) {
        String output;
        boolean carryWasSet; // record input carry flag
        String test, test2;
        output = "";

        switch (ALUMode) {

            /*
             * mode 0 = clear mode, just make output = "00"
             */
            case CLEAR: {
                System.out.println("ALU in mode 0");
                setMode("Clear");
                output = "00";
                // set flags
                aluOutput.setNChanged(true);
                aluOutput.setNValue(false);
                aluOutput.setZChanged(true);
                aluOutput.setZValue(true);
                aluOutput.setVChanged(true);
                aluOutput.setVValue(false);
                aluOutput.setCChanged(true);
                aluOutput.setCValue(false);
                break;
            }
                /*
                 * mode 1 = decrement mode, output = input1 - 1
                 */
            case DECREMENT: {
                setMode("Dec");

                if (accInput == 0) { // make output = 255 , and set flag
                    output = "255";
                } else {
                    output = "" + (--accInput);
                }

                aluOutput = testForFlags(output);
                aluOutput.setVChanged(true);
                aluOutput.setVValue((accInput == 128));
                break;
            } // end case 2

                /*
                 * mode 2 = increment mode, output = input1 + 1
                 */
            case INCREMENT: {

                setMode("Inc");

                System.out.println("ALU mode = Inc");
                System.out.println("Input = " + accInput);

                output = "" + (++accInput);

                System.out.println("Output = " + output);

                // number will be ready at this stage..
                // so check for special cases
                if (output.equals("256")) { // make output = 0 , and set carry
                    // flag
                    output = "0";
                    // remember that inc does not set carry
                } // end if

                aluOutput = testForFlags(output);
                aluOutput.setVChanged(true);
                aluOutput.setVValue((accInput == 127));
                break;
            } // end case 2

                /*
                 * mode 3 = shift left (arithmetic)
                 */
            case SHIFT_LEFT: {
                setMode("Shift <");
                output = "" + bitShift(otherInput, accInput, 0); // mode 0 is
                // shift
                // left

                // returns ready made 8-bit output
                aluOutput = testForFlags(output);
                aluOutput.setCChanged(true);

                test = java.lang.Integer.toBinaryString(accInput);

                while (test.length() < 8) {
                    test = "0" + test;
                }
                System.out.println("Testing char " + (test).charAt(0));
                if ('1' == (test).charAt(0)) {
                    aluOutput.setCValue(true);
                }
                aluOutput.setVChanged(true);
                aluOutput.setVValue((aluOutput.isNValue() != aluOutput
                        .isCValue()));
                break;
            }

                /*
                 * mode 4 = shift right (arithmetic)
                 */
            case SHIFT_RIGHT: {
                setMode("Shift >");

                test = nc.intToBin(accInput);
                test2 = nc.intToBin(bitShift(otherInput, accInput, 1));
                while (test.length() < 8) {
                    test = "0" + test;
                } // make it 8-bit
                while (test2.length() < 7) {
                    test2 = "0" + test2;
                } // make it 8-bit

                output = "" + nc.binToInt("" + test.charAt(0) + test2);
                aluOutput = testForFlags(output);
                aluOutput.setCChanged(true);

                test = nc.intToBin(accInput);
                if ('1' == (test.charAt(test.length() - 1))) {
                    aluOutput.setCValue(true);
                }
                aluOutput.setVChanged(true);
                aluOutput.setVValue((aluOutput.isNValue() != aluOutput
                        .isCValue()));
                break;
            }
                /*
                 * mode 5 = rotate right
                 */
            case ROTATE_RIGHT: {
                setMode("Rotate >");

                carryWasSet = aluOutput.isCValue();

                System.out.println("ALU mode = Rotate >");
                System.out.println("Input = " + accInput + " = ("
                        + nc.intToBin(accInput) + ")");

                test = nc.intToBin(accInput);
                test2 = nc.intToBin(bitShift(otherInput, accInput, 3));
                while (test.length() < 8) {
                    test = "0" + test;
                }
                while (test2.length() < 7) {
                    test2 = "0" + test2;
                }
                // / System.out.println("test2= "+test2);
                if (carryWasSet) {
                    System.out.println("carry was set");
                    output = "" + nc.binToInt("1" + test2); // add extra 1 to
                    // start
                } else {
                    output = "" + nc.binToInt("0" + test2);
                }
                aluOutput = testForFlags(output);
                aluOutput.setCChanged(true);
                if ('1' == (test.charAt(test.length() - 1))) {
                    aluOutput.setCValue(true);
                }
                // System.out.println("output at end = "+output);
                aluOutput.setVChanged(true);
                System.out.println("Output = " + output);
                aluOutput.setVValue((aluOutput.isNValue() != aluOutput
                        .isCValue()));
                break;
            }
                /*
                 * mode 6 = rotate left
                 */
            case ROTATE_LEFT: {
                setMode("Rotate <");
                carryWasSet = aluOutput.isCValue();
                test = nc.intToBin(accInput);
                test2 = nc.intToBin(bitShift(otherInput, accInput, 2));
                while (test.length() < 8) {
                    test = "0" + test;
                }
                while (test2.length() < 7) {
                    test2 = "0" + test2;
                }
                System.out.println("test2= " + test2);
                if (carryWasSet) {
                    System.out.println("carry was set");
                    output = "" + nc.binToInt("" + test2 + "1"); // add extra 1
                    // to
                    // end
                } else {
                    output = "" + nc.binToInt("" + test2 + "0");
                }
                aluOutput = testForFlags(output);
                aluOutput.setCChanged(true);
                if ('1' == (test.charAt(0))) {
                    aluOutput.setCValue(true);
                }
                System.out.println("output at end = " + output);
                aluOutput.setVChanged(true);
                aluOutput.setVValue((aluOutput.isNValue() != aluOutput
                        .isCValue()));
                break;
            }

                /*
                 * mode 7 = negate
                 */
            case NEGATE: {
                setMode("Negate");
                output = "" + bitShift(otherInput, accInput, 4); // mode 4 is
                // negate
                aluOutput = testForFlags(output);
                aluOutput.setVChanged(true);
                aluOutput.setVValue((accInput == 128));
                aluOutput.setCChanged(true);
                aluOutput.setCValue((0 != (java.lang.Integer.decode(output)
                        .intValue())));
                break;
            }

                /*
                 * mode 8 = compliment
                 */
            case COMPLIMENT: { //
                setMode("Compliment");
                output = "" + bitShift(otherInput, accInput, 5); // mode 4 is
                // complement
                aluOutput = testForFlags(output);
                aluOutput.setVChanged(true);
                aluOutput.setVValue(false);
                aluOutput.setCChanged(true);
                aluOutput.setCValue(true);
                break;
            }
                /*
                 * mode 9 = pass mode
                 */
            case PASS_MODE: {
                setMode("Pass");
                if (otherInput == -1) { // ie only one item input
                    output = "" + accInput;
                } else {
                    // pass other input
                    output = "" + otherInput;
                }
                aluOutput = testForFlags(output);
                aluOutput.setVChanged(true);
                aluOutput.setVValue(false);
                break;
            }
                /*
                 * mode 10 = add mode
                 */
            case ADD_MODE: {
                setMode("Add");
                if (accInput + otherInput > 255) {
                    output = "" + (accInput + otherInput - 256);
                } else {
                    output = "" + (accInput + otherInput);
                }
                aluOutput = testForFlags(output); // check z&n flags
                aluOutput.setHChanged(true);
                System.out.println("Half Adding " + accInput % 16 + " to "
                        + otherInput % 16 + " = "
                        + (accInput % 16 + otherInput % 16));
                if ((nc.intToBin(accInput % 16 + otherInput % 16)).length() == 5) {
                    aluOutput.setHValue(true);
                }
                aluOutput.setVChanged(true);
                // function yet to be implemented for v
                aluOutput.setVValue(calculateV(accInput, otherInput, output,
                        true));
                aluOutput.setCChanged(true);
                if ((nc.intToBin(accInput + otherInput)).length() == 9) {
                    aluOutput.setCValue(true);
                }
                break;
            }

                /*
                 * mode 11 = add with carry
                 */

            case ADD_WITH_CARRY: {
                setMode("Add+Carry");
                int carryBit = 0;
                if (aluOutput.isCValue()) {
                    carryBit += 1;
                }
                if (accInput + otherInput + carryBit > 255) {
                    output = "" + (accInput + otherInput + carryBit - 256);
                } else {
                    output = "" + (accInput + otherInput + carryBit);
                }
                aluOutput.setHChanged(true);
                aluOutput = testForFlags(output); // check z&n flags
                if ((nc.intToBin(accInput % 16 + otherInput % 16)).length() == 5) {
                    aluOutput.setHValue(true);
                }
                aluOutput.setVChanged(true);
                aluOutput.setVValue(calculateV(accInput, otherInput, output,
                        true));
                aluOutput.setCChanged(true);
                if ((nc.intToBin(accInput + otherInput)).length() == 9) {
                    aluOutput.setCValue(true);
                }
                break;
            }
                /*
                 * mode 12 = subtract
                 */
            case SUBTRACT: {
                setMode("Subtract");
                if (accInput - otherInput < 0) {
                    output = "" + (accInput - otherInput + 256);
                } else {
                    output = "" + (accInput - otherInput);
                }
                aluOutput = testForFlags(output); // check z&n flags
                aluOutput.setVChanged(true);
                // function yet to be implemented for v
                aluOutput.setVValue(calculateV(accInput, otherInput, output,
                        false));
                aluOutput.setCChanged(true);
                // test for carry by finding out if
                // subtracting a larger number from a smaller one
                if (accInput < otherInput) {
                    aluOutput.setCValue(true);
                }
                break;
            }
                /*
                 * mode 13 = subtract with carry
                 */
            case SUBTRACT_WITH_CARRY: {
                setMode("Subtract-carry");
                int carryBit = 0;
                if (aluOutput.isCValue()) {
                    carryBit += 1;
                }
                if (accInput - otherInput - carryBit < 0) {
                    output = "" + (accInput - otherInput - carryBit + 256);
                } else {
                    output = "" + (accInput - otherInput);
                }
                aluOutput = testForFlags(output); // check z&n flags
                aluOutput.setVChanged(true);
                // function yet to be implemented for v
                aluOutput.setVValue(calculateV(accInput, otherInput, output,
                        false));
                aluOutput.setCChanged(true);
                // test for carry by finding out if
                // subtracting a larger number from a smaller one
                if (accInput < otherInput) {
                    aluOutput.setCValue(true);
                }
                break;
            }
                /*
                 * mode 14 = inclusive or
                 */
            case INCLUSIVE_OR: {
                setMode("OR");
                output = "" + bitShift(otherInput, accInput, 6, false); // mode
                // 6 is
                // or/xor
                // false means "or" not "xor"
                // ie true is for special case
                aluOutput = testForFlags(output); // check z&n flags
                aluOutput.setVChanged(true);
                // function yet to be implemented for v
                aluOutput.setVValue(false);
                break;
            }
                /*
                 * mode 15 = exclusive or
                 */
            case EXCLUSIVE_OR: {
                setMode("XOR");
                output = "" + bitShift(otherInput, accInput, 6, true); // mode 6
                // is
                // or/xor
                // true means "xor" not "or"
                // ie true is for special case
                aluOutput = testForFlags(output); // check z&n flags
                aluOutput.setVChanged(true);
                aluOutput.setVValue(false);
                break;
            }
                /*
                 * mode 16 = CMP = compare
                 */
            case COMPARE: {
                setMode("Compare");
                // have to do the subtraction to get flag results
                if (accInput - otherInput < 0) {
                    output = "" + (accInput - otherInput + 256);
                } else {
                    output = "" + (accInput - otherInput);
                }
                aluOutput = testForFlags(output); // check z&n flags
                aluOutput.setVChanged(true);
                aluOutput.setVValue(calculateV(accInput, otherInput, output,
                        false));
                aluOutput.setCChanged(true);
                // test for carry by finding out if
                // subtracting a larger number from a smaller one
                if (accInput < otherInput) {
                    aluOutput.setCValue(true);
                }
                aluOutput = testForFlags(output); // check z&n flags
                aluOutput.setVChanged(true);
                // function yet to be implemented for v
                aluOutput.setVValue(false);
                output = "" + accInput;
                break;
            }
                /*
                 * mode 17 = and
                 */
            case AND: {
                setMode("AND");
                output = "" + bitShift(otherInput, accInput, 7); // mode 7 is
                // and
                aluOutput = testForFlags(output); // check z&n flags
                aluOutput.setVChanged(true);
                aluOutput.setVValue(false);
                break;
            }
            default: {
                System.err.println("This ALU mode (" + ALUMode
                        + ") not yet implemented");
                break;
            }
        } // end switch
        aluOutput.setOutput(output);
        return aluOutput;
    }

    /**
     * calculates the value for the v flag, given the specified inputs
     */
    public boolean calculateV(String s1, String s2, String result,
            boolean addition) {
        // conditions from book 6800 up by jack quinn (p76)
        System.out.println("Strings are " + s1 + " " + s2 + " " + result);
        if (addition) {
            if ((s1.charAt(0) == s2.charAt(0))
                    && (result.charAt(0) != s2.charAt(0))) {
                return true;
            }
        } else {
            if ((s1.charAt(0) != s2.charAt(0))
                    && (result.charAt(0) == s2.charAt(0))) {
                return true;
            }
        }
        // in all other situations
        return false;
    }

    /**
     * over load method
     */
    public boolean calculateV(int n1, int n2, String result, boolean addition) {
        String s1, s2;
        s1 = nc.intToBin(n1);
        while (s1.length() < 8) {
            s1 = "0" + s1;
        } // make string 8-bit binary
        s2 = nc.intToBin(n2);
        while (s2.length() < 8) {
            s2 = "0" + s2;
        } // make string 8-bit binary
        // result is in decimal form -> change to binary
        result = nc.intToBin((java.lang.Integer.decode(result)).intValue());
        while (result.length() < 8) {
            result = "0" + result;
        } // make string 8-bit binary
        return calculateV(s1, s2, result, addition);
    }

    /**
     * tests for zero, negative flags
     */
    public ALUOutput testForFlags(String inputString) {
        ALUOutput a = new ALUOutput();
        int input;
        input = new Integer(inputString).intValue();
        inputString = nc.intToBin(input);
        // ensure bin input is 8-bit
        while (inputString.length() < 8) {
            inputString = "0" + inputString; // add digits to the start of
            // string input
        }
        if (inputString.charAt(0) == '1') {
            a.setNChanged(true);
            a.setNValue(true);
        } else {
            a.setNChanged(true);
            a.setNValue(false);
        }
        if (java.lang.Integer.decode(inputString).intValue() == 0) {
            a.setZChanged(true);
            a.setZValue(true);
        } else {
            a.setZChanged(true);
            a.setZValue(false);
        }
        return a;
    }

    /**
     * overload method
     */
    public int bitShift(int otherInput, int accInput, int mode) {
        return bitShift(otherInput, accInput, mode, false);
    }

    public int bitShift(int otherInput, int accInput, int mode,
            boolean specialMode) {
        // mode 0=shift left
        String input, input2, output;
        input = nc.intToBin(accInput);
        input2 = nc.intToBin(otherInput);
        // ensure bin input is 8-bit
        while (input.length() < 8) {
            input = "0" + input; // add digits to the start of string input
        }
        while (input2.length() < 8) {
            input2 = "0" + input2; // add digits to the start of string input
        }
        output = "";
        switch (mode) {
            case 0: { // shift left
                for (int i = 0; i < input.length() - 1; i++) {
                    output = output + input.charAt(i + 1);
                }
                output = output + "0"; // add extra 0 at end
                break;
            }
            case 1: { // shift right (arithmetic)
                // output="0"; //add leading 0
                for (int i = 1; i < input.length() - 1; i++) {
                    output = output + input.charAt(i);
                }
                output = "" + input.charAt(0) + output; // add static bit to
                // start
                break;
            }
            case 2: { // rotate left
                for (int i = 0; i < input.length() - 1; i++) {
                    output = output + input.charAt(i + 1);
                }
                // output=output+input.charAt(0);
                break;
            }
            case 3: { // rotate right
                // add end char to start //should be the carry added
                // output=""+input.charAt(input.length()-1);//this is now done
                // above
                for (int i = 0; i < input.length() - 1; i++) {
                    output = output + input.charAt(i);
                } // creates 7-bit number
                break;
            }
            case 4: { // compliment (NOT) - just the opposite to whats already
                // there
                for (int i = 0; i < input.length(); i++) {
                    if (input.charAt(i) == '0') {
                        output = output + "1";
                    } else {
                        output = output + "0";
                    }
                }
                break;
            }
            case 5: { // 2's compliment (COM)
                // input
                // output
                boolean firstComplete = false;
                System.out.println("inputs are: " + input + "  " + input2);
                // iterate through string, and output 0's until you get to
                // the first 1. Then add 1 to end
                output = "";
                for (int i = input.length() - 1; i >= 0; i--) { // count
                    // downwards
                    System.out.println("doing:: " + input.charAt(i));
                    if (!firstComplete) {
                        if (input.charAt(i) == '0') {
                            output = "0" + output;
                        } else {
                            output = "1" + output;
                            firstComplete = true;
                        }
                    } else {
                        if (input.charAt(i) == '0') {
                            output = "1" + output;
                        } else {
                            output = "0" + output;
                        }
                    }
                } // end for (i)
                break;
            }
            case 6: { // or / xor -> specialmode ==true for xor
                boolean out;
                output = "";
                System.out.println("inputs are: " + input + "  " + input2);
                for (int i = 0; i < input.length(); i++) {
                    if (!specialMode) { // ie just or
                        out = ((input.charAt(i) == '1') || (input2.charAt(i) == '1'));
                        System.out.println("output  : " + out);
                    } else { // xor
                        out = (input.charAt(i) != input2.charAt(i));
                    }
                    // add 1 or 0 to output depending on "out"
                    output = output + (out ? "1" : "0");
                }
                // ("output: "+ output);
                break;
            }
            case 7: { // AND Mode
                boolean out;
                output = "";
                System.out.println("inputs are: " + input + "  " + input2);
                for (int i = 0; i < input.length(); i++) {
                    out = ((input.charAt(i) == '1') && (input2.charAt(i) == '1'));
                    // add 1 or 0 to output depending on "out"
                    output = output + (out ? "1" : "0");
                }
                break;
            }
        } // switch
        System.out.println("binary output = " + output);
        return nc.binToInt(output);
    }

    /*
     * sets the Mode Label (input = string)
     */
    public void setMode(String mt) {
        modeLabel.setLabel("Mode = " + mt);
        repaint();
    }

    /*
     * returns the Mode Label (output = string)
     */
    public String getMode() {
        return modeLabel.getLabel();
    }

    /*
     * implement the necessary drawing
     */
    public void paint(Graphics g) {
        // cant use super method here
        this.setLocation(x, y); // draw it where requested
        if (active) {
            c1 = Color.white;
            c2 = Color.lightGray;
            c3 = Color.darkGray;
            c4 = Color.black;
            g.setColor(activeColor);
        } else {
            c1 = Color.black;
            c2 = Color.darkGray;
            c3 = Color.lightGray;
            c4 = Color.white;
            g.setColor(passiveColor);
        }
        // //////draw inside polygon (do this first as it is slightly overdrawn)
        Polygon insidePoly = new Polygon();
        insidePoly.addPoint(4, 2); // u
        insidePoly.addPoint(inputw + 1, 2); // v
        insidePoly.addPoint(inputw + vwidth / 2, vheight + 1); // w
        insidePoly.addPoint(inputw + vwidth, 2); // x
        insidePoly.addPoint(2 * inputw + vwidth - 3, 2); // y
        insidePoly.addPoint(inputw + vwidth / 2 + outputw / 2 - 1, toph); // z
        insidePoly.addPoint(inputw + vwidth / 2 + outputw / 2 - 1, toph
                + bottomh - 1);// aa
        insidePoly.addPoint(inputw + vwidth / 2 - outputw / 2 + 2, toph
                + bottomh - 1);// ab
        insidePoly.addPoint(inputw + vwidth / 2 - outputw / 2 + 2, toph); // ac
        g.fillPolygon(insidePoly);
        // draw white bits
        g.setColor(c1);// ///white); //black so i can see them
        g.drawLine(0, 0, inputw, 0); // a,c
        g.drawLine(inputw + vwidth, 0, 2 * inputw + vwidth, 0); // g,i
        g.drawLine(inputw + vwidth, 0, inputw + vwidth / 2, vheight); // g,e
        g.drawLine(inputw + vwidth / 2 - outputw / 2, toph, 0, 0); // m,a
        g.drawLine(inputw + vwidth / 2 - outputw / 2, toph, inputw + vwidth / 2
                - outputw / 2, toph + bottomh); // m,t
        // draw light gray bits
        g.setColor(c2);
        g.drawLine(2, 1, inputw, 1); // b,d
        g.drawLine(2, 1, inputw + vwidth / 2 - outputw / 2 + 1, toph); // b,n
        g.drawLine(inputw + vwidth / 2, vheight + 1, inputw + vwidth, 1); // f,h
        g.drawLine(inputw + vwidth, 1, 2 * inputw + vwidth - 2, 1); // h,k
        g.drawLine(inputw + vwidth / 2 - outputw / 2 + 1, toph, inputw + vwidth
                / 2 - outputw / 2 + 1, toph + bottomh - 2); // n,s (s-1)
        // draw dark gray bits
        g.setColor(c3);
        g.drawLine(inputw, 1, inputw + vwidth / 2, vheight + 1); // d,f
        g.drawLine(2 * inputw + vwidth - 2, 1, inputw + vwidth / 2 + outputw
                / 2 - 1, toph); // j,p
        g.drawLine(inputw + vwidth / 2 + outputw / 2 - 1, toph, inputw + vwidth
                / 2 + outputw / 2 - 1, toph + bottomh - 1); // p,r
        g.drawLine(inputw + vwidth / 2 + outputw / 2 - 1, toph + bottomh - 1,
                inputw + vwidth / 2 - outputw / 2 + 1, toph + bottomh - 1); // r,s
        // draw black bits
        g.setColor(c4);
        g.drawLine(inputw, 0, inputw + vwidth / 2, vheight); // c,e
        g.drawLine(2 * inputw + vwidth, 0, inputw + vwidth / 2 + outputw / 2,
                toph); // i,o
        g.drawLine(inputw + vwidth / 2 + outputw / 2, toph, inputw + vwidth / 2
                + outputw / 2, toph + bottomh); // o,q
        g.drawLine(inputw + vwidth / 2 + outputw / 2, toph + bottomh, inputw
                + vwidth / 2 - outputw / 2, toph + bottomh); // q,t
        // ////phew - time to draw text...
        g.setColor(Color.white);
        g.setFont(modeLabel.getFont());
        // modeText
        g.setColor(Color.white);
        g.fillRect(inputw + vwidth / 2 - modeLabel.getWidth() / 2 - 3,
                vheight + 5, modeLabel.getWidth() + 6, modeLabel.getHeight());
        g.setColor(Color.black);
        g
                .drawString(modeLabel.getLabel(), inputw + vwidth / 2
                        - modeLabel.getWidth() / 2, vheight
                        + modeLabel.getHeight() + 3);
        // labelText
        g.setColor(Color.white);
        g.drawString(nameLabel.getLabel(), inputw + vwidth / 2
                - nameLabel.getWidth() / 2, vheight + 2 * nameLabel.getHeight()
                + 3);
        // ////
    }
}
