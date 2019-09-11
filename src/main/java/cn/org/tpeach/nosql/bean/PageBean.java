package cn.org.tpeach.nosql.bean;

import java.util.ArrayList;
import java.util.List;
/**
 * page 不能小于1 和大于总页数 否则按1或者最后一页计算
 * @author Administrator
 *
 */
public class PageBean {
	// 当前页
	private int page = 1;
	// 每页数量
	private int rows = 20;
	// 总数量
	private int total;
	
	public int getPage() {
		if(page < 1) {
			this.page = 1;
		}else if(page > this.getTotalPage()) {
			this.page = this.getTotalPage();
		}
		return page ;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows < 0 ? 0 : rows;
	}
	
	public int getTotal() {
		if(total < 0) {
			this.total = 0;
		}
		return this.total;
	}
	
	public void setTotal(int total) {
		this.total = total;

	}
	
	/**
	 * 获取总页数
	 * @return
	 */
	public int getTotalPage() {
		if(this.getRows() != 0) {
			return (this.getTotal()  +  this.getRows()  - 1) / this.getRows(); 	
		}
		return 1;
		
//		return totalPage = total % pageSize == 0 ? (total / pageSize) : (total / pageSize + 1);
	}


	/**
	 * 获取起始索引
	 * @return
	 */
	public int getStartIndex() {
		return this.getRows() * (this.getPage() - 1);
	}
	
	
	/**
	 * 获取结束索引
	 * @return
	 */
	public int getEndIndex() {
		return this.getStartIndex()+this.getCurentPageRows();
	}
	/**
	 * 当前页显示行数
	 * @return
	 */
	public int getCurentPageRows() {
		return this.getPage()==this.getTotalPage()?(this.getTotal()-(this.getTotalPage()-1) * this.getRows()) : this.getRows();
		
	}

	/**
	 * 是否第一页
	 * @return
	 */
	public boolean isFirstPage(){
		return this.getPage() == 1;
	}

	/**
	 * 是否最后一页
	 * @return
	 */
	public boolean isLastPage(){
		return this.getPage() == this.getTotalPage();
	}
	/**
	 * 是否有下一页
	 * @return
	 */
	public boolean hasNextPage(){
		return !isLastPage();
	}
	/**
	 * 是否有上一页
	 * @return
	 */
	public boolean hasPreviousPage(){
		return !isFirstPage();
	}
	@Override
	public String toString() {
		return "PageBean [总页数=" + this.getPage() +",当前页=" + this.getPage() + ", 行数=" + this.getRows() + ", 总数量=" + this.getTotal() +",起始索引=" +getStartIndex()+",结束索引=" +getEndIndex()+",当前页显示行数=" +getCurentPageRows()+"]";
	}

	public static void main(String[] args) {
		List<String> list = new ArrayList<>();
		for(int i =0;i<23432;i++) {
			list.add(i+"");
		}
		PageBean page =  new PageBean();
		page.setTotal(list.size());
		page.setRows(5000);
		page.setPage(4353);
		
		System.out.println(page);
//		System.out.println("总页数："+page.getTotalPage());
//		for (int curPage = 1; curPage <= page.getTotalPage();page.setPage(++curPage)) {
//			System.out.println("当前页："+curPage +">>>>>>>"+"开始下标："+page.getStartIndex()+"，结束下标："+page.getEndIndex()+"，显示数量："+page.getCurentPageRows());
//			list.subList(page.getStartIndex(),page.getEndIndex());
//		}
		
		
	}

}
