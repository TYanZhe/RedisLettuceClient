package cn.org.tpeach.nosql.view.component;

import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * @author tyz
 * @Title: SimlpleGBC
 * @ProjectName RedisLark
 * @Description: 组件的约束 继承GridBagConstraints,简化操作
 * @date 2019-06-30 21:06
 * @since 1.0.0
 */
public class EasyGBC extends GridBagConstraints {

	private EasyGBC() {
		super();
	}
	/**
	 * gridx，gridy：其实就是组件行列的设置，注意都是从0开始的，比如 gridx=0，gridy=1时放在0行1列<br/>	
	 * @param gridx 方格左上角单元格所在行号，行号在表格中以0开始，从左到右依次编号
	 * @param gridy 方格左上角单元格所在列号，列号在表格中以0开始，从上到下依次编号
	 */
	public static EasyGBC build(int gridx, int gridy) {
		EasyGBC gbc = new EasyGBC();
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		return gbc;
	}

	/**
	 * gridx，gridy：其实就是组件行列的设置，注意都是从0开始的，比如 gridx=0，gridy=1时放在0行1列<br/>
	 * gridwidth gridheight 默认值为GridBagConstraints.REMAINDER常量，代表此组件为此行或此列的最后一个组件，会占据所有剩余的空间； <br/>
	 * @param gridx gridx 方格左上角单元格所在行号，行号在表格中以0开始，从左到右依次编号
	 * @param gridy 方格左上角单元格所在列号，列号在表格中以0开始，从上到下依次编号
	 * @param colume 方格在横向占用的单元格数
	 * @param row 方格在纵向占用的单元格数
	 */
	public static EasyGBC build(int gridx, int gridy, int colume, int row) {
		EasyGBC simlpleGBC = new EasyGBC();
		simlpleGBC.gridx = gridx;
		simlpleGBC.gridy = gridy;
		simlpleGBC.gridwidth = colume;
		simlpleGBC.gridheight = row;
		return simlpleGBC;
	}

	/**
	 * 当组件空间大于组件本身时，要将组件置于何处。
	 * @param anchor  CENTER（默认值）、NORTH、NORTHEAST、EAST、SOUTHEAST、WEST、NORTHWEST
	 * @return
	 */
	public EasyGBC setAnchor(int anchor) {
		this.anchor = anchor;
		return this;
	}

	/**
	 * 如果显示区域比组件的区域大的时候，可以用来控制组件的行为。控制组件是垂直填充，还是水平填充，或者两个方向一起填充
	 * @param fill NONE(不调整组件大小) HORIZONTAL(水平填充) VERTICAL(垂直填充)  BOTH(使组件完全填满其显示区域)
	 * @return
	 */
	public EasyGBC setFill(int fill) {
		this.fill = fill;
		return this;
	}
	/**
	 * 如果显示区域比组件的区域大的时候，可以用来控制组件的行为。控制组件是垂直填充，还是水平填充，或者两个方向一起填充
	 * 设置为BOTH(使组件完全填满其显示区域)
	 * @return
	 */
	public EasyGBC setFill() {
		this.fill = this.BOTH;
		return this;
	}
	/**
	 * weightx，weighty：当窗口变大时，设置各组件跟着变大的比例。 <br/>
	 * 比如组件A的weightx=0.5，组件B的weightx=1， <br/>
	 * 那么窗口X轴变大时剩余的空间就会以1：2的比例分配给组件A和B
	 * @param weightx
	 * @param weighty
	 * @return
	 */
	public EasyGBC setWeight(double weightx, double weighty) {
		this.weightx = weightx;
		this.weighty = weighty;
		return this;
	}

	/**
	 * 组件的外部填充(可看做是组件的外边距，也可以看做是显示区域的内边距)<br/>
	 * 等距离外部填充，设置组件之间彼此的间距。它有四个参数，分别是上，左，下，右，默认为（0，0，0，0）；
	 * @param distance
	 * @return
	 */
	public EasyGBC resetInsets(int distance) {
		this.insets = new Insets(distance, distance, distance, distance);
		return this;
	}

	/**
	 * 组件的外部填充(可看做是组件的外边距，也可以看做是显示区域的内边距)<br/>
	 * 外部填充，设置组件之间彼此的间距。它有四个参数，分别是上，左，下，右，默认为（0，0，0，0）；
	 * @param distance
	 * @return
	 */
	public EasyGBC resetInsets(int top, int left, int bottom, int right) {
		this.insets = new Insets(top, left, bottom, right);
		return this;
	}
	/**
	 * 组件的外部填充(可看做是组件的外边距，也可以看做是显示区域的内边距)<br/>
	 * 外部填充，设置组件之间彼此的间距。它有四个参数，分别是上，左，下，右，默认为（0，0，0，0）；
	 * @param distance
	 * @return
	 */
	public EasyGBC resetInsets(Insets insets) {
		this.insets = insets;
		return this;
	}
	/**
	 * 组件的内部填充(可看做组件的内边距)，即对组件最大大小的添加量
	 * @param ipadx 默认为0
	 * @param ipady 默认为0
	 * @return
	 */
	public EasyGBC resetIpad(int ipadx, int ipady) {
		this.ipadx = ipadx;
		this.ipady = ipady;
		return this;
	}
}
