package analyze;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tool {
	private static Map<String, String> KEYKIND;  // 编码
	static {
		KEYKIND = new HashMap<String, String>();
		KEYKIND.put("(", "SLP");
		KEYKIND.put(")", "SRP");
		KEYKIND.put("!=", "NE");
		KEYKIND.put("!", "NOT");
		KEYKIND.put("{", "LP");
		KEYKIND.put("}", "RP");
		KEYKIND.put(";", "SEMI");
		KEYKIND.put("++", "INC");
		KEYKIND.put("--", "INC");
		KEYKIND.put("==", "EQ");
		KEYKIND.put("=", "E");
		KEYKIND.put("+", "ADD");
		KEYKIND.put("-", "SUB");
		KEYKIND.put("*", "MUL");
		KEYKIND.put("/", "EXC");
		KEYKIND.put("+=", "ADDEQ");
		KEYKIND.put("-=", "SUBEQ");
		KEYKIND.put("*=", "MULEQ");
		KEYKIND.put("/=", "EXCEQ");
		KEYKIND.put("<", "LE");
		KEYKIND.put(">", "GR");
		KEYKIND.put("<=", "LEQ");
		KEYKIND.put(">=", "GEQ");
		KEYKIND.put("<<", "LT");
		KEYKIND.put(">>", "RT");
		KEYKIND.put("&&", "AND");
		KEYKIND.put("||", "OR");
		KEYKIND.put("&", "ANDB");
		KEYKIND.put("|", "ORB");
		KEYKIND.put("while", "WHILE");
		KEYKIND.put("do", "DO");
		KEYKIND.put("int", "INT");
		KEYKIND.put("long", "LONG");
		KEYKIND.put("float", "FLOAT");
		KEYKIND.put("char", "CHAR");
		KEYKIND.put("double", "DOUBLE");
		KEYKIND.put("if", "IF");
		KEYKIND.put("else", "ELSE");
		KEYKIND.put("switch", "SWITCH");
		KEYKIND.put("for", "FOR");
		KEYKIND.put("return", "RETURN");
		KEYKIND.put("break", "BREAK");
		KEYKIND.put("true", "TRUE");
		KEYKIND.put("false", "FALSE");
	}
	private static List<Token> tokenList = new ArrayList<Token>();
	private static String erroInfo;
	// 符号表
	private static Map<String, String> charTable = new HashMap<String, String>();

	// 读文件
	public static String readFile(String fileName) {
		String encoding = "UTF-8";
		File file = new File(fileName);
		Long filelength = file.length();
		byte[] filecontent = new byte[filelength.intValue()];
		try {
			FileInputStream in = new FileInputStream(file);
			in.read(filecontent);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			return new String(filecontent, encoding);
		} catch (UnsupportedEncodingException e) {
			System.err.println("The OS does not support " + encoding);
			e.printStackTrace();
			return null;
		}
	}
	
	// 存储 token
	public static void addToken(String word, String code, String value) {
		Token token = new Token(word, code, value);
		tokenList.add(token);
	}
	
	// 存储错误信息
	public static void addErroInfo(int row, char ch, String info) {
		erroInfo = erroInfo + "第\t" + row + "\t行\t字符\t" + ch + "附近出现" + info + "错误" + "\n";
	}
	
	// 记录符号表
	public static void addChar(String word) {
		charTable.put(word, "");
	}
	
	// 获取 KEYKIND
	public static Map<String, String> getKEYKIND() {
		return KEYKIND;
	}
	
	// 获取 token
	public static List<Token> getTokenList() {
		return tokenList;
	}
	
	// 获取错误信息
	public static String getErroInfo() {
		return erroInfo;
	}
	
	// 获取符号表
	public static Map<String, String> getCharTable() {
		return charTable;
	}
	
}
