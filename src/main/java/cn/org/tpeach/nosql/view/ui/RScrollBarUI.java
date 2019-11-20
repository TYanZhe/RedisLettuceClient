package cn.org.tpeach.nosql.view.ui;

import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.view.component.EasyJSP;
import cn.org.tpeach.nosql.view.component.OnlyReadArea;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class LogFrame extends JFrame {

    OnlyReadArea textArea = new OnlyReadArea(50, 100, 1000);


    public LogFrame() throws HeadlessException {
        JScrollPane scrollPane = new EasyJSP(textArea);

//        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scrollPane);
        this.pack();
        this.setSize(this.getWidth(), this.getHeight() - 50);
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }


    public void appendText(String text) {
        textArea.println(text);
    }
}
class EnterArrowButton extends BasicArrowButton{
    @Getter
    @Setter
    private boolean enter;

    public EnterArrowButton(int direction) {
        super(direction);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setEnter(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setEnter(false);
            }
        });
    }
}
// BasicScrollBarUI
public class RScrollBarUI extends javax.swing.plaf.metal.MetalScrollBarUI {
    //    private Color frameColor = new Color(145, 105, 55);


    @Override
    public Dimension getPreferredSize(JComponent c) {
        return new Dimension(16, 16);
    }

    // 重绘滚动条的滑块
    @Override
    public void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        super.paintThumb(g, c, thumbBounds);
        int tw = thumbBounds.width;
        int th = thumbBounds.height;
        // 重定图形上下文的原点，这句一定要写，不然会出现拖动滑块时滑块不动的现象
        g.translate(thumbBounds.x, thumbBounds.y);

        Graphics2D g2 = (Graphics2D) g;
        GradientPaint gp = null;
        if (this.scrollbar.getOrientation() == JScrollBar.VERTICAL) {
//            gp = new GradientPaint(0, 0, new Color(242, 222, 198), getWidth(), 0, new Color(207, 190, 164));
            gp = new GradientPaint(0, 0, new Color(205,205,205), tw, 0, new Color(205,205,205));
        }
        if (this.scrollbar.getOrientation() == JScrollBar.HORIZONTAL) {
            gp = new GradientPaint(0, 0, new Color(205,205,205), 0, th, new Color(205,205,205));
        }
        g2.setPaint(gp);

        g2.fillRoundRect(0, 0, tw , th , 0, 0);
//        g2.setColor(frameColor);
//        g2.drawRoundRect(0, 0, tw - 1, th - 1, 5, 5);
    }

    // 重绘滑块的滑动区域背景
    @Override
    public void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2 = (Graphics2D) g;
        GradientPaint gp = null;
        if (this.scrollbar.getOrientation() == JScrollBar.VERTICAL) {
//            gp = new GradientPaint(0, 0, new Color(198, 178, 151), trackBounds.width, 0, new Color(234, 214, 190));
            gp = new GradientPaint(0, 0, new Color(240,240,240), trackBounds.width, 0, new Color(240,240,240));
        }
        if (this.scrollbar.getOrientation() == JScrollBar.HORIZONTAL) {
//            gp = new GradientPaint(0, 0, new Color(198, 178, 151), 0, trackBounds.height, new Color(234, 214, 190));
            gp = new GradientPaint(0, 0, new Color(240,240,240), 0, trackBounds.height, new Color(240,240,240));
        }
        g2.setPaint(gp);
        g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
//        g2.setColor(new Color(175, 155, 95));
//        g2.drawRect(trackBounds.x, trackBounds.y, trackBounds.width - 1, trackBounds.height - 1);
        if (trackHighlight == BasicScrollBarUI.DECREASE_HIGHLIGHT){
            this.paintDecreaseHighlight(g);
        }
        if (trackHighlight == BasicScrollBarUI.INCREASE_HIGHLIGHT){
            this.paintIncreaseHighlight(g);
        }
    }

    // 重绘当鼠标点击滑动到向上或向左按钮之间的区域
    @Override
    protected void paintDecreaseHighlight(Graphics g) {
//        g.setColor(Color.green);
//        int x = this.getTrackBounds().x;
//        int y = this.getTrackBounds().y;
//        int w = 0, h = 0;
//        if (this.scrollbar.getOrientation() == JScrollBar.VERTICAL) {
//            w = this.getThumbBounds().width;
//            h = this.getThumbBounds().y - y;
//
//        }
//        if (this.scrollbar.getOrientation() == JScrollBar.HORIZONTAL) {
//            w = this.getThumbBounds().x - x;
//            h = this.getThumbBounds().height;
//        }
//        g.fillRect(x, y, w, h);
    }

    // 重绘当鼠标点击滑块至向下或向右按钮之间的区域
    @Override
    protected void paintIncreaseHighlight(Graphics g) {
//        Insets insets = scrollbar.getInsets();
//        g.setColor(Color.blue);
//        int x = this.getThumbBounds().x;
//        int y = this.getThumbBounds().y;
//        int w = this.getTrackBounds().width;
//        int h = this.getTrackBounds().height;
//        g.fillRect(x, y, w, h);
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return new EnterArrowButton(orientation) {
            // 重绘按钮的三角标记
            @Override
            public void paintTriangle(Graphics g, int x, int y, int size, int direction, boolean isEnabled) {
                Graphics2D g2 = (Graphics2D) g;
                //        //消除文字锯齿
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//        //消除画图锯齿
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = null;
                Image arrowImg = null;
                switch (this.getDirection()) {
                    case BasicArrowButton.SOUTH:
//                        gp = new GradientPaint(0, 0, new Color(242, 222, 198), getWidth(), 0, new Color(207, 190, 164));
                        if(!isEnter()){
                            gp = new GradientPaint(0, 0, new Color(240,240,240), getWidth(), 0, new Color(240,240,240));
                        }else{
                            gp = new GradientPaint(0, 0, new Color(218, 218, 218), getWidth(), 0, new Color(218, 218, 218));
                        }

                        arrowImg = PublicConstant.Image.getImageIcon(PublicConstant.Image.arrow_down_scroll).getImage();
                        break;
                    case BasicArrowButton.EAST:
//                        gp = new GradientPaint(0, 0, new Color(242, 222, 198), 0, getHeight(), new Color(207, 190, 164));
                        if(isEnter()){
                            gp = new GradientPaint(0, 0, new Color(218, 218, 218), 0, getHeight(), new Color(218, 218, 218));
                        }else{
                            gp = new GradientPaint(0, 0, new Color(240,240,240), 0, getHeight(), new Color(240,240,240));
                        }
                    arrowImg = PublicConstant.Image.getImageIcon(PublicConstant.Image.arrow_right_scroll).getImage();
                    break;
                    default:
                        break;
                }
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
//                g2.setColor(frameColor);
//                g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2.drawImage(arrowImg, (getWidth() - 2) / 2 - 5, (getHeight() - 2) / 2 - 5,  null);
            }
        };
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        final BasicArrowButton basicArrowButton = new EnterArrowButton(orientation) {
            @Override
            public void paintTriangle(Graphics g, int x, int y, int size, int direction, boolean isEnabled) {
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = null;
                Image arrowImg = null;
                switch (this.getDirection()) {
                    case BasicArrowButton.NORTH:
//                        gp = new GradientPaint(0, 0, new Color(242, 222, 198), getWidth(), 0, new Color(207, 190, 164));
                        if(!isEnter()){
                            gp = new GradientPaint(0, 0, new Color(240, 240, 240), getWidth(), 0, new Color(240, 240, 240));
                        }else{
                            gp = new GradientPaint(0, 0, new Color(218, 218, 218), getWidth(), 0, new Color(218, 218, 218));
                        }

                        arrowImg = PublicConstant.Image.getImageIcon(PublicConstant.Image.arrow_up_scroll).getImage();
                        break;
                    case BasicArrowButton.WEST:

//                        gp = new GradientPaint(0, 0, new Color(242, 222, 198), 0, getHeight(), new Color(207, 190, 164));

                        if(isEnter()){
                            gp = new GradientPaint(0, 0, new Color(218, 218, 218), 0, getHeight(), new Color(218, 218, 218));
                        }else{
                            gp = new GradientPaint(0, 0, new Color(240, 240, 240), 0, getHeight(), new Color(240, 240, 240));
                        }
                        arrowImg = PublicConstant.Image.getImageIcon(PublicConstant.Image.arrow_left_scroll).getImage();
                        break;
                    default:
                        break;
                }

                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
//                g2.setColor(frameColor);
//                g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2.drawImage(arrowImg, (getWidth() - 2) / 2 - 5, (getHeight() - 2) / 2 - 5, null);
            }
        };


        return basicArrowButton;
    }
}