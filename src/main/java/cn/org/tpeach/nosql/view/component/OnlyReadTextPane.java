package cn.org.tpeach.nosql.view.component;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class OnlyReadTextPane extends JTextPane {
    private int rowHeight;
    private SimpleAttributeSet attrSet = new SimpleAttributeSet();
    private AreaOutputStream out = this.new AreaOutputStream();
    private final PrintStream printStream = new PrintStream(out);
    private Color defaultFontColor = Color.GREEN.darker().darker().darker();
    /**
     * 是否限制最多显示条数，超过删除，显示最新
     */
    @Getter
    @Setter
    private boolean limit = true;
    @Getter
    @Setter
    private Color candy;
    /**
     * 最多显示条数
     */
    @Getter
    @Setter
    protected int maxEntries = 100;
    /**
     * 已经在显示的条数
     */
    private int entries = 0;

    public OnlyReadTextPane() {
        init();
    }

    public OnlyReadTextPane(StyledDocument doc) {
        super(doc);

        init();
    }

    public void init() {
        candy = new Color(230, 230, 255);
        setOpaque(false);
        this.setFont(new Font("黑体",Font.PLAIN,15));
//        this.setFont(new Font("宋体", Font.PLAIN, 14));
        setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        setForeground(Color.GREEN.darker().darker().darker());
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                //鼠标进入Text区后变为文本输入指针
                OnlyReadTextPane.this.setCursor(new Cursor(Cursor.TEXT_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                //鼠标离开Text区后恢复默认形态
                OnlyReadTextPane.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        this.getCaret().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                //使Text区的文本光标显示
                try{
                    OnlyReadTextPane.this.getCaret().setVisible(true);
                }catch (Exception ex){}

            }
        });
    }
    public void clear(){
        this.setText("");
        this.entries = 0;
    }
    @Override
    public final boolean isEditable() {
        return false;
    }

    public int getLineCount() {
        Element map = getDocument().getDefaultRootElement();
        return map.getElementCount();
    }

    public int getLineEndOffset(int line) throws BadLocationException {
        int lineCount = getLineCount();
        if (line < 0) {
            throw new BadLocationException("Negative line", -1);
        } else if (line >= lineCount) {
            throw new BadLocationException("No such line", getDocument().getLength() + 1);
        } else {
            Element map = getDocument().getDefaultRootElement();
            Element lineElem = map.getElement(line);
            int endOffset = lineElem.getEndOffset();
            // hide the implicit break at the end of the document
            return ((line == lineCount - 1) ? (endOffset - 1) : endOffset);
        }
    }

    private StyledDocument autoClear() {

        StyledDocument doc = null;
        try {
            doc = this.getStyledDocument();
            if (limit) {
                if (entries >= maxEntries) {
//                    int endOfs = this.get
                    int endOfs = this.getLineEndOffset(entries - maxEntries);
                    doc.remove(0, endOfs);
                    entries = entries - 1;
                }
                entries = entries + 1;
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return doc;
    }

    public void println(int x, Color fontColor) {
        if (fontColor != null) {
            out.setFontColor(fontColor);
        }
        this.printStream.println(x);
    }

    public void println(char x, Color fontColor) {
        if (fontColor != null) {
            out.setFontColor(fontColor);
        }
        Document document = autoClear();
        this.printStream.println(x);
        this.setCaretPosition(document.getLength());
    }

    public void println(long x, Color fontColor) {
        if (fontColor != null) {
            out.setFontColor(fontColor);
        }
        Document document = autoClear();
        this.printStream.println(x);
        this.setCaretPosition(document.getLength());
    }

    public void println(float x, Color fontColor) {
        if (fontColor != null) {
            out.setFontColor(fontColor);
        }
        Document document = autoClear();
        this.printStream.println(x);
        this.setCaretPosition(document.getLength());
    }

    public void println(double x, Color fontColor) {
        if (fontColor != null) {
            out.setFontColor(fontColor);
        }
        Document document = autoClear();
        this.printStream.println(x);
        this.setCaretPosition(document.getLength());
    }

    public void println(char[] x, Color fontColor) {
        if (fontColor != null) {
            out.setFontColor(fontColor);
        }
        Document document = autoClear();
        this.printStream.println(x);
        this.setCaretPosition(document.getLength());
    }

    public void println(boolean x, Color fontColor) {
        if (fontColor != null) {
            out.setFontColor(fontColor);
        }
        Document document = autoClear();
        this.printStream.println(x);
        this.setCaretPosition(document.getLength());
    }

    public void println(String x, Color fontColor) {
        if (fontColor != null) {
            out.setFontColor(fontColor);
        }
        Document document = autoClear();
        this.printStream.println(x);
        this.setCaretPosition(document.getLength());
    }


    protected int getRowHeight() {
        if (rowHeight == 0) {
            Font font = getFont();
            FontMetrics metrics = getFontMetrics(font);
            rowHeight = metrics.getHeight();
        }
        return rowHeight;
    }

    private void insert(String text) {
        insert(text, defaultFontColor, null);
    }

    private void insert(String text, Color color) {
        insert(text, color,null);
    }

    private void insert(String text, Color color, Color backColor) {
        try { // 插入文本
            if (color != null) {
                StyleConstants.setForeground(attrSet, color);

            }
            if (backColor != null) {
                StyleConstants.setBackground(attrSet, backColor);
            }
            this.getStyledDocument().insertString(this.getStyledDocument().getLength(), text, attrSet);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {

        int width = getWidth();
        int height = getHeight();

        Color old = g.getColor();
        g.setColor(getBackground());
        g.fillRect(0, 0, width, height);

        Rectangle r = new Rectangle();
        r.x = 0;
        r.y = 0;
        r.width = width;
        int rowHeight = getRowHeight();
        r.height = rowHeight;
        g.setColor(candy);
        for (int heightIncrement = 2 * rowHeight; r.y < height; r.y += heightIncrement) {
            g.fillRect(r.x, r.y, r.width, r.height);
        }
        g.setColor(old);

        super.paintComponent(g);
    }

    private class AreaOutputStream extends OutputStream {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        @Getter
        @Setter
        private Color fontColor;

        @Override
        public void write(int b) {
            out.write(b);
            if ('\n' == (char) b) {
                insert(out.toString(), fontColor);
                out.reset();
                fontColor = defaultFontColor;
            }
        }
    }

}
