package cn.org.tpeach.nosql.view.component;

import cn.org.tpeach.nosql.framework.LarkFrame;
import cn.org.tpeach.nosql.tools.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Slf4j
public class OnlyReadTextPane extends JTextPane {
    private int rowHeight;
    private SimpleAttributeSet attrSet = new SimpleAttributeSet();
    private Color defaultFontColor = Color.GREEN.darker().darker().darker();
    /**
     * 是否限制最多显示条数，超过删除，显示最新
     */
//    @Getter
//    @Setter
    private final boolean limit = true;
    @Getter
    @Setter
    private Color candy;
    /**
     * 最多显示条数
     */
    @Getter
    @Setter
    protected int maxEntries = 1000;
    /**
     * 已经在显示的条数
     */
    private int entries = 0;
    @Getter
    private EasyJSP jsp;

    public OnlyReadTextPane() {
        init();
    }

    public OnlyReadTextPane(StyledDocument doc) {
        super(doc);

        init();
    }

    public void init() {
        jsp = new EasyJSP(this).hiddenHorizontalScrollBar();
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
//        StyleConstants.setFontSize(attrSet,24);
        clear();
    }

    @Override
    public final boolean isEditable() {
        return false;
    }
    public synchronized void clear(){
        try {
            getDocument().remove(0, getDocument().getLength());
        } catch (BadLocationException e) {
            this.setText("");
            e.printStackTrace();
        }
        this.entries = 0;
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
    private synchronized void autoClear() {

        Document doc = null;
        try {
            doc = this.getDocument();
            if (limit) {
                if (entries >= maxEntries) {
//                    int endOfs = this.getLineEndOffset(entries - maxEntries);
                    for (int i = 0; i < entries - maxEntries; i++) {
                        String text = this.getText();
                        doc.remove(0, text.indexOf("\n"));
                        entries = entries - 1;
                    }

                }
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

    }
    public synchronized void println(String s, Color fontColor) {
        if(StringUtils.isBlank(s)){
            return;
        }

        if(!(s.endsWith("\r\n")|| s.endsWith("\n"))){
            s += "\r\n";
        }
        if(fontColor != null){
            insert(s, fontColor);
        }else{
            insert(s, defaultFontColor);
        }
        autoClear();
        try{
//            this.setCaretPosition(getDocument().getLength());
            //解决setCaretPosition空指针异常
            JScrollBar verticalScrollBar = jsp.getVerticalScrollBar();
            if(verticalScrollBar != null){
                verticalScrollBar.setValue(verticalScrollBar.getMaximum());
            }
        }catch (Exception e){}

    }

    private synchronized void insert(String text) {
        insert(text, defaultFontColor, null);
    }

    private synchronized void insert(String text, Color color) {
        insert(text, color,null);
    }

    private  synchronized void insert(String text, Color color, Color backColor) {
        Integer length = null;
        try { // 插入文本
            if (color != null) {
                StyleConstants.setForeground(attrSet, color);
            }
            if (backColor != null) {
                StyleConstants.setBackground(attrSet, backColor);
            }
            Document docs =  getDocument();//获得文本对象
            length = docs.getLength();
            docs.insertString(length, text, attrSet);
            entries = getLineCount();
        } catch (BadLocationException e) {
            log.error(length+" 插入日志Panel失败:"+text,e);
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

    protected int getRowHeight() {
        if (rowHeight == 0) {
            Font font = getFont();
            FontMetrics metrics = getFontMetrics(font);
            rowHeight = metrics.getHeight();
        }
        return rowHeight;
    }


}
