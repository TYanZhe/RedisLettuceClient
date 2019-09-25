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
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class MagnifyTextDialog extends BaseDialog<String,String>{

    private static final class SingleHolder{
        private static MagnifyTextDialog instance = new MagnifyTextDialog(null,null);
    }


    public static MagnifyTextDialog getInstance() {
        final MagnifyTextDialog instance = SingleHolder.instance;
        instance.reset();
        return instance;
    }
    @Setter
    @Getter
    private String text;
    private boolean needBtn;

    private RTextArea onlyReadArea;

    private MagnifyTextDialog(JFrame parent, String s) {
        super(parent, s);
    }



    @Override
    public void initDialog(String s) {
        Dimension screenSize = SwingTools.getScreenSize();
        int width = (int) (screenSize.width * 0.35);
        int minHeight = this.getMinHeight();
        int minWidth = this.getMinWidth();
        this.setMinHeight(width*minHeight/minWidth);
        this.setMinWidth(width);
        onlyReadArea = new RTextArea( );
        onlyReadArea.setLineWrap(true);
//        onlyReadArea.setEditable(false);
        SwingTools.addTextCopyMenu(onlyReadArea);
    }



    @Override
    public boolean isNeedBtn() {
        return needBtn;
    }



    @Override
    protected void contextUiImpl(JPanel contextPanel, JPanel btnPanel) {
        this.setBtnPanelHeight(66);
        btnPanel.setBorder(BorderFactory.createEmptyBorder());
        btnPanel.setPreferredSize(new Dimension(this.getWidth(), btnPanelHeight));
        contextPanel.setBorder(BorderFactory.createEmptyBorder(25,25,25,25));
        contextPanel.setLayout(new BorderLayout());
        contextPanel.add(onlyReadArea.getJScrollPane(),BorderLayout.CENTER);
        contextPanel.updateUI();
    }
    @Override
    protected void setMiddlePanel(JPanel middlePanel){
//        middlePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    private void reset(){
        this.consumer = null;
        this.text = null;
        onlyReadArea.setEditable(true);
        this.needBtn = true;

    }
    @Override
    protected void submit(ActionEvent e) {
        if(this.consumer != null){
            this.consumer.accept(onlyReadArea.getText());
        }
        this.close();
    }
    @Override
    public synchronized void open(){
        open(null);
    }

    @Override
    public void getResult(Consumer<String> consumer) {

    }

    public synchronized void open(Consumer<String> result) {
        this.consumer = result;
        onlyReadArea.append(text);
        onlyReadArea.paintImmediately(onlyReadArea.getBounds());
        super.open();
    }

    public void setEditable(boolean b){
        onlyReadArea.setEditable(b);
        needBtn = b;


    }

    @Override
    public void close() {
        onlyReadArea.setText(null);
        super.close();
    }
}
