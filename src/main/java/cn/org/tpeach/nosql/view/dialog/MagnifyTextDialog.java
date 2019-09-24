package cn.org.tpeach.nosql.view.dialog;

import cn.org.tpeach.nosql.constant.I18nKey;
import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.framework.LarkFrame;
import cn.org.tpeach.nosql.tools.StringUtils;
import cn.org.tpeach.nosql.tools.SwingTools;
import cn.org.tpeach.nosql.view.common.ServiceManager;
import cn.org.tpeach.nosql.view.component.EasyJSP;
import cn.org.tpeach.nosql.view.component.OnlyReadArea;
import cn.org.tpeach.nosql.view.component.RTextArea;
import cn.org.tpeach.nosql.view.menu.JRedisPopupMenu;
import cn.org.tpeach.nosql.view.menu.MenuManager;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MagnifyTextDialog extends BaseDialog<String,String>{

    private static final class SingleHolder{
        private static MagnifyTextDialog instance = new MagnifyTextDialog(null,null);
    }

    public static MagnifyTextDialog getInstance() {
        return MagnifyTextDialog.SingleHolder.instance;
    }
    @Setter
    @Getter
    private String text;

    private RTextArea onlyReadArea;

    private MagnifyTextDialog(JFrame parent, String s) {
        super(parent, s);
    }

    @Override
    public void initDialog(String s) {
        Dimension screenSize = SwingTools.getScreenSize();
        int width = (int) (screenSize.width * 0.4);
        int minHeight = this.getMinHeight();
        int minWidth = this.getMinWidth();
        this.setMinHeight(width*minHeight/minWidth);
        this.setMinWidth(width);
        onlyReadArea = new RTextArea(20,20);
        onlyReadArea.setLineWrap(true);
        onlyReadArea.setEditable(false);
        SwingTools.addTextCopyMenu(onlyReadArea);
    }



    @Override
    public boolean isNeedBtn() {
        return false;
    }



    @Override
    protected void contextUiImpl(JPanel contextPanel, JPanel btnPanel) {
        contextPanel.setBorder(BorderFactory.createEmptyBorder(25,25,25,25));
        contextPanel.setLayout(new BorderLayout());
        contextPanel.add(onlyReadArea.getJScrollPane(),BorderLayout.CENTER);
        contextPanel.updateUI();
    }

    @Override
    public void open() {
        onlyReadArea.append(text);
        onlyReadArea.paintImmediately(onlyReadArea.getBounds());
        super.open();
    }

    @Override
    public void close() {
        onlyReadArea.setText(null);
        super.close();
    }
}
