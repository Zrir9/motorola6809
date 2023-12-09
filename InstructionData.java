package m6800.data;

import m6800.applet.enums.AddressMode;
import m6800.applet.enums.AluMode;

/*
 * @(#)instructionData.java
 *
 *
 */

/**
 * instructionData() is a data type containing all infor mation needed to run an
 * instruction
 * 
 * @author Simon McCaughey
 * @version 1.0, 29 April 1998
 */

public class InstructionData {

    private AddressMode addressMode;
    private String opcode;
    private String value;
    private AluMode ALUMode;

    /**
     * constructor - simply to initialise values - no arguments
     */
    public InstructionData() {
        // setAddressMode(0);
        setOpcode("");
        setValue("");
        // setALUMode("");
    }

    public void setAddressMode(AddressMode addressMode) {
        this.addressMode = addressMode;
    }

    public AddressMode getAddressMode() {
        return addressMode;
    }

    public void setOpcode(String opcode) {
        this.opcode = opcode;
    }

    public String getOpcode() {
        return opcode;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setALUMode(AluMode aLUMode) {
        ALUMode = aLUMode;
    }

    public AluMode getALUMode() {
        return ALUMode;
    }
}
