package cn.org.tpeach.nosql.view.dialog;

import cn.org.tpeach.nosql.constant.RedisInfoKeyConstant;
import cn.org.tpeach.nosql.framework.LarkFrame;
import cn.org.tpeach.nosql.redis.bean.RedisConnectInfo;
import cn.org.tpeach.nosql.redis.bean.RedisTreeItem;
import cn.org.tpeach.nosql.redis.service.IRedisConfigService;
import cn.org.tpeach.nosql.redis.service.IRedisConnectService;
import cn.org.tpeach.nosql.service.ServiceProxy;
import cn.org.tpeach.nosql.tools.DateUtils;
import cn.org.tpeach.nosql.tools.MapUtils;
import cn.org.tpeach.nosql.tools.StringUtils;
import cn.org.tpeach.nosql.tools.SwingTools;
import cn.org.tpeach.nosql.view.jfree.chart.RChartFactory;
import cn.org.tpeach.nosql.view.jfree.plot.RCategoryPlot;
import lombok.Getter;
import lombok.Setter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.*;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategorySeriesLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


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
    @Getter
    private boolean init;

    private ChartPanel barChartPanel,memoryChartPanel,cpuChartPanel,netChartPanel;

    private JFreeChart barchart,memoryChart,cpuChart ,netChart ;

    private  TimeTableXYDataset memoryDataset,cpuDataset;
    private DefaultCategoryDataset netDataset;
    private AtomicBoolean isExecute = new AtomicBoolean(false);
    @Getter
    private RedisTreeItem redisTreeItem;
    private RedisTreeItem oldRedisTreeItem;
    @Getter
    @Setter
    private Map<String, String> redisInfoMap;
    private RedisConnectInfo redisConnectInfo;
    private Vector<Date> timeSeriesList = new Vector<>();
    IRedisConnectService redisConnectService = ServiceProxy.getBeanProxy("redisConnectService", IRedisConnectService.class);
    IRedisConfigService redisConfigService = ServiceProxy.getBeanProxy("redisConfigService", IRedisConfigService.class);
    public MonitorDialog(Frame owner) {
        super(owner);
        this.setSize( initWidth,initHeight);
        this.setUndecorated(true);
        this.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
            }
            @Override
            public void windowLostFocus(WindowEvent e) {
                try{
                    if (  SwingUtilities.isDescendingFrom(e.getOppositeWindow(), MonitorDialog.this)) {
                        return;
                    }
                    MonitorDialog.this.outCLose = true;
                    LarkFrame.executorService.schedule(()->MonitorDialog.this.outCLose = false, 300,TimeUnit.MILLISECONDS);
                    MonitorDialog.this.setVisible(false);
                }catch (Exception ex){}

            }
        });
        this.addComponentListener(new ComponentAdapter()  {

            @Override
            public void componentShown(ComponentEvent e) {
                updateData(true);
                updateData(false);
                task();
            }
        });
    }

    public void setRedisTreeItem(RedisTreeItem redisTreeItem) {
        this.oldRedisTreeItem = this.redisTreeItem;
        this.redisTreeItem = redisTreeItem;
        this.redisConnectInfo = redisConfigService.getRedisConfigById(redisTreeItem.getId());
    }
    public void monitorInit(RedisTreeItem redisTreeItem){

       this.setRedisTreeItem(redisTreeItem);
        //获取连接信息
        this.redisInfoMap = redisConnectService.getConnectInfo(redisTreeItem.getId(),false);
        if(MapUtils.isEmpty(redisInfoMap)){
            SwingTools.showMessageInfoDialog(null,"连接中，请稍后查看...","Monitor");
            return;
        }

        JPanel jpanel = new JPanel();
        jpanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        jpanel.setBackground(new Color(33,33,33));
        this.setLayout(new BorderLayout());

        this.add(jpanel,BorderLayout.CENTER);

        // jfreechart https://blog.csdn.net/chenyuangege/article/details/79012205
        jpanel.setLayout(new GridLayout(2,2));

        //内存

        memoryDataset = new TimeTableXYDataset();
        memoryChart = createTimeSeriesChart(memoryDataset,"内存占用量实时监控","数量(单位：MB)");
        memoryChartPanel = new ChartPanel(memoryChart);
        cpuDataset = new TimeTableXYDataset();
//        cpuChart = createCpuChart(cpuDataset) ;
        cpuChart = createTimeSeriesChart(cpuDataset,"主线程CPU耗时监控","单位：S");
        cpuChartPanel = new ChartPanel(cpuChart);
        netDataset=new DefaultCategoryDataset();
        netChart = createNetChart(netDataset);
        netChartPanel =  new ChartPanel(netChart);
        barchart =createKeySizeChart(createBarDataset(),true);
        barChartPanel = new ChartPanel(barchart);


        jpanel.add(memoryChartPanel);
        jpanel.add(cpuChartPanel);
        jpanel.add(netChartPanel);
        jpanel.add(barChartPanel);
        this.init = true;

    }
    private synchronized void task(){
        if(isExecute.get()){
            return;
        }
        if(!this.isInit() || !this.isVisible()){
            return;
        }
        isExecute.set(true);
        LarkFrame.executorService.schedule(()->{
            updateData(false);
            isExecute.set(false);
            task();
        },1, TimeUnit.SECONDS);
    }
    public void updateData(boolean isNewData){
        redisInfoMap = redisConnectService.getConnectInfo(redisTreeItem.getId(),true,false);
        CategoryPlot keySizePlot = (CategoryPlot) barchart.getPlot();
        keySizePlot.setDataset(this.createBarDataset());
        long now = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        int maxLenght = 10;
        final Double usedMemory = Double.valueOf(redisInfoMap.get(RedisInfoKeyConstant.usedMemory))/(1024*1024);
        final Double usedCpuSys = Double.valueOf(redisInfoMap.get(RedisInfoKeyConstant.usedCpuSys)) ;
        final Double usedCpuUser = Double.valueOf(redisInfoMap.get(RedisInfoKeyConstant.usedCpuUser)) ;
        final Double instantaneousIutputKbps = Double.valueOf(redisInfoMap.get(RedisInfoKeyConstant.instantaneousIutputKbps)) ;
        final Double instantaneousOutputKbps = Double.valueOf(redisInfoMap.get(RedisInfoKeyConstant.instantaneousOutputKbps)) ;
        if(isNewData){
            memoryDataset.clear();
            timeSeriesList.clear();
            cpuDataset.clear();
            netDataset.clear();
            now = now - 1000*maxLenght;
            for (int i = 0; i < maxLenght; i++) {
                calendar.setTimeInMillis(now);
                timeSeriesList.add(calendar.getTime());
                memoryDataset.add(new Second(calendar.getTime()),usedMemory,"内存");
                cpuDataset.add(new Second(calendar.getTime()),usedCpuSys,"核心态");
                cpuDataset.add(new Second(calendar.getTime()),usedCpuUser,"用户态");
                if(i!=maxLenght-1){
                    netDataset.addValue(0,"输入带宽",DateUtils.format(calendar.getTime(),"HH:mm:ss"));
                    netDataset.addValue(0,"输出带宽",DateUtils.format(calendar.getTime(),"HH:mm:ss"));
                }else{
                    netDataset.addValue(instantaneousIutputKbps,"输入带宽",DateUtils.format(calendar.getTime(),"HH:mm:ss"));
                    netDataset.addValue(instantaneousOutputKbps,"输出带宽",DateUtils.format(calendar.getTime(),"HH:mm:ss"));
                }
                now+=1000;
            }

        }else{
            final Date firstSeries = timeSeriesList.remove(0);
            memoryDataset.remove(new Second(firstSeries),"内存");
            cpuDataset.remove(new Second(firstSeries),"核心态");
            cpuDataset.remove(new Second(firstSeries),"用户态");
            netDataset.removeColumn(DateUtils.format(firstSeries,"HH:mm:ss"));

            memoryDataset.add(new Second(calendar.getTime()),usedMemory,"内存");
            cpuDataset.add(new Second(calendar.getTime()),usedCpuSys,"核心态");
            cpuDataset.add(new Second(calendar.getTime()),usedCpuUser,"用户态");
            netDataset.addValue(instantaneousIutputKbps,"输入带宽",DateUtils.format(calendar.getTime(),"HH:mm:ss"));
            netDataset.addValue(instantaneousOutputKbps,"输出带宽",DateUtils.format(calendar.getTime(),"HH:mm:ss"));
            timeSeriesList.add(calendar.getTime());

        }

    }


    public  JFreeChart createNetChart(CategoryDataset categoryDataset) {
        // 创建主题样式
        StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
// 设置标题字体
        standardChartTheme.setExtraLargeFont(new Font("宋体", Font.BOLD, 20));
// 设置图例的字体
        standardChartTheme.setRegularFont(new Font("黑体", Font.PLAIN, 12));
// 设置轴向的字体
        standardChartTheme.setLargeFont(new Font("宋体", Font.PLAIN, 15));
// 应用主题样式
        ChartFactory.setChartTheme(standardChartTheme);
        //创建JFreeChart对象：ChartFactory.createAreaChart
        JFreeChart jfreechart = ChartFactory.createAreaChart("网络流量监控",    //标题
                "时间",    //categoryAxisLabel （category轴，横轴，X轴标签）
                "单位:Kbps",    //valueAxisLabel（value轴，纵轴，Y轴的标签）
                categoryDataset, // dataset
                PlotOrientation.VERTICAL,
                true, // legend
                false, // tooltips
                false); // URLs

        //使用CategoryPlot设置各种参数。以下设置可以省略。
        CategoryPlot  plot = (CategoryPlot) jfreechart.getPlot();

        //背景色　透明度
        plot.setBackgroundAlpha(0f);
        //前景色　透明度
        plot.setForegroundAlpha(0.5f);
        RectangleInsets offset = new RectangleInsets(0, 0, 0, 0);
        plot.setAxisOffset(offset);// 坐标轴到数据区的间距
        //其他设置 参考 CategoryPlot类
        jfreechart.setBackgroundPaint(Color.white); // 设定背景色为白色
        CategoryPlot categoryplot = jfreechart.getCategoryPlot(); // 获得

        categoryplot.setBackgroundPaint(Color.lightGray); // 设定图表数据显示部分背景色
        categoryplot.setDomainGridlinePaint(Color.white); // 横坐标网格线白色
        categoryplot.setDomainGridlinesVisible(false); // 可见
        categoryplot.setRangeGridlinePaint(Color.white); // 纵坐标网格线白色

        // 1、对标题
        Font font1 = new Font("SansSerif", 10, 20); // 设定字体、类型、字号
        // Font font1 = new Font("SimSun", 10, 20); //也可以
        jfreechart.getTitle().setFont(font1); // 标题
        // 2、对图里面的汉字设定,也就是Plot的设定
        Font font2 = new Font("SansSerif", 10, 16); // 设定字体、类型、字号
        categoryplot.getDomainAxis().setLabelFont(font2);
        categoryplot.getRangeAxis().setLabelFont(font2);
        // 3、下面的方块区域是 LegendTitle 对象
        Font font3 = new Font("SansSerif", 10, 12); // 设定字体、类型、字号
        jfreechart.getLegend().setItemFont(font3);// 最下方
        //获得横坐标
        CategoryAxis categoryAxis=plot.getDomainAxis();
        categoryAxis.setLowerMargin(0);
        categoryAxis.setUpperMargin(0);
        categoryAxis.setLabelFont(new Font("黑体",Font.ITALIC,10));//设置横坐标字体
        categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        // y轴设置
        ValueAxis rangeAxis = plot.getRangeAxis();
        // 设置最高的一个 Item 与图片顶端的距离
        rangeAxis.setUpperMargin(0.15);
        // 设置最低的一个 Item 与图片底端的距离
        rangeAxis.setLowerMargin(0.15);
        final CategoryItemRenderer renderer = plot.getRenderer();
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        return jfreechart;
    }




    public JFreeChart createTimeSeriesChart( XYDataset dataset,String title,String valueLabel){
        StandardChartTheme mChartTheme = new StandardChartTheme("CN");
        mChartTheme.setLargeFont(new Font("宋体", Font.BOLD, 15));
        mChartTheme.setExtraLargeFont(new Font("宋体", Font.PLAIN, 15));
        mChartTheme.setRegularFont(new Font("宋体", Font.PLAIN, 12));
        ChartFactory.setChartTheme(mChartTheme);
        JFreeChart mChart =ChartFactory.createTimeSeriesChart(title,
                "时间",
                valueLabel, dataset, true, true, true);
//        JFreeChart mChart =ChartFactory.createXYLineChart(
//                "内存占用量实时监控",//图名字
//                "时间",//横坐标
//                "数量",//纵坐标
//                dataset,//数据集
//                PlotOrientation.VERTICAL,
//                true, // 显示图例
//                true, // 采用标准生成器
//                false);// 是否生成超链接


            XYPlot plot = (XYPlot) mChart.getPlot();

//        plot.setBackgroundPaint(Color.LIGHT_GRAY);
//        plot.setRangeGridlinePaint(Color.BLUE);//背景底部横虚线
//        plot.setOutlinePaint(Color.RED);//边界线
//
//        mChart.setBorderPaint(new Color(0,204,205));
//        mChart.setBorderVisible(true);


        final XYItemRenderer renderer = plot.getRenderer();
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelGenerator(new StandardXYItemLabelGenerator("{2}", NumberFormat.getNumberInstance(),new DecimalFormat("#.##")));
        // Y轴
        NumberAxis numberaxis = (NumberAxis) plot.getRangeAxis();
//        numberaxis.setLowerBound(0);
//        numberaxis.setUpperBound(100);
//        numberaxis.setTickUnit(new NumberTickUnit(100d));

        // numberaxis.setAutoRangeIncludesZero(true);
        numberaxis.setLowerMargin(0); // 数据轴下（左）边距 ­
        numberaxis.setMinorTickMarksVisible(false);// 标记线是否显示
        numberaxis.setTickMarkInsideLength(0);// 外刻度线向内长度
        numberaxis.setTickMarkOutsideLength(0);
        // 设置最高的一个 Item 与图片顶端的距离
        numberaxis.setUpperMargin(0.15);
        // 设置最低的一个 Item 与图片底端的距离
        numberaxis.setLowerMargin(0.15);
        plot.setRangeAxis(numberaxis);
        // X轴的设计
        DateAxis domainAxis = (DateAxis) plot.getDomainAxis();
        domainAxis.setLabelFont(new Font("黑体",Font.ITALIC,15));//设置横坐标字体
//        domainAxis.setAutoRange(true);// 自动设置数据轴数据范围
        //时间轴间距
        domainAxis.setTickUnit(new DateTickUnit(DateTickUnitType.SECOND, 1));
        domainAxis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss", Locale.CHINESE));
        //domainAxis(没找到倾斜多少度显示)
        domainAxis.setVerticalTickLabels(true);
        plot.setDomainAxis(domainAxis);
        RectangleInsets offset = new RectangleInsets(0, 0, 0, 0);
        plot.setAxisOffset(offset);// 坐标轴到数据区的间距
        plot.setBackgroundAlpha(0.0f);// 去掉背景色
        plot.setOutlinePaint(null);// 去掉边框

        //设置子标题
//        TextTitle subtitle = new TextTitle(startDateTimeStr+" 到 "+stopDateTimeStr, new Font("宋体", Font.PLAIN, 12));
//        mChart.addSubtitle(subtitle);
//        //设置主标题
//        mChart.setTitle(new TextTitle(plateNum+"内存统计", new Font("黑体", Font.BOLD, 15)));
//        mChart.setAntiAlias(true);


        return mChart;
    }


    public  CategoryDataset createBarDataset() //创建柱状图数据集
    {
        DefaultCategoryDataset dataset=new DefaultCategoryDataset();
        if(this.redisConnectInfo != null ){
            if(redisConnectInfo.getStructure() == 0){
                int index = 16;
                for(int i=0;i<index;i++){
                    String keySize = redisInfoMap.get("db" + i);
                    if(StringUtils.isNotBlank(keySize)){
                         String s = keySize.split(",")[0].split("=")[1];
                        dataset.addValue(Integer.valueOf(s),"db"+i,"db"+i);
                    }else{
                        dataset.addValue(0,"db"+i,"db"+i);
                    }
                }
            }else{
                final Long dbKeySize = redisConnectService.getDbKeySize(redisConnectInfo.getId(), 0,false);
                dataset.addValue(dbKeySize,"db0","db0");
            }

        }else{
            dataset.addValue(0,"db0","db0");
        }
        return dataset;
    }
    public  JFreeChart createKeySizeChart(CategoryDataset dataset,boolean flag) //用数据集创建一个图表
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
        JFreeChart chart= RChartFactory.createBarChart("hi",
                "数据库下标", // 目录轴的显示标签
                "键数量",// 数值轴的显示标签
                dataset,  // 数据集
                PlotOrientation.VERTICAL,// 图表方向：水平、垂直
                false,// 是否显示图例(对于简单的柱状图必须是false)
                true,// 是否生成工具
                false// 是否生成URL链接
        ); //创建一个JFreeChart
        chart.setTitle(new TextTitle("数据库键监控",new Font("宋体",Font.BOLD+Font.ITALIC,20)));//可以重新设置标题，替换“hi”标题
        if(flag){

            RCategoryPlot plot = (RCategoryPlot) chart.getPlot();
            final BarRenderer renderer = (BarRenderer) plot.getRenderer();
            renderer.setItemMargin(0.01);
            // 设置柱子宽度
            renderer.setMaximumBarWidth(0.1);
            // 设置柱子高度
            renderer.setMinimumBarLength(0.2);
            // 设置每个地区所包含的平行柱的之间距离
            renderer.setItemMargin(0.01);
            // 显示每个柱的数值，并修改该数值的字体属性
            renderer.setIncludeBaseInRange(true);
            renderer.setDefaultItemLabelsVisible(true);
            renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
            renderer.setShadowVisible(false);//取消柱子的阴影效果
            renderer.setDrawBarOutline(false);
            renderer.setMaximumBarWidth(0.1); //设置柱子宽度
            renderer.setMinimumBarLength(0.00); //设置柱子高度
            //设置不显示边框线
            renderer.setDrawBarOutline(false);
            renderer.setSeriesPaint(0,new Color(131,175,155));
            renderer.setBarPainter( new StandardBarPainter() );//取消渐变色
            renderer.setItemMargin(-0.01);


            CategoryAxis categoryAxis=plot.getDomainAxis();//获得横坐标
            categoryAxis.setLabelFont(new Font("黑体",Font.ITALIC,10));//设置横坐标字体
            categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
            // y轴设置
            ValueAxis rangeAxis = plot.getRangeAxis();
            // 设置最高的一个 Item 与图片顶端的距离
            rangeAxis.setUpperMargin(0.15);
            // 设置最低的一个 Item 与图片底端的距离
            rangeAxis.setLowerMargin(0.15);

            plot.setRangeAxis(rangeAxis);
            plot.setForegroundAlpha(0.6f);//柱的透明度
  /*          LarkFrame.executorService.scheduleAtFixedRate(()->{
                plot.setDataset(createBarDataset());
            },1,1,TimeUnit.SECONDS);*/
            plot.setRenderer(renderer);

            plot.setBackgroundPaint(Color.white);
            return chart;
        }



        Font labelFont = new Font("SansSerif", Font.TRUETYPE_FONT, 12);
        /*
         * VALUE_TEXT_ANTIALIAS_OFF表示将文字的抗锯齿关闭,
         * 使用的关闭抗锯齿后，字体尽量选择12到14号的宋体字,这样文字最清晰好看
         */
        // chart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        chart.setTextAntiAlias(false);
        chart.setBackgroundPaint(Color.white);
        // create plot
        CategoryPlot plot = chart.getCategoryPlot();
        // 设置横虚线可见
        plot.setRangeGridlinesVisible(true);
        // 虚线色彩
        plot.setRangeGridlinePaint(Color.gray);
        LarkFrame.executorService.scheduleAtFixedRate(()->{
            plot.setDataset(MonitorDialog.this.createBarDataset());
        },1,1,TimeUnit.SECONDS);


        // 数据轴精度
        NumberAxis vn = (NumberAxis) plot.getRangeAxis();
        // vn.setAutoRangeIncludesZero(true);
        DecimalFormat df = new DecimalFormat("#0");
        vn.setNumberFormatOverride(df); // 数据轴数据标签的显示格式
        // x轴设置
        CategoryAxis domainAxis = plot.getDomainAxis();
//        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45); domainAxis.setMaximumCategoryLabelWidthRatio(5.0f);
//        domainAxis.setMaximumCategoryLabelLines(1);
//        domainAxis.setTickLabelsVisible(false);
//        chart.setBackgroundImageAlpha(0.01F);
//        domainAxis.setTickMarksVisible(true);
//        domainAxis.setCategoryLabelPositionOffset(20);


        domainAxis.setLabelFont(labelFont);// 轴标题
        domainAxis.setTickLabelFont(labelFont);// 轴数值

        // Lable（Math.PI/3.0）度倾斜
        // domainAxis.setCategoryLabelPositions(CategoryLabelPositions
        // .createUpRotationLabelPositions(Math.PI / 3.0));

        domainAxis.setMaximumCategoryLabelWidthRatio(0.6f);// 横轴上的 Lable 是否完整显示

        // 设置距离图片左端距离
        domainAxis.setLowerMargin(0.1);
        // 设置距离图片右端距离
        domainAxis.setUpperMargin(0.1);
        // 设置 columnKey 是否间隔显示
        // domainAxis.setSkipCategoryLabelsToFit(true);

        plot.setDomainAxis(domainAxis);
        // 设置柱图背景色
        plot.setBackgroundPaint(new Color(255, 255, 204));
        // y轴设置
        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setLabelFont(labelFont);
        rangeAxis.setTickLabelFont(labelFont);
        // 设置最高的一个 Item 与图片顶端的距离
        rangeAxis.setUpperMargin(0.15);
        // 设置最低的一个 Item 与图片底端的距离
        rangeAxis.setLowerMargin(0.15);
        plot.setRangeAxis(rangeAxis);
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
//        BarRenderer renderer = new BarRenderer();
        // 设置柱子宽度
        renderer.setMaximumBarWidth(0.05);
        // 设置柱子高度
        renderer.setMinimumBarLength(0.2);
        // 设置柱子边框颜色
//        renderer.setBaseOutlinePaint(Color.BLACK);
        // 设置柱子边框可见
        renderer.setDrawBarOutline(true);
        renderer.setItemMargin(0.1);
        // // 设置柱的颜色
        renderer.setSeriesPaint(0, new Color(204, 255, 255));
        renderer.setSeriesPaint(1, new Color(153, 204, 255));
        renderer.setSeriesPaint(2, new Color(51, 204, 204));

        // 设置每个地区所包含的平行柱的之间距离
        renderer.setItemMargin(0.0);

        // 显示每个柱的数值，并修改该数值的字体属性
        renderer.setIncludeBaseInRange(true);
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());

        renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator("{0}  \r\n键: {2}",NumberFormat.getInstance()),true);
        renderer.setLegendItemToolTipGenerator(new StandardCategorySeriesLabelGenerator());

        plot.setRenderer(renderer);
        // 设置柱的透明度
        plot.setForegroundAlpha(1.0f);
        return chart;
    }







}

