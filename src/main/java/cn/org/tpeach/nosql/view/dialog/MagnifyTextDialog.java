package cn.org.tpeach.nosql.view.dialog;

import cn.org.tpeach.nosql.tools.SwingTools;
import cn.org.tpeach.nosql.view.component.RTextArea;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public class MagnifyTextDialog extends BaseDialog<String,String>{


    @Setter
    @Getter
    private String text;
    private boolean needBtn;
    private RTextArea textArea;

    protected RTextArea getTextArea() {
        return textArea;
    }
    protected void setTextArea(RTextArea textArea) {
        this.textArea = textArea;
    }
    public MagnifyTextDialog(JFrame parent, String s) {
        super(parent, s);
        textArea = new RTextArea( );
         reset();
    }



    @Override
    public void initDialog(String s) {
        Dimension screenSize = SwingTools.getScreenSize();
        int width = (int) (screenSize.width * 0.35);
        int minHeight = this.getMinHeight();
        int minWidth = this.getMinWidth();
        this.setMinHeight(width*minHeight/minWidth);
        this.setMinWidth(width);
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
        contextPanel.add(textArea.getJScrollPane(),BorderLayout.CENTER);
        contextPanel.updateUI();
    }
    @Override
    protected void setMiddlePanel(JPanel middlePanel){
//        middlePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    protected void reset(){

        textArea.setLineWrap(true);
        SwingTools.addTextCopyMenu(textArea);
        this.consumer = null;
        this.text = null;
        textArea.setEditable(true);
        this.needBtn = true;

    }
    @Override
    protected void submit(ActionEvent e) {
        if(this.consumer != null){
            this.consumer.accept(textArea.getText());
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
        textArea.append(text);
        textArea.paintImmediately(textArea.getBounds());
        super.open();
    }

    public void setEditable(boolean b){
        textArea.setEditable(b);
        needBtn = b;


    }

    @Override
    public void close() {
        textArea.setText(null);
        super.close();
        this.setVisible(false);
        SwingTools.swingWorkerExec(()->this.dispose());
    }
}
