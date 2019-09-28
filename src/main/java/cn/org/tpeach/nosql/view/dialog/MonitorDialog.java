package cn.org.tpeach.nosql.view.dialog;

import cn.org.tpeach.nosql.framework.LarkFrame;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.concurrent.TimeUnit;

/**
 * @author tyz
 * @Title: MonitorDialog
 * @ProjectName redisLark-Github
 * @Description: TODO
 * @date 2019-09-28 11:26
 * @since 1.0.0
 */
public class MonitorDialog extends JDialog {
    @Getter
    private int initWidth = 300;
    @Getter
    private int initHeight = 200;
    @Getter
    @Setter
    private boolean outCLose;
    public static CategoryDataset createDataset() //创建柱状图数据集
    {
        DefaultCategoryDataset dataset=new DefaultCategoryDataset();
        dataset.setValue(10,"db0","db0");
        dataset.setValue(20,"db1","db1");
        dataset.setValue(40,"db2","db2");
        dataset.setValue(15,"db3","db3");
        return dataset;
    }
    public static JFreeChart createChart(CategoryDataset dataset) //用数据集创建一个图表
    {
        // 创建主题样式
        StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
// 设置标题字体
        standardChartTheme.setExtraLargeFont(new Font("宋体", Font.BOLD, 20));
// 设置图例的字体
        standardChartTheme.setRegularFont(new Font("宋体", Font.PLAIN, 15));
// 设置轴向的字体
        standardChartTheme.setLargeFont(new Font("宋体", Font.PLAIN, 15));
// 应用主题样式
        ChartFactory.setChartTheme(standardChartTheme);
        JFreeChart chart= ChartFactory.createBarChart("hi", "数据库下标",
                "键数量", dataset, PlotOrientation.VERTICAL, true, true, false); //创建一个JFreeChart
        chart.setTitle(new TextTitle("数据库键监控",new Font("宋体",Font.BOLD+Font.ITALIC,20)));//可以重新设置标题，替换“hi”标题
        CategoryPlot plot=(CategoryPlot)chart.getPlot();//获得图标中间部分，即plot
        CategoryAxis categoryAxis=plot.getDomainAxis();//获得横坐标
        categoryAxis.setLabelFont(new Font("微软雅黑",Font.BOLD,12));//设置横坐标字体
        return chart;
    }
    public MonitorDialog() {
    }

    public MonitorDialog(Frame owner) {
        super(owner);
        this.setUndecorated(true);
        JPanel jpanel = new JPanel();
        jpanel.setBackground(new Color(33,33,33));
        this.setLayout(new BorderLayout());

        this.add(jpanel,BorderLayout.CENTER);
        this.setSize( initWidth,initHeight);
        //Highcharts jfreechart
        jpanel.setLayout(new GridLayout(2,2));
        JFreeChart chart =createChart(createDataset());
        ChartPanel chartPanel = new ChartPanel(chart);
        jpanel.add(chartPanel);
        JPanel jPane2 = new JPanel();
        jPane2.setBackground(Color.GREEN);
        jpanel.add(jPane2);
        JPanel jPane3 = new JPanel();
        jPane3.setBackground(Color.BLUE);
        jpanel.add(jPane3);

        jpanel.add(new ChartPanel(createChart(createDataset())));

        this.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
            }
            @Override
            public void windowLostFocus(WindowEvent e) {
                if (SwingUtilities.isDescendingFrom(e.getOppositeWindow(), MonitorDialog.this)) {
                    return;
                }
                MonitorDialog.this.outCLose = true;
                LarkFrame.executorService.schedule(()->MonitorDialog.this.outCLose = false, 300,TimeUnit.MILLISECONDS);
                MonitorDialog.this.setVisible(false);
            }
        });
    }


}
