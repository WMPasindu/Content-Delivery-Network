package com.pasindu.dev.assignment.node_three;



import model.NodeModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/node-service")
public class NodeThreeRequestService {

	@Value("${server.port}")
	private int portId;
	@Value("${node_id}")
	private int nodeId;
	@Value("${region}")
	private int regionId;
	@Value("${priority}")
	private String priority;
	private int[] portList = {6064, 6065};

	@Autowired
	private RestTemplate restTemplate;

	@RequestMapping("/request-region")
	public NodeRegionModel sendRegion() {
		System.out.println("Region #################### : ");
		NodeRegionModel nodeRegionModel = new NodeRegionModel(nodeId, portId, regionId, priority);
		return nodeRegionModel;
	}

	@RequestMapping("/request-election")
	public boolean getElectionRequest() {
		System.out.println("election #################### : ");
		try {
			sendElectMessageToAll();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@RequestMapping("/check-master/{port}")
	public void checkMaster(@PathVariable int port) {
		Boolean isMasterAvailable = restTemplate.getForObject("http://localhost:6060/registry-service/ping-master/" + port,
				Boolean.class);
		if (!isMasterAvailable) {
			sendElectMessageToAll();
		} else {
//			TODO : update configuration file with master node -> process to file upload or download request
			System.out.println("Master Still Available");
		}
	}

	@RequestMapping("/send-elect-message-to-all-nodes")
	public void sendElectMessageToAll() {
		Map<Integer, String> nodesAvailability = checkNodesAvailability(portList);
		System.out.println("Reached here ------- ##### " + nodesAvailability);

		if (nodesAvailability.size() <= 0) {
			System.out.println("portId ------- ##### " + portId);
			Boolean responseStatus = restTemplate.getForObject("http://localhost:6060/registry-service/i-am-master/" + portId,
					Boolean.class);
			System.out.println("I am the master " + portId + " shared :" + responseStatus);
		}
	}

	private Map<Integer, String> checkNodesAvailability(int[] portList) {
		Map<Integer, String> nodesStatus = new HashMap<>();
		for (int port : portList) {
			try (Socket socket = new Socket()) {
				socket.connect(new InetSocketAddress("localhost", port), 5000);
				nodesStatus.put(port, "OK");
			} catch (IOException e) {
				System.out.println(port + " KILLED"); // Either timeout or unreachable or failed DNS lookup.
			}
		}
		return nodesStatus;
	}

	@GetMapping("/download-file/{fileName}")
	private ResponseEntity<Resource> requestDownloadService(@PathVariable String fileName,
															HttpServletRequest request) {
		Resource resource;
		String fileBasePath = "E:\\DC_Assignment\\file_set\\upload_files\\node_3\\";
		Path path = Paths.get(fileBasePath + fileName);
		try {
			resource = new UrlResource(path.toUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}

		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			System.out.println("Could not determine file type.");
		}

		// Fallback to the default content type if type could not be determined
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	@RequestMapping("/files-list/{userRegion}")
	public Set<String> listFilesUsingJavaIO(@PathVariable int userRegion) {
		return Stream.of(new File("E:\\DC_Assignment\\file_set\\downloads\\node_one\\").listFiles())
				.filter(file -> !file.isDirectory())
				.map(File::getName)
				.collect(Collectors.toSet());
	}

	@PostMapping("/upload")
	public String singleFileUpload(@PathParam("file") MultipartFile file) {
		String UPLOADED_FOLDER = "E:\\DC_Assignment\\file_set\\upload_files\\node_3\\";
		try {
			// Get the file and save it somewhere
			byte[] bytes = file.getBytes();
			Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
			Files.write(path, bytes);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return "redirect:/uploadStatus";
	}

}
