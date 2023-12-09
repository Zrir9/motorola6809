package m6800.applet.enums;

public enum AddressMode {

    /**
     * 1 - immediate
     */
    IMMEDIATE(1),
    /**
     * 2 - direct
     */
    DIRECT(2),
    /**
     * 5 - inherent
     */
    INHERENT(5),
    /**
     * 6 - relative
     */
    RELATIVE(6);

    private int value;

    private AddressMode(int value) {
        this.value = value;
    }

    public int getValue() {

        return value;
    }

    public static AddressMode fromInt(int value) {
        AddressMode result = null;
        for (AddressMode addressMode : AddressMode.values()) {
            if (addressMode.getValue() == value) {
                result = addressMode;
                break;
            }
        }
        return result;
    }

}
