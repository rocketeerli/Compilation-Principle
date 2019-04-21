package syntactic;

import java.util.List;

public class Enter {

	public static void main(String[] args) {
		// 读文法文件
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
			System.out.println("规约成功!!!");
		} else {
			System.out.println("规约失败!!!");
		}
	}

}
