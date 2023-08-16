/*
 *Vaturi Omri - 305744666
 * Tzach Itshak Ofir - 208062943
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class GUI extends JFrame {
    private int lookaheadBufferSize = 0;
    private int minMatch = 2;
    private int windowSliderSize = 0;
    private int slidingWindowSize = 0;
    private boolean LZSS;
    private boolean huffman;
    private boolean LZ77;
    private final JLabel searchBufferLable;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUI gui = new GUI();
            gui.setVisible(true);
        });
    }

    public GUI() {

        setTitle("LZSS With Huffman and LZ77");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 320);

        JPanel mainPanel = new JPanel();
        Color bgColor = Color.decode("#0033CC");
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(bgColor);

        JLabel logo = new JLabel();
        ImageIcon logoIcon = new ImageIcon("logo.png");
        Image img = logoIcon.getImage().getScaledInstance(180, 200, Image.SCALE_SMOOTH);
        logoIcon = new ImageIcon(img);
        logo.setBorder(new EmptyBorder(0, 30, 5, 30));
        logo.setIcon(logoIcon);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);


        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filePanel.setBorder(new EmptyBorder(0, 30, 0, 30));
        JLabel inFileLabel = new JLabel("File path:");
        JPanel fileInputPanel = new JPanel();
        fileInputPanel.setLayout(new BoxLayout(fileInputPanel, BoxLayout.X_AXIS));
        JTextField inFilePathText = new JTextField(15);
        JButton addFileButton = new JButton("Choose File");
        addFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                inFilePathText.setText(selectedFile.getAbsolutePath());
            }
        });
        fileInputPanel.add(inFilePathText);
        fileInputPanel.add(addFileButton);
        filePanel.add(inFileLabel);
        filePanel.add(fileInputPanel);


        // Center panel with vertical BoxLayout
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new EmptyBorder(0, 30, 30, 30));
        // Look-Ahead Buffer JComboBox
        JPanel windowSliding = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> windowSize_Selector = new JComboBox<>(new String[]{"Choose Window Sliding size", "32 Bytes", "64 Bytes", "128 Bytes", "256 Bytes", "512 Bytes", "1024 Bytes", "2048 Bytes", "4096 Bytes", "8192 Bytes", "16384 Bytes", "32768 Bytes"});
        windowSize_Selector.setPreferredSize(new Dimension(280, 25));
        windowSliding.add(windowSize_Selector);

        JPanel lookaheadBuff = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> lookaheadBuffer_Selector = new JComboBox<>(new String[]{"\"T\"- Choose Look-Ahead Buffer Size.", "32 Bytes", "64 Bytes", "128 Bytes", "256 Bytes", "512 Bytes", "1024 Bytes", "2048 Bytes", "4096 Bytes", "8192 Bytes", "16384 Bytes", "32768 Bytes"});
        lookaheadBuffer_Selector.setPreferredSize(new Dimension(280, 25));
        lookaheadBuff.add(lookaheadBuffer_Selector);

        JPanel minMatchPanel = new JPanel();
        minMatchPanel.setLayout(new BoxLayout(minMatchPanel, BoxLayout.Y_AXIS));

        JPanel minMatchSubPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel MinmumLength = new JLabel("Minimum Find");
        JComboBox<String> minMatchBox = new JComboBox<>();
        for (int i = 2; i < 64; i++) {
            minMatchBox.addItem(i + " Bytes");
        }
        minMatchBox.setPreferredSize(new Dimension(100, 25));
        minMatchBox.setEnabled(false);
        minMatchSubPanel.add(MinmumLength);
        minMatchSubPanel.add(minMatchBox);


        searchBufferLable = new JLabel("Window sliding Size: 0 Bytes");
        JPanel slidingWindowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        slidingWindowPanel.add(searchBufferLable);

        minMatchPanel.add(minMatchSubPanel);
        minMatchPanel.add(slidingWindowPanel);
        // Radio Buttons
        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.X_AXIS));
        JRadioButton LZHuff = new JRadioButton("LZSS and Huffman");
        JRadioButton LZSSButton = new JRadioButton("LZSS");
        JRadioButton LZ77Button = new JRadioButton("LZ77");
        ButtonGroup Options = new ButtonGroup();
        Options.add(LZHuff);
        Options.add(LZSSButton);
        Options.add(LZ77Button);
        radioPanel.add(LZHuff);
        radioPanel.add(LZSSButton);
        radioPanel.add(LZ77Button);


        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        JButton compressButton = new JButton("Compress");
        JButton decompressButton = new JButton("Decompress");
        buttons.add(compressButton);
        buttons.add(Box.createHorizontalStrut(10));
        buttons.add(decompressButton);

        JPanel namesPanel = new JPanel();
        namesPanel.setLayout(new BoxLayout(namesPanel, BoxLayout.X_AXIS));
        namesPanel.setBorder(new EmptyBorder(15, 30, 3, 30));
        JLabel names = new JLabel();
        names.setText("Vaturi Omri | Tzach Itshak Ofir");
        namesPanel.add(names);


        centerPanel.add(windowSliding);
        centerPanel.add(lookaheadBuff);
        centerPanel.add(minMatchPanel);
        centerPanel.add(radioPanel);
        centerPanel.add(buttons);
        centerPanel.add(namesPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 0)));
        mainPanel.add(logo);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 0)));
        mainPanel.add(filePanel);
        mainPanel.add(centerPanel);

        add(mainPanel);

        windowSize_Selector.addActionListener(e -> {
            String selectedWindowSize = (String) windowSize_Selector.getSelectedItem();
            windowSliderSize = Integer.parseInt(selectedWindowSize.split(" ")[0]);
            updateSlidingWindowSize();
        });

        lookaheadBuffer_Selector.addActionListener(e -> {
            String selectedLookahead = (String) lookaheadBuffer_Selector.getSelectedItem();
            lookaheadBufferSize = Integer.parseInt(selectedLookahead.split(" ")[0]);
            updateSlidingWindowSize();
        });


        // Add action listener to the minMatchBox JComboBox
        minMatchBox.addActionListener(e -> {
            String selectedMinMatch = (String) minMatchBox.getSelectedItem();
            minMatch = Integer.parseInt(selectedMinMatch.split(" ")[0]);
        });

        // Add action listeners to the JRadioButtons
        LZHuff.addActionListener(e -> {
            if (LZHuff.isSelected()) {
                minMatchBox.setEnabled(true);
                lookaheadBuffer_Selector.setEnabled(true);
                huffman = true;
                LZSS = true;
                LZ77 = false;

            }

        });

        LZSSButton.addActionListener(e -> {
            if (LZSSButton.isSelected()) {
                minMatchBox.setEnabled(true);
                lookaheadBuffer_Selector.setEnabled(true);
                LZSS = true;
                huffman = false;
                LZ77 = false;
            }

        });

        LZ77Button.addActionListener(e -> {
            if (LZ77Button.isSelected()) {
                minMatchBox.setEnabled(false);
                LZSS = false;
                huffman = false;
                LZ77 = true;
            }
        });

        compressButton.addActionListener(e -> {
            try {
                compress(inFilePathText.getText());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        decompressButton.addActionListener(e -> {
            // Call your decompression method here, passing in the selected options
            try {
                decompress(inFilePathText.getText());
            } catch (IOException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
        pack();
        setLocationRelativeTo(centerPanel);
        setVisible(true);
    }

    // Dummy methods for compress and decompress. Replace these with your actual methods.
    private void compress(String filePath) throws IOException {
        String output_path;
        if (!filePath.isEmpty()) {
            if (LZ77) {
                output_path = fileNameEncode(filePath, '7', "Compressed", huffman);
                LZ77Compress lz77_c = new LZ77Compress(filePath, output_path, windowSliderSize, lookaheadBufferSize);
                lz77_c.compress();
            } else if (LZSS) {
                output_path = fileNameEncode(filePath, 's', "Compressed", huffman);
                LZSSCompressor lzss_c = new LZSSCompressor(filePath, output_path, windowSliderSize, lookaheadBufferSize, minMatch);
                lzss_c.compress();
                if (huffman) {
                    String huffman_output_path = fileNameEncode(output_path, 'h', "Compressed", false);  // Create a temporary output path for the LZSS compressed file
                    HuffEncoderDecoder huffEncoder = new HuffEncoderDecoder();
                    huffEncoder.compress(output_path, huffman_output_path);  // Compress the LZSS compressed file with Huffman
                    File lzssCompressedFile = new File(huffman_output_path);  // Get the compressed LZSS file
                    lzssCompressedFile.delete();  // Delete the temporary LZSS compressed file
                }

            } else {
                JOptionPane.showMessageDialog(null, "Methode not selected, please select Compress method", "Error", JOptionPane.ERROR_MESSAGE);

            }
        } else {
            JOptionPane.showMessageDialog(null, "File to compress is not selected, please choose file.", "Error", JOptionPane.ERROR_MESSAGE);

        }
    }

    private void decompress(String filePath) throws IOException, ClassNotFoundException {
        String output_path;
        if (!filePath.isEmpty()) {
            if (LZ77) {
                output_path = fileNameEncode(filePath, '7', "Decompressed", huffman);
                output_path.replace("_7_Compressed", "");
                LZ77Decompressor lz77_d = new LZ77Decompressor(filePath, output_path);
                lz77_d.performDecompression();
            } else if (LZSS) {
                output_path = fileNameEncode(filePath, 's', "Decompressed", huffman);
                LZSSDecompressor lzss_d = new LZSSDecompressor(filePath, output_path);
                lzss_d.decompress();
                if (huffman) {
                    String huffman_output_path = fileNameEncode(output_path, 'h', "Decompressed", false);  // Create a temporary output path for the LZSS decompressed file
                    HuffEncoderDecoder huffDecoder = new HuffEncoderDecoder();
                    huffDecoder.decompress(output_path, huffman_output_path);  // Decompress the LZSS decompressed file with Huffman
                    File lzssDecompressedFile = new File(huffman_output_path);  // Get the decompressed LZSS file
                    lzssDecompressedFile.delete();  // Delete the temporary LZSS decompressed file
                }


            } else {
                JOptionPane.showMessageDialog(null, "Please select a compression method.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "No file was selected for compression, please select a file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void updateSlidingWindowSize() {
        searchBufferLable.setText("Search Buffer Size: " + (windowSliderSize - lookaheadBufferSize) + " Bytes");
    }

    private String fileNameEncode(String filePath, char compressionMethod, String action, boolean lzss_huff) {
        String file = null;
        if (action.equals("Decompressed") && filePath.contains("_Compressed")) {
            file = filePath.replace("_Compressed", "_Decompressed");
        } else if (action.equals("Compressed")) {
            for (int i = filePath.length() - 1; i > 0; i--) {
                if (filePath.charAt(i) == '.') {
                    if (lzss_huff) {
                        file = filePath.substring(0, i) + "_h" + compressionMethod + "_Compressed" + filePath.substring(i);
                    } else {
                        file = filePath.substring(0, i) + "_" + compressionMethod + "_Compressed" + filePath.substring(i);
                    }
                    break;
                }
            }
        } else {
            file = filePath;
        }
        return file;
    }
}
