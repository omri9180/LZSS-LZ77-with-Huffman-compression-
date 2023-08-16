/*
 *Vaturi Omri - 305744666
 * Tzach Itshak Ofir - 208062943
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.PriorityQueue;

public class HuffEncoderDecoder {
    PriorityQueue<Node> minEncoder;
    PriorityQueue<Node> minDecoder;
    HashMap<Byte, String> encoderMap;
    int[] frequency;
    FileOutputStream output;
    Node rootEn;
    Node rootDe;

    public HuffEncoderDecoder() {
        minEncoder = new PriorityQueue<>(new NodeCompare());
        minDecoder = new PriorityQueue<>(new NodeCompare());
        encoderMap = new HashMap<>();
        frequency = new int[256];
    }

    public void compress(String input_names, String output_names) throws IOException {
        File file_in = new File(input_names);
        ObjectOutputStream writeCompressedData = new ObjectOutputStream(new FileOutputStream(output_names));
        byte[] data = readFromFile(file_in);
        buildHeap(data, minEncoder);
        writeSymbol(writeCompressedData, frequency, minEncoder);
        rootEn = buildHuffmanTree(minEncoder);
        createHuffmanCode(rootEn, "", encoderMap);
        encode(writeCompressedData, data, encoderMap);
        System.out.println("The file has encoded");
        writeCompressedData.close();


    }


    public void decompress(String input_names, String output_names) throws IOException {
        ObjectInputStream readFile = new ObjectInputStream(new FileInputStream(input_names));
        output = new FileOutputStream(output_names);
        minDecoder = readSymbol(readFile, minDecoder);
        rootDe = buildHuffmanTree(minDecoder);
        decode(readFile, rootDe, output);
        System.out.println("The file has decoded");
        readFile.close();
        output.close();
    }

    public byte[] readFromFile(File file) throws IOException {
        FileInputStream input = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        input.read(data);
        input.close();
        return data;
    }

    public void writeSymbol(ObjectOutputStream out, int[] f, PriorityQueue<Node> heap) throws IOException {
        out.writeInt(heap.size());
        for (int i = 0; i < f.length; i++) {
            if (f[i] != 0) {
                out.write(i - 128);
                out.writeInt(f[i]);
            }
        }
    }

    public void buildHeap(byte[] data, PriorityQueue<Node> heap) {
        for (byte datum : data) frequency[datum + 128]++;
        for (int i = 0; i < frequency.length; i++) {
            if (frequency[i] != 0) {
                heap.add(new Node((byte) (i - 128), frequency[i])); // build min heap
            }
        }
    }

    public Node buildHuffmanTree(PriorityQueue<Node> heap) {
        while (heap.size() != 1) {
            Node left = heap.poll();
            Node right = heap.poll();
            Node f = new Node(left.getFreq() + right.getFreq(), left, right);
            heap.add(f);
        }
        return heap.poll();
    }

    public void createHuffmanCode(Node root, String s, HashMap<Byte, String> map) {
        if (root.getLeft() == null && root.getRight() == null) {
            map.put(root.getCh(), s);
            return;
        }
        createHuffmanCode(root.getLeft(), s + "1", map);
        createHuffmanCode(root.getRight(), s + "0", map);
    }

    public void encode(ObjectOutputStream out, byte[] d, HashMap<Byte, String> map) throws IOException {
        String encodedData = "";
        for (byte b : d) {
            encodedData += map.get(b);
            if (encodedData.length() > 1000) {
                encodedData = encodeWord(encodedData, out, 8);
            }
        }
        encodedData = encodeWord(encodedData, out, 8);
        if (!encodedData.isEmpty()) {
            byte lastByte = (byte) encodedData.length(); // the real size of the last part
            byte newCode = StringToByte(encodedData); // the last part
            out.write(newCode);
            out.write(lastByte); // write the size of the last part
        }
    }

    public String encodeWord(String data, ObjectOutputStream out, int end) throws IOException {
        while (data.length() > end) {
            String word = data.substring(0, 8); // get one byte of encoded data
            data = data.substring(8);
            byte newCode = StringToByte(word); // convert the value of word to byte
            out.write(newCode);
        }
        return data;
    }

    public byte StringToByte(String s) {
        int num = Integer.parseInt(s, 2);
        return (byte) num;
    }

    public PriorityQueue<Node> readSymbol(ObjectInputStream in, PriorityQueue<Node> heap) throws IOException {
        int symbols = in.readInt();
        for (int i = 0; i < symbols; i++) {
            int ch = in.read();
            int freq = in.readInt();
            heap.add(new Node((byte) (ch), freq));
        }
        return heap;
    }

    public String IntToString(int val) {
        StringBuilder data = new StringBuilder(Integer.toBinaryString(val));
        if (val < 0)
            data = new StringBuilder(data.substring(data.length() - 8));
        else
            while (data.length() < 8)
                data.insert(0, "0");
        return data.toString();
    }

    public String findLetter(String s, Node root, FileOutputStream out) throws IOException {
        Node current = root;
        int i = 0;
        while (current.getLeft() != null && current.getRight() != null) {
            if (s.charAt(i) == '0')
                current = current.getRight();
            else
                current = current.getLeft();
            i++;
        }
        out.write(current.getCh());
        return s.substring(i);
    }

    public void decode(ObjectInputStream in, Node root, FileOutputStream out) throws IOException {
        String decodedData = "";
        while (true) {
            int x = in.read();
            if (x != -1) {
                decodedData += IntToString(x);
                if (decodedData.length() > 1000) {
                    while (decodedData.length() > 100)
                        decodedData = findLetter(decodedData, root, out);
                }
            } else {
                break;
            }
        }
        String lenOfLastByte = decodedData.substring(decodedData.length() - 8);
        int lastByte = StringToByte(lenOfLastByte);
        String temp1 = decodedData.substring(0, decodedData.length() - 16);
        String temp2 = decodedData.substring(decodedData.length() - 8 - lastByte, decodedData.length() - 8);
        decodedData = temp1 + temp2;
        while (!decodedData.isEmpty())
            decodedData = findLetter(decodedData, root, out);
    }


//    public int compare(Node o1, Node o2) {
//        return o1.getFreq() - o2.getFreq();
//    }
}
