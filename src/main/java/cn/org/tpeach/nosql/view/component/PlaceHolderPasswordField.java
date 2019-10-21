
package cn.org.tpeach.nosql.view.component;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.plaf.*;
import javax.accessibility.*;

import java.awt.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.*;
import java.util.Arrays;


public class PlaceHolderPasswordField extends JPasswordField {
    private static final long serialVersionUID = 3101518850256374312L;
    PlaceholderCommon placeholderCommon = PlaceholderCommon.getInstance();
    //提示信息
    @Getter
    @Setter
    private String placeholder;
    public PlaceHolderPasswordField() {
        placeholderCommon.init(this);
    }

    public PlaceHolderPasswordField(String text) {
        super(text);
        placeholderCommon.init(this);
    }

    public PlaceHolderPasswordField(int columns) {
        super(columns);
        placeholderCommon.init(this);
    }

    public PlaceHolderPasswordField(String text, int columns) {
        super(text, columns);
        placeholderCommon.init(this);
    }

    public PlaceHolderPasswordField(Document doc, String txt, int columns) {
        super(doc, txt, columns);
        placeholderCommon.init(this);
    }


    public void resetHeightSize(int height) {
        placeholderCommon.resetHeightSize(this,height);
    }


    @Override
    public Color getDisabledTextColor() {
        return Color.GRAY;
    }

    @Override
    protected void paintComponent(final Graphics pG) {
        super.paintComponent(pG);
        placeholderCommon.paintComponent(this,pG,placeholder);
    }
}
