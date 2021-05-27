package com.pasindu.dev.assignment.node_registry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/registry-service")
public class RegistryService {

//    http://localhost:6060/registry-service/request-region/0

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private IRegionService regionService;

    private int userRegion = 1;
    private int master = 0;
        private int[] portList = {6061, 6062, 6063, 6064, 6065};
//    private int[] portList = {6061, 6062, 6063};
//    private int[] portList = {6061};

    @RequestMapping("/registry-files-list")
    public Set<String> listFilesUsingJavaIO() {
        return Stream.of(new File("E:\\DC_Assignment\\file_set\\downloads\\registry\\download\\").listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }

    @RequestMapping("/request-region/{region}")
    public void pingToMasterDirectly(@PathVariable int region) {
        System.out.println("User Region : " + region);
        userRegion = region;
        if (userRegion >= 0) {
            getRegionsWithPriorities();
        }
    }

    @RequestMapping("/i-am-master/{master}")
    public boolean setMasterNode(@PathVariable int master) {
        try {
            this.master = master;
            System.out.println("Master elected. PORT : " + master);
            pingToMaster();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void getRegionsWithPriorities() {
        ArrayList<NodeRegionModel> nodeModel = new ArrayList<>();

        for (int port : portList) {
            NodeRegionModel nodeRegionModel =
                    restTemplate.getForObject("http://localhost:" + port + "/node-service/request-region",
                            NodeRegionModel.class);
            System.out.println("getRegionsWithPriorities : " + nodeRegionModel);
            nodeModel.add(new NodeRegionModel
                    (nodeRegionModel.getNodeId(), nodeRegionModel.getPortId(), nodeRegionModel.getRegionId(), nodeRegionModel.getPriority()));
        }
        addRegions(nodeModel);
    }

    public void addRegions(ArrayList<NodeRegionModel> nodeRegionModel) {
        for (NodeRegionModel nodeDetails : nodeRegionModel) {
            regionService.createRegionsList(nodeDetails);
        }
        votingService();
    }

    public void votingService() {
        regionService.getAll().forEach((node) -> {
            System.out.println("Region voted order : node : " +node.getNodeId()
                    + ", Region Id : " + node.getRegionId()
                    + ", Priority : " + node.getPriority()
                    + ", Port Id : "+ node.getPortId()
            );
            if (node.getRegionId() == userRegion && node.getPriority().equals("low")) {
                System.out.println("Selected Region Node : " + node.getNodeId() + ", Selected Region port : " + node.getPortId());
                regionService.selectedNode(node);
            }
        });
        pingToMaster();
    }

    private void pingToMaster() {
        if (master == 0) {
            requestElection();
        } else {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress("localhost", master), 5000);
                requestDownloadService();
            } catch (IOException e) {
                requestElection(); // Either timeout or unreachable or failed DNS lookup.
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void requestElection() {
        for (int port : portList) {
            Boolean status = restTemplate.getForObject("http://localhost:" + port + "/node-service/request-election",
                    Boolean.class);
            System.out.println("Response return : " + port + " : " + status);
        }
    }

    private Object requestDownloadService() {
        ResponseEntity responseEntity = restTemplate.getForEntity("http://localhost:" + regionService.getSelectedNode().getPortId() + "/node-service/download-file/hello.pdf",
                Resource.class);
        System.out.println("Resource type : " + responseEntity.getBody().toString());
        return responseEntity.getBody();
    }

    private void getFileListFromMaster() {
        ResponseEntity<String[]> responseEntity = restTemplate.getForEntity("http://localhost:" + master + "/node-service/files-list/"+userRegion,
                String[].class);
        List<String> object = Arrays.asList(responseEntity.getBody());
        System.out.println("file List from master node : " + object.size());
    }
}
