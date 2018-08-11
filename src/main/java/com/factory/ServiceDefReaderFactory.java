package com.factory;

public class ServiceDefReaderFactory {

	public static IServiceDefReader getServiceDefReader(String type){
		if("default".equalsIgnoreCase(type)){
			return new ServiceDefLanguageReader();
		}else{
			return null;
		}
	}
}
