package m6800.data;

import m6800.applet.enums.AddressMode;
import m6800.applet.enums.AluMode;

/*
 * @(#)lineData.java
 *
 *
 */

/**
 * A class used by simulatorData class to hold information in the assembly task
 * : a data structure for each line of code
 * 
 * @author Simon McCaughey
 * @version 1.0, 28 April 1998
 */

public class LineData {

    /**
     * type of line - code or comment or directive
     */
    public String type;

    /**
     * current pc offset from start (2nd pass)
     */
    public int location;

    /**
     * field for any labels in source code
     */
    public String label;

    /**
     * text of op-code
     */
    public String opcode;

    /**
     * full text version of operand (eg MAIN)
     */
    public String textOperand;

    /**
     * actual representation of operand (eg -4)
     */
    public String hexOperand;

    /**
     * 1=immediate, 2=direct, 5=inherent, 6=relative
     */
    private AddressMode addressMode;

    /**
     * comment stored from source file
     */
    public String comment;

    /**
     * no of bytes used by instruction
     */
    public int bytes;

    /**
     * ALU mode used to implement instruction
     */
    public AluMode ALUMode;

    /**
     * opcode hex value
     */
    public String machineCode;

    /**
     * start and end for use in the
     */
    public int start;

    /**
     * debug step mode to highlight the text
     */
    public int end;

    /**
     * Empty constructor as values do not need to be initialised
     */
    public LineData() {
    }

    /**
     * @return the addressMode
     */
    public AddressMode getAddressMode() {
        return addressMode;
    }

    /**
     * @param addressMode
     *            the addressMode to set
     */
    public void setAddressMode(AddressMode addressMode) {
        this.addressMode = addressMode;
    }
}
