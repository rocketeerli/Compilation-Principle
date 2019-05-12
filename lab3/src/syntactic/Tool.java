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
	// 存储符号表
	public static Map<String, List<String>> symbolTable = new HashMap<>();
	// 存储在符号表中的入口
	public static Queue<String> symbolName = new LinkedList<String>();
	// 存储常数表
	public static Queue<String> numberTable = new LinkedList<String>();
	
	// 读取词法分析输出的 token
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
	
	// 读取词法分析生成的符号表
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
	
	// 根据产生式，将变量内容写入符号表
	public static void enter(String idName, String type, Integer offset) {
		List<String> list = symbolTable.get(idName);
		list.add(type);
		list.add(String.valueOf(offset));
	}
	
	// 回填技术的三个方法
	/**
	 * makelist(i)
	 * 创建一个只包含 i 的新链表
	 * */
	public static List<Integer> makelist(Integer i) {
		List<Integer> list = new ArrayList<>();
		list.add(i);
		return list;
	}
	/**
	 * merge(p1, p2)
	 * 合并由 p1 和 p2 指向的两个链表，并返回新链表
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
	 * 将 i 插入到链表 p 中的每一条语句中，作为该语句的目标标号
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
