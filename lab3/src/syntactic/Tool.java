package syntactic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import codeGen.Generator;

public class Tool {
	// �洢���ű�
	public static Map<String, List<String>> symbolTable = new HashMap<>();
	// �洢�ڷ��ű��е����
	public static Queue<String> symbolName = new LinkedList<String>();
	// �洢������
	public static Queue<String> numberTable = new LinkedList<String>();
	
	// ��ȡ�ʷ���������� token
	public static List<String> getToken(String fileName) {
		List<String> symbols = new ArrayList<>();
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				String orignal = line.split("<")[0].replaceAll(" |\t", "");
				String code = line.split("<")[1].split(" |\t")[0];
				if (code.equals("IDN")) {
					symbols.add("id");
					symbolName.add(orignal);
				} else if (code.equals("CONST")) {
					symbols.add("num");
					numberTable.add(orignal);
				} else {
					symbols.add(orignal);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return symbols;
	}
	
	// ��ȡ�ʷ��������ɵķ��ű�
	public static void getSymbolTable(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				String id = line.split("<")[1].split(" |\t")[0].replaceAll(",", "");
				symbolTable.put(id, new ArrayList<>());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// ���ݲ���ʽ������������д����ű�
	public static void enter(String idName, String type, Integer offset) {
		List<String> list = symbolTable.get(idName);
		list.add(type);
		list.add(String.valueOf(offset));
	}
	
	// ���������������
	/**
	 * makelist(i)
	 * ����һ��ֻ���� i ��������
	 * */
	public static List<Integer> makelist(Integer i) {
		List<Integer> list = new ArrayList<>();
		list.add(i);
		return list;
	}
	/**
	 * merge(p1, p2)
	 * �ϲ��� p1 �� p2 ָ�����������������������
	 * */
	public static List<Integer> merge(List<Integer> p1, List<Integer> p2) {
		List<Integer> list = new ArrayList<>();
		list.addAll(p1);
		for(Integer index : p2) {
			if (!list.contains(index)) {
				list.add(index);
			}
		}
		return list;
	}
	/**
	 * backpatch(p, i)
	 * �� i ���뵽���� p �е�ÿһ������У���Ϊ������Ŀ����
	 * */
	public static void backpatch(List<Integer> p, Integer i) {
		for(Integer index : p) {
			String code = Generator.codeList.get(index);
			Generator.codeList.remove(code);
			String left = code.split("goto")[0];
			Generator.codeList.add(index, left + " goto " + i);
		}
	}
}
