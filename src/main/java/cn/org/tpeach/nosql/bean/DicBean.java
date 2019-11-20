/**
 * 
 */
package cn.org.tpeach.nosql.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
　 * <p>Title: DicBean.java</p> 
　 * @author taoyz 
　 * @date 2019年9月3日 
　 * @version 1.0 
 */
@Getter
@Setter
@Builder
public class DicBean {
	
	public DicBean() {
	}

	public DicBean(String code, String value) {
		super();
		this.code = code;
		this.value = value;
	}
	
	public DicBean(String id, String code, String value) {
		super();
		this.id = id;
		this.code = code;
		this.value = value;
	}

	private String id;
	private String code;
	private String value;
	@Override
	public String toString() {
		return value ;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DicBean dicBean = (DicBean) o;
		return Objects.equals(code, dicBean.code);
	}

	@Override
	public int hashCode() {
		return Objects.hash(code);
	}
}
