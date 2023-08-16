/*
 *Vaturi Omri - 305744666
 * Tzach Itshak Ofir - 208062943
 */
public class PatternMatch {
    private int length;
    private int offset;
    private String value;

    // Constructor that initializes the fields with provided values
    public PatternMatch(int length, int offset, String value) {
        this.length = length;
        this.offset = offset;
        this.value = value;
    }

    // Default constructor that initializes the fields with default values
    public PatternMatch() {
        this(0, 0, "");
    }

    // Getter and setter methods
    public int getLength() {
        return this.length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getOffset() {
        return this.offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void incLength() {
        length++;
    }

    // Appends a byte to the value string
    public void addByte(byte value) {
        this.value += (char) (Byte.toUnsignedInt(value));
    } // char performs signed conversion

    // Resets the fields to their default values
    public void reset() {
        this.offset = 0;
        this.length = 0;
        this.value = "";
    }
}