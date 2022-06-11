import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.text.rtf.RTFEditorKit;
import java.io.*;
import java.util.*;

public class Memo extends JFrame
    implements ActionListener, CaretListener, PopupMenuListener ,DocumentListener{ 
        int currentCaretPosition, previousCaretPosition; //キャレットの位置
        int insertPosition; //ドキュメントに挿入された位置
        Object firstItem, secondItem; //PopupEventメソッドで使用
        JComboBox firstCombo, secondCombo; //PopupEventメソッドで使用

        JTextPane textPane; //入力するための画面

        DefaultStyledDocument doc; //入力した文字列を保存するための部品
        StyleContext sc; //文字列の属性を保存するための部品

        JToolBar toolBar; //ツールバー部品

        JComboBox<String> comboFonts, comboSizes, comboColor, comboBackground; //選択用comboBox
        ComboRenderer renderer; //JComboBoxのドロップダウン箇所を描画する部品
        
        JToggleButton toggleB, toggleI, toggleS, toggleU;
        //太字、斜体、取り消し線、下線の選択部品

        boolean insertFlg, attrFlg, caretEventFlg, caretActionFlg;

        RTFEditorKit rtfEditor; 

        JMenuItem saveItem;

        public static void main(String[] args) {
            Memo memo = new Memo();

            memo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            memo.setBounds(10, 10, 600, 300);
            memo.setTitle("メモ帳");
            memo.setVisible(true);
        }

        public Memo() {
            createText(); // 入力部分の準備
            createMenuBar(); // ツールバーの準備
            createToolBar(); // ツールバーの準備
            initDocument(); // ドキュメントの初期化
            comboBackground.setSelectedIndex(7);
            rtfEditor = new RTFEditorKit();
        }

        public void createText() {
            //　入力部分の準備
            textPane = new JTextPane();
            sc = new StyleContext();   
            doc = new DefaultStyledDocument(sc);
            doc.addDocumentListener(this);
            textPane.setDocument(doc);
            textPane.addCaretListener(this);          

            JScrollPane scroll = new JScrollPane(textPane,
              JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
              JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            );
            getContentPane().add(scroll);
        }

        public void createMenuBar() {
            
            // ツールバーの準備
            JMenuBar menuBar = new JMenuBar();
            JMenu fileMenu = new JMenu("ファイル", true);
            menuBar.add(fileMenu);

            JMenuItem newItem = new JMenuItem("新規ファイル");
            fileMenu.add(newItem);
            newItem.addActionListener(this);
            newItem.setActionCommand("newItem");

            JMenuItem openItem = new JMenuItem("ファイルを開く");
            fileMenu.add(openItem);
            openItem.addActionListener(this);
            openItem.setActionCommand("openItem");

            saveItem = new JMenuItem("保存する");
            fileMenu.add(saveItem);
            saveItem.addActionListener(this);
            saveItem.setActionCommand("saveItem");
            
            fileMenu.addSeparator();

            JMenuItem exitItem = new JMenuItem("閉じる");
            fileMenu.add(exitItem);
            exitItem.addActionListener(this);
            exitItem.setActionCommand("exitItem");

            setJMenuBar(menuBar);
        }

        public void initDocument() {
            // ドキュメントの初期化
            StringBuilder sb = new StringBuilder();
            try {
                //文書を挿入する
                doc.insertString(0, new String(sb), 
                sc.getStyle(StyleContext.DEFAULT_STYLE));
            } catch (BadLocationException e) {
                System.err.println(e.getStackTrace());
            }
        }

        public void createToolBar() {
            // ツールバーの準備
            toolBar = new JToolBar();
            toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
            ListCellRenderer<Object> renderer = 
                new ComboRenderer<Object>(doc, textPane);

            GraphicsEnvironment ge = 
                GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] familyName = ge.getAvailableFontFamilyNames();

            comboFonts = new JComboBox<String>(familyName);
            comboFonts.addActionListener(this);
            comboFonts.setActionCommand("comboFonts");
            comboFonts.setRenderer(renderer);
            comboFonts.addPopupMenuListener(this);
            toolBar.add(comboFonts);  

            comboSizes = new JComboBox<String>(new String[] {"8", "9", "10", 
                "11", "12", "14", "16", "18", "20", "22", "24", "26", 
                "28", "36", "48", "72"}
            );
            comboSizes.addActionListener(this);
            comboSizes.setActionCommand("comboSizes");
            comboSizes.setRenderer(renderer);
            comboSizes.addPopupMenuListener(this);
            toolBar.add(comboSizes);
            toolBar.addSeparator();

            toggleB = new JToggleButton("<html><b>B</b></html>");
            toggleB.addActionListener(this);
            toggleB.setActionCommand("toggleB");
            toolBar.add(toggleB);

            toggleI = new JToggleButton("<html><i> I </i></html>");
            toggleI.addActionListener(this);
            toggleI.setActionCommand("toggleI");
            toolBar.add(toggleI);
 
            toggleS = new JToggleButton("<html><s>S</s></html>");
            toggleS.addActionListener(this);
            toggleS.setActionCommand("toggleS");
            toolBar.add(toggleS);

            toggleU = new JToggleButton("<html><u>U</u></html>");
            toggleU.addActionListener(this);
            toggleU.setActionCommand("toggleU");
            toolBar.add(toggleU);

            toolBar.addSeparator();

            Vector<String> vector = new Vector<>();
            for (int i = 0; i < 8; i++) {
                vector.add("<html><id=\"" + i + "\"><b>Ａ</b></html>");
            }   
            comboColor = new JComboBox<String>(vector);
            comboColor.addActionListener(this);
            comboColor.setActionCommand("comboColor");
            comboColor.addPopupMenuListener(this);      
            comboColor.setRenderer(renderer);
            toolBar.add(comboColor);

            Vector<String> vector2 = new Vector<>();
            for (int i = 0; i < 8; i++) {
                vector2.add("<html><id=\"" + i + "\"><b>■</b></html>");
            }
            comboBackground = new JComboBox<String>(vector2);
            comboBackground.addActionListener(this);
            comboBackground.setActionCommand("comboBackground");
            comboBackground.addPopupMenuListener(this);
            comboBackground.setRenderer(renderer);
            toolBar.add(comboBackground);

            getContentPane().add(toolBar, BorderLayout.NORTH);
        }

        public void actionPerformed(ActionEvent e) { // menubar comboBox
            if (caretActionFlg == true ) {
                caretActionFlg = false;
                //System.out.println("caretActionFlgをfalseにしました");
                return;
            }

            String actionCommand = e.getActionCommand();
            // comboColorが変更されたら選択されたときの色を変更する
            int start = textPane.getSelectionStart();
            int end = textPane.getSelectionEnd();
            MutableAttributeSet attr = new SimpleAttributeSet();
            String selectedText = textPane.getSelectedText();
            
            if (firstCombo != null && selectedText != null) {
                firstItem = firstCombo.getSelectedItem();
            } else if (firstCombo == comboBackground) {
                firstItem = firstCombo.getSelectedItem();
            }

            if (actionCommand == "comboColor") {
                if (selectedText == null) { 
                    showMessage("範囲を指定してください");
                    return; 
                }
                comboColor.setForeground(ColorArray.colorArray.get(comboColor.getSelectedIndex()));

                StyleConstants.setForeground(attr, ColorArray.colorArray.get(comboColor.getSelectedIndex()));
                doc.setCharacterAttributes(start, end - start, attr, false);
            }

            else if (actionCommand == "comboBackground") {
                textPane.setBackground(ColorArray.colorArray.get(comboBackground.getSelectedIndex()));
                comboBackground.setForeground(ColorArray.colorArray.get(comboBackground.getSelectedIndex()));
            }
             else if (actionCommand == "comboSizes") {
                StyleConstants.setFontSize(attr, Integer.parseInt(comboSizes.getSelectedItem().toString()));
                doc.setCharacterAttributes(start, end - start, attr, false);
                if (selectedText == null) {
                    showMessage("範囲を指定してください");
                }
            }
             else if (actionCommand == "comboFonts") {
                StyleConstants.setFontFamily(attr, comboFonts.getSelectedItem().toString());
                doc.setCharacterAttributes(start, end - start, attr, false);
                if (selectedText == null) {
                    showMessage("範囲を指定してください");
                }
            }
             else if (actionCommand == "toggleB") {
                setAttrFlg(actionCommand, start, end);
                StyleConstants.setBold(attr, attrFlg);
                doc.setCharacterAttributes(start, end - start, attr, false);
                if (selectedText == null) {
                    toggleB.setSelected(false);
                }
            }
             else if (actionCommand == "toggleI") {
                setAttrFlg(actionCommand, start, end); 
                StyleConstants.setItalic(attr, attrFlg);
                doc.setCharacterAttributes(start, end - start, attr, false);
                if (selectedText == null) {
                    toggleI.setSelected(false);
                }
            }
             else if (actionCommand == "toggleS") {
                setAttrFlg(actionCommand, start, end);
                StyleConstants.setStrikeThrough(attr, attrFlg);
                doc.setCharacterAttributes(start, end - start, attr, false);
                if (selectedText == null) {
                    toggleS.setSelected(false);
                }
            }    
             else if (actionCommand == "toggleU") {
                setAttrFlg(actionCommand, start, end);
                StyleConstants.setUnderline(attr, attrFlg);
                doc.setCharacterAttributes(start, end - start, attr, false);
                if (selectedText == null) {
                    toggleU.setSelected(false);
                }
            }
             else if (actionCommand == "newItem") {
                fileOperation(actionCommand);
            }
             else if (actionCommand == "openItem") {
                fileOperation(actionCommand);
            }
             else if (actionCommand == "exitItem") {
                fileOperation(actionCommand);
            }
             else if (actionCommand == "saveItem") {
                fileOperation(actionCommand);
            }
        }
        //選択されたテキストのどの箇所にもb,i,s,uが反映されていない場合、attrFlgをtrueにする。
        public void setAttrFlg(String actionCommand, int start, int end) {
            attrFlg = false;
            java.util.List<Element> elementArray = new ArrayList<Element>();
            for (int i = start; i < end; i++) {
                elementArray.add(doc.getCharacterElement(i));
            }
            
            switch (actionCommand) {
                case "toggleB" :{
                    for (Element element : elementArray) {
                        if (!StyleConstants.isBold(element.getAttributes())) {
                            attrFlg = true;
                            return;
                        }
                    } break;
                }
                case "toggleU" :{
                    for (Element element : elementArray) {
                        if (!StyleConstants.isUnderline(element.getAttributes())) {
                            attrFlg = true;
                            return;
                        }
                    } break;
                }
                case "toggleI" :{
                    for (Element element : elementArray) {
                        if (!StyleConstants.isItalic(element.getAttributes())) {
                            attrFlg = true;
                            return;
                        }
                    } break;
                }
                case "toggleS" :{
                    for (Element element : elementArray) {
                        if (!StyleConstants.isStrikeThrough(element.getAttributes())) {
                            attrFlg = true;
                            return;
                        }
                    } break;
                }


            }
            
        }
    
        public void caretUpdate(CaretEvent e) { // textPane
        
            currentCaretPosition = e.getDot();
            System.out.println("caretUpdate()");
            String str = null;
            try {
                str = doc.getText((currentCaretPosition), 1);
                char[] charArray = str.toCharArray();
                System.out.println("codepoint : " + Character.codePointAt(charArray, 0));
            } catch (Exception ex) {
                System.out.println("error");
            }
            if (str != null && str.equals("\n")) {
                toggleI.setSelected(false);
                toggleS.setSelected(false);
                toggleU.setSelected(false);
                toggleB.setSelected(false);
                
                Element ele = doc.getCharacterElement(currentCaretPosition - 1);
                AttributeSet eleAttr = ele.getAttributes();
                comboColor.setForeground(StyleConstants.getForeground(eleAttr));
                toggleB.setSelected(StyleConstants.isBold(eleAttr));
                toggleI.setSelected(StyleConstants.isItalic(eleAttr));
                toggleS.setSelected(StyleConstants.isStrikeThrough(eleAttr));
                toggleU.setSelected(StyleConstants.isUnderline(eleAttr));
                System.out.println("returnします"); return;
            }
            Element element = doc.getCharacterElement(currentCaretPosition);
            AttributeSet elementAttr = element.getAttributes();
            String fontFamily = StyleConstants.getFontFamily(elementAttr);
            int fontSize = StyleConstants.getFontSize(elementAttr);
            caretActionFlg = true;
            comboSizes.setSelectedItem(Integer.toString(fontSize));
            caretActionFlg = true;
            comboFonts.setSelectedItem(fontFamily);
            //toggleB.setSelected(StyleConstants.isBold(elementAttr));
            toggleB.setSelected(StyleConstants.isBold(elementAttr));
            toggleI.setSelected(StyleConstants.isItalic(elementAttr));
            toggleS.setSelected(StyleConstants.isStrikeThrough(elementAttr));
            toggleU.setSelected(StyleConstants.isUnderline(elementAttr));
            
        }
        public void popupMenuCanceled(PopupMenuEvent e) {} //comboBox
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            if (firstCombo == null) {
                firstCombo = (JComboBox) e.getSource();
                firstItem = firstCombo.getSelectedItem();
            } else if (firstCombo != null) {
                secondCombo = (JComboBox) e.getSource();
                secondItem = secondCombo.getSelectedItem();
            }
        }
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            firstCombo.setSelectedItem(firstItem);
            if (firstCombo == comboBackground) {
                textPane.setBackground(ColorArray.colorArray.get(comboBackground.getSelectedIndex()));
            }
            firstCombo = null;
            firstItem = null;
            if (secondCombo != null) {
                firstCombo = secondCombo;
                firstItem = secondItem;
                secondCombo = null;
                secondItem = null;
            }
            if (firstCombo == null) {
                textPane.requestFocusInWindow();
            }
        }
        public void insertUpdate(DocumentEvent e) {
            insertPosition = e.getOffset();
            caretEventFlg = true;
        } 
        public void changedUpdate(DocumentEvent e) {}
        public void removeUpdate(DocumentEvent e) {}
        public void invokeLater() {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    MutableAttributeSet attr = new SimpleAttributeSet();
                    StyleConstants.setFontSize(attr, 50);
                    doc.setCharacterAttributes(insertPosition, 1, attr, false);
                }
            });
        }
        public void showMessage(String str) {
            JOptionPane.showMessageDialog(this, str);
        }

        public void fileOperation(String actionCommand) {
            JFileChooser chooser = new JFileChooser();
            RtfFilter filter = new RtfFilter();
            chooser.setFileFilter(filter);

            // 新規ファイル　開く前に保存するか聞く
            if (actionCommand.equals("newItem")) {
                int option = JOptionPane.showConfirmDialog(this, "現在のファイルの情報を保存しますか？",
                "確認", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (option == 0) {
                    saveItem.doClick();
                } else if (option == 1) {
                    try {
                        doc.remove(0, doc.getLength());
                        comboBackground.setSelectedIndex(7);
                    } catch (Exception e) { System.out.println("erro");}
                } else {
                    return;
                }
            } 
            // ファイルを開く
            else if (actionCommand.equals("openItem")) {
                
                showMessage("このメモ帳はRTF形式ファイル専用です");
                if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                File fChoosen = chooser.getSelectedFile();
                try {
                    InputStream in =new FileInputStream(fChoosen);
                    rtfEditor.read(in, doc, 0);
                    in.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                textPane.setDocument(doc);
            }
                // セーブ 保存しましたと表示する
            else if (actionCommand.equals("saveItem")) {
                showMessage("RTF形式で保存してください");
                if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                File fChoosen = chooser.getSelectedFile();
                try {
                    OutputStream out = new FileOutputStream(fChoosen);
                    rtfEditor.write(out, doc, 0, doc.getLength());
                    out.close();
                    showMessage("保存しました");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showMessage("保存できませんでした");
                }
            } else { // p
                return;
            }
        }
    }
