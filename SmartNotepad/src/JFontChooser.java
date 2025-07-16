import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JFontChooser extends JPanel {
    private JComboBox<String> fontBox;
    private JComboBox<String> styleBox;
    private JComboBox<Integer> sizeBox;
    private Font selectedFont;

    public JFontChooser(Font initialFont) {
        setLayout(new GridLayout(3, 2, 10, 10));

        // Available fonts from system
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fontBox = new JComboBox<>(fonts);
        fontBox.setSelectedItem(initialFont.getFamily());

        String[] styles = {"Plain", "Bold", "Italic", "Bold Italic"};
        styleBox = new JComboBox<>(styles);
        styleBox.setSelectedIndex(initialFont.getStyle());

        Integer[] sizes = new Integer[50];
        for (int i = 0; i < sizes.length; i++) {
            sizes[i] = i + 1;
        }
        sizeBox = new JComboBox<>(sizes);
        sizeBox.setSelectedItem(initialFont.getSize());

        add(new JLabel("Font:"));
        add(fontBox);
        add(new JLabel("Style:"));
        add(styleBox);
        add(new JLabel("Size:"));
        add(sizeBox);
    }

    public Font getSelectedFont() {
        String fontFamily = (String) fontBox.getSelectedItem();
        int fontStyle = styleBox.getSelectedIndex();
        int fontSize = (int) sizeBox.getSelectedItem();

        selectedFont = new Font(fontFamily, fontStyle, fontSize);
        return selectedFont;
    }
}
