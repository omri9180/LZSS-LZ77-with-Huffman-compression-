import java.io.*;
import java.util.BitSet;

public class LZ77Compress {
    private final String outputPath;
    private final String inputPath;
    private final int windowSize;
    private final int lookaheadBufferSize;
    private final int searchBufferSize;
    private int nextByteIndex = 0;
    private int SearchBufferPosition = 0;
    private int LookaheadPosition = 0;
    private byte[] fileBytes;
    private int windowShiftSize = 0;
    private final StringBuilder slidingWindowBuffer;
    private final StringBuilder compressedData;


    public LZ77Compress(String file, String outPath, int window, int lookAhead) {
        this.outputPath = outPath;
        this.inputPath = file;
        this.windowSize = window;
        this.lookaheadBufferSize = lookAhead;
        this.searchBufferSize = window - lookAhead;
        this.slidingWindowBuffer = new StringBuilder();
        this.compressedData = new StringBuilder();
    }

    public void compress() throws IOException {
        loadInputFile();
        initializeSlidingWindow();
        performCompression(slidingWindowBuffer, compressedData);
        appendPaddingBits(compressedData);
        saveCompressedData(compressedData);

    }

    private void loadInputFile(){
        try (FileInputStream read = new FileInputStream(inputPath)) {
            fileBytes = read.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void saveCompressedData(StringBuilder compressedData) throws IOException {
        ObjectOutputStream dataWrite = new ObjectOutputStream(new FileOutputStream(outputPath));
        BitSet encodedBits = convertToBits(compressedData);
        dataWrite.writeObject(encodedBits);
        dataWrite.close();
    }

    private BitSet convertToBits(StringBuilder binaryData) {
        BitSet encodedBits = new BitSet(binaryData.length());
        int index = binaryData.indexOf("1", 0);
        while (index != -1) {
            encodedBits.set(index);
            index = binaryData.indexOf("1", index + 1);
        }
        return encodedBits;
    }

    private void initializeSlidingWindow() {
        for (int i = 0; i < lookaheadBufferSize; i++) {
            if (fileBytes.length > nextByteIndex) {
                slidingWindowBuffer.append((char) Byte.toUnsignedInt(fileBytes[nextByteIndex]));
                nextByteIndex++;
                LookaheadPosition++;
            }
        }
    }

    private void appendPaddingBits(StringBuilder compressed) {
        int bitsLeft = compressed.length() % 64;
        String bitsLeftBinary = integerToBinaryString(bitsLeft);
        compressed.insert(0, bitsLeftBinary);
    }

    private void performCompression(StringBuilder slidingWindow, StringBuilder compress_D) {
        while (LookaheadPosition > 0) {
            PatternMatch findPatternMatchBY = searchForPattern(slidingWindow);
            prepareCompressedSegment(compress_D, findPatternMatchBY.getOffset(), findPatternMatchBY.getLength(), slidingWindow.charAt(SearchBufferPosition + findPatternMatchBY.getLength()));
            windowShiftSize = (1 + findPatternMatchBY.getLength());
            shiftSlidingWindow(slidingWindow);
        }
    }

    private void shiftSlidingWindow(StringBuilder strBY) {
        for (int i = 0; i < windowShiftSize && i < fileBytes.length; i++) {
            if (shouldDeleteCharacter(strBY)) {
                strBY.deleteCharAt(0);
            }

            if (nextByteIndex < fileBytes.length) {
                char nextBY = (char) Byte.toUnsignedInt(fileBytes[nextByteIndex++]);
                strBY.append(nextBY);
            } else {
                LookaheadPosition--;
            }
            if (SearchBufferPosition < searchBufferSize) SearchBufferPosition++;
        }
        windowShiftSize = 0;
    }

    private boolean shouldDeleteCharacter(StringBuilder str) {
        if (str.length() == windowSize) {
            return true;
        }
        if (LookaheadPosition < lookaheadBufferSize) {
            return SearchBufferPosition == searchBufferSize;
        }
        return false;
    }

    private PatternMatch searchForPattern(StringBuilder strBY) {
        PatternMatch match = new PatternMatch();
        String matched;
        int offset;
        int matchLA_Index = SearchBufferPosition;
        if (isPatternFound(strBY)) {
            matched = String.valueOf(strBY.charAt(matchLA_Index));
            offset = getMatchingPatternIndex(strBY, matched);
            while (offset != -1 && matchLA_Index < strBY.length() - 1) {
                match.setLength(match.getLength() + 1);
                match.setOffset(offset);
                match.setValue(matched);
                matchLA_Index++;
                matched += strBY.charAt(matchLA_Index);
                offset = getMatchingPatternIndex(strBY, matched);
            }
        }
        return match;
    }

    private boolean isPatternFound(StringBuilder by) {
        return SearchBufferPosition != 0 && patternExistsInBuffer(by, by.charAt(SearchBufferPosition));
    }

    private boolean patternExistsInBuffer(StringBuilder strBy, char ch_inStrBY) {
        for (int i = 0; i < SearchBufferPosition; i++) {
            if (strBy.charAt(i) == ch_inStrBY) return true;
        }
        return false;
    }

    private int getMatchingPatternIndex(StringBuilder strBY, String tmpValue) {
        String tmpMatch;
        int offset;
        for (int i = SearchBufferPosition - 1; i >= 0; i--) {
            tmpMatch = strBY.substring(i, i + tmpValue.length());
            offset = SearchBufferPosition - i;
            if (tmpMatch.equals(tmpValue)) return offset;
        }
        return -1;
    }

    private void prepareCompressedSegment(StringBuilder compress, int offset, int len, char ch) {
        compress.append(integerToBinaryString(offset)).append(integerToBinaryString(len)).append(characterToBinaryString(ch));
    }

    private String characterToBinaryString(char ch) {
        StringBuilder binaryString = new StringBuilder(Integer.toBinaryString(ch));
        // Pad to 8 bits
        while (binaryString.length() < 8) {
            binaryString.insert(0, "0");
        }

        return binaryString.toString();
    }

    private String integerToBinaryString(int num) {
        int bitSize = (num >= 256) ? 16 : 8;
        StringBuilder binaryString = new StringBuilder(Integer.toString(num, 2));
        // Pad to full bit length
        while (binaryString.length() < bitSize) {
            binaryString.insert(0, "0");
        }
        // Add leading bit
        if (bitSize == 8) {
            binaryString.insert(0, "0");
        } else {
            binaryString.insert(0, "1");
        }
        return binaryString.toString();
    }
}
