package cn.org.tpeach.nosql.view.component;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class OnlyReadArea extends JTextArea {
    /**
	 * 是否限制最多显示条数，超过删除，显示最新
	 */
    @Getter
    @Setter
    private boolean limit = true;
	private static final long serialVersionUID = -7569933228127917215L;
	private final PrintStream printStream = new PrintStream(this.new AreaOutputStream());
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
     * 已经在JTextArea上显示的条数
     */
    private int entries = 0;
    public OnlyReadArea() {
        super();
        init();

    }

    public OnlyReadArea(int rows, int columns) {
        super(rows, columns);
        init();

    }

    public OnlyReadArea(int maxEntries) {
        super();
        this.maxEntries = maxEntries;
        init();
    }

    public OnlyReadArea(String text, int maxEntries) {
        super(text);
        this.maxEntries = maxEntries;
        init();
    }

    public OnlyReadArea(int rows, int columns, int maxEntries) {
        super(rows, columns);
        this.maxEntries = maxEntries;
        init();
    }



    public void init(){
        candy = new Color(230, 230, 255);
        setOpaque(false);
        this.setFont(new Font("宋体",Font.PLAIN,14));
        setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        setForeground(Color.GREEN.darker().darker().darker());
        this.addMouseListener(new MouseAdapter()   {
            @Override
            public void mouseEntered(MouseEvent mouseEvent)   {
                //鼠标进入Text区后变为文本输入指针
                OnlyReadArea.this.setCursor(new   Cursor(Cursor.TEXT_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent  mouseEvent)   {
                //鼠标离开Text区后恢复默认形态
                OnlyReadArea.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        this.getCaret().addChangeListener(new ChangeListener()   {
            @Override
            public void stateChanged(ChangeEvent e)   {
                //使Text区的文本光标显示
                OnlyReadArea.this.getCaret().setVisible(true);
            }
        });
    }

    @Override
    public final boolean isEditable() {
        return false;
    }

    public  void println(int x){
        this.printStream.println(x);
    }
    public void println(char x){
        Document document = autoClear();
        this.printStream.println(x);
        this.setCaretPosition(document.getLength());
    }
    public void println(long x){
        Document document = autoClear();
        this.printStream.println(x);
        this.setCaretPosition(document.getLength());
    }
    public void println(float x){
        Document document = autoClear();
        this.printStream.println(x);
        this.setCaretPosition(document.getLength());
    }
    public void println(double x){
        Document document = autoClear();
        this.printStream.println(x);
        this.setCaretPosition(document.getLength());
    }
    public void println(char[] x){
        Document document = autoClear();
        this.printStream.println(x);
        this.setCaretPosition(document.getLength());
    }
    public void println(boolean x){
        Document document = autoClear();
        this.printStream.println(x);
        this.setCaretPosition(document.getLength());
    }
    public void println(String x){
        Document document = autoClear();
        this.printStream.println(x);
        this.setCaretPosition(document.getLength());
    }

    private Document autoClear(){

        Document doc = null;
        try {
            doc = this.getDocument();
            if(limit){
                if (entries >= maxEntries) {
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
        r.height = getRowHeight();

        g.setColor(candy);
        for(int heightIncrement = 2 * getRowHeight(); r.y < height; r.y += heightIncrement) {
            g.fillRect(r.x, r.y, r.width, r.height);
        }
        g.setColor(old);

        super.paintComponent(g);
    }

    private class AreaOutputStream extends OutputStream {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        @Override
        public void write(int b) {
            out.write(b);
            if ('\n' == (char) b) {
                append(out.toString());
                out.reset();
            }
        }
    }
}