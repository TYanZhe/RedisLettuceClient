package cn.org.tpeach.nosql.view.component;

import cn.org.tpeach.nosql.tools.StringUtils;

import javax.swing.*;

/**
 *
 */
public class PrefixTextLabel extends JLabel {
    private String prefix;

    public PrefixTextLabel(String text, Icon icon, int horizontalAlignment, String prefix) {
        super(text, icon, horizontalAlignment);
        this.prefix = prefix;
    }

    public PrefixTextLabel(String text, int horizontalAlignment, String prefix) {
        super(text, horizontalAlignment);
        this.prefix = prefix;
    }

    public PrefixTextLabel(String text, String prefix) {
        super(text);
        this.prefix = prefix;
    }

    public PrefixTextLabel(Icon image, int horizontalAlignment, String prefix) {
        super(image, horizontalAlignment);
        this.prefix = prefix;
        this.setText("");
    }

    public PrefixTextLabel(Icon image, String prefix) {
        super(image);
        this.prefix = prefix;
        this.setText("");
    }

    public PrefixTextLabel(String prefix) {
        this.prefix = prefix;
        this.setText("");
    }

    @Override
    public void setText(String text) {
        if(StringUtils.isNotBlank(prefix)){
            super.setText(prefix+text);
        }else{
            super.setText(text);
        }

    }
}
