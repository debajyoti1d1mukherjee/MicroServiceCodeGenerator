package com.factory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceDefLanguageReader implements IServiceDefReader {

	public  List<Map<String, String>> readDefination(String srcDirPath,String srcSDLPath) throws IOException{
		BufferedReader br = null;
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		File directory = new File(srcDirPath);
		File[] fileList = directory.listFiles();
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
			int count=0;

			while ((sCurrentLine = br.readLine()) != null) {
				if (null != sCurrentLine) {
					if (sCurrentLine.startsWith("'")) {
						sCurrentLine = sCurrentLine.substring(1);

					}
					if (sCurrentLine.endsWith("'")) {
						sCurrentLine = sCurrentLine.substring(0, sCurrentLine.length() - 1);
					}
					String[] strray = sCurrentLine.split(":");
					String key ="";
					if (strray != null && strray.length > 1) {
						if(strray[0].trim().contains("Method")){
							key = strray[0]+String.valueOf(count);
						}else{
							key = strray[0];
						}
						String value = strray[1];

						map.put(key, value);
						count++;

					}
				}
				
			}

			list.add(map);
		}
		return list;
	}

}
