/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.org.tpeach.nosql.view.component;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author smart
 */
public class BoxPanel extends JPanel {

    private int strutWidth = 10;

    public BoxPanel() {
           this.add(Box.createHorizontalStrut(strutWidth), false);
    }

    public BoxPanel(int strutWidth) {
        this.strutWidth = strutWidth;
    }

    @Override
    public Component add(Component comp) {
        return add(comp, true); 
    }

    private Component add(Component comp, boolean isSuper) {
        if(comp instanceof JComboBox || (comp instanceof JLabel && "View as:".equals(((JLabel) comp).getText()))){
            // 右对齐
             this.add(Box.createHorizontalGlue(), false);
        }
        Component comp1 = super.add(comp);
        if (isSuper) {
            this.add(Box.createHorizontalStrut(strutWidth), false);
        }
        return comp1;
    }

}
