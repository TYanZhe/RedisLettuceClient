package cn.org.tpeach.nosql.view.dialog;

import cn.org.tpeach.nosql.tools.SwingTools;
import cn.org.tpeach.nosql.view.component.RTextArea;
import lombok.Setter;

import java.awt.*;
import java.awt.event.ActionEvent;

public class LoadingAssistDialog extends TextAreaDialog {
    @Setter
    private boolean autoSubmit = true;
    private static final class SingleHolder{
        private static LoadingAssistDialog instance = new LoadingAssistDialog();
    }

    @Override
    public void after() {
        super.after();
    }

    public static LoadingAssistDialog getInstance(RTextArea textArea) {
        final LoadingAssistDialog instance = SingleHolder.instance;
        instance.setTextArea(textArea);
        instance.reset();
        return instance;

    }
    public LoadingAssistDialog() {
        super(null, null);
    }

    @Override
    protected void submit(ActionEvent e) {
        if(this.consumer != null){
            this.consumer.accept(getTextArea().getText());
        }
        int componentCount = contextPanel.getComponentCount();
        for (int i = componentCount -1; i >=0; i--) {
            contextPanel.remove(i);
        }
        this.setVisible(false);
        this.autoSubmit = true;
        SwingTools.swingWorkerExec(()->this.dispose());
    }
    @Override
    public void close() {
        this.submit((ActionEvent)null);
    }

    @Override
    protected void reset(){
        this.getTextArea().setText(null);
        contextPanel.add(this.getTextArea().getJScrollPane(), BorderLayout.CENTER);
        super.reset();

    }
    @Override
    public RTextArea getTextArea() {
        return super.getTextArea();
    }
    @Override
    public void visibleExec() {
        if(autoSubmit){
            this.submit((ActionEvent)null);
        }else{
            this.setVisible(true);
        }

    }
}
