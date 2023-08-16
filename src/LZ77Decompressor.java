/*
 *Vaturi Omri - 305744666
 * Tzach Itshak Ofir - 208062943
 */

import java.io.*;
import java.util.BitSet;

public class LZ77Decompressor {
    private final String inputPath;
    private final String outputPath;
    private int currentBitPosition = 0;

    public LZ77Decompressor(String inPath, String outPath) {
        this.inputPath = inPath;
        this.outputPath = outPath;
    }

    public void performDecompression() throws IOException, ClassNotFoundException {
        ObjectInputStream readCompressedFile = new ObjectInputStream(new FileInputStream(inputPath));
        BitSet data = loadCompressedData(readCompressedFile);
        StringBuilder decompress = new StringBuilder();
        int bitSetLength = getDecodedDataLength(data);
        decodeCompressedData(decompress, bitSetLength, data);
        saveDecompressedData(decompress);
    }

    private void saveDecompressedData(StringBuilder decodeData) throws IOException {
        FileOutputStream writeDecompress = new FileOutputStream(outputPath);

        for (int i = 0; i < decodeData.length(); i++) {
            byte by = (byte) decodeData.charAt(i);
            writeDecompress.write(by);
        }
        writeDecompress.close();
    }

    private void decodeCompressedData(StringBuilder decompress, int bitSetLength, BitSet data) {
        while (currentBitPosition < bitSetLength) {
            PatternMatch match = extractPatternFromBits(data);
            addDecompressedData(decompress, match);
        }
        currentBitPosition = 0;
    }

    private void addDecompressedData(StringBuilder decompress, PatternMatch match) {
        if (match.getLength() == 0 && match.getOffset() == 0) {
            decompress.append(match.getValue());
        } else {
            int relativeOffset = decompress.length() - match.getOffset();
            for (int i = 0; i < match.getLength(); i++) {
                char decodeChar = decompress.charAt(relativeOffset++);
                decompress.append(decodeChar);
            }
            decompress.append(match.getValue());
        }
    }

    private PatternMatch extractPatternFromBits(BitSet data) {
        int offset = extractLengthAndUpdatePosition(data);
        int length = extractLengthAndUpdatePosition(data);
        char character = (char) extractPatternLength(8, data);
        return new PatternMatch(length, offset, String.valueOf(character));
    }

    private int extractLengthAndUpdatePosition(BitSet source) {
        return extractPatternLength(source.get(currentBitPosition++) ? 16 : 8, source);
    }

    private int extractPatternLength(int length, BitSet data) {
        StringBuilder binary = new StringBuilder();
        for (int i = 0; i < length; i++) {
            binary.append(data.get(currentBitPosition++) ? '1' : '0');
        }
        return Integer.parseInt(binary.toString(), 2);
    }

    private int getDecodedDataLength(BitSet data) {
        StringBuilder binary = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            binary.append(data.get(i) ? '1' : '0');
            currentBitPosition++;
        }
        int length = Integer.parseInt(binary.toString(), 2);

        // Subtract the padding from the total length
        int padding = 64 - length;

        return data.size() - padding;
    }

    private BitSet loadCompressedData(ObjectInputStream read) throws IOException, ClassNotFoundException {
        return (BitSet) read.readObject();
    }

}
