
# LZSS with Huffman and LZ77 Compression Project

## Description

This project combines LZSS (Lempel-Ziv-Storer-Szymanski) compression with Huffman coding and LZ77 (Lempel-Ziv 77) compression techniques to achieve efficient data compression.

## File Structure

- `GUI.java`: Main graphical user interface for the application.
- `HuffEncoderDecoder.java`: Handles Huffman encoding and decoding.
- `LZ77Compress.java`: Implements the LZ77 compression algorithm.
- `LZ77Decompressor.java`: Handles the decompression using LZ77.
- `LZSSCompressor.java`: Implements the LZSS compression technique.
- `LZSSDecompressor.java`: Responsible for LZSS decompression.
- `Node.java`: Defines the node structure used in Huffman trees.
- `NodeCompare.java`: Provides comparison functionality for nodes.
- `PatternMatch.java`: Utility for pattern matching.

## Usage Instructions

1. Compile all the java files.
   ```
   javac *.java
   ```
2. Run the `GUI.java` to access the graphical interface and interact with the application.
   ```
   java GUI
   ```

## Dependencies

- Java SE Development Kit
