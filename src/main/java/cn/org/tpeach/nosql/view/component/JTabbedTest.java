package cn.org.tpeach.nosql.view.component;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class JTabbedTest {

    private static JTabbedPane jtp;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame f = new JFrame();
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                jtp = new JTabbedPane();
                jtp.setPreferredSize(new Dimension(320, 200));
                jtp.addTab("Reds", new ColorPanel(0, Color.RED));
                jtp.setBackgroundAt(0, Color.RED);
                jtp.addTab("Greens", new ColorPanel(1, Color.GREEN));
                jtp.setBackgroundAt(1, Color.GREEN);
                jtp.addTab("Blues", new ColorPanel(2, Color.BLUE));
                jtp.setBackgroundAt(2, Color.BLUE);

                f.add(jtp, BorderLayout.CENTER);
                f.pack();
                f.setVisible(true);
            }
        });
    }

    private static class ColorPanel extends JPanel implements ActionListener {

        private final Random rnd = new Random();
        private final Timer timer = new Timer(1000, this);
        private Color color;
        private Color original;
        private int mask;
        private JLabel label = new JLabel("Stackoverflow!");
        private int index;

        public ColorPanel(int index, Color color) {
            super(true);
            this.color = color;
            this.original = color;
            this.mask = color.getRGB();
            this.index = index;
            this.setBackground(color);
            label.setForeground(color);
            this.add(label);
            timer.start();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            color = new Color(rnd.nextInt() & mask);
            this.setBackground(color);
            jtp.setBackgroundAt(index, original);
        }
    }
}