/**
 * 
 */
package cn.org.tpeach.nosql.view.table;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;

/**
　 * <p>Title: RTableModel.java</p> 
　 * @author taoyz 
　 * @date 2019年8月30日 
　 * @version 1.0 
 */
public class RTableModel extends DefaultTableModel{


	private static final long serialVersionUID = 8643196717898414744L;
	public RTableModel() {
		super();
	}

	public RTableModel(int rowCount, int columnCount) {
		super(rowCount, columnCount);
	}

	public RTableModel(Object[] columnNames, int rowCount) {
		super(columnNames, rowCount);
	}

	public RTableModel(Object[][] data, Object[] columnNames) {
		super(data, columnNames);
	}

	@SuppressWarnings("rawtypes")
	public RTableModel(Vector columnNames, int rowCount) {
		super(columnNames, rowCount);
	}

	@SuppressWarnings("rawtypes")
	public RTableModel(Vector data, Vector columnNames) {
		super(data, columnNames);
	}

	/**
	 * show grid lines when no items
	 */
	@Override
	public int getRowCount(){
		  int lowRowCount = 22;
		  return super.getRowCount() > lowRowCount ? super.getRowCount() : lowRowCount; 
		
	}

	

	@Override
	public void setDataVector(Vector dataVector, Vector columnIdentifiers) {

		super.setDataVector(dataVector, columnIdentifiers);
	}

	@Override
	public void setDataVector(Object[][] dataVector, Object[] columnIdentifiers) {
		super.setDataVector(dataVector, columnIdentifiers);
	}

	@Override
	public Object getValueAt(int row,int column){
		if(row > super.getRowCount()) {
			return "";
		}
		return super.getValueAt(row, column);
		
	}
	
	
}
