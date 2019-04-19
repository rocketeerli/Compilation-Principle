package analyze;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class Enter {

	public static void main(String[] args) throws IOException{
		Parser parser = new Parser("test.txt");
		System.out.println("源代码：");
		System.out.println(parser.getCode());
		parser.parser();
		// 输出符号表
		System.out.println("符号表:");
		File file =new File("./symbolTable.txt");
		Writer out =new FileWriter(file);
		for (String word : Tool.getCharTable().keySet()) {
			System.out.println("<" + word + ", - >");
			// 输出到文件中
			out.write("<" + word + ", - >" + "\n");
		}
		out.close();
		
		// 输出 token
		System.out.println("token:");
		file =new File("./token.txt");
		out =new FileWriter(file);
		for (Token token : Tool.getTokenList()) {
			System.out.println(token);
			// 输出到文件中
			out.write(token + "\n");
		}
		out.close();
		
		// 打印错误信息
		String erroInfo = Tool.getErroInfo();
		if (erroInfo == null) {
			System.out.println("无错误信息");
		}
		else {
			System.out.println(erroInfo);
		}
	}

}
