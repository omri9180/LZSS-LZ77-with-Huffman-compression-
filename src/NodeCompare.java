/*
 *Vaturi Omri - 305744666
 * Tzach Itshak Ofir - 208062943
 */

import java.util.Comparator;

public class NodeCompare implements Comparator<Node> {

    @Override
    public int compare(Node o1, Node o2) {
        return o1.getFreq() - o2.getFreq();
    }

}