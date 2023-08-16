import java.io.*;
import java.util.BitSet;

public class LZSSDecompressor {
    protected String inputFilePath;
    protected String outputFilePath;
    protected int currentBitPosition = 0;
    private int sourceSize = 0;
    private int windowSize = 0;
    private int lookaheadBuffr = 0;
    private boolean huffman;

    public LZSSDecompressor(String inPath, String outPath) {
        this.inputFilePath = inPath;
        this.outputFilePath = outPath;
//        this.huffman = huffman;
    }



    public void decompress() throws IOException, ClassNotFoundException {

        BitSet source = loadBitSetFromFile();
        this.currentBitPosition = 0;
        initializeDecompressionParameters(source, currentBitPosition);
        StringBuilder decodedData = new StringBuilder();
        StringBuilder searchBuffer = new StringBuilder();
        Decode(source, decodedData, searchBuffer);
        WriteToFile(decodedData);
    }

//    private String decompressWithHuffman(String filePath) {
//        String file = null;
//        if (filePath.contains("_Compressed")) {
//            file = filePath.replace("_Compressed", "_Decompressed");
//        }
//        return file;
//    }

    private void WriteToFile(StringBuilder decodedData) throws IOException {
        FileOutputStream outputFileStream = new FileOutputStream(this.outputFilePath);
        for (int i = 0; i < decodedData.length(); i++) {
            byte currentByte = (byte) decodedData.charAt(i);
            outputFileStream.write(currentByte);
        }
        outputFileStream.close();
    }

    private void Decode(BitSet source, StringBuilder decodedData, StringBuilder searchBuffer) {
        PatternMatch match = new PatternMatch();

        while (this.currentBitPosition < this.sourceSize) {
            match.reset();
            if (source.get(this.currentBitPosition)) {
                this.currentBitPosition++;

                if (source.get(this.currentBitPosition)) {
                    this.currentBitPosition++;
                    match.setOffset(readTwoBytesFromBitSet(source));
                } else {
                    this.currentBitPosition++;
                    match.setOffset(readOneByteFromBitSet(source));
                }


                if (source.get(this.currentBitPosition)) {
                    this.currentBitPosition++;
                    match.setLength(readTwoBytesFromBitSet(source));
                } else {
                    this.currentBitPosition++;
                    match.setLength(readOneByteFromBitSet(source));
                }

                match.setValue(searchBuffer.substring(match.getOffset(), match.getOffset() + match.getLength()));
                decodedData.append(match.getValue());
            } else {
                this.currentBitPosition++;
                match.setLength(1);
                match.addByte((byte) readOneByteFromBitSet(source));
                decodedData.append(match.getValue());
            }
            updateSearchBuffer(match, searchBuffer, this.windowSize);
        }
    }

    private void updateSearchBuffer(PatternMatch match, StringBuilder searchBuffer, int windowSize) {
        for (int i = 0; i < match.getLength(); i++) {
            if (searchBuffer.length() >= windowSize)
                searchBuffer.deleteCharAt(0);
            searchBuffer.append(match.getValue().charAt(i));
        }
    }

    private BitSet loadBitSetFromFile() throws IOException, ClassNotFoundException {
        FileInputStream input = new FileInputStream(this.inputFilePath);
        ObjectInputStream objectInput = new ObjectInputStream(input);
        Object restoredObject = objectInput.readObject();
        objectInput.close();

        if (restoredObject instanceof BitSet) {
            return (BitSet) restoredObject;
        } else if (restoredObject instanceof byte[]) {
            return fromByteArray((byte[]) restoredObject);
        } else {
            throw new IOException("Unexpected data type in the compressed file.");
        }
    }

    private BitSet fromByteArray(byte[] bytes) {
        BitSet bits = new BitSet();
        for (int i = 0; i < bytes.length * 8; i++) {
            if ((bytes[bytes.length - i / 8 - 1] & (1 << (i % 8))) > 0) {
                bits.set(i);
            }
        }
        return bits;
    }

    private void initializeDecompressionParameters(BitSet source, int sourcePosition) {
        this.sourceSize = source.size() - readOneByteFromBitSet(source);
        this.windowSize = (int) (Math.pow(2, readOneByteFromBitSet(source)));
        this.lookaheadBuffr = (int) (Math.pow(2, readOneByteFromBitSet(source)));
    }

    private int readTwoBytesFromBitSet(BitSet source) {
        int nextTwoBytes = 0;

        for (int i = 15; i >= 0; i--) {
            if (source.get(this.currentBitPosition)) {
                nextTwoBytes += (int) Math.pow(2, i);
            }
            this.currentBitPosition++;
        }

        return nextTwoBytes;
    }

    private int readOneByteFromBitSet(BitSet source) {
        int nextByte = 0;
        for (int i = 7; i >= 0; i--) {
            if (source.get(this.currentBitPosition)) {
                nextByte += (int) Math.pow(2, i);
            }
            this.currentBitPosition++;
        }

        return nextByte;
    }
}
