package m6800.applet.components;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import m6800.applet.SimulatorApplet;
import m6800.panels.GeneralPanel;

/**
 * controlPanel() provides a panel object which contains and the control
 * features of the program.
 */
public class AppletControlPanel extends GeneralPanel implements ActionListener,
        AdjustmentListener, Runnable {
    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Boolean to indicate compile requested.
     */
    private boolean compileRequested;
    /**
     * Boolean to indicate step requested.
     */
    private boolean stepRequested;
    /**
     * Boolean to indicate run requested.
     */
    private boolean runRequested;
    /**
     * Thread.
     */
    private Thread manager;
    /**
     * Assemble button.
     */
    public Button buttonAssemble;
    /**
     * Bin/Hex button.
     */
    public Button buttonBinHex;
    // image button does not work reliably in all browsers
    // public imageButton runButton,stepButton,stopButton;
    public Button runButton, stepButton, stopButton;
    private Choice memorySizeChooser;
    Label memoryLabel, speedTitleLabel;
    Scrollbar speedScroll;
    Label speedLabel;
    GridBagLayout gridBag;

    // //////////////////////////////////////////////////
    /**
     * constructor method controlPanel() .
     * 
     */
    public AppletControlPanel() {
        // layout??
        setCompileRequested(false);
        setStepRequested(false);
        this.setBackground(getCenterColor()); // color the background
        buttonAssemble = new Button("Assemble");
        // image button does not work reliably in all browsers
        // stepButton=new imageButton("Step",imageButton.STEP);
        // runButton=new imageButton("Run",imageButton.PLAY);
        // stopButton=new imageButton("Stop",imageButton.STOP);
        stepButton = new Button("Step");
        runButton = new Button("Run");
        stopButton = new Button("Stop");
        stepButton.setEnabled(false); // initially disabled
        stopButton.setEnabled(false); // initially disabled
        memorySizeChooser = new Choice();
        memorySizeChooser.addItem("10");
        memorySizeChooser.addItem("25");
        memorySizeChooser.addItem("50");
        memorySizeChooser.addItem("100");
        memoryLabel = new Label("Data Memory Size:");
        runButton.setEnabled(false); // initially disabled
        speedLabel = new Label("Speed = 50");
        speedTitleLabel = new Label("Execution Speed:");
        buttonBinHex = new Button("Bin/Hex");

        speedScroll = new Scrollbar(Scrollbar.HORIZONTAL, 50, 5, 0, 100);

        gridBag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        setLayout(gridBag);
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = GridBagConstraints.REMAINDER; // end of row
        c.insets = new Insets(2, 2, 5, 2);
        c.weightx = 1.0;
        gridBag.setConstraints(buttonAssemble, c);
        add(buttonAssemble);

        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        gridBag.setConstraints(stepButton, c);
        add(stepButton);
        c.gridwidth = GridBagConstraints.RELATIVE; // next to last in row
        gridBag.setConstraints(runButton, c);
        add(runButton);
        c.gridwidth = GridBagConstraints.REMAINDER; // end of row
        gridBag.setConstraints(stopButton, c);
        add(stopButton);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.REMAINDER; // end of row
        c.insets = new Insets(10, 10, 2, 10);
        gridBag.setConstraints(speedTitleLabel, c);
        add(speedTitleLabel);
        c.insets = new Insets(0, 10, 0, 10);
        gridBag.setConstraints(speedScroll, c);
        add(speedScroll);

        c.fill = GridBagConstraints.NONE;
        c.gridwidth = GridBagConstraints.REMAINDER; // end of row
        c.insets = new Insets(2, 2, 10, 2);
        c.weightx = 1.0;
        gridBag.setConstraints(speedLabel, c);
        add(speedLabel);

        c.fill = GridBagConstraints.NONE;
        c.gridwidth = GridBagConstraints.REMAINDER; // end of row
        c.insets = new Insets(10, 2, 10, 2);
        c.weightx = 1.0;
        gridBag.setConstraints(buttonBinHex, c);
        add(buttonBinHex); // above normal memory

        c.insets = new Insets(10, 0, 0, 0);
        gridBag.setConstraints(memoryLabel, c);
        add(memoryLabel);

        c.insets = new Insets(0, 10, 0, 10);
        gridBag.setConstraints(memorySizeChooser, c);
        add(memorySizeChooser);

        panelWidth = 130; // set specific values for height and width
        panelHeight = 300;
        size = new Dimension(panelWidth + 1, panelHeight + 1); // +1 so that all
        // area up to w can be used
        buttonAssemble.addActionListener(this);
        stepButton.addActionListener(this);
        stopButton.addActionListener(this);
        runButton.addActionListener(this);
        buttonBinHex.addActionListener(this);
        speedScroll.addAdjustmentListener(this);

        // TODO temporary speed adjustment
        SimulatorApplet.setAnimationDelay(1);
    }

    /**
     * make this a thread.
     */
    public void run() {
    } // end run()

    /**
     * Start.
     */
    public void start() {
        if (manager == null) {
            manager = new Thread(this);
            manager.start();
        }
    }

    /**
     * Stop method.
     */
    @SuppressWarnings("deprecation")
    public void stop() {
        if (manager != null) {
            manager = new Thread(this);
            manager.stop();
            manager = null;
        }
    }

    /**
     * listener for scroll bar events.
     */
    public void adjustmentValueChanged(final AdjustmentEvent e) {

        int speed;

        speed = e.getValue();

        if (speed > 95) {
            speed = 96;
        }
        speedLabel.setText("Speed = " + (speed));
        SimulatorApplet.setAnimationDelay(96 - speed);
    }

    /**
     * listener for button events.
     * 
     * @param event
     *            - the ActionEvent
     */
    public void actionPerformed(ActionEvent event) {
        // System.out.println(e.getActionCommand());
        String buttonLabel = event.getActionCommand();
        if (buttonLabel.equals("Step")) {
            setStepRequested(true);
            SimulatorApplet.cpuPanel.animationRequest = true;
            try {
                notifyAll();
            } catch (Exception ex) {
                System.out.println("Exception notify caught");
            }
        }

        if (buttonLabel.equals("Run")) {

            setRunRequested(true);
            SimulatorApplet.cpuPanel.animationRequest = true;
            try {
                notifyAll();
            } catch (Exception ex) {
                System.out.println("Exception notify caught");

            }

        }

        if (buttonLabel.equals("Stop")) {

            setRunRequested(false);
            try {
                notifyAll();
            } catch (Exception ex) {
                System.out.println("Exception notify caught");

            }

        }
        if (buttonLabel.equals("Assemble")) {
            setCompileRequested(true);
            SimulatorApplet.cpuPanel.resetAllValues();
            try {
                notify();
            } catch (Exception ex) {
            }
        }
        if (buttonLabel.equals("Bin/Hex")) {
            if (SimulatorApplet.cpuPanel.accumulatorA.getHexMode()) {
                SimulatorApplet.cpuPanel.accumulatorA.setHexMode(false);
                SimulatorApplet.cpuPanel.dataBuffer.setHexMode(false);
                SimulatorApplet.cpuPanel.programCounter.setHexMode(false);
            } else {
                SimulatorApplet.cpuPanel.accumulatorA.setHexMode(true);
                SimulatorApplet.cpuPanel.dataBuffer.setHexMode(true);
                SimulatorApplet.cpuPanel.programCounter.setHexMode(true);
            }
        }
        System.out.println(buttonLabel + " Pressed");
    }

    // returns the data memory size as specified by the user
    public int getDataMemorySize() {

        int index = memorySizeChooser.getSelectedIndex();
        String item = memorySizeChooser.getItem(index);

        return (Integer.valueOf(item)).intValue();

    }

    public void setRunRequested(boolean runRequested) {
        this.runRequested = runRequested;
    }

    public boolean isRunRequested() {
        return runRequested;
    }

    public void setStepRequested(boolean stepRequested) {
        this.stepRequested = stepRequested;
    }

    public boolean isStepRequested() {
        return stepRequested;
    }

    public void setCompileRequested(boolean compileRequested) {
        this.compileRequested = compileRequested;
    }

    public boolean isCompileRequested() {
        return compileRequested;
    }

}
