package com.bang.bean;

import java.util.List;

import net.sf.json.JSONArray;

import org.apache.ibatis.session.RowBounds;

public class RowsBoundsCommon extends RowBounds {
	private String col;
	private List<Object> queryresult;
	private int totalCount;
	private int totalPage;
	private int currentPage=1;
	private int pageCount=10;
	private boolean haveCol=true;
	public boolean isHaveCol() {
		return haveCol;
	}
	public void setHaveCol(boolean haveCol) {
		this.haveCol = haveCol;
	}
	public String getCol() {
		return col;
	}
	public void setCol(String col) {
		this.col = col;
	}
	public List<Object> getQueryresult() {
		return queryresult;
	}
	public void setQueryresult(List<Object> queryresult) {
		this.queryresult = queryresult;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getPageCount() {
		return pageCount;
	}
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	@Override
	public int getLimit() {
		return pageCount;
	}
	@Override
	public int getOffset() {
		return 0;
	}
	public RowsBoundsCommon(int currentPage, int pageCount,String col){
		super(currentPage, pageCount);
		this.currentPage = currentPage;
		this.pageCount = pageCount;
		this.col = col;
	}
	public RowsBoundsCommon(){
	}
	@Override
	public String toString() {
		String temp = getCol();
		if(temp==null){
			temp="";
		}
		temp=temp.replaceAll("\"", "\\\\\"");
		return "{\"col\":\""+temp+"\",\"totalCount\":\""+totalCount+"\"," +
				"\"totalPage\":\""+totalPage+"\",\"currentPage\":\""+currentPage+"\",\"pageCount\":\""+pageCount+"\"," +
						"\"queryresult\":"+JSONArray.fromObject(queryresult)+"}";
	}
}
