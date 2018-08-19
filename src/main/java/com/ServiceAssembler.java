package com;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import beangenerator.JsonToPojo;

public class ServiceAssembler {

	public static String createMethod(Map<String, String> map, String templateGetMethod, String templatePostMethod,
			String methodAppender, String methodType, String methodName, String requestName, String requestDef,
			String responseName, String responseDef , String getFallBckMethod , String postFallBckMethod , String respObjPath , String projPath , String targetRespBean, 
			String srcJSONRequestPath , String srcJSONRespPath , String srcJavaPath , String fallbackRequired) throws IOException {
		if ("GET".equalsIgnoreCase(methodType)) {
			createBean(responseDef, map.get("name"), "response", responseName , null, srcJSONRespPath,srcJavaPath);
			String tempGetMethod = templateGetMethod;
			tempGetMethod = tempGetMethod.replaceAll("templateMethod", methodName);
			String capRespName = responseName.substring(0, 1).toUpperCase() + responseName.substring(1);
			tempGetMethod = tempGetMethod.replaceAll("TemplateResponse", "com." + map.get("name") + "." + capRespName);
			copyBean("com\\" + map.get("name"), map.get("name"), capRespName , respObjPath , projPath , targetRespBean , srcJSONRequestPath);
			tempGetMethod = tempGetMethod.replaceAll("TEMPLATE_METHOD_TYPE", methodType);
			methodAppender = methodAppender + "\n";
			methodAppender = methodAppender + tempGetMethod;
			if("Y".equalsIgnoreCase(fallbackRequired)){
				methodAppender = appendGetFallBackMethod(map, methodAppender, methodName, getFallBckMethod, capRespName);
			}
			
		} else if ("POST".equalsIgnoreCase(methodType)) {
			createBean(requestDef, map.get("name"), "request", requestName ,srcJSONRequestPath , null,srcJavaPath);
			createBean(responseDef, map.get("name"), "response", responseName,null , srcJSONRespPath, srcJavaPath);
			String tempPostMethod = templatePostMethod;
			tempPostMethod = tempPostMethod.replaceAll("templateMethod", methodName);
			String capRespName = responseName.substring(0, 1).toUpperCase() + responseName.substring(1);
			String capReqName = requestName.substring(0, 1).toUpperCase() + requestName.substring(1);
			copyBean("com\\" + map.get("name"), map.get("name"), capRespName , respObjPath , projPath , targetRespBean , srcJSONRequestPath);
			copyBean("com\\" + map.get("name"), map.get("name"), capReqName , respObjPath , projPath , targetRespBean , srcJSONRequestPath);
			tempPostMethod = tempPostMethod.replaceAll("TemplateResponse",
					"com." + map.get("name") + "." + capRespName);
			tempPostMethod = tempPostMethod.replaceAll("TempleteRequest", "com." + map.get("name") + "." + capReqName);
			tempPostMethod = tempPostMethod.replaceAll("TEMPLATE_METHOD_TYPE", methodType);
			methodAppender = methodAppender + "\n";
			methodAppender = methodAppender + tempPostMethod;
			if("Y".equalsIgnoreCase(fallbackRequired)){
				methodAppender = appendPostFallBackMethod(map, methodAppender, methodName, postFallBckMethod, capRespName,
					capReqName);
			}
		}
		return methodAppender;
	}
	
	private static void copyBean(String path, String serviceName, String beanName , String srcRespObjectPath, String projectPath,String targetRespBean, String srcJSONRequestPath) throws IOException {

		String scrPath = srcRespObjectPath + path + "\\" + beanName + ".java";
		BufferedReader br = new BufferedReader(new FileReader(scrPath));
		String content = "";
		String line = "";
		while ((content = br.readLine()) != null) {
			line = line + content;
			line = line + "\n";
		}
		File f = new File(projectPath + "\\" + serviceName + "\\src\\main\\java\\" + path + "\\");
		if (!f.exists()) {
			f.mkdirs();
		}
		targetRespBean = projectPath + "\\" + serviceName + "\\src\\main\\java\\" + path + "\\" + beanName + ".java";
		File file = new File(targetRespBean);
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		bw.write(line);
		bw.flush();
		bw.close();

	}
	
	private static String appendPostFallBackMethod(Map<String, String> map, String methodAppender, String methodName,
			String postFallBckMethod, String capRespName, String capReqName) {
		String tempPostFallBackMethod = postFallBckMethod;
		tempPostFallBackMethod = tempPostFallBackMethod.replaceAll("templateMethod", methodName+"FallBack");
		tempPostFallBackMethod = tempPostFallBackMethod.replaceAll("TempleteRequest", "com." + map.get("name") + "." + capReqName);
		tempPostFallBackMethod = tempPostFallBackMethod.replaceAll("TemplateResponse",
				"com." + map.get("name") + "." + capRespName);
		methodAppender = methodAppender + "\n";
		methodAppender = methodAppender + tempPostFallBackMethod;
		return methodAppender;
	}

	private static String appendGetFallBackMethod(Map<String, String> map, String methodAppender, String methodName,
			String getFallBckMethod, String capRespName) {
		String tempGetFallBckMethod = getFallBckMethod;
		tempGetFallBckMethod = tempGetFallBckMethod.replaceAll("templateMethod", methodName+"FallBack");
		tempGetFallBckMethod = tempGetFallBckMethod.replaceAll("TemplateResponse", "com." + map.get("name") + "." + capRespName);
		methodAppender = methodAppender + "\n";
		methodAppender = methodAppender + tempGetFallBckMethod;
		return methodAppender;
	}
	
	private static void createBean(String beanDefination, String serviceName, String type, String name , String srcJSONRequestPath , String srcJSONRespPath, String srcJavaPath)
			throws IOException {
		String path = "";
		if ("request".equalsIgnoreCase(type)) {
			path = srcJSONRequestPath;
			path = path.substring(0, path.indexOf("request.json"));
			path = path + name;
		} else if ("response".equalsIgnoreCase(type)) {
			path = srcJSONRespPath;
			path = path.substring(0, path.indexOf("response.json"));
			path = path + name;
		}
		String beanDef = "";

		beanDef = beanDefination.substring(1, beanDefination.length() - 1);
		String jsonStr = "{";
		String[] strArray = beanDef.split(",");
		for (int i = 0; i < strArray.length; i++) {
			String tempStr = strArray[i];
			String[] str = tempStr.split("=");
			jsonStr = jsonStr + "\"" + str[0] + "\"" + ":" + "\"" + str[1] + "\"" + ",";
		}
		jsonStr = jsonStr.substring(0, jsonStr.length() - 1) + "}";
		BufferedWriter bw = new BufferedWriter(new FileWriter(path));
		bw.write(jsonStr);
		bw.flush();
		bw.close();
		File inputJson = new File(path);
		File outputPojoDirectory = new File(srcJavaPath);
		outputPojoDirectory.mkdirs();
		try {
			new JsonToPojo().convert2JSON(inputJson.toURI().toURL(), outputPojoDirectory, "com." + serviceName,
					inputJson.getName().replace(".json", ""));
		} catch (IOException e) {
			System.out.println("Encountered issue while converting to pojo: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
