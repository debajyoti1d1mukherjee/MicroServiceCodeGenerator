package com;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import beangenerator.JsonToPojo;

public class CodeGenerator {
	private static String projectPath = "";
	private static String srcFiles = "";
	private static String resourceFiles = "";
	private static String dockerPath = "";
	private static String targetPath = "";
	private static String settingsFile = "";
	private static String mvnPath = "";
	private static String srcSettings = "";
	private static String srcMvn = "";
	private static String srcTobCopied = "";
	private static String srcProperties = "";
	private static String srcDocker = "";
	private static String srcTemplateService = "";
	private static String srcSDLPath = "";
	private static String srcJSONPath = "";
	private static String srcJavaPath = "";
	private static String inUsePorts = "";
	private static String unUsedPort = "";
	private static String dockerAccount = "";
	private static String targetServiceProperties = "";
	private static String targetMvnConfPath = "";
	private static String targetTemplateServicePath = "";
	private static String targetTemplateDeploymentPath = "";
	private static String targetDockerFilePath = "";
	private static String srcRespObjectPath = "";
	private static String srcDirPath = "";
	private static String codeCommitBatchPath = "";
	private static String outputPath = "";
	private static String dockerPort = "";
	private static String targetRespBean = "";
	private static Map<String,String> dockerPortMap = new HashMap<String,String>();
	private static ResourceBundle ports = null;

	public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
		readConfigurationProperties();
		List<Map<String, String>> serviceList = readSDL();
		createDirectoryStructure(serviceList);
		createResponseBean(serviceList);
		copyResponseBean(serviceList);
		createTemplateService(serviceList);
		createServiceFromDefination(serviceList);
		updatePOM(serviceList);
		updateBootstrapProperties(serviceList);
		updatePropertiesForServerPort(0, ports);
		updateDockerfile(serviceList);
		updateDeploymentYML(serviceList);
		updateServiceYML(serviceList);
		//writeOutputFile();
	}

	private static void readConfigurationProperties() throws IOException, URISyntaxException {
		int count = 0;
		ResourceBundle mybundle = ResourceBundle.getBundle("com.configurations_en_US");
		ports = ResourceBundle.getBundle("com.ports_en_US");
		srcSettings = mybundle.getString("SRC_SETTINGS");
		srcMvn = mybundle.getString("SRC_MVN");
		srcTobCopied = mybundle.getString("SRC_TO_BE_COPIED_FILES");
		srcProperties = mybundle.getString("SRC_TO_BE_COPIED_PROPERTIES");
		srcDocker = mybundle.getString("SRC_TO_BE_COPIED_DOCKER");
		srcTemplateService = mybundle.getString("SRC_TEMPLATE_SERVICE");
		srcSDLPath = mybundle.getString("SRC_SDL_PATH");
		srcJSONPath = mybundle.getString("SRC_RESPONSE_JSON_PATH");
		srcJavaPath = mybundle.getString("SRC_JAVA_PATH");
		dockerAccount = mybundle.getString("DOCKER_ACCOUNT_NAME");
		srcRespObjectPath = mybundle.getString("SRC_RESPONSE_OBJECT_PATH");
		srcDirPath = mybundle.getString("SRC_SDL_DIRECTORY_PATH");
		codeCommitBatchPath = mybundle.getString("TARGET_CODE_COMMIT_BATCH_PATH");
		outputPath = mybundle.getString("SRC_EOP_FILE");
		projectPath = mybundle.getString("TARGET_PROJECT_PATH");
		getLastUsedDockerPort(count, ports);
	}

	private static void writeOutputFile() throws IOException {
		// File f = new
		// File("C:\\software\\microservicesKubernetes\\CodeGenerator\\output_yml\\test.txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath));
		bw.write("output generated...");
		bw.flush();
		bw.close();
	}

	private static void getLastUsedDockerPort(int count, ResourceBundle ports)
			throws IOException, FileNotFoundException, URISyntaxException {
		inUsePorts = ports.getString("IN_USE_PORTS");
		inUsePorts = inUsePorts.trim();
		String[] portsArray = inUsePorts.split(":");
		for (String port : portsArray) {
			count++;
		}
		unUsedPort = portsArray[count - 1].trim();
		int portVal = Integer.parseInt(unUsedPort.trim()) + 10;
		dockerPort = String.valueOf(portVal);		
	}
	
	private static void updatePropertiesForServerPort(int count, ResourceBundle ports)
			throws IOException, FileNotFoundException, URISyntaxException {

		for (String port : dockerPortMap.values()){
			inUsePorts = inUsePorts + ":" + port;
		}
		System.out.println("inusePorts==" + inUsePorts);
		Properties p = new Properties();
		InputStream in = CodeGenerator.class.getResourceAsStream("ports_en_US.properties");
		System.out.println("in==" + in);
		p.load(in);
		OutputStream outputStream = new FileOutputStream(
				"C:\\software\\microservicesKubernetes\\msprojectgenerator\\src\\main\\java\\com\\ports_en_US.properties");
		p.setProperty("IN_USE_PORTS", inUsePorts);
		p.store(outputStream, null);
		outputStream.close();
		outputStream.flush();
	}

	private static void updateBootstrapProperties(List<Map<String, String>> list) throws IOException {		
		int port = Integer.parseInt(dockerPort);
		for (Map<String, String> map : list) {
			port = port + 10;
			targetServiceProperties = projectPath + "\\"+map.get("name")+"\\src\\main\\resources\\bootstrap.properties";
			BufferedReader br = new BufferedReader(new FileReader(targetServiceProperties));
			String content = "";
			String line = "";
			while ((content = br.readLine()) != null) {
				line = line + content;
				line = line + "\n";
			}
			line = line.replaceAll("template_name", map.get("name"));
			line = line.replaceAll("template_port", String.valueOf(port));
			BufferedWriter bw = new BufferedWriter(new FileWriter(targetServiceProperties));
			bw.write(line);
			bw.flush();
			bw.close();
			dockerPortMap.put(map.get("name"), String.valueOf(port));
		}

	}

	private static void updatePOM(List<Map<String, String>> list) throws IOException {
		for (Map<String, String> map : list) {
			targetMvnConfPath = projectPath + "\\"+map.get("name")+ "\\pom.xml";
			BufferedReader br = new BufferedReader(new FileReader(targetMvnConfPath));
			String content = "";
			String line = "";
			while ((content = br.readLine()) != null) {
				line = line + content;
				line = line + "\n";
			}
			line = line.replaceAll("template_group", map.get("name") + "-code_generation_automation");
			line = line.replaceAll("template_artifact", map.get("name"));
			line = line.replaceAll("template.jar", map.get("name") + ".jar");
			line = line.replaceAll("template_image", map.get("name"));
			line = line.replaceAll("template_name", map.get("name"));
			String path = targetMvnConfPath;
			BufferedWriter bw = new BufferedWriter(new FileWriter(path));
			bw.write(line);
			bw.flush();
			bw.close();
		}
	}

	private static void updateServiceYML(List<Map<String, String>> list) throws IOException {
		for (Map<String, String> map : list) {
			targetTemplateServicePath = projectPath + "\\"+map.get("name")+ "\\templateservice.yaml";
			BufferedReader br = new BufferedReader(new FileReader(targetTemplateServicePath));
			String content = "";
			String line = "";
			while ((content = br.readLine()) != null) {
				line = line + content;
				line = line + "\n";
			}
			String port = dockerPortMap.get(map.get("name"));
			line = line.replaceAll("templateservice", map.get("name"));
			line = line.replaceAll("templateport", dockerPortMap.get(map.get("name")));
			String path = projectPath + "\\" +map.get("name") +"\\"+map.get("name") + "service.yml";
			BufferedWriter bw = new BufferedWriter(new FileWriter(path));
			bw.write(line);
			bw.flush();
			bw.close();
			File file = new File(targetTemplateServicePath);
			file.deleteOnExit();
		}

	}

	private static void updateDeploymentYML(List<Map<String, String>> list) throws IOException {
		for (Map<String, String> map : list) {
			targetTemplateDeploymentPath = projectPath + "\\"+map.get("name")+ "\\templatedeployment.yaml";
			BufferedReader br = new BufferedReader(new FileReader(targetTemplateDeploymentPath));
			String content = "";
			String line = "";
			while ((content = br.readLine()) != null) {
				line = line + content;
				line = line + "\n";
			}
			String port = dockerPortMap.get(map.get("name"));
			line = line.replaceAll("templatename", map.get("name"));
			line = line.replaceAll("templateport", dockerPortMap.get(map.get("name")));
			line = line.replaceAll("templateimage", dockerAccount + "\\\\" + map.get("name"));
			String path = projectPath + "\\" +map.get("name") +"\\"+map.get("name") +"deployment.yml";
			BufferedWriter bw = new BufferedWriter(new FileWriter(path));
			bw.write(line);
			bw.flush();
			bw.close();
			File file = new File(targetTemplateDeploymentPath);
			file.deleteOnExit();
		}

	}

	private static void updateDockerfile(List<Map<String, String>> list) throws IOException {
		
		for (Map<String, String> map : list) {
			targetDockerFilePath = projectPath + "\\"+map.get("name")+ "\\src\\main\\docker\\Dockerfile";
			File targetDockerFile = new File(targetDockerFilePath);
			BufferedReader br = new BufferedReader(new FileReader(targetDockerFilePath));
			String content = "";
			String line = "";
			while ((content = br.readLine()) != null) {
				line = line + content;
				line = line + "\n";
			}
			line = line.replaceAll("template_app", map.get("name"));
			line = line.replaceAll("template_app.jar", map.get("name") + ".jar");
			line = line.replaceAll("template_port", dockerPortMap.get(map.get("name")));
			BufferedWriter bw = new BufferedWriter(new FileWriter(targetDockerFilePath));
			bw.write(line);
			bw.flush();
			bw.close();
		}
	}

	private static void copyResponseBean(List<Map<String, String>> list) throws IOException {
		for(Map<String, String> map : list ){
		BufferedReader br = new BufferedReader(new FileReader(srcRespObjectPath));
		String content = "";
		String line = "";
		while ((content = br.readLine()) != null) {
			line = line + content;
			line = line + "\n";
		}
		targetRespBean = projectPath+ "\\"+ map.get("name")+ "\\src\\main\\java\\com\\Response.java";
		BufferedWriter bw = new BufferedWriter(new FileWriter(targetRespBean));
		bw.write(line);
		bw.flush();
		bw.close();
		}
	}

	private static void createResponseBean(List<Map<String, String>> list) throws IOException {
		// String responseBeanDef = map.get("UsageResponse");
		String responseBeanDef = "";
		for (Map<String, String> map : list) {
			Set<Map.Entry<String, String>> set = map.entrySet();
			for (Map.Entry<String, String> me : set) {
				if (me.getKey().contains("Response")) {
					responseBeanDef = me.getValue();
				}
			}
			responseBeanDef = responseBeanDef.substring(1, responseBeanDef.length() - 1);
			String jsonStr = "{";
			String[] strArray = responseBeanDef.split(",");
			for (int i = 0; i < strArray.length; i++) {
				String tempStr = strArray[i];
				String[] str = tempStr.split("=");
				jsonStr = jsonStr + "\"" + str[0] + "\"" + ":" + "\"" + str[1] + "\"" + ",";
			}
			jsonStr = jsonStr.substring(0, jsonStr.length() - 1) + "}";
			BufferedWriter bw = new BufferedWriter(new FileWriter(srcJSONPath));
			bw.write(jsonStr);
			bw.flush();
			bw.close();
			File inputJson = new File(srcJSONPath);
			File outputPojoDirectory = new File(srcJavaPath);
			outputPojoDirectory.mkdirs();
			try {
				new JsonToPojo().convert2JSON(inputJson.toURI().toURL(), outputPojoDirectory, "com",
						inputJson.getName().replace(".json", ""));
			} catch (IOException e) {
				System.out.println("Encountered issue while converting to pojo: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private static void createServiceFromDefination(List<Map<String, String>> list) throws IOException {
		for (Map<String, String> map : list) {
			String serviceName = map.get("name") + "ServiceApplication.java";
			String filepath = projectPath +  "\\" + map.get("name")+"\\src\\main\\java\\com" + "\\" + serviceName;
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			String content = "";
			String line = "";
			while ((content = br.readLine()) != null) {
				line = line + content;
				line = line + "\n";

			}
			String applnName = map.get("name") + "ServiceApplication";
			String contrpllerName = map.get("name") + "RestController";
			String line1 = line.replace("TemplateServiceApplication".trim(), applnName);
			line1 = line1.replaceAll("TemplateRestController".trim(), contrpllerName);
			line1 = line1.replaceAll("templateMethod".trim(), "get" + map.get("name"));
			BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));
			bw.write(line1);
			bw.flush();
			bw.close();
		}
	}

	private static void createTemplateService(List<Map<String, String>> list) {
		for (Map<String, String> map : list) {
			String serviceName = map.get("name") + "ServiceApplication.java";
			String tagetDirectory = projectPath + "\\"+ map.get("name")+ "\\src\\main\\java\\com\\"+serviceName;
			File targetServiceFile = new File(tagetDirectory);
			try {
				BufferedReader br = new BufferedReader(new FileReader(srcTemplateService));
				BufferedWriter bwriter = new BufferedWriter(new FileWriter(targetServiceFile));
				String sCurrentLine;
				while ((sCurrentLine = br.readLine()) != null) {
					bwriter.write(sCurrentLine);
					bwriter.newLine();
				}
				bwriter.flush();
				bwriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static List<Map<String, String>> readSDL() throws IOException {
		BufferedReader br = null;
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		File directory = new File(srcDirPath);
		File[] fileList = directory.listFiles();
		System.out.println("Number of Files===="+fileList.length);
		for (File sdlFile : fileList) {
			// File sdlFile = fileList[0];
			if (null != sdlFile) {
				br = new BufferedReader(new FileReader(sdlFile));
			} else {
				br = new BufferedReader(new FileReader(srcSDLPath));
			}

			String sCurrentLine;
			Map<String, String> map = new HashMap<String, String>();

			while ((sCurrentLine = br.readLine()) != null) {
				if (null != sCurrentLine) {
					if (sCurrentLine.startsWith("'")) {
						sCurrentLine = sCurrentLine.substring(1);

					}
					if (sCurrentLine.endsWith("'")) {
						sCurrentLine = sCurrentLine.substring(0, sCurrentLine.length() - 1);
					}
					String[] strray = sCurrentLine.split(":");
					if (strray != null && strray.length > 1) {
						map.put(strray[0], strray[1]);
					}
				}
			}
			list.add(map);
			System.out.println("Number of map in list="+list.size());
		}
		return list;
	}

	private static void createDirectoryStructure(List<Map<String, String>> list) {
		for (Map<String, String> map : list) {
			File file = new File(projectPath + "\\" + map.get("name"));
			if (!file.exists()) {
				file.mkdir();
			}
			File srcJava = new File(projectPath +  "\\" + map.get("name")+"\\src\\main\\java\\com");
			if (!srcJava.exists()) {
				srcJava.mkdirs();
			}
			File resourcesDir = new File(projectPath + "\\" + map.get("name")+"\\src\\main\\resources");
			if (!resourcesDir.exists()) {
				resourcesDir.mkdirs();
			}
			File dockerDir = new File(projectPath +  "\\" + map.get("name")+"\\src\\main\\docker");
			if (!dockerDir.exists()) {
				dockerDir.mkdirs();
				System.out.println("dockerDir=====" + dockerDir);
			}
			File targetDir = new File(projectPath +  "\\" + map.get("name")+"\\target");
			if (!targetDir.exists()) {
				targetDir.mkdir();
			}
			File settings = new File(projectPath + "\\" + map.get("name")+ "\\.settings");
			if (!settings.exists()) {
				settings.mkdir();
			}
			File mvn = new File(projectPath + "\\" + map.get("name")+ "\\.mvn");
			if (!mvn.exists()) {
				mvn.mkdir();
			}
			try {
				FileUtils.copyDirectory(new File(srcSettings), settings);
				FileUtils.copyDirectory(new File(srcMvn), mvn);
				FileUtils.copyDirectory(new File(srcTobCopied), file);
				FileUtils.copyDirectory(new File(srcProperties), resourcesDir);
				FileUtils.copyDirectory(new File(srcDocker), dockerDir);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
