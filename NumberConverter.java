package m6800.util;

/*
 * @(#)numberConverter.java
 *
 *
 */

/**
 * A class designed to be instanciated inside each instance of any class that
 * requires String number conversions
 * 
 * @author Simon McCaughey
 * @version 1.0, 15 April 1998
 */
public class NumberConverter {

    /**
     * Constructor : no arguments, nothing to initialise
     */
    public NumberConverter() {
        return;
    }

    /**
     * input = string in binary format
     * <p>
     * output = string in binary format
     * <p>
     * 
     * @return the 2's compliment of input
     *         <p>
     */
    public String compliment(String input) {

        /*
         * variable to record when the first '1' has been reached returns the
         * 2's compliment of input
         */
        boolean firstComplete = false;

        /*
         * string to be returned from method
         */
        String output = ""; // initialise empty

        /*
         * iterate through string, and output 0's until you get to the first 1.
         * Then add 1 to end
         */
        for (int i = input.length() - 1; i >= 0; i--) { // count downwards
            if (!firstComplete) {
                if (input.charAt(i) == '0') {
                    output = "0" + output;
                } else {
                    output = "1" + output;
                    firstComplete = true;
                }
            } else { // simply invert the number
                if (input.charAt(i) == '0') {
                    output = "1" + output;
                } else {
                    output = "0" + output;
                }
            }
        } // end for (i)
        return output;
    }

    /**
     * input = integer in int format output = string in binary format returns
     * the binary representation of the input
     */
    public String intToBin(int input) {
        return java.lang.Integer.toBinaryString(input);
    }

    /**
     * input = integer in String format output = string in hexidecimal format
     * returns a hex string of the integer value
     */
    public String intToHex(String input) {
        return intToHex((java.lang.Integer.decode(input)).intValue());
    }

    /**
     * input = integer in int format output = string in hexidecimal format
     * returns a hex string of the integer value filled out to either 2-digit or
     * 4-digit (eg 0F)
     */
    public String intToHex(int input) {

        /*
         * string to be returned from method
         */
        String output;

        output = java.lang.Integer.toHexString(input);

        /* fill left hand side with zeros to make 2 or 4 - digit number */
        switch (output.length()) {
            case 0: // make it a 2-digits long empty string
            {
                output = "00";
                break;
            }
            case 1: /* add 1 digit to make output.length()==2 */
            {
                output = "0" + output;
                break;
            }
            case 3: /* add 1 digit to make output.length()==4 */
            {
                output = "0" + output;
                break;
            }
        }
        /* ensure output is upper case, and return it */
        return output.toUpperCase();
    }

    /**
     * input = string in hexidecimal format, output = integer in int format,
     * return the int value of the given input hex string.
     */
    public int hexToInt(String input) {

        boolean done = false;

        /*
         * variable for the return value
         */
        int output = 0;

        /*
         * variable to contain the current charactors integer value
         */
        int charValue = 0;

        /*
         * variable to hold the multilpier value to weight the chars in
         * different columns of the input number
         */
        int multiplier = 1;
        for (int x = input.length() - 1; x != -1; x--) { // count from least sig
            // -> most

            /*
             * first test if the number is an integer if it isnt a
             * NumberFormatException will be thrown
             */
            try {
                charValue = (java.lang.Integer.decode("" + input.charAt(x))
                        .intValue());
            }

            /*
             * if the digit is not an int catch the NumberFormatException and ->
             * find out which letter
             */
            catch (NumberFormatException nfe) {
                if ((("" + input.charAt(x)).toUpperCase()).equals("A")) {
                    charValue = 10;
                    done = true;
                }
                if ((("" + input.charAt(x)).toUpperCase()).equals("B")) {
                    charValue = 11;
                    done = true;
                }
                if ((("" + input.charAt(x)).toUpperCase()).equals("C")) {
                    charValue = 12;
                    done = true;
                }
                if ((("" + input.charAt(x)).toUpperCase()).equals("D")) {
                    charValue = 13;
                    done = true;
                }
                if ((("" + input.charAt(x)).toUpperCase()).equals("E")) {
                    charValue = 14;
                    done = true;
                }
                if ((("" + input.charAt(x)).toUpperCase()).equals("F")) {
                    charValue = 15;
                    done = true;
                }
                if (!done) {

                    throw new NumberFormatException("" + input.charAt(x));

                }
            }

            /* add current digit to output */
            output = output + multiplier * charValue;

            /*
             * increace the multiplier for the next column by the number base
             */
            multiplier = multiplier * 16;

        }

        return output;
    }

    /**
     * input = signed int output = 1 sign bit + 7-bit in hex form return the
     * signed hex representation of the given input int
     */
    public String intToSignedHex(int input) {

        /* use the Java function that returns signed binary string */
        String output = java.lang.Integer.toBinaryString(input);

        /* if longer than 8-bit (negative) */
        if (output.length() > 8) {

            /* crop string to 8 bits */
            output = output.substring(output.length() - 8);

            /*
             * else shorter than 8-bit (positive) then fill the left hand side
             * with zeros
             */
        } else {
            while (output.length() < 8) {
                output = "0" + output;
            }
        }

        return intToHex(binToInt(output)).toUpperCase();
    }

    /**
     * input = binary number in string format output = unsigned integer return
     * the unsigned int value of the input binary number
     */
    public int binToInt(String input) {

        /*
         * variable for the return value
         */
        int output = 0;

        /*
         * variable to contain the current charactors integer value
         */
        int charValue = 0;

        /*
         * variable to hold the multilpier value to weight the chars in
         * different columns of the input number
         */
        int multiplier = 1;

        /* count from least sig -> most */
        for (int x = input.length() - 1; x != -1; x--) {

            /* first test if the number is a '1' or '0' */
            charValue = (java.lang.Integer.decode("" + input.charAt(x))
                    .intValue());
            /* add current digit to output */
            output = output + multiplier * charValue;

            /* increace the multiplier for the next digit */
            multiplier = multiplier * 2;
        }
        return output;
    }

    /**
     * input = hex string output = decoded int from 7-bits + sign bit return the
     * signed int value of the input hex number
     */
    public int signedHexToInt(String inputHex) {

        /*
         * variable for the temporary binary stage
         */
        String binString;

        /*
         * record the sign of the input
         */
        boolean negative;

        /*
         * variable for the return value
         */
        int output = 0;

        /*
         * variable to hold the multilpier value to weight the chars in
         * different columns of the input number
         */
        int multiplier = 1;

        /*
         * variable to contain the current charactors integer value
         */
        int charValue;

        /* first convert hex into integer - ie unsigned */
        int n = hexToInt(inputHex);

        /* next convert int into binary string */
        binString = java.lang.Integer.toBinaryString(n);

        /* ensure that the input is 8-bits long */
        while (binString.length() < 8) {
            binString = "0" + binString;
        }

        /* strip off first bit to test for sign */
        negative = (binString.charAt(0) == '1');
        binString = binString.substring(1);

        /* convert the rest of the number into and int */
        if (negative) {

            /* get the compliment of the input */
            binString = compliment(binString);

            /* get the unsigned int value */
            output = binToInt(binString);

            /* multiply by -1 to make output negative */
            output = output * (-1);

        } else { /* for positive number */

            /* count from least sig -> most */
            for (int x = binString.length() - 1; x != -1; x--) {
                charValue = (java.lang.Integer.decode("" + binString.charAt(x))
                        .intValue());
                /* add current digit to output */
                output = output + multiplier * charValue;

                /* increace the multiplier for the next digit */
                multiplier = multiplier * 2;
            }
        }

        return output;
    }
}
