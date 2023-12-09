package m6800.applet;

/*
 * @(#)SimulatorApplet.java
 *
 *
 */

import java.applet.Applet;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.ScrollPane;
import java.awt.TextArea;

import org.apache.log4j.Logger;

import m6800.applet.components.AppletControlPanel;
import m6800.applet.components.AppletImageCanvas;
import m6800.applet.components.AppletMemoryOverviewPanel;
import m6800.applet.enums.AluMode;
import m6800.canvas.GeneralCellCanvas;
import m6800.data.ALUOutput;
import m6800.data.LineData;
import m6800.data.SimulatorData;
import m6800.exceptions.BadProgramLineException;
import m6800.exceptions.CompileAndRunException;
import m6800.panels.CPUPanel;
import m6800.panels.MemoryInspectorPanel;
import m6800.panels.MultiPanel;
import m6800.util.NumberConverter;

/**
 * This is the main part of the applet - the public class.
 * 
 * @author Simon McCaughey
 * @version $Id 1.0, 29 April 1998$
 */
public class SimulatorApplet extends Applet implements Runnable {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(SimulatorApplet.class);

    /**
     * the thread this applet runs in.
     */
    private Thread manager;

    /**
     * variables for the layout manager.
     */
    private GridBagLayout gridBag;

    /**
     * for all number format conversions.
     */
    private NumberConverter numberConverter;

    /**
     * data memory inspectors.
     * 
     * made static to allow references from buttons
     */
    private MemoryInspectorPanel programMemoryInspector;

    /**
     * program memory inspector.
     * 
     * made static to allow references from buttons
     */
    private MemoryInspectorPanel dataMemoryInspector;

    /**
     * user program entry area.
     */
    private TextArea programText;

    /**
     * Error display area.
     */
    private TextArea errorDisplay;

    /**
     * Multi panel.
     */
    private MultiPanel multi;

    /**
     * memory overview object.
     */
    ScrollPane memoryOverview;

    /**
     * panel for inside memory overview.
     */
    private AppletMemoryOverviewPanel memoryOverviewPanel;
    /**
     * data structure for all running data.
     */
    private SimulatorData simulatorData;
    /**
     * CPU display area.
     */
    public static CPUPanel cpuPanel;
    /**
     * the control area for the software.
     */
    private AppletControlPanel control;

    /**
     * variable for the animation (in milliseconds).
     */
    private static int delay = 1000;
    /**
     * used by runInstruction to keep track of program location.
     */
    private int currentProgramLocation;
    /**
     * Current Program Counter value.
     */
    private int currentPcValue;

    /**
     * Background color.
     */
    private Color bgColor;

    /**
     * make this a thread - run, start & stop all implemented run contains a
     * listening loop to act on control panel buttons.
     */
    public void run() {
        // use a while() loop to continually wait on an action inside the
        // control panel
        while (true) { // forever
            while (!control.isCompileRequested() && !control.isStepRequested()
                    && multi.errorToDisplay) {

                if (multi.errorToDisplay) {

                    logger.info("here anyway too " + multi.getErrorStart()
                            + " " + multi.getErrorEnd());

                    programText.setSelectionStart(multi.getErrorStart());
                    programText.setSelectionEnd(multi.getErrorEnd());

                    multi.errorToDisplay = false;
                }

            }// end while !compileRequested

            if (control.isRunRequested()) {
                control.runButton.setEnabled(false);
                control.stopButton.setEnabled(true);

                showStatus("Running Continuously, press stop to halt after current instruction");
                logger.info("Run Requested");

                while (control.isRunRequested()) {
                    runNextInstruction(); // cause the next instruction to be
                    // animated
                    // request has been completed
                }
                control.runButton.setEnabled(true);
                control.stopButton.setEnabled(false);

                showStatus("Simulation Stopped");

            }

            if (control.isStepRequested()) {

                logger.info("Step Requested");
                showStatus("Running Instruction");
                runNextInstruction(); // cause the next instruction to be
                // animated
                showStatus("");
                control.setStepRequested(false); // tell control that its
                // request has been completed
            }
            if (control.isCompileRequested()) {
                logger.info("Compile Requested");

                control.stepButton.setEnabled(false);
                control.runButton.setEnabled(false);
                control.stopButton.setEnabled(false);

                currentProgramLocation = 0; // keeps count of program line
                currentPcValue = 0; // PC counter

                multi.clearAllErrors();

                try {
                    showStatus("Assembling user code...Please wait");
                    // compile whatever is currently in the text box enable the
                    // buttons
                    compileAndLoad();
                    control.stepButton.setEnabled(true);
                    control.runButton.setEnabled(true);
                    control.stopButton.setEnabled(false);
                    showStatus("Code assembled successfully");
                } catch (CompileAndRunException ce) {
                    logger.info("Program Cannot Run: " + ce.getErrors().size()
                            + " Errors\n");

                    for (int i = 0; i < ce.getErrors().size(); i++) {

                        BadProgramLineException bple = ce.getErrors()
                                .elementAt(i);
                        multi.addError(bple);
                        logger.info(" " + bple.getLineNumber() + " "
                                + bple.getError() + " " + bple.getStart() + " "
                                + bple.getEnd());
                    }

                    // disable the buttons
                    control.stepButton.setEnabled(false);
                    control.runButton.setEnabled(false);
                    control.stopButton.setEnabled(false);
                    showStatus("Program Cannot run : " + ce.getErrors().size()
                            + " error(s), click error to find source");

                }
                // tell control that its request has been completed
                // and load it into the memoryOverviewer
                control.setCompileRequested(false);

            }
        }// end while (true)
    } // end run()

    /**
     * very standard start method.
     */
    public void start() {
        if (manager == null) {
            manager = new Thread(this);
            manager.start();
        }
    }

    // /**
    // * very standard stop method.
    // */
    // @SuppressWarnings("deprecation")
    // public void stop() {
    // if (manager != null) {
    // manager = new Thread(this);
    // manager.stop();
    // manager = null;
    // }
    // }

    /**
     * initially draw all components, and start data structures.
     */
    public void init() {

        // Colour the background
        bgColor = AppletColors.bgColor;
        this.setBackground(bgColor);

        // output some lines to differentiate from last output (for debugging)
        logger.info("\n\n\n\n");

        // initialise number converter object
        numberConverter = new NumberConverter();
        AppletImageCanvas ic1;

        // create the logo image
        Image image1 = getImage(getCodeBase(), "m6800.gif");
        ic1 = new AppletImageCanvas(image1, this, 138, 74);
        logger.debug("adding Control Panel");
        showStatus("adding Control Panel");
        control = new AppletControlPanel();
        control.start();
        logger.debug("adding Simulator Data");
        showStatus("adding Simulator Data");
        simulatorData = new SimulatorData("" + getCodeBase());
        simulatorData.start();

        // area of program for text area ///////////////
        // fill in example program
        programText = new TextArea("", 5, 40); // use constructor to set
        // size (see Help)
        programText.setBackground(Color.white);
        programText.append("\t.processor m6800\n");
        programText.append("\t.org 0c000 \n");
        programText.append("MAIN: \tINCA \t;test inherent\n");
        programText.append("\tLDAA #02 \t;test immediate\n");
        programText.append("\tLDAA 12 \t;test direct mode\n");
        programText.append("\tBRA MAIN \t;test branch instruction\n");
        programText.append("\tSUBA #03 \t;ad 2 to acc a\n");
        programText.append("BRANCH1: DECA \t;load 2 into acc a\n");
        programText.append("\tDECA \t;load 2 into acc a\n");
        programText.append("\tINCA \t;load 2 into acc a\n");
        programText.append("\tCLRA  \t;jump back\n");
        programText.append("\tINCA \t;load 2 into acc a\n");
        programText.append("\tINCA \t;load 2 into acc a\n");
        programText.append("\tINCA \t;load 2 into acc a\n");
        programText.append("\n");
        logger.debug("adding memory overview");
        showStatus("adding memory overview");

        memoryOverviewPanel = new AppletMemoryOverviewPanel();

        multi = new MultiPanel();

        logger.debug("adding Memory overview");
        showStatus("adding Memory overview");
        programMemoryInspector = new MemoryInspectorPanel();
        // data memory
        dataMemoryInspector = new MemoryInspectorPanel();
        // clear any previous data
        errorDisplay = new TextArea();
        logger.debug("adding CPU Panel");
        showStatus("adding CPU Panel");
        cpuPanel = new CPUPanel();
        // reset connection path
        cpuPanel.showConnectionPath(AnimationPaths.NORMAL);

        // apply layout
        gridBag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gridBag);
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(2, 2, 2, 2);
        c.weightx = 1.0;
        gridBag.setConstraints(ic1, c);
        add(ic1);
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.RELATIVE; // next to last in row
        gridBag.setConstraints(programText, c);
        add(programText);
        c.gridwidth = GridBagConstraints.REMAINDER; // end of row
        gridBag.setConstraints(multi, c);
        add(multi);
        c.fill = GridBagConstraints.NONE; // make both as large as possible
        c.gridwidth = 1; // reset to the default
        c.gridheight = 5;
        c.weighty = 1.0;
        gridBag.setConstraints(control, c);
        add(control);
        c.weighty = 0.0; // reset to the default
        c.gridwidth = GridBagConstraints.REMAINDER; // end of row
        c.gridheight = 2; // reset to the default
        gridBag.setConstraints(errorDisplay, c);
        // set it up here, but do not add
        c.weighty = 0.0; // reset to the default
        c.gridwidth = GridBagConstraints.REMAINDER; // end of row
        c.gridheight = 1; // reset to the default
        gridBag.setConstraints(dataMemoryInspector, c);
        add(dataMemoryInspector); // above normal memory
        gridBag.setConstraints(programMemoryInspector, c);
        add(programMemoryInspector);
        gridBag.setConstraints(cpuPanel, c);
        add(cpuPanel);
        System.err.println("Initialisation Complete.. waiting for user input");

    }

    /**
     * used by control panel to vary the animation delay should be implemented
     * in a mode OO way!!
     * 
     * @param i
     *            - delay to set
     */
    public static void setAnimationDelay(final int i) {
        delay = i * 30;
        logger.info("Delay = " + delay);
    }

    /**
     * loads program into data structure, and memory inspector. called by user
     * pressing start Button
     * 
     * @throws CompileAndRunException
     *             - exception thrown if compilation fails
     * 
     */
    public void compileAndLoad() throws CompileAndRunException {
        GeneralCellCanvas tempCell;
        try {
            simulatorData.compile(programText.getText());
            simulatorData.dumpAllData(simulatorData.getTotal());
            // Can't get past this stage if there is A problem
            // initialise the program memory inspector
            programMemoryInspector.initialise(simulatorData.intOrigon());
            // sequentially add each element to the memory inspector
            for (int a = 0; a < simulatorData.getMemoryUsed() + 1; a++) { // +1
                // to make it add the last element
                tempCell = simulatorData.getMemoryElement(a);
                programMemoryInspector.addCell(tempCell);
            }
            // tell the memory inspector that it has received all elements
            programMemoryInspector.execute();
            // initialise the data memory inspector to location 0
            dataMemoryInspector.initialise(0);
            // sequentially add 20 blank elements to the memory inspector
            for (int a = 0; a < control.getDataMemorySize(); a++) {
                tempCell = new GeneralCellCanvas(a, "FF", "FF"); // make new
                // empty
                // cell
                dataMemoryInspector.addCell(tempCell);
                System.out.print(".");
            }

            // tell the memory inspector that it has received all elements
            dataMemoryInspector.execute();

            // set PC=0 ready for execution
            cpuPanel.programCounter.setValue(numberConverter
                    .intToHex(simulatorData.intOrigon() + currentPcValue));
        } catch (CompileAndRunException cre) {
            throw cre; // throw it out to next level
        }
    }

    /**
     * the part of this that runs each instruction runs the instruction at
     * currentProgramLocation.
     */
    public synchronized void runNextInstruction() {
        ALUOutput aluOutput = new ALUOutput();

        logger.debug("Starting..Step Execution");
        // allow no exceptions to crash the program
        LineData currentLine = simulatorData
                .getProgramLine(currentProgramLocation++);

        // ////////////////////////
        // start of fetch cycle //
        // ////////////////////////
        // generic to all instructions - the fetch of the op-code
        // output debug information
        logger.debug("Animating: " + currentLine.location + "\t"
                + currentLine.label + "\t" + currentLine.opcode + "\t"
                + currentLine.textOperand + "\t" + currentLine.hexOperand
                + "\t" + currentLine.getAddressMode() + "\t"
                + currentLine.comment + "\t" + currentLine.bytes + " "
                + currentLine.machineCode + " " + currentLine.ALUMode);
        // highlight the text in the text box
        programText.setSelectionStart(currentLine.start);
        programText.setSelectionEnd(currentLine.end);
        // programText.setEditable(false); //to be done by control panel
        // PC was incremented after the last instruction
        cpuPanel.programCounter.setActive();
        pause(delay / 5);
        fetchMemoryLocation();
        cpuPanel.showConnectionPath(AnimationPaths.DATA_TO_IR);
        pause(delay);
        // data travels from data buffer into IR
        cpuPanel.IR.setActive();
        // set up animation
        cpuPanel
                .animateDataMove(4, true, programMemoryInspector.getCellValue());
        while (cpuPanel.nextMove(2)) { // while there is a next move should
            // return
            // true
            pause(delay / 15);
        }
        cpuPanel.IR.setInstruction(programMemoryInspector.getCellHexValue(),
                cpuPanel.dataBuffer.getValue());
        cpuPanel.dataBuffer.setInActive();
        cpuPanel.showConnectionPath(AnimationPaths.NORMAL);
        logger.debug("Instruction now fetched");

        // fetch cycle
        // instruction is now in the IR ready to be processed:
        // this is address mode dependent /
        // ////////////////////////////////
        switch (currentLine.getAddressMode()) {
            // ////////////////////////////////////////////////////////
            // ////////////////////////////////////////////////////////
            // ////////////////////////////////////////////////////////
            // immediate instructions
            case IMMEDIATE: {
                System.out
                        .println("Running immediate mode instruction -> alu mode = "
                                + currentLine.ALUMode);
                // immed mode goes:
                // fetch instruction (aready complete)
                // inc PC
                // PC -> AddressBuffer
                // memory (operand) -> Data Buffer
                // Set ALU mode
                // Data Buffer -> ALU
                // Acc -> ALU (if not load) (alu mode 9)
                // ALU output -> Acc
                // start fetch operand cycle
                // inc PC
                cpuPanel.programCounter.setActive();
                fetchMemoryLocation();
                cpuPanel.ALU.setActive();
                // Data Buffer -> ALU
                pause(delay);
                cpuPanel.showConnectionPath(AnimationPaths.DATA_TO_ALU);
                cpuPanel.animateDataMove(5, true, programMemoryInspector
                        .getCellValue());
                while (cpuPanel.nextMove(2)) { // while there is a next move
                    // should
                    // return true
                    pause(delay / 15);
                }
                // Acc -> ALU (if not load) (alu mode 9)
                pause(delay / 3);
                cpuPanel.dataBuffer.setInActive();
                pause(delay / 3);
                // test for load instruction
                if (currentLine.ALUMode != AluMode.PASS_MODE) {
                    cpuPanel.accumulatorA.setActive();
                    pause(delay / 3);
                    cpuPanel.showConnectionPath(AnimationPaths.ACC_TO_ALU);
                    cpuPanel.animateDataMove(2, true, cpuPanel.accumulatorA
                            .getValue());
                    while (cpuPanel.nextMove(2)) { // while there is a next move
                        // should
                        // return true
                        pause(delay / 15);
                    }
                } // end if not load instruction
                // ALU output -> Acc
                // get alu output
                // //////
                aluOutput = getFlags(); // set flags to send to ALU
                aluOutput = cpuPanel.ALU.getOutput(currentLine.ALUMode,
                        numberConverter.hexToInt(cpuPanel.accumulatorA
                                .getValue()),
                        numberConverter.hexToInt(programMemoryInspector
                                .getCellValue()), aluOutput);
                setFlags(aluOutput);
                // should test for error - unreturned ALUOutput Object????
                System.err.println("output value = " + aluOutput.getOutput());
                pause(delay);
                cpuPanel.showConnectionPath(AnimationPaths.ALU_TO_ACC);
                cpuPanel.accumulatorA.setActive();
                cpuPanel.animateDataMove(3, true, (numberConverter
                        .intToHex(aluOutput.getOutput())));
                // while there is a next move returns true
                while (cpuPanel.nextMove(2)) {
                    pause(delay / 15);
                }
                cpuPanel.accumulatorA.setValue(numberConverter
                        .intToHex(aluOutput.getOutput()));
                cpuPanel.showConnectionPath(AnimationPaths.NORMAL);
                cpuPanel.ALU.setInActive();
                pause(delay);
                cpuPanel.accumulatorA.setInActive();
                break;
            } // case 1
                // ////////////////////////////////////////////////////////////////////
                // ////////////////////////////////////////////////////////////////////
                // ////////////////////////////////////////////////////////////////////
                // direct instructions
            case DIRECT: {
                // trying to implement the store instruction
                System.out
                        .println("Running direct mode instruction -> alu mode = "
                                + currentLine.ALUMode);
                // direct mode goes:
                // fetch instruction (aready complete)
                // inc PC
                // PC -> AddressBuffer
                // memory (operand) -> Data Buffer
                // data buffer contains address of "operand"
                // data buffer -> address buffer
                // de-activate program memory
                // activate data memory location in address reg
                // data memory value into data buffer
                // same as immediate from here
                // Set ALU mode
                // Data Buffer -> ALU
                // Acc -> ALU (if not load) (alu mode 9)
                // ALU output -> Acc
                // start fetch operand cycle
                // inc PC
                cpuPanel.programCounter.setActive();
                fetchMemoryLocation(); // gets the data location into data
                // buffer
                // this is where it gets different from an immediate instruction
                logger.debug("got address in data buffer");
                // data buffer -> address buffer
                pause(delay);
                cpuPanel.showConnectionPath(AnimationPaths.DATA_TO_ADD);
                cpuPanel.addressBuffer.setActive();
                pause(delay);
                cpuPanel.animateDataMove(6, true, cpuPanel.dataBuffer
                        .getValue());
                // while there is a next move returns true
                while (cpuPanel.nextMove(2)) {
                    pause(delay / 15);
                }
                cpuPanel.addressBuffer.setValue("00"
                        + cpuPanel.dataBuffer.getValue());
                pause(delay / 2);
                cpuPanel.showConnectionPath(AnimationPaths.NORMAL);
                cpuPanel.addressBuffer.setInActive();
                cpuPanel.dataBuffer.setInActive();
                // de-activate program memory
                programMemoryInspector.deActivate();
                // activate data memory location in address reg
                pause(delay);

                dataMemoryInspector.gotoLocation(numberConverter
                        .hexToInt(cpuPanel.addressBuffer.getValue()));

                // data memory value into data buffer
                pause(delay);
                // not this for a store
                // if not store
                // let store = alu mode 18
                if (currentLine.ALUMode != AluMode.STORE) { // go the normal
                    // path
                    cpuPanel.dataBuffer.setActive();
                    pause(delay / 2);
                    cpuPanel.dataBuffer.setValue(dataMemoryInspector
                            .getCellValue());
                    pause(delay);
                    dataMemoryInspector.deActivate();
                    // same as immediate instruction from here
                    // (almost -but not quite)
                    // end not this for a store
                    cpuPanel.ALU.setActive();
                    // not this for a store
                    // Data Buffer -> ALU
                    pause(delay);
                    cpuPanel.showConnectionPath(AnimationPaths.DATA_TO_ALU);
                    cpuPanel.animateDataMove(5, true, dataMemoryInspector
                            .getCellValue());
                    while (cpuPanel.nextMove(2)) { // while there is a next move
                        // should
                        // return true
                        pause(delay / 15);
                    }
                    // Acc -> ALU (if not load) (alu mode 9)
                    pause(delay / 3);
                    cpuPanel.dataBuffer.setInActive();
                    pause(delay / 3);
                    // test for load instruction
                    if (currentLine.ALUMode != AluMode.PASS_MODE) { // ie if not
                        // load
                        cpuPanel.accumulatorA.setActive();
                        pause(delay / 3);
                        cpuPanel.showConnectionPath(AnimationPaths.ACC_TO_ALU);
                        cpuPanel.animateDataMove(2, true, cpuPanel.accumulatorA
                                .getValue());
                        while (cpuPanel.nextMove(2)) { // while there is a next
                            // move
                            // should return true
                            pause(delay / 15);
                        }
                    } // end if not load instruction
                    // ALU output -> Acc
                    // get alu output
                    // //////
                    aluOutput = getFlags(); // set flags to send to ALU
                    // use dataMemIns here to use this value for calculations
                    aluOutput = cpuPanel.ALU.getOutput(currentLine.ALUMode,
                            numberConverter.hexToInt(cpuPanel.accumulatorA
                                    .getValue()), numberConverter
                                    .hexToInt(dataMemoryInspector
                                            .getCellValue()), aluOutput);
                    setFlags(aluOutput);
                    pause(delay);
                    cpuPanel.showConnectionPath(AnimationPaths.ALU_TO_ACC);
                    cpuPanel.accumulatorA.setActive();
                    cpuPanel.animateDataMove(3, true, (numberConverter
                            .intToHex(aluOutput.getOutput())));
                    // while there is a next move returns true
                    while (cpuPanel.nextMove(2)) {
                        pause(delay / 15);
                    }
                    cpuPanel.accumulatorA.setValue(numberConverter
                            .intToHex(aluOutput.getOutput()));
                    cpuPanel.showConnectionPath(AnimationPaths.NORMAL);
                    cpuPanel.ALU.setInActive();
                    pause(delay);
                    cpuPanel.accumulatorA.setInActive();
                    break;
                } // end if not alu mode 18 (store)
                else {
                    // implement store
                    cpuPanel.ALU.setActive();
                    // Acc A -> ALU
                    pause(delay);
                    cpuPanel.showConnectionPath(AnimationPaths.ACC_TO_ALU);
                    cpuPanel.animateDataMove(2, true, cpuPanel.accumulatorA
                            .getValue());
                    while (cpuPanel.nextMove(2)) { // while there is a next move
                        // should
                        // return true
                        pause(delay / 15);
                    }
                    aluOutput = getFlags(); // set flags to send to ALU
                    aluOutput = cpuPanel.ALU.getOutput(AluMode.PASS_MODE,
                            numberConverter.hexToInt(cpuPanel.accumulatorA
                                    .getValue()),
                            numberConverter.hexToInt(cpuPanel.accumulatorA
                                    .getValue()), aluOutput);
                    setFlags(aluOutput);
                    // should test for error - unreturned ALUOutput Object??????
                    // alu -> data buffer
                    cpuPanel.showConnectionPath(AnimationPaths.ALU_TO_DATA);
                    cpuPanel.dataBuffer.setActive();
                    cpuPanel.animateDataMove(7, true, cpuPanel.accumulatorA
                            .getValue());
                    while (cpuPanel.nextMove(2)) { // while there is a next move
                        // should
                        // return true
                        pause(delay / 15);
                    }
                    // data has gone form alu to data buffer
                    cpuPanel.dataBuffer.setValue(numberConverter.intToHex(
                            aluOutput.getOutput()).toUpperCase());
                    pause(delay);
                    // now put the data into memory
                    dataMemoryInspector.setCellValue(numberConverter.intToHex(
                            aluOutput.getOutput()).toUpperCase());
                    pause(delay);
                    cpuPanel.showConnectionPath(AnimationPaths.NORMAL);
                    dataMemoryInspector.deActivate();
                    cpuPanel.dataBuffer.setInActive();
                    cpuPanel.ALU.setInActive();
                    cpuPanel.dataBuffer.setInActive();
                    break;
                }
            } // case 2
                // ////////////////////////////////////////////////////////
                // ////////////////////////////////////////////////////////
                // ////////////////////////////////////////////////////////
                // inherent instructions
            case INHERENT: {
                // this section implements standard code to run an
                // inherent instruction
                pause(delay);
                // get ready to change the ALU mode
                cpuPanel.ALU.setActive();
                pause(delay);
                // select ALU mode
                logger.debug("switching mode " + currentLine.ALUMode);
                // code to be executed for each instruction
                pause(delay);
                cpuPanel.accumulatorA.setActive();
                pause(delay);
                cpuPanel.showConnectionPath(AnimationPaths.ACC_TO_ALU);
                cpuPanel.animateDataMove(2, true, cpuPanel.accumulatorA
                        .getValue());
                while (cpuPanel.nextMove(2)) { // while there is a next move
                    // should
                    // return true
                    pause(delay / 15);
                }
                pause(delay);
                cpuPanel.showConnectionPath(AnimationPaths.ALU_TO_ACC);
                aluOutput = getFlags(); // set flags to send to ALU
                aluOutput = cpuPanel.ALU.getOutput(currentLine.ALUMode,
                        numberConverter.hexToInt(cpuPanel.accumulatorA
                                .getValue()), aluOutput);
                setFlags(aluOutput);
                // should test for error - unreturned ALUOutput Object?????
                // System.err.println("output value = "+ALUop.output);
                pause(delay);
                cpuPanel.animateDataMove(3, true, numberConverter
                        .intToHex(aluOutput.getOutput()));
                while (cpuPanel.nextMove(3)) { // while there is a next move
                    // should
                    // return true
                    pause(delay / 15);
                }
                cpuPanel.accumulatorA.setValue(java.lang.Integer
                        .toHexString((java.lang.Integer.decode(aluOutput
                                .getOutput())).intValue()));
                // instruction completed
                cpuPanel.showConnectionPath(AnimationPaths.NORMAL);
                cpuPanel.ALU.setInActive();
                pause(delay);
                cpuPanel.accumulatorA.setInActive();
                break;
            } // end case 5
                // ////////////////////////////////////////////////////////
                // ////////////////////////////////////////////////////////
                // ////////////////////////////////////////////////////////
                // branch instructions
            case RELATIVE: {
                // this section implements standard code to run a
                // branch instruction
                boolean branch = false;
                // first check condition
                // if true then branch
                // inc PC
                // memory forward
                // mem -> data buffer
                // data buffer -> pc
                // PC = PC + operand
                // if false simply pc++
                // ////////////////////
                // if true then branch
                switch (currentLine.ALUMode) { // in this case not actually
                    // alu mode but branch type
                    case CLEAR: {
                        logger.debug("BRA instruction");
                        branch = true;
                        break;
                    }
                    case DECREMENT: { // branch if equal to zero
                        if (cpuPanel.CCR.zeroSet()) {
                            branch = true;
                        }
                        break;
                    }
                    case INCREMENT: { // branch if not equal
                        // to zero
                        if (!cpuPanel.CCR.zeroSet()) {
                            branch = true;
                        }
                        break;
                    }
                    case SHIFT_LEFT: { // branch if carry clear
                        if (!cpuPanel.CCR.carrySet()) {
                            branch = true;
                        }
                        break;
                    }
                    case SHIFT_RIGHT: { // branch if carry set
                        if (cpuPanel.CCR.carrySet()) {
                            branch = true;
                        }
                        break;
                    }
                    case ROTATE_RIGHT: { // branch if positive (bit 7 = 0)
                        if (!cpuPanel.CCR.negativeSet()) {
                            branch = true;
                        }
                        break;
                    }
                    case ROTATE_LEFT: { // branch if negative (bit 7 = 1)
                        if (cpuPanel.CCR.negativeSet()) {
                            branch = true;
                        }
                        break;
                    }
                    default:
                        break;
                }
                if (branch) { // if branch is true the condition has been met
                    // inc PC
                    // memory forward
                    // mem -> data buffer
                    cpuPanel.programCounter.setActive();
                    fetchMemoryLocation();
                    // data buffer -> pc
                    cpuPanel.showConnectionPath(AnimationPaths.DATA_TO_PC);
                    cpuPanel.programCounter.setActive();
                    cpuPanel.animateDataMove(8, true, cpuPanel.dataBuffer
                            .getValue());
                    while (cpuPanel.nextMove(2)) { // while there is a next move
                        // should
                        // return true
                        pause(delay / 15);
                    }
                    // now get set up for next instruction
                    currentProgramLocation = simulatorData.startPosition(
                            currentPcValue - 2, currentLine.hexOperand,
                            currentLine.bytes);
                    logger.debug("new line = " + currentProgramLocation);
                    // PC = PC + operand
                    // have to code this as to whether its forward or backward?
                    // prehaps during assembly assign hexOp to a + or - number??
                    logger.debug("signed return = "
                            + numberConverter
                                    .signedHexToInt(cpuPanel.dataBuffer
                                            .getValue()));
                    currentPcValue = currentPcValue
                            + numberConverter
                                    .signedHexToInt(cpuPanel.dataBuffer
                                            .getValue());
                    cpuPanel.programCounter.setValue(numberConverter
                            .intToHex(currentPcValue
                                    + simulatorData.intOrigon()));
                    pause(delay);
                    cpuPanel.dataBuffer.setInActive();
                    cpuPanel.programCounter.setInActive();
                    cpuPanel.showConnectionPath(AnimationPaths.NORMAL);
                } else {
                    // make pc point past the operand
                    currentPcValue++;
                    cpuPanel.programCounter.setValue(numberConverter
                            .intToHex(currentPcValue
                                    + simulatorData.intOrigon()));
                }
                break;
            } // end case 6
            default: {
                System.err
                        .println("\nThis address mode has not been implemented\n");
                break;
            } // end default
        }// /end switch (address mode)
        cpuPanel.IR.setInActive();
        logger.info("Stopping..");
        programText.setEditable(true);
    } // end runNextInstuction()

    /**
     * Take an ALUOutput object and applies it to the ALU.
     * 
     * @param a
     *            - the ALU Output
     */
    public void setFlags(final ALUOutput a) {
        cpuPanel.CCR.clearChanges(); // clear all changes from last operation
        if (a.isHChanged()) {
            if (a.isHValue()) {
                cpuPanel.CCR.setHalfCarry();
            } else {
                cpuPanel.CCR.clearHalfCarry();
            }
        }
        if (a.isIChanged()) {
            if (a.isIValue()) {
                cpuPanel.CCR.setInterrupt();
            } else {
                cpuPanel.CCR.clearInterrupt();
            }
        }
        if (a.isNChanged()) {
            if (a.isNValue() == true) {
                cpuPanel.CCR.setNegative();
            } else {
                cpuPanel.CCR.clearNegative();
            }
        }
        if (a.isZChanged()) {
            if (a.isZValue() == true) {
                cpuPanel.CCR.setZero();
            } else {
                cpuPanel.CCR.clearZero();
            }
        }
        if (a.isVChanged()) {
            if (a.isVValue() == true) {
                cpuPanel.CCR.setOverflow();
            } else {
                cpuPanel.CCR.clearOverflow();
            }
        }
        if (a.isCChanged()) {
            if (a.isCValue() == true) {
                cpuPanel.CCR.setCarry();
            } else {
                cpuPanel.CCR.clearCarry();
            }
        }
    }

    /**
     * returns an ALUOutput object containing the current flag values.
     * 
     * @return -returns an ALUOutput object containing the current flag values
     */
    public ALUOutput getFlags() {
        ALUOutput o = new ALUOutput();
        // initialise all to false
        o.setHValue(false);
        o.setIValue(false);
        o.setNValue(false);
        o.setZValue(false);
        o.setVValue(false);
        o.setCValue(false);
        if (cpuPanel.CCR.halfCarrySet()) {
            o.setHValue(true);
        }
        if (cpuPanel.CCR.interruptSet()) {
            o.setIValue(true);
        }
        if (cpuPanel.CCR.negativeSet()) {
            o.setNValue(true);
        }
        if (cpuPanel.CCR.zeroSet()) {
            o.setZValue(true);
        }
        if (cpuPanel.CCR.overflowSet()) {
            o.setVValue(true);
        }
        if (cpuPanel.CCR.carrySet()) {
            o.setCValue(true);
            logger.debug("Recording Carry set");
        }
        return o;
    }

    /**
     * pause().
     * 
     * used to implement a simple delay (in Ms).
     * 
     * @param time
     *            - time to pause for.
     * 
     */
    public synchronized void pause(final int time) {
        try {
            Thread.sleep(time);
            notify();
        } catch (InterruptedException e) {
            // do nothing
        }
    }

    /**
     * simply to avoid repetition of code , and promote uniformity of animation.
     */
    public void fetchMemoryLocation() {
        logger.debug("Fetching next memory location");
        pause(delay);
        // make bus active between PC and Address buffer
        cpuPanel.showConnectionPath(AnimationPaths.PC_TO_ADD);
        pause(delay);
        // put current PC value into the address buffer
        cpuPanel.addressBuffer.setActive();
        pause(delay);
        cpuPanel.animateDataMove(0, true, cpuPanel.programCounter.getValue());
        while (cpuPanel.nextMove(2)) { // while there is a next move should
            // return
            // true
            pause(delay / 15);
        }
        cpuPanel.addressBuffer.setValue(cpuPanel.programCounter.getValue());
        pause(delay);
        // move memory to the next instruction (what about jumps??)
        // advance memory
        logger
                .debug("get address = "
                        + programMemoryInspector.getCellAddress());
        logger.debug("pc current = " + currentPcValue);
        programMemoryInspector.gotoLocation(currentPcValue);
        cpuPanel.programCounter.setInActive();
        cpuPanel.showConnectionPath(AnimationPaths.NORMAL);
        pause(delay);
        // data buffer becomes active
        cpuPanel.dataBuffer.setActive();
        pause(delay);
        // data comes from memory into data buffer
        cpuPanel.dataBuffer.setValue(programMemoryInspector.getCellValue());
        cpuPanel.addressBuffer.setInActive();
        currentPcValue++;
        cpuPanel.programCounter.setValue(numberConverter.intToHex(simulatorData
                .intOrigon()
                + currentPcValue));

    }
} // end SimulatorApplet Class

