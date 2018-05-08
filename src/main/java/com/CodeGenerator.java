package com;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

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

	public static void main(String[] args) throws IOException {
		readConfigurationProperties();
		createDirectoryStructure();
		Map<String,String> map= readSDL();
		createResponseBean(map);
		copyResponseBean();
		createTemplateService(map);
		createServiceFromDefination(map);
		updateDockerfile(map);
		updateDeploymentYML(map);
		updateServiceYML(map);
		updatePOM(map);
		updateBootstrapProperties(map);
	}

	private static void readConfigurationProperties() {
		ResourceBundle mybundle = ResourceBundle.getBundle("com.configurations_en_US");
		projectPath = mybundle.getString("TARGET_PROJECT_PATH");
		srcFiles = mybundle.getString("TARGET_SOURCE_FILES");
		resourceFiles = mybundle.getString("TARGET_RESOURCE_FILES");
		dockerPath = mybundle.getString("TARGET_DOCKER_PATH");
		targetPath = mybundle.getString("TARGET_TARGET_PATH");
		settingsFile = mybundle.getString("TARGET_SETTINGS_FILE");
		mvnPath  = mybundle.getString("TARGET_MVN_PATH");
		srcSettings = mybundle.getString("SRC_SETTINGS");
		srcMvn  = mybundle.getString("SRC_MVN");
		srcTobCopied  = mybundle.getString("SRC_TO_BE_COPIED_FILES");
		srcProperties  = mybundle.getString("SRC_TO_BE_COPIED_PROPERTIES");
		srcDocker  = mybundle.getString("SRC_TO_BE_COPIED_DOCKER");
		srcTemplateService = mybundle.getString("SRC_TEMPLATE_SERVICE");
		srcSDLPath = mybundle.getString("SRC_SDL_PATH");
		srcJSONPath = mybundle.getString("SRC_RESPONSE_JSON_PATH");
		srcJavaPath = mybundle.getString("SRC_JAVA_PATH");
	}

	private static void updateBootstrapProperties(Map<String, String> map) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("C:\\msproject\\src\\main\\resources\\bootstrap.properties"));
		String content ="";
		String line = "";
		while((content = br.readLine()) != null){
			System.out.println("line="+content);
			line= line + content;
			line = line+"\n";			
		}
		line = line.replaceAll("template_name", map.get("name"));
		BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\msproject\\src\\main\\resources\\bootstrap.properties"));	
		bw.write(line);
		bw.flush();bw.close();
		
	}

	private static void updatePOM(Map<String, String> map) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("C:\\msproject\\pom.xml"));
		String content ="";
		String line = "";
		while((content = br.readLine()) != null){
			System.out.println("line="+content);
			line= line + content;
			line = line+"\n";			
		}
		line = line.replaceAll("template_group", map.get("name")+"-code_generation_automation");
		line = line.replaceAll("template_artifact", map.get("name"));
		line = line.replaceAll("template.jar", map.get("name")+".jar");
		String path = "C:\\msproject\\pom.xml";
		BufferedWriter bw = new BufferedWriter(new FileWriter(path));	
		bw.write(line);
		bw.flush();bw.close();
	}

	private static void updateServiceYML(Map<String, String> map) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("C:\\msproject\\templateservice.yaml"));
		String content ="";
		String line = "";
		while((content = br.readLine()) != null){
			System.out.println("line="+content);
			line= line + content;
			line = line+"\n";			
		}
		line = line.replaceAll("templateservice", map.get("name"));
		String path = "C:\\msproject\\"+map.get("name")+"service.yml";
		BufferedWriter bw = new BufferedWriter(new FileWriter(path));	
		bw.write(line);
		bw.flush();bw.close();
		File file = new File("C:\\msproject\\templateservice.yaml");
		file.deleteOnExit();
		
	}

	private static void updateDeploymentYML(Map<String, String> map) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("C:\\msproject\\templatedeployment.yaml"));
		String content ="";
		String line = "";
		while((content = br.readLine()) != null){
			System.out.println("line="+content);
			line= line + content;
			line = line+"\n";			
		}
		line = line.replaceAll("templatename", map.get("name"));
		String path = "C:\\msproject\\"+map.get("name")+"deployment.yml";
		BufferedWriter bw = new BufferedWriter(new FileWriter(path));	
		bw.write(line);
		bw.flush();bw.close();
		File file = new File("C:\\msproject\\templatedeployment.yaml");
		file.deleteOnExit();
		
	}

	private static void updateDockerfile(Map<String, String> map) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("C:\\msproject\\src\\main\\docker\\Dockerfile"));
		String content ="";
		String line = "";
		while((content = br.readLine()) != null){
			System.out.println("line="+content);
			line= line + content;
			line = line+"\n";			
		}
		line = line.replaceAll("template_app", map.get("name"));
		line = line.replaceAll("template_app.jar", map.get("name")+".jar");
		BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\msproject\\src\\main\\docker\\Dockerfile"));	
		bw.write(line);
		bw.flush();bw.close();
		
	}

	private static void copyResponseBean() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("C:\\software\\microservicesKubernetes\\msprojectgenerator\\src\\main\\java\\com\\Response.java"));
		String content ="";
		String line = "";
		while((content = br.readLine()) != null){
			System.out.println("line="+content);
			line= line + content;
			line = line+"\n";			
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\msproject\\src\\main\\java\\com\\Response.java"));	
		bw.write(line);
		bw.flush();bw.close();
	}

	private static void createResponseBean(Map<String, String> map) throws IOException {
		String responseBeanDef = map.get("UsageResponse");
		responseBeanDef = responseBeanDef.substring(1,responseBeanDef.length()-1);
		String jsonStr = "{";
		String[] strArray = responseBeanDef.split(",");
		for (int i=0 ; i<strArray.length;i++){
			 String tempStr = strArray[i];
			 String[] str = tempStr.split("=");
			 System.out.println(str[0]+" "+str[1]);
			 jsonStr = jsonStr +  str[0] +  ":" +  str[1] +",";
		}
		jsonStr = jsonStr.substring(0, jsonStr.length()-1)+ "}";
			System.out.println("jsonstr = "+jsonStr);
		BufferedWriter bw = new BufferedWriter(new FileWriter(srcJSONPath));	
		bw.write(jsonStr);
		bw.flush();bw.close();
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

	private static void createServiceFromDefination(Map<String, String> map) throws IOException {
		String serviceName = map.get("name")+"ServiceApplication.java";
		String filepath = srcFiles+"\\"+serviceName;
		BufferedReader br = new BufferedReader(new FileReader(filepath));
		String content ="";
		String line = "";
		while((content = br.readLine()) != null){
			System.out.println("line="+content);
			line= line + content;
			line = line+"\n";
			
		}
		String applnName = map.get("name")+"ServiceApplication";
		String contrpllerName = map.get("name")+"RestController";
	    String line1 = line.replace("TemplateServiceApplication".trim(), applnName);
	    line1 = line1.replaceAll("TemplateRestController".trim(), contrpllerName);
	    line1 = line1.replaceAll("templateMethod".trim(), "get"+map.get("name"));
	    BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));
	    bw.write(line1);
	    bw.flush();
	    bw.close();
	}


	private static void createTemplateService(Map<String, String> map) {
		
		String serviceName = map.get("name")+"ServiceApplication.java";
		String tagetDirectory = srcFiles+"\\"+serviceName;
	    	try{
	    	    BufferedReader br = new BufferedReader(new FileReader(srcTemplateService));
	    	    BufferedWriter bwriter = new BufferedWriter(new FileWriter(tagetDirectory));
	    	    String sCurrentLine;
	    	    while((sCurrentLine = br.readLine()) != null){
	    	    	System.out.println("sCurrentLine=="+sCurrentLine);
	    	    	bwriter.write(sCurrentLine);
	    	    	bwriter.newLine();
	    	    }
	    	    bwriter.flush();
	    	    bwriter.close();
	    	}catch(IOException e){
	    	    e.printStackTrace();
	    	}
	    }
		
	

	private static Map<String,String> readSDL() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(srcSDLPath));
		String sCurrentLine;
		Map<String,String> map = new HashMap<String,String>();

		while ((sCurrentLine = br.readLine()) != null) {
			System.out.println(sCurrentLine);
			String[] strray = sCurrentLine.split(":");
			System.out.print("key="+strray[0]);
			System.out.println("   value="+strray[1]);
			map.put(strray[0], strray[1]);
		}
		return map;
	}

	private static void createDirectoryStructure() {
		File file = new File(projectPath);
        if (!file.exists()) {
            file.mkdir();            
        }
        File srcJava = new File(srcFiles);
        if(!srcJava.exists()){
        	srcJava.mkdirs();
        }
        File resourcesDir = new File(resourceFiles);
        if(!resourcesDir.exists()){
        	resourcesDir.mkdirs();
        }
        File dockerDir = new File(dockerPath);
        if(!dockerDir.exists()){
        	dockerDir.mkdirs();
        }
        File targetDir = new File(targetPath);
        if (!targetDir.exists()) {
        	targetDir.mkdir();            
        }
        File settings = new File(settingsFile);
        if (!settings.exists()) {
        	settings.mkdir();            
        }
        File mvn = new File(mvnPath);
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
