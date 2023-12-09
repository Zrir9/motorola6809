package m6800.exceptions;

/*
 * @(#)CompileAndRunException.java
 *
 *
 */

import java.util.Vector;

/**
 * A class used by XX class to return information in the assembly task
 * 
 * @author Simon McCaughey
 * @version 1.0, 28 April 1998
 */

public class CompileAndRunException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /*
     * vector containing an element for each error
     */
    private Vector<BadProgramLineException> errors;

    /**
     * Constructor arguments : called with the filled vector of all errors ready
     * to be sent back to the caller for user feedback
     */
    public CompileAndRunException(
            Vector<BadProgramLineException> allLineExceptions) {
        super();
        setErrors(allLineExceptions);
    }

    public void setErrors(Vector<BadProgramLineException> errors) {
        this.errors = errors;
    }

    public Vector<BadProgramLineException> getErrors() {
        return errors;
    }
}
