package cn.org.tpeach.nosql.view.component;

import cn.org.tpeach.nosql.tools.SwingTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ROptionPane extends JOptionPane {



    public static void showMessageDialog(Component parentComponent,
                                         Object message, String title, int messageType)
            throws HeadlessException {
        showMessageDialog(parentComponent, message, title, messageType, null);
    }
    public static void showMessageDialog(Component parentComponent,
                                         Object message, String title, int messageType, Icon icon)
            throws HeadlessException {
        showOptionDialog(parentComponent, message, title, DEFAULT_OPTION,
                messageType, icon, null, null);
    }

    public static int showOptionDialog(Component parentComponent,
                                       Object message, String title, int optionType, int messageType,
                                       Icon icon, Object[] options, Object initialValue)
            throws HeadlessException {
        JOptionPane             pane = new JOptionPane("<html><div style='width:"+((int)(SwingTools.getScreenSize().width * 0.18))+"px;'>"
                + message + "</div></html>", messageType,
                optionType, icon,
                options, initialValue);

        pane.setInitialValue(initialValue);
        pane.setComponentOrientation(((parentComponent == null) ?
                getRootFrame() : parentComponent).getComponentOrientation());
        SwingTools.addMouseClickedListener(pane,e->{
            SwingTools.copyMenuByValue(e,pane,()->message.toString(),t->{});
        });


        int style = styleFromMessageType(messageType);
        Class<? extends JOptionPane> clazz = pane.getClass();
        JDialog dialog = null;
        try {
            Method m=clazz.getDeclaredMethod("createDialog", new Class[]{Component.class,String.class,int.class});
            m.setAccessible(true);
            dialog = (JDialog) m.invoke(pane, parentComponent, title, style);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
//        JDialog dialog = pane.createDialog(parentComponent, title, style);

        pane.selectInitialValue();
        dialog.show();
        dialog.dispose();

        Object        selectedValue = pane.getValue();

        if(selectedValue == null)
            return CLOSED_OPTION;
        if(options == null) {
            if(selectedValue instanceof Integer)
                return ((Integer)selectedValue).intValue();
            return CLOSED_OPTION;
        }
        for(int counter = 0, maxCounter = options.length;
            counter < maxCounter; counter++) {
            if(options[counter].equals(selectedValue))
                return counter;
        }
        return CLOSED_OPTION;
    }

    private static int styleFromMessageType(int messageType) {
        switch (messageType) {
            case ERROR_MESSAGE:
                return JRootPane.ERROR_DIALOG;
            case QUESTION_MESSAGE:
                return JRootPane.QUESTION_DIALOG;
            case WARNING_MESSAGE:
                return JRootPane.WARNING_DIALOG;
            case INFORMATION_MESSAGE:
                return JRootPane.INFORMATION_DIALOG;
            case PLAIN_MESSAGE:
            default:
                return JRootPane.PLAIN_DIALOG;
        }
    }





}
