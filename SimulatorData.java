package m6800.data;

/*
 * @(#)generalCell.java
 *
 * 2002 Simon McCaughey
 *
 */

import java.util.Vector;

import m6800.applet.enums.AddressMode;
import m6800.canvas.GeneralCellCanvas;
import m6800.exceptions.BadProgramLineException;
import m6800.exceptions.CompileAndRunException;
import m6800.exceptions.InstructionNotFoundException;
import m6800.util.NumberConverter;

/**
 * simulatorData() a data structure containing a VECTOR (not public) of
 * formatted information -- "compiled" from an input stream
 * 
 * @author Simon McCaughey
 * @version 1.0, 29 April 1998
 */
public class SimulatorData implements Runnable {

    private LookupTable instructions; // data structure for all instructions
    private NumberConverter numberConverter;
    private Thread managerThread;

    // a vector of type lineData to hold all running data
    private Vector<LineData> runData;// private data structure for execution
    // information
    private Vector<LabelAndLocation> labelLocations; // store label locations as
    // they are found
    private Vector<String> labelList;// record all labels to ensure they exist

    LabelAndLocation labelAndLocation;
    private int origon; // only accessible through methods

    // make this a thread
    public void run() {
    }

    public void start() {
        if (managerThread == null) {
            managerThread = new Thread(this);
            managerThread.start();
        }
    }

    @SuppressWarnings("deprecation")
    public void stop() {
        if (managerThread != null) {
            managerThread = new Thread(this);
            managerThread.stop();
            managerThread = null;
        }
    }

    // returns the origon as defined in ".org C000"
    public String stringOrigon() {
        return java.lang.Integer.toHexString(origon); // constant currently ->
        // needs to be added to
        // assembler
    }

    // returns the origon as defined in ".org C000"
    public int intOrigon() {
        return origon; // constant currently -> needs to be added to assembler
    }

    /**
     * Constructor arguments :
     * <P>
     * codeBase : the applets url, to allow access to the instruction file
     */
    public SimulatorData(String codeBase) { // initialise new vectors (private)
        instructions = new LookupTable(codeBase);

        // initialise lookup table of instructions
        // creates object and gets all instructions
        runData = new Vector<LineData>(); // vector of type linedata
        labelLocations = new Vector<LabelAndLocation>(); // vector of type
        labelList = new Vector<String>(); // of type string
        numberConverter = new NumberConverter();
    }

    /**
     * initialise() accepts the input text (from the text box) as one long
     * string and parses it into lines, and calls addNewLine() with each full
     * line
     */
    public void compile(String inputData) throws CompileAndRunException {
        Vector<BadProgramLineException> errors = new Vector<BadProgramLineException>();
        String outputString;
        int start = 0; // int to store position in text box of start of line
        int end = 0; // int to store end position
        int count = 0;
        // used in the highlighting of strings in step mode
        // firstly reset all vectors
        runData.removeAllElements();
        labelLocations.removeAllElements();
        labelList.removeAllElements();
        outputString = "";
        int lineNumber = 0;
        for (int i = 0; i < inputData.length(); i++) {
            if ((inputData.charAt(i) == 10)
                    && (outputString.trim().length() != 0)) {
                /*
                 * ie if end of non zero line end seems to lose one digit each
                 * time ?? dont know why tried using CharacterIterator, but its
                 * just the same ?? this method works (untidily)
                 * 
                 * there is definitely something wrong with the text box, or my
                 * understanding of the text box!!
                 */
                try {
                    lineNumber++;
                    addNewLine(lineNumber, outputString, start, end + count);

                } catch (BadProgramLineException bple) {
                    // add exception to list
                    System.out.println("Exception caught : "
                            + bple.getLineNumber() + " " + bple.getError()
                            + " " + bple.getStart() + " " + bple.getEnd());
                    errors.addElement(bple);
                }
                outputString = ""; // reset
                count++;
                start = end;
            } else { // add another char
                outputString += "" + inputData.charAt(i);
                end += 1;
            }
        }
        if (errors.size() > 0) {
            throw new CompileAndRunException(errors);
        }

        secondPass(); // call second pass
    }

    /**
     * parse and add each line
     */
    public void addNewLine(int lineNumber, String lineToAdd, int start, int end)
        throws BadProgramLineException {
        int totalDigits;
        char c;
        int x, semiColonPosition, colonPosition, spaceBetween;
        boolean firstExecutable = false;
        if (lineToAdd == null) {
            return;
        }
        // first check for directive
        if (lineToAdd.trim().charAt(0) == '.') {
            lineToAdd = lineToAdd.trim();
            // check for existance of directive
            if (!firstExecutable) {
                if (lineToAdd.startsWith(".processor")) {
                    System.out.println("Directive .pro found");
                    if (lineToAdd.toUpperCase().endsWith("M6800")) {
                        return;
                    } else {
                        // unrecognised thing throw exception
                        System.out.println("Directive not found exception");
                        lineToAdd = lineToAdd.substring(10).trim();
                        throw new BadProgramLineException(lineNumber,
                                "Processor " + lineToAdd + " not known", start,
                                end);
                    }
                    // do nothing with this
                }
                if (lineToAdd.startsWith(".org")) {
                    System.out.println("Directive .org found");
                    // take last 5 chars of string : 0c000
                    String s = lineToAdd.substring(lineToAdd.length() - 5);
                    origon = numberConverter.hexToInt(s.trim());
                    return;
                }
            } // end if firstExecutable
            if (lineToAdd.startsWith(".end")) {
                // do nothing
            }
            // if the code is here, the line starting with . has not been
            // identified
            // need to throw exception
            System.err.println("Directive error");
            throw new BadProgramLineException(0, "Directive " + lineToAdd
                    + " not understood", start, end);
        }
        // next check for comment
        if (lineToAdd.trim().charAt(0) == ';') {
            // need to add this line to the system as a comment???
            return;
        }
        // if(!finished){
        firstExecutable = true; // tell . instructions that end is
        // the only one allowed after this point
        // if the code gets here then the line must be executable
        // ////////bit by bit separate out each string in the line
        LineData temp = new LineData(); // make new line information object to
        // add
        // first add debug locations for text box charactors
        temp.start = start;
        temp.end = end;
        temp.type = "executable";
        // ////////set location to last times location + bytes
        if (getTotal() == 0) { // check for fisrt program line situation
            // origon=hexToInt("C000"); //needs to be read in properly later
            temp.location = 0;
        } else { // otherwise
            temp.location = getLocation(getTotal() - 1)
                    + getBytes(getTotal() - 1);
        }
        // ////////Parse string into label middle and comment
        // label is anything to left of :
        // comment is anything to right of ;
        // find positions of labels within string
        colonPosition = 0; // initialise to 0 for later substring()
        semiColonPosition = lineToAdd.length(); // same as above
        for (x = 0; x < lineToAdd.length(); x++) {
            c = lineToAdd.charAt(x);
            switch (c) {
                case (':'): {
                    colonPosition = x;
                    break;
                }
                case (';'): {
                    semiColonPosition = x;
                    break;
                }
            }
        }

        // ////////separate label
        temp.label = (lineToAdd.substring(0, colonPosition));
        if (temp.label.length() != 0) { // add it to the 2nd pass table
            labelAndLocation = new LabelAndLocation();
            labelAndLocation.setLabel(temp.label); // record name of label
            labelAndLocation.setLocation(temp.location); // record relative
            // address of label
            labelLocations.addElement(labelAndLocation);
        }
        // ////////separate comment
        if (lineToAdd.length() == semiColonPosition) {
            temp.comment = ""; // no comment
        } else {
            temp.comment = lineToAdd.substring(semiColonPosition + 1, lineToAdd
                    .length());
        }
        // break string down to just op-code & operand
        lineToAdd = lineToAdd.substring(colonPosition == 0 ? 0
                : colonPosition + 1, semiColonPosition);
        // ////////separate op-code and operand
        lineToAdd = lineToAdd.trim(); // remove all spaces from start and end
        spaceBetween = 0;
        for (x = 0; x < lineToAdd.length(); x++) { // look for spaces in the
            // string
            if ((c = lineToAdd.charAt(x)) == ' ')
                spaceBetween = x;
        }
        // System.out.println("Space in line ... "+lineToAdd+" at "+spaceBetween
        // );
        // ////////check for existence of operand
        if (spaceBetween == 0) {
            temp.opcode = lineToAdd.trim();
            temp.textOperand = "";
        } else {
            temp.opcode = (lineToAdd.substring(0, spaceBetween)).trim();
            temp.textOperand = (lineToAdd.substring(spaceBetween + 1, lineToAdd
                    .length())).trim();
        }
        // ////// fill in hex operand as blank, will be filled out later
        temp.hexOperand = ""; // found out in second pass

        // ///////////////////////////////////////////////////
        // /set addressing mode and therefore bytes field
        AddressMode addressMode = getAddressMode(temp.opcode, temp.textOperand);
        temp.setAddressMode(addressMode);
        // /no of bytes used ba certain address mode is always the same
        switch (temp.getAddressMode()) {
            case IMMEDIATE: {
                temp.bytes = 2; // immediate
                break;
            }
            case DIRECT: {
                temp.bytes = 2; // direct
                break;
            }
            case INHERENT: {
                temp.bytes = 1; // inherent
                break;
            }
            case RELATIVE: {
                temp.bytes = 2; // relative
                break;
            }
        }
        // if addressing mode is 6 (ie a BXX instruction) then add label to
        // label table to ensure it is resolved in the second pass

        if (temp.textOperand.length() != 0) { // if the operand is not zero
            // length

            // check if it is a label
            totalDigits = 0;

            System.err.println("totalDigits = " + totalDigits + " for "
                    + temp.textOperand);
            if (isLabel(temp.textOperand)) {
                // add it to the list of labels
                labelList.addElement(temp.textOperand);
                System.err.println("Label Added : " + temp.textOperand);
            }
            // if there is actually a number given parse it and put it in

        }
        // parse complete... add extra information by instruction lookup
        try {
            System.out.println("opcode = mode: " + temp.opcode + " , "
                    + temp.getAddressMode());
            temp.ALUMode = instructions.getALUMode(temp.opcode, temp
                    .getAddressMode());
            temp.machineCode = instructions.getMachineCode(temp.opcode, temp
                    .getAddressMode());
        } catch (InstructionNotFoundException ie) {
            System.err.println("Instruction not found : " + temp.opcode);
            // throw exception here
            throw new BadProgramLineException(0, "Instruction " + temp.opcode
                    + " not found", start, end);
        }
        // now that all the first pass data is complete in this line .. add it
        runData.addElement(temp);
    } // end addNewLine()

    public AddressMode getAddressMode(String opcode, String operand) {

        if (operand.length() == 0) {
            // no operand therefore inherent
            return AddressMode.INHERENT;
        }
        for (int d = 0; d < operand.length(); d++) {
            if (operand.charAt(d) == '#') {
                if (opcode.toUpperCase().charAt(0) == 'B') {
                    return AddressMode.RELATIVE;
                } else {
                    return AddressMode.IMMEDIATE;
                }
            }
            // operand contains # = direct
        }
        if ((!opcode.equals("BIT")) && (opcode.charAt(0) == 'B')) {
            // relative
            return AddressMode.RELATIVE;
        }
        // has to be direct; the only one left
        return AddressMode.DIRECT;
    }

    // ////// secondPass() to be made private later
    // ///////////////////////////////////////////////////
    private void secondPass() throws CompileAndRunException {
        // ////check all labels and replace .hexOperand
        Vector<BadProgramLineException> errors = new Vector<BadProgramLineException>();
        // System.out.println("Second Pass");
        int x, y;

        boolean found;
        LabelAndLocation v;
        int totalFound; // for label count to ensure all labels are declared
        totalFound = 0;

        // for each label found in operands field
        for (x = 0; x < labelList.size(); x++) {
            found = false;
            for (y = 0; y < labelLocations.size(); y++) {
                LabelAndLocation obj = labelLocations.elementAt(y);

                v = ((LabelAndLocation) obj);
                System.out.println("Comparing " + labelList.elementAt(x)
                        + " to " + v.getLabel() + " total found  = "
                        + totalFound);
                if (labelList.elementAt(x).equals(v.getLabel())) {
                    totalFound += 1;
                    found = true;
                    break; // break
                }
            }
            if (!found) {
                System.err.println("ERROR : Label " + labelList.elementAt(x)
                        + " not found");
                // serious error -- undefined label
                LineData l;
                int i;
                // find line in error to return start and end locations
                for (i = 0; i != runData.size(); i++) {
                    l = ((LineData) runData.elementAt(i));
                    if (l.textOperand.equals(labelList.elementAt(x))) {
                        BadProgramLineException bple = new BadProgramLineException(
                                i, "Undefined label " + labelList.elementAt(x),
                                l.start, l.end);
                        errors.addElement(bple);
                        break;
                    }
                }
            }
        }

        for (int line = 0; line < runData.size(); line++) {
            // check each line
            found = false;
            LineData lineData = runData.elementAt(line);

            for (int labelLine = 0; labelLine < labelLocations.size(); labelLine++) {
                // for each label
                if (lineData.textOperand.length() == 0) { // no operand
                    found = true;
                    break;
                }

                LabelAndLocation labelAndLocation = labelLocations
                        .elementAt(labelLine);

                if (lineData.textOperand.equals(labelAndLocation.getLabel())) {

                    // check to see if jump forwards or backwards
                    if (labelAndLocation.getLocation() >= lineData.location) { // jump
                        // forwards
                        // or
                        // 0
                        lineData.hexOperand = numberConverter
                                .intToSignedHex(labelAndLocation.getLocation() - 2); // See
                        // note
                        // **
                    } else { // jump backwards
                        lineData.hexOperand = numberConverter
                                .intToSignedHex(labelAndLocation.getLocation()
                                        - lineData.location - 2);// See note **
                    }

                    // NOTE ** :take 1 away to have it point to the
                    // location before the instruction so that
                    // when pc++ pc points to it

                    runData.setElementAt(lineData, line);
                    found = true;
                    break; // jump out of for loop
                }
                // check for equality of labels
            }
            if (!found) {

                try {
                    switch (lineData.getAddressMode()) {
                        case IMMEDIATE: { // remove the #
                            lineData.hexOperand = operandValue(lineData.textOperand);
                            break;
                        }
                        case DIRECT: { // output=input
                            lineData.hexOperand = operandValue(lineData.textOperand);
                            break;
                        }
                        case RELATIVE: { // output=input
                            System.out.println("Processing mode 6 :"
                                    + lineData.textOperand);

                            lineData.hexOperand = operandValue(lineData.textOperand);

                            // now check to see if the specified jump location
                            // exists
                            // ie current location + bytes +- jump
                            if (startPosition(lineData.location,
                                    lineData.hexOperand, lineData.bytes) == -1) {

                                // returns -1 if not found
                                // gets here if before or after program
                                // throw exception here
                                System.err
                                        .println("\n\n\nInvalid branch requested");
                                // //////
                                BadProgramLineException bple = new BadProgramLineException(
                                        0, "Branch out of range : "
                                                + lineData.hexOperand,
                                        lineData.start, lineData.end);
                                errors.addElement(bple);
                                // //////

                            }
                            // System.out.println("Processed mode 6 :"+temp.textOperand+", hex= "+ld.hexOperand);
                            break;
                        }
                    }
                } catch (NumberFormatException exc) {

                    // if the user entered a hex value without a 'h' at the end
                    BadProgramLineException bple = new BadProgramLineException(
                            0, "Number : " + lineData.textOperand
                                    + " is not an Integer", lineData.start,
                            lineData.end);
                    errors.addElement(bple);

                }
                // ld.hexOperand="None";
                runData.setElementAt(lineData, line);
            }
        }
        if (errors.size() > 0) {
            throw new CompileAndRunException(errors);
        }
    }

    // called by second pass to ensure that the specified branch is possible
    // returns -1 if in error, otherwise returns line number containing
    // the instruction to jump to
    // aalso caled by runnext instruction to see where to branch to
    public int startPosition(int location, String signedJump, int bytes) {
        System.out
                .println("///////////Starting branch computation///////////////");
        int jump = numberConverter.signedHexToInt(signedJump);
        System.out.println("signed jump= " + jump + " from location "
                + location + " (bytes " + bytes + ")");
        location = location + jump + bytes;// +1; //+1 is increment in pc
        System.out.println("new location= " + location);
        for (int i = 0; i < runData.size(); i++) {
            System.out.println("comparing " + location + " with "
                    + getProgramLine(i).location + ", returning " + i);
            if (location == getProgramLine(i).location) {
                return i;
            }
        }
        // if the program gets here then the specified jump is not possible,
        // and an error must be flaged
        return -1;
    }

    // getMemoryUsed() returns number of bytes in the current program
    public int getMemoryUsed() {
        LineData lineData = runData.lastElement();
        if (lineData.textOperand.length() == 0) {
            return lineData.location;
        } else {
            return lineData.location + 1;
        }
    }

    // //////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////
    // returns the specified line given the starting location of the line
    public LineData getProgramLine(int index) {
        Object o;
        o = runData.elementAt(index);
        return (LineData) o;
    }

    // //////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////
    public GeneralCellCanvas getMemoryElement(int index) {
        // called by the memory inspector sequentially to get data
        LineData lineData;
        GeneralCellCanvas g = null;
        System.out.print(".");
        // System.out.print("Searching for " +index);
        for (int x = 0; x != runData.size(); x++) {
            lineData = runData.elementAt(x);

            if (lineData.location == index) { // return the opcode
                // System.out.print(" op-code found " +index);
                // initialise with opcode in both - fixed at addition to array
                g = new GeneralCellCanvas(lineData.location, lineData.opcode,
                        lineData.machineCode);
                // ""+getAddressMode(temp.opcode,temp.textOperand));
                // doing a fly thing - passing the address mode
                // in the label field - will be filled out
                // when assigning the object to the memory inspector
                return g;
            }
            // check for operand
            if ((lineData.location + 1 == index)
                    && (lineData.textOperand.length() != 0)) {
                // return the operand
                // System.out.print(" operand found " +index);
                g = new GeneralCellCanvas(lineData.location + 1,
                        lineData.textOperand, lineData.hexOperand);
                return g;
            }
            // if we are still here then not found
        }
        System.out.println("had to retrun NULL");
        return g; // null if not found
    }

    // ////
    public int getTotalLabels() {
        return labelLocations.size();
    }

    // ////
    public int getTotal() {
        return runData.size();
    }

    // ////test output to dump all data to std out
    public void dumpAllData(int totalElements) {
        int position;
        System.out.println("\n\nData Dump in progress");
        System.out
                .println("\nlocat\tlabel\topcode\ttextop\thexop\tad mode\tcomment\tbytes");
        for (position = 0; position != totalElements; position++) {
            LineData lineData = runData.elementAt(position);

            System.out.println("" + lineData.location + "\t" + lineData.label
                    + "\t" + lineData.opcode + "\t" + lineData.textOperand
                    + "\t" + lineData.hexOperand + "\t"
                    + lineData.getAddressMode() + "\t" + lineData.comment
                    + "\t" + lineData.bytes + " " + lineData.machineCode + " "
                    + lineData.ALUMode);
        }
    }

    /**
     * test output to dump all labels and locations to std out
     * 
     * @param totalElements
     */
    public void dumpAllLabels(int totalElements) {
        int position;
        System.out.println("\n\nLabel Dump in progress");
        System.out.println("\nlabel\tlocation");
        for (position = 0; position != totalElements; position++) {
            LabelAndLocation labelAndLocation = labelLocations
                    .elementAt(position);

            System.out.println("" + labelAndLocation.getLabel() + "\t"
                    + labelAndLocation.getLocation());
        }
        System.out.println("LabelList:");
        for (position = 0; position != labelList.size(); position++) {
            System.out.println("" + labelList.elementAt(position));
        }
    }

    // returns location variable of indexed object
    public int getLocation(int position) {
        LineData lineData = runData.elementAt(position);

        return lineData.location;
    }

    // returns bytes variable of indexed object
    public int getBytes(int position) {

        LineData lineData = runData.elementAt(position);

        return lineData.bytes;
    }

    // returns a string containing the "value" of the users typed operand
    // ie strips #, d, and h from end
    // defaults to decimal
    private String operandValue(String operand) {

        System.out.println("input = " + operand);

        int length = operand.length();

        if (operand.charAt(0) == '#') {

            operand = operand.substring(1);
            length = operand.length(); // update length

        }

        /*
         * case of hexadecimal input - strip the 'h'
         */
        if ((operand.charAt(length - 1) == 'h')
                || (operand.charAt(length - 1) == 'H')) {

            operand = operand.substring(0, length - 1);
            length = operand.length(); // update length
            return operand;

        }
        /*
         * case of decimal input - strip the 'd'
         */
        if ((operand.charAt(length - 1) == 'd')
                || (operand.charAt(length - 1) == 'D')) {

            operand = operand.substring(0, length - 1);
            operand = numberConverter.intToHex(operand);
            length = operand.length(); // update length
            return operand;

        }

        // default to decimal
        operand = numberConverter.intToHex(operand);
        return operand;

    }

    // returns true if string parameter is a label

    private boolean isLabel(String operand) {

        // strip off all allowed charactors

        int length = operand.length();

        try {

            if (operand.charAt(0) == '#') {

                operand = operand.substring(1);
                length = operand.length(); // update length

            }

            if ((operand.charAt(length - 1) == 'h')
                    || (operand.charAt(length - 1) == 'H')) {

                operand = operand.substring(0, length - 1);
                length = operand.length(); // update length

            }

            if ((operand.charAt(length - 1) == 'd')
                    || (operand.charAt(length - 1) == 'D')) {

                operand = operand.substring(0, length - 1);
                operand = numberConverter.intToHex(operand);
                length = operand.length(); // update length

            }

            // if hexToInt doesn't return an exception then it is a number

            numberConverter.hexToInt(operand);

            return false;
        } catch (NumberFormatException e) {

            System.err.println("caught format exception ...............");
            return true;

        }

    }

    // /////////////////////////////////////////////////////////////////////////
}
