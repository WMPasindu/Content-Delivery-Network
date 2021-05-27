package com.pasindu.dev.assignment.node_registry;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegionServiceImpl implements IRegionService {

	private NodeRegionModel nodeRegionModel;
	private ArrayList<NodeRegionModel> regionsArrayList = new ArrayList<NodeRegionModel>();

	@Override
	public void createRegionsList(NodeRegionModel nodeRegionModel) {
		regionsArrayList.add(nodeRegionModel);
	}

	@Override
	public List<NodeRegionModel> getAll() {
		return regionsArrayList;
	}

	@Override
	public void selectedNode(NodeRegionModel nodeRegionModel) {
		this.nodeRegionModel = nodeRegionModel;
	}

	@Override
	public NodeRegionModel getSelectedNode() {
		return this.nodeRegionModel;
	}
}
