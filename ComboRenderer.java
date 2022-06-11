import javax.swing.*;
import java.awt.*;
import javax.swing.text.*;
import java.util.*;
import java.util.regex.*;


public class ComboRenderer<E>
extends JLabel implements ListCellRenderer<Object> {
    DefaultStyledDocument doc;
    JTextPane textPane;
    int start;
    int end;
    Pattern colorPattern, fontPattern, sizePattern, backgroundPattern;
    Matcher colorMatcher, fontMatcher, sizeMatcher, backgroundMatcher;
    String[] fontFamily;
    String[] fontSizes;
    String test;
    MutableAttributeSet attr;


    ComboRenderer(DefaultStyledDocument doc, JTextPane textPane) {
      this.doc = doc;
      this.textPane = textPane;
      attr = new SimpleAttributeSet();
      ColorArray.init();
      fontSizes = new String[] {"8", "9", "10", 
      "11", "12", "14", "16", "18", "20", "22", "24", "26", 
      "28", "36", "48", "72"};
    }

    public void createMatcher(String data) {
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      fontFamily = ge.getAvailableFontFamilyNames();
      StringBuilder sb = new StringBuilder();
      for (String str : fontFamily) {
        sb.append(str + "|");
      }
      sb.deleteCharAt(sb.length() - 1);
      
      String regex = new String(sb);
      fontPattern = Pattern.compile(new String(regex));
      colorPattern = Pattern.compile("^<html>.*Ａ.*</html>$");
      backgroundPattern = Pattern.compile("^<html>.*■.*</html>$");
      sizePattern = Pattern.compile("[1-9][0-9]?");

      fontMatcher = fontPattern.matcher(data);
      colorMatcher = colorPattern.matcher(data);
      sizeMatcher = sizePattern.matcher(data);
      backgroundMatcher = backgroundPattern.matcher(data);
    }
    
    MutableAttributeSet initAttribute() {
      MutableAttributeSet attr = new SimpleAttributeSet();
      return attr;
    }

    public Component getListCellRendererComponent(
      JList list, 
      Object value,
      int index,
      boolean isSelected,
      boolean cellHasFocus
      ){
    
      start = textPane.getSelectionStart();
      end = textPane.getSelectionEnd();

      String data = value.toString();
      setText(data);
      createMatcher(data);
      setOpaque(true);  

      if (isSelected == true){
        // カラーマッチ
        if (colorMatcher.matches()) {
          if (index >= 0 && index < 8) {
            setForeground(ColorArray.colorArray.get(index));
            setBackground(Color.gray);
            attr = initAttribute();
            StyleConstants.setForeground(attr, ColorArray.colorArray.get(index));
            doc.setCharacterAttributes(start, end - start, attr, false);
            
          }    
        } else if (fontMatcher.matches()) {
          setForeground(Color.white);
          setBackground(Color.black);
          attr = initAttribute();
          if (index >= 0 && index < fontFamily.length) {
            StyleConstants.setFontFamily(attr, fontFamily[index]);
            doc.setCharacterAttributes(start, end - start, attr, false);
          }
        } else if (sizeMatcher.matches()) {
          attr = initAttribute();
          setForeground(Color.white);
          setBackground(Color.black);
          if (index >= 0 && index < fontSizes.length) {
            StyleConstants.setFontSize(attr, Integer.parseInt(fontSizes[index]));
            doc.setCharacterAttributes(start, end - start, attr, false);
          }
        } else if (backgroundMatcher.matches()) {
          if (index >= 0 && index < 8) {
            setForeground(ColorArray.colorArray.get(index));
            setBackground(Color.gray);
            attr = initAttribute();
            textPane.setBackground(ColorArray.colorArray.get(index));
          }
        }

      }else if (isSelected == false) {
        // カラーマッチ
        if (colorMatcher.matches()) {
          if (index >= 0 && index < 8) {  
            setForeground(ColorArray.colorArray.get(index));
            setBackground(Color.white);
          }
        } else if (backgroundMatcher.matches()) {
          if (index >= 0 && index < 8) {
            setForeground(ColorArray.colorArray.get(index));
            setBackground(Color.white);
          }
        } else {
          setForeground(Color.black);
          setBackground(Color.white);
        }
      }
      return this;
    }
}