package cn.org.tpeach.nosql.view.jfree.plot;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;

/**
 * @author tyz
 * @Title: RCategoryPlot
 * @ProjectName redisLark-Github
 * @Description: TODO
 * @date 2019-10-04 22:47
 * @since 1.0.0
 */
public class RCategoryPlot extends CategoryPlot {
    public RCategoryPlot() {
    }

    public RCategoryPlot(CategoryDataset dataset, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryItemRenderer renderer) {
        super(dataset, domainAxis, rangeAxis, renderer);
    }
}
