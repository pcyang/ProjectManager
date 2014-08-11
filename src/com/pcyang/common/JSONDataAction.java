package com.pcyang.common;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.Action;
import com.pcyang.common.Utilities.Pair;

public class JSONDataAction{
	
	private List<Map<String,String>> columns = new ArrayList<Map<String,String>>();
	private Map<String, List<Map<Integer,String>>> columns_label = new HashMap<String, List<Map<Integer,String>>>();
	
	public String execute() {
		Connection conn = DerbyDatabase.getConnInstance();
		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String,String[]> params = request.getParameterMap();
		if(params != null && params.size() > 0)
		{
			if( params.get("subteam_id") != null){
				String subteam_id = params.get("subteam_id")[0];
				Map<String, List<Map<Integer,String>>> tempMap = new HashMap<String, List<Map<Integer,String>>>();
				tempMap.put(subteam_id, DerbyDatabase.getSubteamStatusID(conn, subteam_id));
				setColumns_label(tempMap);
			}
			else if(params.get("team_id") != null){
				List<Pair<String,String>> subteams = DerbyDatabase.getSubteamsUsingTeam(conn, params.get("team_id")[0]);
				Map<String, List<Map<Integer,String>>> tempMap = new HashMap<String, List<Map<Integer,String>>>();
				for(Pair<String,String> subteam : subteams){
					tempMap.put(subteam.getR(), DerbyDatabase.getSubteamStatusID(conn, subteam.getL()));
				}
				setColumns_label(tempMap);
			}
			else if(params.get("group_id")!=null){
				List<Pair<String,String>> subteams = DerbyDatabase.getSubteamsUsingGroup(conn, params.get("group_id")[0]);
				Map<String, List<Map<Integer,String>>> tempMap = new HashMap<String, List<Map<Integer,String>>>();
				for(Pair<String,String> subteam : subteams){
					tempMap.put(subteam.getR(), DerbyDatabase.getSubteamStatusID(conn, subteam.getL()));
				}
				setColumns_label(tempMap);
			}
			columns = DerbyDatabase.getProjects(conn, params);
		}
		else
		{
			columns = DerbyDatabase.getProjects(conn);
		}
		return Action.SUCCESS;
	}

	public List<Map<String,String>> getColumns() {
		return columns;
	}

	public void setColumns(List<Map<String,String>> columns) {
		this.columns = columns;
	}

	public Map<String, List<Map<Integer,String>>> getColumns_label() {
		return columns_label;
	}

	public void setColumns_label(Map<String, List<Map<Integer,String>>> columns_label) {
		this.columns_label = columns_label;
	}


}