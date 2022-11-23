package com.ejavier.dm;

public class DmField {
	
	private String fieldname;
	private int start;
	private int end;
	private int length; 
	private boolean isStatic;
	private boolean isNumeric;
	private String replacement;
	
	public String getFieldname() {
		return fieldname;
	}
	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public boolean isStatic() {
		return isStatic;
	}
	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}
	public String getReplacement() {
		return replacement;
	}
	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}
	public boolean isNumeric() {
		return isNumeric;
	}
	public void setNumeric(boolean isNumeric) {
		this.isNumeric = isNumeric;
	}
	
	
	

}
