
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

    private final JLabel searchBufferLable; // Declare the label here

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUI gui = new GUI();
            gui.setVisible(true);
        });
    }

    public GUI() {

        setTitle("LZSS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 320);

        // Create a main panel with vertical BoxLayout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // File selection panel
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filePanel.setBorder(new EmptyBorder(30, 30, 0, 30));
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

        // Search Buffer JComboBox
        JPanel lookaheadBuff = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> lookaheadBuffer_Selector = new JComboBox<>(new String[]{"\"T\"- Choose Look-Ahead Buffer Size.", "32 Bytes", "64 Bytes", "128 Bytes", "256 Bytes", "512 Bytes", "1024 Bytes", "2048 Bytes", "4096 Bytes", "8192 Bytes", "16384 Bytes", "32768 Bytes"});
        lookaheadBuffer_Selector.setPreferredSize(new Dimension(280, 25));
        lookaheadBuff.add(lookaheadBuffer_Selector);

        // Minimum Find JComboBox with Sliding Window Size
        JPanel minMatchPanel = new JPanel();
        minMatchPanel.setLayout(new BoxLayout(minMatchPanel, BoxLayout.Y_AXIS));

        JPanel minMatchSubPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel MinmumLength = new JLabel("Minimum Find");
        JComboBox<String> minMatchBox = new JComboBox<>();
        for (int i = 2; i < 21; i++) {
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

        // Compress and Decompress Buttons
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        JButton compressButton = new JButton("Compress");
        JButton decompressButton = new JButton("Decompress");
        buttons.add(compressButton);
        buttons.add(Box.createHorizontalStrut(10));
        buttons.add(decompressButton);

        centerPanel.add(windowSliding);
        centerPanel.add(lookaheadBuff);
        centerPanel.add(minMatchPanel);
        centerPanel.add(radioPanel);
        centerPanel.add(buttons);
        mainPanel.add(filePanel);
        mainPanel.add(centerPanel);

        add(mainPanel);

        // Action Listeners
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
                LZSS = false;
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
                lookaheadBuffer_Selector.setEnabled(false);
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
        String output_path = null;
        if (!filePath.isEmpty()) {
            if (LZ77) {
                output_path = fileNameEncode(filePath, '7', "Compressed", huffman);
                LZ77Compress lz77_c = new LZ77Compress(filePath, output_path, windowSliderSize, lookaheadBufferSize);
                lz77_c.compress();
            } else if (huffman) {
                output_path = fileNameEncode(filePath, 's', "Compressed", huffman);
                HuffmanEncoderDecoder lzssHuff_c = new HuffmanEncoderDecoder();
                lzssHuff_c.Compress(filePath, output_path, windowSliderSize, lookaheadBufferSize, minMatch);
            } else if (LZSS) {
                output_path = fileNameEncode(filePath, 's', "Compressed", huffman);
                LZSSCompressor lzss_c = new LZSSCompressor(filePath, output_path, windowSliderSize, lookaheadBufferSize, minMatch);
                lzss_c.compress();
            } else {
                JOptionPane.showMessageDialog(null, "Methode not selected, please select Compress method", "Error", JOptionPane.ERROR_MESSAGE);

            }
        } else {
            JOptionPane.showMessageDialog(null, "File to compress is not selected, please choose file.", "Error", JOptionPane.ERROR_MESSAGE);

        }
    }

    private void decompress(String filePath) throws IOException, ClassNotFoundException {
        String output_path = null;

        if (!filePath.isEmpty()) {
            if (LZ77) {
                output_path = fileNameEncode(filePath, '7', "Decompressed", huffman);
                output_path.replace("_7_Compressed", "");
                LZ77Decompressor lz77_d = new LZ77Decompressor(filePath, output_path);
                lz77_d.performDecompression();
            } else if (huffman) {
                output_path = fileNameEncode(filePath, 's', "Decompressed", huffman);
                HuffmanEncoderDecoder lzssHuff_d = new HuffmanEncoderDecoder();
                lzssHuff_d.Decompress(filePath, output_path);

            } else if (LZSS) {
                output_path = fileNameEncode(filePath, 's', "Decompressed", huffman);
                LZSSDecompressor lzss_d = new LZSSDecompressor(filePath, output_path);
                lzss_d.decompress();


            } else {
                JOptionPane.showMessageDialog(null, "Method not selected, please select Compress method", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "File to compress is not selected, please choose file.", "Error", JOptionPane.ERROR_MESSAGE);
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
