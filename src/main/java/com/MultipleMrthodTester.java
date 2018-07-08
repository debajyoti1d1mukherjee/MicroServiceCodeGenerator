package com;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MultipleMrthodTester {

	public static void main(String[] args) throws IOException {
		// C:\software\microservicesKubernetes\msprojectgenerator\src\main\java\com\TemplateService
		BufferedReader br = new BufferedReader(new FileReader("C:\\software\\microservicesKubernetes\\msprojectgenerator\\src\\main\\java\\com\\TemplateServiceMultipleMethods"));
		String content = "";
		String line = "";
		while ((content = br.readLine()) != null) {
	
				line = line + content;
				line = line + "\n";


		}
		int x = line.indexOf("//TEMPLATE_GET_METHOD_START");
		int y = line.lastIndexOf("//TEMPLATE_GET_METHOD_END");
		String getLine = line.substring(x, y);
		//line= line + "}";
		System.out.println("LINE GET==="+getLine);
		
		x = line.indexOf("//TEMPLATE_POST_METHOD_START");
		y = line.lastIndexOf(" //TEMPLATE_POST_METHOD_END");
		String postLine = line.substring(x, y);
		System.out.println("LINE POST==="+postLine);
		
		x = line.indexOf("//TEMPLATE_POST_METHOD_END");
		String remainingClass = line.substring(x);
		System.out.println("LINE remaining==="+remainingClass);
		
		x = line.indexOf("package");
		y = line.lastIndexOf("//TEMPLATE_GET_METHOD_START");
		String beforeMethods = line.substring(x, y);
		System.out.println("LINE POST==="+beforeMethods);
		
		
		
//		
//		int a = line.indexOf("//TEMPLATE METHOD END");
//		System.out.println("a=="+a);
//		line = line.substring(a);
//		System.out.println("LINE==="+line);
		
		String str = "findBalance;findBalanceRequest&{id=String,name=String};findBalanceResponse&{balance=String,name=String};fallbackmathodrequired=Y ";
		String[] arr = str.split(";");
		System.out.println("hello"+ arr[1]);
		
		
		File f0 = new File("C:\\scproject\\Balance\\src\\main\\java\\com\\balance\\");
		if(!f0.exists()){
			f0.mkdirs();
		}
		File f = new File("C:\\scproject\\Balance\\src\\main\\java\\com\\balance\\GetBalanceResponse1.java");
							//C:\scproject\balance\src\main\java\com\balance\GetBalanceResponse.java
		//File f = new File("c:\\hello.txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		bw.write("TEST");
		bw.flush();

	
	}
	

}
