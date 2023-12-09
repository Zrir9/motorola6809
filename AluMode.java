package m6800.applet.enums;

public enum AluMode {

    /**
     * 0 - clear mode.
     */
    CLEAR(0),

    /**
     * 1 - decrement mode.
     */
    DECREMENT(1),

    /**
     * 2 - increment mode.
     */
    INCREMENT(2),

    /**
     * 3 - shift left.
     */
    SHIFT_LEFT(3),

    /**
     * 4 - shift right.
     */
    SHIFT_RIGHT(4),

    /**
     * 5 - rotate right.
     */
    ROTATE_RIGHT(5),

    /**
     * 6 - rotate left.
     */
    ROTATE_LEFT(6),

    /**
     * 7 - Negate.
     */
    NEGATE(7), COMPLIMENT(8),
    /**
     * 
     */
    PASS_MODE(9),

    /**
     * 10 - Add mode.
     */
    ADD_MODE(10),

    /**
     * 11 - add with carry.
     */
    ADD_WITH_CARRY(11),

    /**
     * 12 - Subtract.
     */
    SUBTRACT(12),

    /**
     * 13 - Subtract with carry.
     */
    SUBTRACT_WITH_CARRY(13),

    /**
     * 14 - Inclusive or.
     */
    INCLUSIVE_OR(14),

    /**
     * 15 - exclusive or.
     */
    EXCLUSIVE_OR(15),

    /**
     * 16 - Compare.
     */
    COMPARE(16),

    /**
     * 17 - And.
     */
    AND(17),

    /**
     * Store.
     */
    STORE(18),

    ;

    private int value;

    private AluMode(int value) {
        this.value = value;

    }

    /**
     * 
     * @return
     */
    public int getValue() {
        return value;
    }

    public static AluMode fromInteger(int value) {
        AluMode result = null;
        for (AluMode aluMode : AluMode.values()) {
            if (aluMode.getValue() == value) {
                result = aluMode;
                break;
            }
        }
        return result;
    }

}
