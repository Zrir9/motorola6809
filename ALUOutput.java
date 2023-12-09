package m6800.data;

////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////
// class to contain the results of an ALU operation, ie output string,
// and flag changes
public class ALUOutput {
    private String output;
    private boolean hChanged;
    private boolean hValue;
    private boolean iChanged;
    private boolean iValue;
    private boolean nChanged;
    private boolean nValue;
    private boolean zChanged;
    private boolean zValue;
    private boolean vChanged;
    private boolean vValue;
    private boolean cChanged;
    private boolean cValue;

    public ALUOutput() {
        setOutput("");
        setHChanged(false);
        setHValue(false);
        setIChanged(false);
        setIValue(false);
        setNChanged(false);
        setNValue(false);
        setZChanged(false);
        setZValue(false);
        setVChanged(false);
        setVValue(false);
        setCChanged(false);
        setCValue(false);
    }

    public void setNChanged(boolean nChanged) {
        this.nChanged = nChanged;
    }

    public boolean isNChanged() {
        return nChanged;
    }

    public void setNValue(boolean nValue) {
        this.nValue = nValue;
    }

    public boolean isNValue() {
        return nValue;
    }

    public void setZChanged(boolean zChanged) {
        this.zChanged = zChanged;
    }

    public boolean isZChanged() {
        return zChanged;
    }

    public void setZValue(boolean zValue) {
        this.zValue = zValue;
    }

    public boolean isZValue() {
        return zValue;
    }

    public void setVChanged(boolean vChanged) {
        this.vChanged = vChanged;
    }

    public boolean isVChanged() {
        return vChanged;
    }

    public void setVValue(boolean vValue) {
        this.vValue = vValue;
    }

    public boolean isVValue() {
        return vValue;
    }

    public void setCChanged(boolean cChanged) {
        this.cChanged = cChanged;
    }

    public boolean isCChanged() {
        return cChanged;
    }

    public void setCValue(boolean cValue) {
        this.cValue = cValue;
    }

    public boolean isCValue() {
        return cValue;
    }

    public void setHChanged(boolean hChanged) {
        this.hChanged = hChanged;
    }

    public boolean isHChanged() {
        return hChanged;
    }

    public void setHValue(boolean hValue) {
        this.hValue = hValue;
    }

    public boolean isHValue() {
        return hValue;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getOutput() {
        return output;
    }

    public void setIChanged(boolean iChanged) {
        this.iChanged = iChanged;
    }

    public boolean isIChanged() {
        return iChanged;
    }

    public void setIValue(boolean iValue) {
        this.iValue = iValue;
    }

    public boolean isIValue() {
        return iValue;
    }
}
