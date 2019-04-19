package analyze;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class Enter {

	public static void main(String[] args) throws IOException{
		Parser parser = new Parser("test.txt");
		System.out.println("Դ���룺");
		System.out.println(parser.getCode());
		parser.parser();
		// ������ű�
		System.out.println("���ű�:");
		File file =new File("./symbolTable.txt");
		Writer out =new FileWriter(file);
		for (String word : Tool.getCharTable().keySet()) {
			System.out.println("<" + word + ", - >");
			// ������ļ���
			out.write("<" + word + ", - >" + "\n");
		}
		out.close();
		
		// ��� token
		System.out.println("token:");
		file =new File("./token.txt");
		out =new FileWriter(file);
		for (Token token : Tool.getTokenList()) {
			System.out.println(token);
			// ������ļ���
			out.write(token + "\n");
		}
		out.close();
		
		// ��ӡ������Ϣ
		String erroInfo = Tool.getErroInfo();
		if (erroInfo == null) {
			System.out.println("�޴�����Ϣ");
		}
		else {
			System.out.println(erroInfo);
		}
	}

}
