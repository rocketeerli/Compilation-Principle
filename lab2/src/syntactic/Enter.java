package syntactic;

import java.util.List;

public class Enter {

	public static void main(String[] args) {
		// ���ķ��ļ�
		Analysis.getGRAMMER("grammer.txt");
		for(String str : Analysis.GRAMMER) {
			System.out.println(str);
		}
		Analysis.createLR();
		Table.createTable();
		Table.writeFile();
		
		List<String> token = Tool.getToken("../lab1/token.txt");
		System.out.println(token);
		for (int i = 0; i < Analysis.stateI.size(); i++) {
			System.out.println(i + "\t:\t" + Analysis.stateI.get(i));
		}
		if (LR.analyzeLR(token)) {
			System.out.println("��Լ�ɹ�!!!");
		} else {
			System.out.println("��Լʧ��!!!");
		}
	}

}
