package cn.org.tpeach.nosql.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * https://blog.csdn.net/zhuiqiuzhuoyue583/article/details/79047454
 */
public class JTextPaneDemo extends JFrame {
    private JScrollPane scrollPane = null;
    private JTextPane text = null;
    private Box box = null; // 放输入组件的容器
    private JButton b_insert = null, b_remove = null, b_icon = null; // 插入按钮;清除按钮;插入图片按钮
    private JTextField addText = null; // 文字输入框
    private JComboBox fontName = null, fontSize = null, fontStyle = null,
            fontColor = null, fontBackColor = null; // 字体名称;字号大小;文字样式;文字颜色;文字背景颜色

    private StyledDocument doc = null;

    public static void main(String args[]) {
        new JTextPaneDemo();
    }

    public JTextPaneDemo() {
        super("JTextPane Test");
        try { // 使用Windows的界面风格
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        text = new JTextPane();
        text.setEditable(false);
        doc = text.getStyledDocument(); // 获得JTextPane的Document
        scrollPane = new JScrollPane(text);
        scrollPane.setPreferredSize(new Dimension(400, 400));
        addText = new JTextField(18);
        String[] str_name = {"宋体", "黑体", "Dialog", "Gulim"};
        String[] str_Size = {"12", "14", "18", "22", "30", "40"};
        String[] str_Style = {"常规", "斜体", "粗体", "粗斜体"};
        String[] str_Color = {"黑色", "红色", "蓝色", "黄色", "绿色"};
        String[] str_BackColor = {"无色", "灰色", "淡红", "淡蓝", "淡黄", "淡绿"};
        fontName = new JComboBox(str_name); // 字体名称
        fontSize = new JComboBox(str_Size); // 字号
        fontStyle = new JComboBox(str_Style); // 样式
        fontColor = new JComboBox(str_Color); // 颜色
        fontBackColor = new JComboBox(str_BackColor); // 背景颜色
        b_insert = new JButton("插入"); // 插入
        b_remove = new JButton("清空"); // 清除
        b_icon = new JButton("图片"); // 插入图片

        b_insert.addActionListener(new ActionListener() { // 插入文字的事件
            public void actionPerformed(ActionEvent e) {
                insert(getFontAttrib());
                addText.setText("");
            }
        });

        b_remove.addActionListener(new ActionListener() { // 清除事件
            public void actionPerformed(ActionEvent e) {
                text.setText("");
            }
        });

        b_icon.addActionListener(new ActionListener() { // 插入图片事件
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser f = new JFileChooser(); // 查找文件
                f.showOpenDialog(null);
                insertIcon(f.getSelectedFile()); // 插入图片
            }
        });
        box = Box.createVerticalBox(); // 竖结构
        Box box_1 = Box.createHorizontalBox(); // 横结构
        Box box_2 = Box.createHorizontalBox(); // 横结构
        box.add(box_1);
        box.add(Box.createVerticalStrut(8)); // 两行的间距
        box.add(box_2);
        box.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8)); // 8个的边距
        // 开始将所需组件加入容器

        box_1.add(new JLabel("字体：")); // 加入标签
        box_1.add(fontName); // 加入组件
        box_1.add(Box.createHorizontalStrut(8)); // 间距
        box_1.add(new JLabel("样式："));
        box_1.add(fontStyle);
        box_1.add(Box.createHorizontalStrut(8));
        box_1.add(new JLabel("字号："));
        box_1.add(fontSize);
        box_1.add(Box.createHorizontalStrut(8));
        box_1.add(new JLabel("颜色："));
        box_1.add(fontColor);
        box_1.add(Box.createHorizontalStrut(8));
        box_1.add(new JLabel("背景："));
        box_1.add(fontBackColor);
        box_1.add(Box.createHorizontalStrut(8));
        box_1.add(b_icon);
        box_2.add(addText);
        box_2.add(Box.createHorizontalStrut(8));
        box_2.add(b_insert);
        box_2.add(Box.createHorizontalStrut(8));
        box_2.add(b_remove);
        this.getRootPane().setDefaultButton(b_insert); // 默认回车按钮
        this.getContentPane().add(scrollPane);
        this.getContentPane().add(box, BorderLayout.SOUTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        addText.requestFocus();
    }


    private void insertIcon(File file) {
        text.setCaretPosition(doc.getLength()); // 设置插入位置
        text.insertIcon(new ImageIcon(file.getPath())); // 插入图片
        insert(new FontAttrib()); // 这样做可以换行
    }


    private void insert(FontAttrib attrib) {
        try { // 插入文本
            doc.insertString(doc.getLength(), attrib.getText() + "\n",
                    attrib.getAttrSet());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }


    private FontAttrib getFontAttrib() {
        FontAttrib att = new FontAttrib();
        att.setText(addText.getText());
        att.setName((String) fontName.getSelectedItem());
        att.setSize(Integer.parseInt((String) fontSize.getSelectedItem()));
        String temp_style = (String) fontStyle.getSelectedItem();
        if (temp_style.equals("常规")) {
            att.setStyle(FontAttrib.GENERAL);
        } else if (temp_style.equals("粗体")) {
            att.setStyle(FontAttrib.BOLD);
        } else if (temp_style.equals("斜体")) {
            att.setStyle(FontAttrib.ITALIC);
        } else if (temp_style.equals("粗斜体")) {
            att.setStyle(FontAttrib.BOLD_ITALIC);
        }
        String temp_color = (String) fontColor.getSelectedItem();
        if (temp_color.equals("黑色")) {
            att.setColor(new Color(0, 0, 0));
        } else if (temp_color.equals("红色")) {
            att.setColor(new Color(255, 0, 0));
        } else if (temp_color.equals("蓝色")) {
            att.setColor(new Color(0, 0, 255));
        } else if (temp_color.equals("黄色")) {
            att.setColor(new Color(255, 255, 0));
        } else if (temp_color.equals("绿色")) {
            att.setColor(new Color(0, 255, 0));
        }
        String temp_backColor = (String) fontBackColor.getSelectedItem();
        if (!temp_backColor.equals("无色")) {
            if (temp_backColor.equals("灰色")) {
                att.setBackColor(new Color(200, 200, 200));
            } else if (temp_backColor.equals("淡红")) {
                att.setBackColor(new Color(255, 200, 200));
            } else if (temp_backColor.equals("淡蓝")) {
                att.setBackColor(new Color(200, 200, 255));
            } else if (temp_backColor.equals("淡黄")) {
                att.setBackColor(new Color(255, 255, 200));
            } else if (temp_backColor.equals("淡绿")) {
                att.setBackColor(new Color(200, 255, 200));
            }
        }
        return att;
    }


    private class FontAttrib {
        public static final int GENERAL = 0; // 常规
        public static final int BOLD = 1; // 粗体
        public static final int ITALIC = 2; // 斜体
        public static final int BOLD_ITALIC = 3; // 粗斜体
        private SimpleAttributeSet attrSet = null; // 属性集
        private String text = null, name = null; // 要输入的文本和字体名称
        private int style = 0, size = 0; // 样式和字号
        private Color color = null, backColor = null; // 文字颜色和背景颜色


        public FontAttrib() {
        }

        public SimpleAttributeSet getAttrSet() {
            attrSet = new SimpleAttributeSet();
            if (name != null) {
                StyleConstants.setFontFamily(attrSet, name);
            }
            if (style == FontAttrib.GENERAL) {
                StyleConstants.setBold(attrSet, false);
                StyleConstants.setItalic(attrSet, false);
            } else if (style == FontAttrib.BOLD) {
                StyleConstants.setBold(attrSet, true);
                StyleConstants.setItalic(attrSet, false);
            } else if (style == FontAttrib.ITALIC) {
                StyleConstants.setBold(attrSet, false);
                StyleConstants.setItalic(attrSet, true);
            } else if (style == FontAttrib.BOLD_ITALIC) {
                StyleConstants.setBold(attrSet, true);
                StyleConstants.setItalic(attrSet, true);
            }
            StyleConstants.setFontSize(attrSet, size);
            if (color != null) {
                StyleConstants.setForeground(attrSet, color);

            }
            if (backColor != null) {
                StyleConstants.setBackground(attrSet, backColor);
            }
            return attrSet;
        }

        public void setAttrSet(SimpleAttributeSet attrSet) {
            this.attrSet = attrSet;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public Color getBackColor() {
            return backColor;
        }

        public void setBackColor(Color backColor) {
            this.backColor = backColor;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getStyle() {
            return style;
        }

        public void setStyle(int style) {
            this.style = style;
        }
    }

}
