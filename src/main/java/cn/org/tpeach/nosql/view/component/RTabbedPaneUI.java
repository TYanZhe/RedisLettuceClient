package cn.org.tpeach.nosql.view.component;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

/**
 * Created by SongFei on 2017/11/8.
 */
public class RTabbedPaneUI extends BasicTabbedPaneUI {

    // 不知道是个什么东西，绘制pane时，计算坐标和宽高的方法需要用到，直接从父类拷贝过来的
    private boolean tabsOverlapBorder = UIManager.getBoolean("TabbedPane.tabsOverlapBorder");

    // 边框和背景的颜色
//    private Color SELECT_COLOR = new Color(57, 181, 215);
    private Color SELECT_COLOR = new Color(254,254,254);

    // 绘制整个选项卡区域
    @Override
    protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
        // 如果没有特殊要求，就使用默认的绘制方案
        super.paintTabArea(g, tabPlacement, selectedIndex);
    }

    // 绘制tab页签的边框
    @Override
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        g.setColor(SELECT_COLOR);
        switch (tabPlacement) {
            case LEFT:
                g.drawLine(x, y, x, y + h - 1);
                g.drawLine(x, y, x + w - 1, y);
                g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
                break;
            case RIGHT:
                g.drawLine(x, y, x + w - 1, y);
                g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
                g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
                break;
            case BOTTOM:
                g.drawLine(x, y, x, y + h - 1);
                g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
                g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
                break;
            case TOP:
            default:
                g.drawLine(x, y, x, y + h - 1);
                g.drawLine(x, y, x + w - 1, y);
                g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
        }
    }

    // 绘制选中的选项卡背景色
    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gradient;
        switch (tabPlacement) {
            case LEFT:
                if (isSelected) {
                    gradient = new GradientPaint(x + 1, y, SELECT_COLOR, x + w, y, Color.WHITE, true);
                } else {
                    gradient = new GradientPaint(x + 1, y, Color.LIGHT_GRAY, x + w, y, Color.WHITE, true);
                }
                g2d.setPaint(gradient);
                g.fillRect(x + 1, y + 1, w - 1, h - 2);
                break;
            case RIGHT:
                if (isSelected) {
                    gradient = new GradientPaint(x + w, y, SELECT_COLOR, x + 1, y, Color.WHITE, true);
                } else {
                    gradient = new GradientPaint(x + w, y, Color.LIGHT_GRAY, x + 1, y, Color.WHITE, true);
                }
                g2d.setPaint(gradient);
                g.fillRect(x, y + 1, w - 1, h - 2);
                break;
            case BOTTOM:
                if (isSelected) {
                    gradient = new GradientPaint(x + 1, y + h, SELECT_COLOR, x + 1, y, Color.WHITE, true);
                } else {
                    gradient = new GradientPaint(x + 1, y + h, Color.LIGHT_GRAY, x + 1, y, Color.WHITE, true);
                }
                g2d.setPaint(gradient);
                g.fillRect(x + 1, y, w - 2, h - 1);
                break;
            case TOP:
            default:
                if (isSelected) {
                    gradient = new GradientPaint(x + 1, y, SELECT_COLOR, x + 1, y + h, Color.WHITE, true);
                } else {
                    gradient = new GradientPaint(x + 1, y, Color.LIGHT_GRAY, x + 1, y + h, Color.WHITE, true);
                }
                g2d.setPaint(gradient);
                g2d.fillRect(x + 1, y + 1, w - 2, h - 1);
        }

    }

    // 绘制TabbedPane容器的四周边框样式
    @Override
    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
        // 如果不想要边框，直接重写一个空方法
        // super.paintContentBorder(g, tabPlacement, selectedIndex);

        // 这些计算坐标和宽高的代码，直接从父类拷贝出来重用即可
        int width = tabPane.getWidth();
        int height = tabPane.getHeight();
        Insets insets = tabPane.getInsets();
        Insets tabAreaInsets = getTabAreaInsets(tabPlacement);

        int x = insets.left;
        int y = insets.top;
        int w = width - insets.right - insets.left;
        int h = height - insets.top - insets.bottom;

        switch (tabPlacement) {
            case LEFT:
                x += calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
                if (tabsOverlapBorder) {
                    x -= tabAreaInsets.right;
                }
                w -= (x - insets.left);
                break;
            case RIGHT:
                w -= calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
                if (tabsOverlapBorder) {
                    w += tabAreaInsets.left;
                }
                break;
            case BOTTOM:
                h -= calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                if (tabsOverlapBorder) {
                    h += tabAreaInsets.top;
                }
                break;
            case TOP:
            default:
                y += calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                if (tabsOverlapBorder) {
                    y -= tabAreaInsets.bottom;
                }
                h -= (y - insets.top);
        }

        // 四个边框的绘制方法，都自己重写一遍，方便控制颜色和一些特效
        paintContentBorderTopEdge(g, tabPlacement, selectedIndex, x, y, w, h);
        paintContentBorderLeftEdge(g, tabPlacement, selectedIndex, x, y, w, h);
        paintContentBorderBottomEdge(g, tabPlacement, selectedIndex, x, y, w, h);
        paintContentBorderRightEdge(g, tabPlacement, selectedIndex, x, y, w, h);
    }

    // 绘制容器左侧边框，不是tab，是pane
    // 上下左右，都可以重写方法来绘制，相应的方法：paintContentBorder*Edge()，由paintContentBorder()方法统一调用
    @Override
    protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        g.setColor(SELECT_COLOR);
        g.drawLine(x, y, x, y + h - 2);
    }

    @Override
    protected void paintContentBorderTopEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        g.setColor(SELECT_COLOR);
        g.drawLine(x, y, x + w - 2, y);
    }

    // 右边和下边，这两个需要注意，x + w 和 y + h 已达到边框临界，必须减掉几个数值，否则边框会显示不出来
    @Override
    protected void paintContentBorderRightEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        g.setColor(SELECT_COLOR);
        g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
    }

    @Override
    protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        g.setColor(SELECT_COLOR);
        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
    }

    // 绘制选中某个Tab后，获得焦点的样式
    @Override
    protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
        // 重写空方法，主要用来去掉虚线
    }

    // 计算tab页签的宽度
    @Override
    protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
        // 可根据placement来做不同的调整
        return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + 3;
    }

    // 计算tab页签的高度
    @Override
    protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
        // 可根据placement来做不同的调整
        return super.calculateTabHeight(tabPlacement, tabIndex, fontHeight)+3;
    }

}