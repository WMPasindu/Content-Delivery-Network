package com.pasindu.dev.assignment.node_one.entity;

import java.util.List;

public class NodeModel {
	public String name;
    public String region;
    public String master;
    public String folderPath;
    public List<Integer> nodeList;
	
	public NodeModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NodeModel(String name, String region, String master, String folderPath, List<Integer> nodeList) {
		super();
		this.name = name;
		this.region = region;
		this.master = master;
		this.folderPath = folderPath;
		this.nodeList = nodeList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public List<Integer> getNodeList() {
		return nodeList;
	}

	public void setNodeList(List<Integer> nodeList) {
		this.nodeList = nodeList;
	}
	
}
