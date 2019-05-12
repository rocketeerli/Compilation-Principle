package syntactic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class Table {
	// Action 表 和 Goto 表
	public static String[][] ACTION = new String[Analysis.stateI.size()][Analysis.finalChar.size()];
	public static String[][] GOTO = new String[Analysis.stateI.size()][Analysis.unfinalChar.size()];

	// 建立 Action 表 和 Goto 表
	public static void createTable() {
		Analysis.finalChar.add("#");
		ACTION = new String[Analysis.stateI.size()][Analysis.finalChar.size()];
		GOTO = new String[Analysis.stateI.size()][Analysis.unfinalChar.size()];
		for (List<String> item : Analysis.stateI) {
			int index = Analysis.stateI.indexOf(item);
			if (item.contains("ST -> P . , #")) {
				ACTION[index][Analysis.finalChar.indexOf("#")] = "acc";
				continue;
			}
			for (String string : item) {
				String[] strList = string.split("->")[1].split(" ");
				String str = null;
				int i = 0;
				for (; i < strList.length; i++) {
					if (strList[i].equals(".")) {
						str = strList[i + 1];
						break;
					}
				}
				// ACTION 规约
				if (str.equals(",")) {
					// 规约的字符
					String ch = strList[strList.length - 1];
					String[] newstr = new String[i];
					for (int j = 0; j < i; j++) {
						newstr[j] = strList[j];
					}
					String pro = string.split("->")[0] + "->" + String.join(" ", newstr);
					int pos = Analysis.GRAMMER.indexOf(pro);
					ACTION[index][Analysis.finalChar.indexOf(ch)] = "R" + pos;
				} else if (Analysis.isFinallChar(str)) { // ACTION 移入
					List<String> s = Analysis.Go(item, str);
					int pos = Analysis.stateI.indexOf(s);
					ACTION[index][Analysis.finalChar.indexOf(str)] = "S" + pos;
				} else { // GOTO
					for (String k : Analysis.unfinalChar) {
						List<String> s = Analysis.Go(item, k);
						if (s.size() > 0) {
							int pos = Analysis.stateI.indexOf(s);
							GOTO[index][Analysis.unfinalChar.indexOf(k)] = "" + pos;
						}
					}
				}
			}
		}
	}
	
	// 将 ACTION 和 GOTO 表写入文件
	public static void writeFile() {		
		List<String> list = new ArrayList<>();
		String string = "\t\t";
		for (int j = 0; j < Analysis.finalChar.size(); j++) {
			string += Analysis.finalChar.get(j) + "\t\t";
		}
		for (int j = 0; j < Analysis.unfinalChar.size(); j++) {
			string += Analysis.unfinalChar.get(j) + "\t\t";
		}
		list.add(string);
		for (int i = 0; i < Analysis.stateI.size(); i++) {
			string = i + "\t\t";
			for (int j = 0; j < Table.ACTION[0].length; j++) {
				if (Table.ACTION[i][j] != null) {
					string += Table.ACTION[i][j] + "\t\t";
					continue;
				}
				string += "\t\t";
			}
			for (int j = 0; j < Table.GOTO[0].length; j++) {
				if (Table.GOTO[i][j] != null) {
					string += Table.GOTO[i][j] + "\t\t";
					continue;
				}
				string += "\t\t";
			}
			list.add(string);
		}
		try {
			File file =new File("./table.txt");
			Writer out =new FileWriter(file);
			for (String str : list) {
				System.out.println(str);
				out.write(str + "\n");
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
