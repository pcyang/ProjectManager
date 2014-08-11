package com.pcyang.common;

import java.sql.Connection;
import java.util.List;

public class HeaderInfoAction{
	private List<String> labels;

	public String execute(){
		Connection conn = DerbyDatabase.getConnInstance();
//		setLabels(DerbyDatabase.getLabels(conn));
		
		return "success";
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}
}