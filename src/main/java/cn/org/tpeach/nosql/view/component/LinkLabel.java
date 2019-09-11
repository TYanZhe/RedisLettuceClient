package cn.org.tpeach.nosql.view.component;

import cn.org.tpeach.nosql.tools.StringUtils;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 
 * 超链接文本标签类
 *
 */
public class LinkLabel extends JLabel {
    private static final long serialVersionUID = 1L;

    private URL link = null;
    private Color preColor = null;
    @Setter
    private Consumer<MouseEvent> mouseClieckConsumer = e -> {
        try {
            Desktop.getDesktop().browse(link.toURI());
        } catch (IOException err) {
            err.printStackTrace();
        } catch (URISyntaxException err) {
            err.printStackTrace();
        }
    };

    private void showUnderlined(boolean isShow){
        Font font = this.getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE,isShow ?TextAttribute.UNDERLINE_ON:null);
        this.setFont(font.deriveFont(attributes));
    }

    public LinkLabel(String vText){
        this(vText,null);
    }
    public LinkLabel(String vText, String vLink) {
        super(   vText  );

        try {
            if(StringUtils.isNotBlank(vLink)){
                if (!vLink.startsWith("http://") && !vLink.startsWith("https://")){
                    vLink = "http://" + vLink;
                }
                this.link = new URL(vLink);
                this.setToolTipText(vLink);
            }else{
                this.setToolTipText(vText);
            }
        } catch (MalformedURLException err) {
            err.printStackTrace();
        }
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                LinkLabel.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                if (preColor != null){
                    LinkLabel.this.setForeground(preColor);
                }
                showUnderlined(false);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                LinkLabel.this.setCursor(Cursor
                        .getPredefinedCursor(Cursor.HAND_CURSOR));
                preColor = LinkLabel.this.getForeground();
                LinkLabel.this.setForeground(Color.BLUE);
                showUnderlined(true);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                mouseClieckConsumer.accept(e);
            }
        });
    }


}