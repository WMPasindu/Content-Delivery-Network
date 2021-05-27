package com.pasindu.dev.assignment.node_two;

/**
 * @author Pasindu Weerakoon
 *
 */
public class NodeRegionModel {

	private int nodeId;
	private int portId;
	private int regionId;
	private String priority;

	public NodeRegionModel() {
	}

	public NodeRegionModel(int nodeId, int portId, int regionId, String priority) {
		super();
		this.nodeId = nodeId;
		this.portId = portId;
		this.regionId = regionId;
		this.priority = priority;
	}

	public int getRegionId() {
		return regionId;
	}

	public void setRegionId(int region) {
		this.regionId = region;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public int getPortId() {
		return portId;
	}

	public void setPortId(int portId) {
		this.portId = portId;
	}
}
