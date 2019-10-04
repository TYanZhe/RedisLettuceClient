package cn.org.tpeach.nosql.view.jfree.chart;

import cn.org.tpeach.nosql.view.jfree.plot.RCategoryPlot;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.chart.urls.StandardCategoryURLGenerator;
import org.jfree.chart.util.Args;
import org.jfree.data.category.CategoryDataset;

/**
 * @author tyz
 * @Title: RChartFactory
 * @ProjectName redisLark-Github
 * @Description: TODO
 * @date 2019-10-04 22:55
 * @since 1.0.0
 */
public class RChartFactory extends ChartFactory {
    private static ChartTheme currentTheme = new StandardChartTheme("JFree");
    public static JFreeChart createBarChart(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        Args.nullNotPermitted(orientation, "orientation");
        CategoryAxis categoryAxis = new CategoryAxis(categoryAxisLabel);
        ValueAxis valueAxis = new NumberAxis(valueAxisLabel);
        BarRenderer renderer = new BarRenderer();
        ItemLabelPosition position1;
        ItemLabelPosition position2;
        if (orientation == PlotOrientation.HORIZONTAL) {
            position1 = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE3, TextAnchor.CENTER_LEFT);
            renderer.setDefaultPositiveItemLabelPosition(position1);
            position2 = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE9, TextAnchor.CENTER_RIGHT);
            renderer.setDefaultNegativeItemLabelPosition(position2);
        } else if (orientation == PlotOrientation.VERTICAL) {
            position1 = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER);
            renderer.setDefaultPositiveItemLabelPosition(position1);
            position2 = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE6, TextAnchor.TOP_CENTER);
            renderer.setDefaultNegativeItemLabelPosition(position2);
        }

        if (tooltips) {
            renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator());
        }

        if (urls) {
            renderer.setDefaultItemURLGenerator(new StandardCategoryURLGenerator());
        }

        RCategoryPlot plot = new RCategoryPlot(dataset, categoryAxis, valueAxis, renderer);
        plot.setOrientation(orientation);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

}
