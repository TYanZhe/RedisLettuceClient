package cn.org.tpeach.nosql.view.component;

import cn.org.tpeach.nosql.view.ui.EclipseTabbedPaneUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

//import nl.boplicity.bt.orderbuilder.workbench.ui.Editor;
//import nl.boplicity.swing.plaf.basic.EclipseTabbedPaneUI;


/**
 * Eclipse style tabbedpane with elipse-look tabs and popup menu on tabs.
 * 
 * FIXME: save dirty tabs first
 * 
 * @author kees
 * @date 9-feb-2006
 *
 */
public class EclipseTabbedPane extends JTabbedPane {


    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(800,700);
        frame.setLayout(new BorderLayout());
        Container contentPane = frame.getContentPane();
        JPanel jPanel = new JPanel();
        JTabbedPane jTabbedPane = new EclipseTabbedPane();
        jTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        for (int i = 0; i < 20; i++) {
            JPanel jPanel1 = new JPanel();
            jPanel1.setPreferredSize(new Dimension(600,500));
            jTabbedPane.addTab("Test"+i,jPanel1);
        }

        jPanel.add(jTabbedPane,BorderLayout.CENTER);
        contentPane.add(jPanel,BorderLayout.CENTER);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static final long serialVersionUID = 1176020466013529902L;
    private JPopupMenu popupMenu;
    private Integer selectedTabIndex;

    public EclipseTabbedPane() {
        super();
        setUI(new EclipseTabbedPaneUI());

        createPopupMenu();

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                EclipseTabbedPane.this.mouseReleased(mouseEvent);
            }

        });
    }

    private void mouseReleased(MouseEvent mouseEvent) {

        if (mouseEvent.isPopupTrigger()) {
            selectedTabIndex = indexAtLocation(mouseEvent.getX(), mouseEvent.getY());

            // Only show for top row
            if (getTabPlacement() == JTabbedPane.TOP) {
                popupMenu.show(this, mouseEvent.getX(), mouseEvent.getY());
            }
        }

    }

    private JPopupMenu createPopupMenu() {

        popupMenu = new JPopupMenu();

        popupMenu.add(new CloseAction("Close"));
        popupMenu.add(new CloseOthersAction("Close Others"));
        popupMenu.add(new CloseAllAction("Close All"));

        return popupMenu;
    }

    private class CloseAction extends AbstractAction {

        private static final long serialVersionUID = -2625928077474199856L;

        public CloseAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            closeTab(selectedTabIndex);
        }
    }

    private class CloseOthersAction extends AbstractAction {

        private static final long serialVersionUID = -2625928077474199856L;

        public CloseOthersAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {

            // First remove higher indexes 
            int tabCount = getTabCount();

            if (selectedTabIndex < tabCount - 1) {
                for (int i = selectedTabIndex + 1; i < tabCount; i++) {
                    closeTab(selectedTabIndex + 1);
                }
            }

            if (selectedTabIndex > 0) {
                for (int i = 0; i < selectedTabIndex; i++) {
                    closeTab(0);
                }
            }
        }
    }

    private void closeTab(int i) {
    }

    private class CloseAllAction extends AbstractAction {

        private static final long serialVersionUID = -2625928077474199856L;

        public CloseAllAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {

            int tabCount = getTabCount();

            for (int i = 0; i < tabCount; i++) {
                closeTab(0);
            }
        }
    }
}