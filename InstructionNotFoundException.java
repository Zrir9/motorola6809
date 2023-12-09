package m6800.exceptions;

/*
 * @(#)InstructionNotFoundException.java
 *
 *
 */

/**
 * A class used by XX class to return information in the assembly task
 * 
 * @author Simon McCaughey
 * @version 1.0, 28 April 1998
 */

public class InstructionNotFoundException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /*
     * string containing the instruction that was not located
     */
    String instruction;

    /**
     * Constructor arguments : called with the instruction that was not located
     */
    public InstructionNotFoundException(String inst) {
        super();
        instruction = inst;
    }
}
