package com.pasindu.dev.assignment.node_registry;

import java.util.List;

public interface IRegionService {
	void createRegionsList(NodeRegionModel nodeRegionModel);
	List<NodeRegionModel> getAll();
	void selectedNode(NodeRegionModel nodeRegionModel);
	NodeRegionModel getSelectedNode();
}
