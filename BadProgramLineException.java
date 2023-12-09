package m6800.exceptions;

/*
 * @(#)BadProgramLineException.java
 *
 *
 */

/**
 * A class used by XX class to return information in the assembly task
 * 
 * @author Simon McCaughey
 * @version 1.0, 28 April 1998
 */

public class BadProgramLineException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private int lineNumber; // line number of the offending line
    private String error; // text to be displayed for the user
    private int start; // start of text for highlighting(if applicable)
    private int end; // end of text for highlighting(if applicable)

    /**
     * Constructor arguments :
     * <P>
     * line : line number to be passed back containing error
     * <P>
     * er : string containing a description of the error
     * <P>
     * s : starting position of erroneous line in input string
     * <P>
     * e : end position of erroneous line in input string
     */
    public BadProgramLineException(int line, String er, int s, int e) {
        super();
        setLineNumber(line);
        setError(er);
        setStart(s);
        setEnd(e);
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getStart() {
        return start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getEnd() {
        return end;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
