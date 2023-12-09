package m6800.data;

/*
 * @(#)lookUpTable.java
 *
 *
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import m6800.applet.enums.AddressMode;
import m6800.applet.enums.AluMode;
import m6800.exceptions.InstructionNotFoundException;

/**
 * LookupTable contains a structure with all data about op-codes, and how to run
 * them
 * <P>
 * input is from one text file, containing formatted instruction data
 * 
 * @author Simon McCaughey
 * @version 1.0, 30 April 1998
 */

public class LookupTable {

    private Vector<InstructionData> lookUp;

    /**
     * Constructor arguments : specify initial settings, such as absolute reads
     * one byte at a time from the input file into the specified format
     */
    public LookupTable(String codeBase) {

        lookUp = new Vector<InstructionData>();
        getAddressModeData(lookUp, codeBase);
    }

    /**
     * returns the machine code for a certain opcode given its name and address
     * mode
     */
    public String getMachineCode(String opcode, AddressMode addressMode)
        throws InstructionNotFoundException {

        /* temporary object for general use */
        Object o;

        /* object to hold data while it is being searched */
        InstructionData inst;

        /* simply iterates through table until match is found */
        for (int i = 0; i < lookUp.size(); i++) {
            o = lookUp.elementAt(i);
            inst = (InstructionData) o;
            if ((inst.getAddressMode() == addressMode)
                    && (inst.getOpcode().equals(opcode))) {
                return inst.getValue();
            }
        }

        /*
         * if the code gets here then all elements have been searched, and no
         * match has been found : throw exception
         */
        throw new InstructionNotFoundException(opcode);
    }

    /**
     * returns the ALU Mode for a certain opcode given its name and address mode
     */
    public AluMode getALUMode(String opcode, AddressMode addressMode)
        throws InstructionNotFoundException {

        /* temporary object for general use */
        Object o;

        /* object to hold data while it is being searched */
        InstructionData inst;

        for (int i = 0; i < lookUp.size(); i++) {
            o = lookUp.elementAt(i);
            inst = (InstructionData) o;
            if ((inst.getAddressMode() == addressMode)
                    && (inst.getOpcode().equals(opcode))) {
                return inst.getALUMode();
            }
        }

        /*
         * if the code gets here then all elements have been searched, and no
         * match has been found : throw exception
         */
        throw new InstructionNotFoundException(opcode);
    }

    /*
     * fills the specified address mode with the information this is the code
     * that reads from the file
     */
    private void getAddressModeData(Vector<InstructionData> thisMode,
            String codeBase) {

        InstructionData instructionData;

        String inputLine;

        try {

            System.out.println("Making url");
            URL host = new URL(codeBase + "5.inst");
            System.out.print("Connecting to " + codeBase + " ...");
            URLConnection hc = host.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(hc
                    .getInputStream()));
            System.out.println("...Connected");
            System.out.print(".");
            while ((inputLine = in.readLine()) != null)

                // old code here, same as for file input
                if (inputLine != null) { // //delete or maybe not!!?? //need to
                    // keep

                    if (inputLine.charAt(0) != ';') {

                        /* only execute this code if non-comment */
                        /* create a new object for the vector */
                        instructionData = new InstructionData();

                        String[] splitLine = inputLine.split(",");
                        if (splitLine.length == 4) {

                            AddressMode addressMode = AddressMode
                                    .fromInt(Integer.parseInt(splitLine[0]));
                            instructionData.setAddressMode(addressMode);

                            instructionData.setOpcode(splitLine[1]);
                            instructionData.setValue(splitLine[2]);
                            AluMode aluMode = AluMode.fromInteger(Integer
                                    .parseInt(splitLine[3]));
                            instructionData.setALUMode(aluMode);

                        } else {
                            // TODO some sort of error here
                        }

                        thisMode.addElement(instructionData);
                    }
                }
            inputLine = "";
            in.close(); // close the input connection
        } catch (MalformedURLException e) {
            System.err.println("Error reading url:MalformedURLException "
                    + e.getMessage());
            // throw exception here to stop program
        } catch (IOException e2) {
            System.err.println("Error reading url: IOException "
                    + e2.getMessage());
            // throw exception here to stop program
        }
    }
}
