package cn.org.tpeach.nosql.view.component;

import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.tools.StringUtils;
import sun.font.FontDesignMetrics;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public enum PlaceholderCommon {
    INSTANCE;
    public static PlaceholderCommon getInstance() {
        return PlaceholderCommon.INSTANCE;
    }


    public void init(JTextComponent textField) {
//        textField.setMinimumSize(new Dimension(0,25));
//        textField.setPreferredSize(new Dimension(0,25));
//        textField.setSelectionColor(new Color(0,120,215));
        textField.setSelectedTextColor(Color.WHITE);
         setBorder(textField,PublicConstant.RColor.defalutInputColor);
        UndoManager undoManager = new UndoManager();
        textField.getDocument().addUndoableEditListener(undoManager);
        textField.addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
                setBorder(textField,PublicConstant.RColor.defalutInputColor);
            }

            @Override
            public void focusGained(FocusEvent e) {
                setBorder(textField,PublicConstant.RColor.selectInputColor);
            }
        });
        textField.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(KeyEvent arg0) {
            }

            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_Z) {
                    if (undoManager.canUndo()) {
                        undoManager.undo();
                    }
                }
                if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_Y) {
                    if (undoManager.canRedo()) {
                        undoManager.redo();
                    }
                }
            }

            @Override
            public void keyTyped(KeyEvent arg0) {
            }
        });


    }

    public void setBorder(JTextComponent textField,Color color){
        textField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(PublicConstant.RColor.defalutInputColor),BorderFactory.createEmptyBorder(0,5,0,5)));

    }

    public void paintComponent(JTextComponent jTextComponent, final Graphics pG, String placeholder) {
        if (StringUtils.isBlank(placeholder) || StringUtils.isNotBlank(jTextComponent.getText())) {
            return;
        }
//        //绘制提示语

        final Graphics2D g = (Graphics2D) pG;
        Font font = jTextComponent.getFont();
        g.setFont(font);
        g.setColor(jTextComponent.getDisabledTextColor());
//        //消除文字锯齿
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//        //消除画图锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(jTextComponent.getDisabledTextColor());
        int maxAscent = FontDesignMetrics.getMetrics(font).getHeight();
//        int y = ( this.getHeight() - maxAscent)/2 + maxAscent + getInsets().top;
        int y =   maxAscent + jTextComponent.getInsets().top;
        g.drawString(placeholder, jTextComponent.getInsets().left+2, y);
    }

    public void resetHeightSize(JTextComponent textField,int height) {
        if(height < 25) {
            return ;
        }
        textField.setMinimumSize(new Dimension(0,height));
        textField.setPreferredSize(new Dimension(0,height));
    }
}
