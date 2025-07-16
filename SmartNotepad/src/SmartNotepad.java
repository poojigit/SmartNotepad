import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class SmartNotepad extends JFrame implements ActionListener {

    JTextArea textArea;
    JScrollPane scrollPane;
    String filePath = null;
    boolean isModified = false;

    public SmartNotepad() {
        setTitle("Untitled - Notepad");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> exitApp());
        add(exitButton, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (confirmSave()) {
                    dispose();
                    System.exit(0);
                }
            }
        });

        createMenuBar();

        textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { isModified = true; }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { isModified = true; }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { isModified = true; }
        });

        setVisible(true);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        addItem(fileMenu, "New", KeyEvent.VK_N);
        addItem(fileMenu, "Open", KeyEvent.VK_O);
        addItem(fileMenu, "Save", KeyEvent.VK_S);
        addItem(fileMenu, "Save As", KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
        fileMenu.addSeparator();
        addItem(fileMenu, "Exit", KeyEvent.VK_Q);

        JMenu editMenu = new JMenu("Edit");
        addItem(editMenu, "Cut", KeyEvent.VK_X);
        addItem(editMenu, "Copy", KeyEvent.VK_C);
        addItem(editMenu, "Paste", KeyEvent.VK_V);
        addItem(editMenu, "Select All", KeyEvent.VK_A);

        JMenu searchMenu = new JMenu("Search");
        addItem(searchMenu, "Find", KeyEvent.VK_F);
        addItem(searchMenu, "Replace", KeyEvent.VK_R);

        JMenu formatMenu = new JMenu("Format");
        addItem(formatMenu, "Font", KeyEvent.VK_T);
        addItem(formatMenu, "Color", KeyEvent.VK_K);

        JMenu themeMenu = new JMenu("Theme");
        addItem(themeMenu, "Light Theme", KeyEvent.VK_L);
        addItem(themeMenu, "Dark Theme", KeyEvent.VK_D);

        formatMenu.addSeparator();
        formatMenu.add(themeMenu);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(searchMenu);
        menuBar.add(formatMenu);

        setJMenuBar(menuBar);
    }

    private void addItem(JMenu menu, String title, int keyCode) {
        addItem(menu, title, keyCode, InputEvent.CTRL_DOWN_MASK);
    }

    private void addItem(JMenu menu, String title, int keyCode, int modifiers) {
        JMenuItem item = new JMenuItem(title);
        item.setAccelerator(KeyStroke.getKeyStroke(keyCode, modifiers));
        item.addActionListener(this);
        menu.add(item);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "New": newFile(); break;
            case "Open": openFile(); break;
            case "Save": saveFile(); break;
            case "Save As": saveFileAs(); break;
            case "Exit": exitApp(); break;
            case "Cut": textArea.cut(); break;
            case "Copy": textArea.copy(); break;
            case "Paste": textArea.paste(); break;
            case "Select All": textArea.selectAll(); break;
            case "Find": findText(); break;
            case "Replace": replaceText(); break;
            case "Font": chooseFont(); break;
            case "Color": chooseColor(); break;
            case "Light Theme": applyLightTheme(); break;
            case "Dark Theme": applyDarkTheme(); break;
        }
    }

    void newFile() {
        if (confirmSave()) {
            textArea.setText("");
            setTitle("Untitled - Notepad");
            filePath = null;
            isModified = false;
        }
    }

    void openFile() {
        if (!confirmSave()) return;
        JFileChooser fc = new JFileChooser();
        int option = fc.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            filePath = file.getAbsolutePath();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                textArea.setText("");
                String line;
                while ((line = br.readLine()) != null) {
                    textArea.append(line + "\n");
                }
                setTitle(file.getName() + " - Notepad");
                isModified = false;
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Unable to open file.");
            }
        }
    }

    void saveFile() {
        if (filePath == null) {
            saveFileAs();
        } else {
            writeToFile(new File(filePath));
        }
    }

    void saveFileAs() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        int option = fc.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            filePath = file.getAbsolutePath();
            if (!filePath.endsWith(".txt")) filePath += ".txt";
            writeToFile(new File(filePath));
        }
    }

    void writeToFile(File file) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(textArea.getText());
            setTitle(file.getName() + " - Notepad");
            isModified = false;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving file.");
        }
    }

    void exitApp() {
        if (confirmSave()) System.exit(0);
    }

    boolean confirmSave() {
        if (isModified) {
            int result = JOptionPane.showConfirmDialog(this, "Save changes?", "Confirm", JOptionPane.YES_NO_CANCEL_OPTION);
            if (result == JOptionPane.CANCEL_OPTION) return false;
            if (result == JOptionPane.YES_OPTION) saveFile();
        }
        return true;
    }

    void findText() {
        String term = JOptionPane.showInputDialog(this, "Find:");
        if (term == null || term.isEmpty()) return;
        String content = textArea.getText();
        int index = content.indexOf(term);
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Text not found.");
        } else {
            textArea.requestFocus();
            textArea.select(index, index + term.length());
        }
    }

    void replaceText() {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        JTextField findField = new JTextField();
        JTextField replaceField = new JTextField();
        panel.add(new JLabel("Find:"));
        panel.add(findField);
        panel.add(new JLabel("Replace with:"));
        panel.add(replaceField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Replace Text", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String content = textArea.getText();
            String find = findField.getText();
            String replace = replaceField.getText();
            textArea.setText(content.replace(find, replace));
        }
    }

    void chooseFont() {
        Font currentFont = textArea.getFont();
        JFontChooser fontChooser = new JFontChooser(currentFont);
        int result = JOptionPane.showConfirmDialog(this, fontChooser, "Choose Font", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            textArea.setFont(fontChooser.getSelectedFont());
        }
    }

    void chooseColor() {
        Color color = JColorChooser.showDialog(this, "Choose Text Color", textArea.getForeground());
        if (color != null) {
            textArea.setForeground(color);
        }
    }

    void applyLightTheme() {
        textArea.setBackground(Color.WHITE);
        textArea.setForeground(Color.BLACK);
        textArea.setCaretColor(Color.BLACK);
    }

    void applyDarkTheme() {
        textArea.setBackground(new Color(30, 30, 30));
        textArea.setForeground(Color.WHITE);
        textArea.setCaretColor(Color.WHITE);
    }
}