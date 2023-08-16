/*
 *Vaturi Omri - 305744666
 * Tzach Itshak Ofir - 208062943
 */
public class Node {
    private int freq;
    private Byte ch;
    private Node left;
    private Node right;

    public Node(byte ch, int freq) {
        this.freq = freq;
        this.ch = ch;
        left = null;
        right = null;
    }

    public Node(int freq, Node left, Node right) {
        this.freq = freq;
        this.ch = null;
        this.left = left;
        this.right = right;
    }

    public Byte getCh() {
        return ch;
    }

    public int getFreq() {
        return freq;
    }

    public Node getLeft() {
        return left;
    }


    public Node getRight() {
        return right;
    }


}
