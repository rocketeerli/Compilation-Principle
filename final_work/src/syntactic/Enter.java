package syntactic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import analyze.Parser;
import analyze.Tool;
import entity.Token;

public class Enter {

	public static void main(String[] args) throws IOException {
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
		
		// ���ķ��ļ�
		Analysis.getGRAMMER("grammer.txt");
		for(String str : Analysis.GRAMMER) {
			System.out.println(str);
		}
		Analysis.createLR();
		Table.createTable();
		Table.writeFile();
		
		List<String> token = syntactic.Tool.getToken("token.txt");
		syntactic.Tool.getSymbolTable("symbolTable.txt");
		System.out.println(token);
		for (int i = 0; i < Analysis.stateI.size(); i++) {
			System.out.println(i + "\t:\t" + Analysis.stateI.get(i));
		}
		if (LR.analyzeLR(token)) {
			System.out.println("��Լ�ɹ�!!!");
		} else {
			System.out.println("��Լʧ��!!!");
		}
		System.out.println("���ɵĴ��룺");
		for (String string : codeGen.Generator.codeList) {
			System.out.println(string);
		}
		
	}

}
