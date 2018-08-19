package com.factory;

import java.util.List;
import java.util.Map;

public interface IServiceDefReader {

	public  List<Map<String, String>> readDefination(String srcDirPath)throws Exception;
}
