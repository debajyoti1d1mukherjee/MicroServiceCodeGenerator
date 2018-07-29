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

public class CodeGeneratorMultiplemethods {
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
	private static String srcJSONRequestPath = "";
	private static String srcJSONRespPath = "";
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
	private static Map<String, String> dockerPortMap = new HashMap<String, String>();
	private static ResourceBundle ports = null;
	private static String srcRootPath = "";

	public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
		readConfigurationProperties();
		List<Map<String, String>> serviceList = readSDL();
		createDirectoryStructure(serviceList);
		createTemplateService(serviceList);
		createServiceFromDefination(serviceList);
		updatePOM(serviceList);
		updateBootstrapProperties(serviceList);
		updatePropertiesForServerPort(0, ports);
		updateDockerfile(serviceList);
		updateDeploymentYML(serviceList);
		updateServiceYML(serviceList);
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
		srcJSONRequestPath = mybundle.getString("SRC_REQUEST_JSON_PATH");
		srcJSONRespPath = mybundle.getString("SRC_RESPONSE_JSON_PATH");
		srcJavaPath = mybundle.getString("SRC_JAVA_PATH");
		dockerAccount = mybundle.getString("DOCKER_ACCOUNT_NAME");
		srcRespObjectPath = mybundle.getString("SRC_RESPONSE_OBJECT_PATH_To_BE_COPIED");
		srcDirPath = mybundle.getString("SRC_SDL_DIRECTORY_PATH");
		codeCommitBatchPath = mybundle.getString("TARGET_CODE_COMMIT_BATCH_PATH");
		outputPath = mybundle.getString("SRC_EOP_FILE");
		projectPath = mybundle.getString("TARGET_PROJECT_PATH");
		srcRootPath = mybundle.getString("SRC_PROJECT_ROOT_PATH");
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

		for (String port : dockerPortMap.values()) {
			inUsePorts = inUsePorts + ":" + port;
		}
		System.out.println("inusePorts==" + inUsePorts);
		Properties p = new Properties();
		InputStream in = CodeGeneratorMultiplemethods.class.getResourceAsStream("ports_en_US.properties");
		System.out.println("in==" + in);
		p.load(in);
		String tempSrecRootPath = srcRootPath + "\\src\\main\\java\\com\\ports_en_US.properties";
		OutputStream outputStream = new FileOutputStream(tempSrecRootPath);
		p.setProperty("IN_USE_PORTS", inUsePorts);
		p.store(outputStream, null);
		outputStream.close();
		outputStream.flush();
	}

	private static void updateBootstrapProperties(List<Map<String, String>> list) throws IOException {
		int port = Integer.parseInt(dockerPort);
		for (Map<String, String> map : list) {
			port = port + 10;
			targetServiceProperties = projectPath + "\\" + map.get("name")
					+ "\\src\\main\\resources\\bootstrap.properties";
			BufferedReader br = new BufferedReader(new FileReader(targetServiceProperties));
			String content = "";
			String line = "";
			while ((content = br.readLine()) != null) {
				line = line + content;
				line = line + "\n";
			}
			line = line.replaceAll("template_name", map.get("name"));
			line = line.replaceAll("template_port", String.valueOf(port).trim().replaceAll("\\s+", ""));
			BufferedWriter bw = new BufferedWriter(new FileWriter(targetServiceProperties));
			bw.write(line);
			bw.flush();
			bw.close();
			dockerPortMap.put(map.get("name"), String.valueOf(port).trim());
		}

	}

	private static void updatePOM(List<Map<String, String>> list) throws IOException {
		for (Map<String, String> map : list) {
			targetMvnConfPath = projectPath + "\\" + map.get("name") + "\\pom.xml";
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
			targetTemplateServicePath = projectPath + "\\" + map.get("name") + "\\templateservice.yaml";
			BufferedReader br = new BufferedReader(new FileReader(targetTemplateServicePath));
			String content = "";
			String line = "";
			while ((content = br.readLine()) != null) {
				line = line + content;
				line = line + "\n";
			}
			String port = dockerPortMap.get(map.get("name"));
			line = line.replaceAll("templateservice", map.get("name").trim());
			line = line.replaceAll("templateport", dockerPortMap.get(map.get("name")));
			String path = projectPath + "\\" + map.get("name") + "\\" + map.get("name") + "service.yml";
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
			targetTemplateDeploymentPath = projectPath + "\\" + map.get("name") + "\\templatedeployment.yaml";
			BufferedReader br = new BufferedReader(new FileReader(targetTemplateDeploymentPath));
			String content = "";
			String line = "";
			while ((content = br.readLine()) != null) {
				line = line + content;
				line = line + "\n";
			}
			String port = dockerPortMap.get(map.get("name"));
			line = line.replaceAll("templatename", map.get("name").trim());
			line = line.replaceAll("templateport", dockerPortMap.get(map.get("name")));
			line = line.replaceAll("templateimage", dockerAccount + "\\/" + map.get("name").trim().toLowerCase());
			String path = projectPath + "\\" + map.get("name") + "\\" + map.get("name") + "deployment.yml";
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
			targetDockerFilePath = projectPath + "\\" + map.get("name") + "\\src\\main\\docker\\Dockerfile";
			File targetDockerFile = new File(targetDockerFilePath);
			BufferedReader br = new BufferedReader(new FileReader(targetDockerFilePath));
			String content = "";
			String line = "";
			while ((content = br.readLine()) != null) {
				line = line + content;
				line = line + "\n";
			}
			line = line.replaceAll("template_app", map.get("name"));
			String replaceWith = "/target/" + map.get("name") + "-0.0.1-SNAPSHOT.jar";
			System.out.println("replaceWith===" + replaceWith);
			line = line.replaceAll("/target/XtemplateX_app.jar", replaceWith);
			line = line.replaceAll("template_port", dockerPortMap.get(map.get("name")));
			BufferedWriter bw = new BufferedWriter(new FileWriter(targetDockerFilePath));
			bw.write(line);
			bw.flush();
			bw.close();
		}
	}



	private static void createServiceFromDefination(List<Map<String, String>> list) throws IOException {
		for (Map<String, String> map : list) {
			String serviceName = map.get("name") + "ServiceApplication.java";
			String filepath = projectPath + "\\" + map.get("name") + "\\src\\main\\java\\com" + "\\" + serviceName;
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			String content = "";
			String line = "";
			while ((content = br.readLine()) != null) {
				line = line + content;
				line = line + "\n";

			}
			String beforeTemplateMethod = TemplateTokenizer.getBeforeTemplateMethod(filepath);
			String templateGetMethod = TemplateTokenizer.getTemplateGetMethod(filepath);
			String templatePostMethod = TemplateTokenizer.getTemplatePostMethod(filepath);
			String aftertemplateMethods = TemplateTokenizer.getAfterTemplateMethodS(filepath);
			String getFallBackMethod = TemplateTokenizer.getTemplateGetFallBacktMethod(filepath);
			String postFallBackMethod = TemplateTokenizer.getTemplatePostFallBacktMethod(filepath);

			String applnName = map.get("name") + "ServiceApplication";
			String controllerName = map.get("name") + "RestController";
			String targetClassAppender = beforeTemplateMethod.replace("TemplateServiceApplication".trim(), applnName);
			targetClassAppender = targetClassAppender.replaceAll("TemplateRestController".trim(), controllerName);
			Set<Map.Entry<String, String>> set = map.entrySet();
			String methodAppender = "";
			for (Map.Entry<String, String> me : set) {
				if (me.getKey().contains("Method")) {
					String methodStr = me.getValue();
					String[] methodStrArr = methodStr.split(";");
					System.out.println("methodStrArr===" + methodStrArr);
					String methodType = methodStrArr[0];
					String methodName = methodStrArr[1];
					String request = methodStrArr[2];
					String response = methodStrArr[3];
					String fallBckMethod = methodStrArr[4];
					String[] requestArr = request.split("&");
					String requestName = requestArr[0];
					String requestDef = requestArr[1];
					String[] responseArr = response.split("&");
					String responseName = responseArr[0];
					String responseDef = responseArr[1];
					//String[] fallBckMethodArray = fallBckMethod.split("=");
					//String fallBackMethodRequired = fallBckMethodArray[1];


					//methodAppender = createMethod(map, templateGetMethod, templatePostMethod, methodAppender,
					//		methodType, methodName, requestName, requestDef, responseName, responseDef,getFallBackMethod,postFallBackMethod);
					
					methodAppender = ServiceAssembler.createMethod(map, templateGetMethod, templatePostMethod, methodAppender, methodType, methodName, 
							requestName, requestDef, responseName, responseDef, getFallBackMethod, postFallBackMethod, 
							srcRespObjectPath, projectPath, "", srcJSONRequestPath, srcJSONRespPath, srcJavaPath);

				}
			}
			targetClassAppender = targetClassAppender + "\n" + methodAppender;
			targetClassAppender = targetClassAppender + "\n" + aftertemplateMethods;
			BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));
			bw.write(targetClassAppender);
			bw.flush();
			bw.close();
		}
	}





	private static void createTemplateService(List<Map<String, String>> list) {
		for (Map<String, String> map : list) {
			String serviceName = map.get("name") + "ServiceApplication.java";
			String tagetDirectory = projectPath + "\\" + map.get("name") + "\\src\\main\\java\\com\\" + serviceName;
			File targetServiceFile = new File(tagetDirectory);
			try {
				String tempSrecRootPath = srcRootPath + "\\src\\main\\java\\com\\TemplateServiceMultipleMethods";
				BufferedReader br = new BufferedReader(new FileReader(
						tempSrecRootPath));
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
		System.out.println("Number of Files====" + fileList.length);
		for (File sdlFile : fileList) {
			// File sdlFile = fileList[0];
			if (null != sdlFile) {
				br = new BufferedReader(new FileReader(sdlFile));
			} else {
				br = new BufferedReader(new FileReader(srcSDLPath));
			}

			String sCurrentLine;
			Map<String, String> map = new HashMap<String, String>();
			Map<String, String> methodMap = new HashMap<String, String>();

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
						String key = strray[0];
						String value = strray[1];

						map.put(key, value);

					}
				}
			}

			list.add(map);
			System.out.println("Number of map in list=" + list.size());
		}
		return list;
	}

	private static void createDirectoryStructure(List<Map<String, String>> list) {
		for (Map<String, String> map : list) {
			File file = new File(projectPath + "\\" + map.get("name"));
			if (!file.exists()) {
				file.mkdir();
			}
			File srcJava = new File(projectPath + "\\" + map.get("name") + "\\src\\main\\java\\com");
			if (!srcJava.exists()) {
				srcJava.mkdirs();
			}
			File resourcesDir = new File(projectPath + "\\" + map.get("name") + "\\src\\main\\resources");
			if (!resourcesDir.exists()) {
				resourcesDir.mkdirs();
			}
			File dockerDir = new File(projectPath + "\\" + map.get("name") + "\\src\\main\\docker");
			if (!dockerDir.exists()) {
				dockerDir.mkdirs();
				System.out.println("dockerDir=====" + dockerDir);
			}
			File targetDir = new File(projectPath + "\\" + map.get("name") + "\\target");
			if (!targetDir.exists()) {
				targetDir.mkdir();
			}
			File settings = new File(projectPath + "\\" + map.get("name") + "\\.settings");
			if (!settings.exists()) {
				settings.mkdir();
			}
			File mvn = new File(projectPath + "\\" + map.get("name") + "\\.mvn");
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
