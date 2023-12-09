package m6800.applet;

/**
 * Class to define paths for the animations.
 * 
 * @version $Id$
 */
public final class AnimationPaths {

    /**
     * Hide the default constructor.
     */
    private AnimationPaths() {
    }

    /**
     * 'NORMAL' path - nothing highlighted.
     */
    public static final int[][] NORMAL = {
            {7, 7, 7, 7, 7, 7, 7, 6, 6, 6, 6, 6, 6, 7, 7, 6, 0, 5, 4, 5, 5, 4,
                    1, 2, 3,},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0,}};
    /**
     * Path from the ALU to the ACC.
     */
    public static final int[][] ALU_TO_ACC = {
            {7, 7, 7, 7, 7, 7, 7, 6, 6, 6, 6, 6, 6, 7, 7, 6, 0, 6, 6, 1, 5, 4,
                    1, 2, 3,},
            {1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0,
                    0, 1, 1,}};
    /**
     * Path from the ALU to the DATA.
     */
    public static final int[][] ALU_TO_DATA = {
            {7, 7, 7, 7, 7, 7, 7, 6, 6, 6, 6, 6, 6, 7, 7, 6, 0, 5, 2, 1, 5, 4,
                    1, 2, 3,},
            {0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 0, 0,
                    0, 1, 1,}};
    /**
     * Path from the ACC to the ALU.
     */
    public static final int[][] ACC_TO_ALU = {
            {7, 7, 7, 7, 7, 7, 7, 6, 6, 6, 6, 6, 6, 7, 7, 6, 0, 5, 4, 5, 5, 4,
                    1, 2, 3,},
            {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0,}};
    /**
     * Path from the Program Counter to the Address register.
     */
    public static final int[][] PC_TO_ADD = {
            {7, 7, 7, 7, 7, 7, 7, 6, 6, 6, 6, 6, 6, 7, 7, 6, 0, 5, 4, 5, 5, 2,
                    1, 2, 3,},
            {0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                    1, 0, 0,}};
    /**
     * Path from the Data register to the Instruction Register.
     */
    public static final int[][] DATA_TO_IR = {
            {7, 7, 7, 7, 7, 7, 7, 6, 6, 6, 6, 6, 6, 7, 7, 6, 0, 5, 2, 6, 1, 4,
                    1, 2, 3,},
            {0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0,
                    0, 0, 0,}};
    /**
     * Path from the Data register to the Program Counter.
     */
    public static final int[][] DATA_TO_PC = {
            {7, 7, 7, 7, 7, 7, 7, 6, 6, 6, 6, 6, 6, 7, 7, 6, 0, 5, 2, 6, 6, 6,
                    1, 2, 3,},
            {0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1,
                    1, 0, 0,}};
    /**
     * Path from the Data Register to the ALU.
     */
    public static final int[][] DATA_TO_ALU = {
            {7, 7, 7, 7, 7, 7, 7, 6, 6, 6, 6, 6, 6, 7, 7, 6, 0, 0, 3, 5, 5, 4,
                    1, 2, 3,},
            {0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0,
                    0, 0, 0,}};
    /**
     * Path from the ALU to the ACC
     */
    public static final int[][] DATA_TO_ADD = {
            {7, 7, 7, 7, 7, 7, 7, 6, 6, 6, 6, 6, 6, 7, 7, 6, 0, 5, 2, 6, 6, 3,
                    1, 2, 3,},
            {0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1,
                    0, 0, 0,}};

}
