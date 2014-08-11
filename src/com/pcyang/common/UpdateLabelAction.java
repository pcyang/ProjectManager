package com.pcyang.common;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

public class UpdateLabelAction{
	public String execute(){
		Connection conn = DerbyDatabase.getConnInstance();
		HttpServletRequest request = ServletActionContext.getRequest();
		String project_id = request.getParameter("project_id");
		String status_id = request.getParameter("status_id");
		if(project_id == null || status_id == null)
			return "failure";
		DerbyDatabase.updateProjectStatus(conn, project_id, status_id);
		return "success";
	}
}