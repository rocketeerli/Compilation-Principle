package entity;

import java.util.List;

public class Attribute {
	private String value;
	private Integer width;
	private List<Integer> truelist;
	private List<Integer> falselist;
	private Integer quad;
	private List<Integer> nextlist;
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public List<Integer> getTruelist() {
		return truelist;
	}

	public void setTruelist(List<Integer> truelist) {
		this.truelist = truelist;
	}

	public List<Integer> getFalselist() {
		return falselist;
	}

	public void setFalselist(List<Integer> falselist) {
		this.falselist = falselist;
	}

	public Integer getQuad() {
		return quad;
	}

	public void setQuad(Integer quad) {
		this.quad = quad;
	}

	public List<Integer> getNextlist() {
		return nextlist;
	}

	public void setNextlist(List<Integer> nextlist) {
		this.nextlist = nextlist;
	}
	
}
