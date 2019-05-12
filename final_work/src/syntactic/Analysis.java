package syntactic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Analysis {
	public static List<String> GRAMMER = new ArrayList<>();   // �ķ� 
	public static List<String> SYMBOL = new ArrayList<>(); // �ַ����ϣ������ս���ͷ��ս����
	public static List<List<String>> stateI = new ArrayList<>(); // ״̬����
	// �洢�ս�� �� ���ս��  ���� Action �� Goto ��ʱʹ�� 
	public static List<String> finalChar = new ArrayList<>();
	public static List<String> unfinalChar = new ArrayList<>();	
	
	// �����ķ��� ���洢�����ַ�
	public static void getGRAMMER(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while((line = reader.readLine()) != null ) {
				String left = line.split("->")[0];
				String[] right = line.split("->")[1].split("\\|");
				for (String r : right) {
					r = " " + r.trim();
					GRAMMER.add(left + "->" + r);  // �洢�ķ�
					// �洢�ַ�
					for (String str : r.split(" ")) {
						if (!SYMBOL.contains(str) && str.length() > 0) {
							SYMBOL.add(str);
							if (isFinallChar(str)) {
								finalChar.add(str);
							} else {
								unfinalChar.add(str);
							}
						}
					}
				}
				// �洢�ַ�
				String str = left.replaceAll(" ", "");
				if (!SYMBOL.contains(str) && str.length() > 0) {
					SYMBOL.add(str);
					if (isFinallChar(str)) {
						finalChar.add(str);
					} else {
						unfinalChar.add(str);
					}
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	// �ݹ�����ķ��հ�
	public static List<String> closure(List<String> I) {
		List<String> C = CLOSURE(I);
		List<String> tmp = new ArrayList<>(C);
		while(true) {
			List<String> list = CLOSURE(tmp);
			if (C.containsAll(list)) {
				break;
			}
			for (String string : list) {
				if (!C.contains(string)) {
					C.add(string);
				}
			}
			tmp = list;
		}
		return C;
	}
	
	// ����һ��հ�
	public static List<String> CLOSURE(List<String> I) {
		List<String> C = new ArrayList<>(I);
		List<String> list = new ArrayList<>();
		Iterator<String> it = C.iterator();
		while(it.hasNext()) {
			String c = it.next();
			String[] strList = c.split("->")[1].split(" ");
			String str = null;
			int index = 0;
			for (; index < strList.length; index++) {
				if (strList[index].equals(".")) {
					str = strList[index+1];
					break;
				}
			}
			if (!str.equals(",")) {
				if (isFinallChar(str)) {
					continue;
				}
				List<String> first_arr = new ArrayList<>();
				for(index = index + 2; index < strList.length; index++) {
					if (strList[index].equals(",")) {
						continue;
					}
					first_arr.add(strList[index]);
				}
				List<String> first = first_str(first_arr);
				for (String item : GRAMMER) {
					if (str.equals(item.split("->")[0].replaceAll(" ", ""))) {
						for(String n : first) {
							String newi = str + " -> ." + item.split("->")[1] + " , " + n;
							if (!list.contains(newi)) {
								list.add(newi);
							}
						}
					}
				}
			}
		}
		C.addAll(list);
		return C;
	}
	
	// ����ת�ƺ��� GO
	public static List<String> Go(List<String> I, String x) {
		List<String> J = new ArrayList<>();
		for (String str : I) {
			String[] strList = str.split("->")[1].split(" ");
			String it = null;
			for (int index = 0; index < strList.length; index++) {
				if (strList[index].equals(".")) {
					it = strList[index+1];
					break;
				}
			}
			if (x.equals(it)) {
				J.add(change_index(str));
			}
		}
		return closure(J);
	}
	
	// ���� �� ��λ��
	public static String change_index(String item) {
		String[] strList = item.split("->")[1].split(" ");
		int index = 0;
		for (; index < strList.length; index++) {
			if (strList[index].equals(".")) {
				break;
			}
		}
		strList[index] = strList[index + 1];
		strList[index+1] = ".";
		String newi = item.split("->")[0] + "->" + String.join(" ", strList);
		return newi;
	}
	
	// �ж��ַ����Ƿ����ս��
	public static boolean isFinallChar(String str) {
		boolean flag = false;
		for (int i = 0; i < str.length(); i++) {
			if (!(str.charAt(i) >= 'A' && str.charAt(i) <= 'Z')) {
				flag = true;
			}
		}
		return flag;
	}
	
	// �����ַ����� First ��
	public static List<String> first_str(List<String> x) {
		List<String> list = first_sig(x.get(0));
		return list;
	}
	
	// ����һ���ַ��� First ��
	public static List<String> first_sig(String x) {
		List<String> list = new ArrayList<>();
		if (isFinallChar(x)) {
			list.add(x);
		} else {
			for (String str : GRAMMER) {
				String ch = str.split("->")[0];
				if (x.equals(ch.replaceAll(" ", ""))) {
					String y = null;
					for(String s : str.split("->")[1].split(" ")) {
						if (s.length() > 0) {
							y = s;
							break;
						}
					}
					List<String> tmp = first_sig(y);
					if (tmp.size() > 0) {
						for(String s : tmp) {
							if (!list.contains(s)) {
								list.add(s);
							}
						}
					}
				}
			}
		}
		return list;
	}
	
	// ���� LR
	public static void createLR() {
		String it = GRAMMER.get(0);
		String one = it.split("->")[0] + "-> ." + it.split("->")[1] + " , " + "#";
		List<String> t = new ArrayList<>();
		t.add(one);
		stateI.add(closure(t));
		List<List<String>> tmp = new ArrayList<>(stateI);
		while(true) {
			
			List<List<String>> list = new ArrayList<>();
			for(List<String> li : tmp) {
				List<List<String>> oneState = addLR(li);
				list.addAll(oneState);
				stateI.addAll(oneState);	
			}
			tmp = list;
			if (tmp.size() == 0) {
				break;
			}
		}
		
	}
	
	// ����һ��״̬
	public static List<List<String>> addLR(List<String> lists) {
		List<List<String>> t = new ArrayList<>();
		for (String x : SYMBOL) {
			List<String> list = Go(lists, x);
			if (list.size() > 0 && !isContained(stateI, list) && !isContained(t, list)) {
				t.add(list);
			}
		}
		return t;
	}
	
	// �ж��Ƿ����
	public static boolean isContained(List<List<String>> lists, List<String> list) {
		for (List<String> strList : lists) {
			if (strList.containsAll(list) && list.containsAll(strList)) {
				return true;
			}
		}
		return false;
	}
}
