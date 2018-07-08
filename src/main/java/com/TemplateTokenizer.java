package com;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TemplateTokenizer {
	
	// Get the portion of contents from template service that is before
		// 'TEMPLATE METHOD START'
		public static String getBeforeTemplateMethod(String filepath) throws IOException {
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			String content = "";
			String line = "";
			while ((content = br.readLine()) != null) {

				line = line + content;
				line = line + "\n";

			}
			line = line.substring(line.indexOf("package"), line.indexOf("//TEMPLATE_GET_METHOD_START"));
			System.out.println("LINE Before Template Methods===" + line);
			return line;
		}

		// Get the portion of contents from template service that corresponds to a
		// method
		public static String getTemplateGetMethod(String filepath) throws IOException {
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			String content = "";
			String line = "";
			while ((content = br.readLine()) != null) {

				line = line + content;
				line = line + "\n";

			}
			line = line.substring(line.indexOf("//TEMPLATE_GET_METHOD_START"),
					line.lastIndexOf("//TEMPLATE_GET_METHOD_END"));
			System.out.println("LINE Get===" + line);
			return line;
		}

		// Get the portion of contents from template service that corresponds to a
		// method
		public static String getTemplatePostMethod(String filepath) throws IOException {
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			String content = "";
			String line = "";
			while ((content = br.readLine()) != null) {

				line = line + content;
				line = line + "\n";

			}
			line = line.substring(line.indexOf("//TEMPLATE_POST_METHOD_START"),
					line.lastIndexOf(" //TEMPLATE_POST_METHOD_END"));
			System.out.println("LINE POST===" + line);
			return line;
		}

		// Get the portion of contents from template service that corresponds to a
		// fallback get method
		public static String getTemplateGetFallBacktMethod(String filepath) throws IOException {
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			String content = "";
			String line = "";
			while ((content = br.readLine()) != null) {

				line = line + content;
				line = line + "\n";

			}
			line = line.substring(line.indexOf("//TEMPLATE_FALLBACK_GET_METHOD_START"),
					line.lastIndexOf("//TEMPLATE_FALLBACk_GET_METHOD_END"));
			System.out.println("LINE POST===" + line);
			return line;
		}
		
		// Get the portion of contents from template service that corresponds to a
			// fallback post method
			public static String getTemplatePostFallBacktMethod(String filepath) throws IOException {
				BufferedReader br = new BufferedReader(new FileReader(filepath));
				String content = "";
				String line = "";
				while ((content = br.readLine()) != null) {

					line = line + content;
					line = line + "\n";

				}
				line = line.substring(line.indexOf("//TEMPLATE_FALLBACK_POST_METHOD_START"),
						line.lastIndexOf("//TEMPLATE_FALLBACK_POST_METHOD_END"));
				System.out.println("LINE POST===" + line);
				return line;
			}

		// Get the portion of contents from template service that is after 'TEMPLATE
		// METHOD END'
		public static String getAfterTemplateMethodS(String filepath) throws IOException {
			System.out.println("AFTER TEMPLATE METHODS");
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			String content = "";
			String line = "";
			while ((content = br.readLine()) != null) {

				line = line + content;
				line = line + "\n";

			}

			int a = line.indexOf("//TEMPLATE_FALLBACK_POST_METHOD_END");
			line = line.substring(a);
			System.out.println("LINE Renaiming===" + line);
			return line;
		}

}
