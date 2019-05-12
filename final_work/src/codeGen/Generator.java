package codeGen;

import java.util.ArrayList;
import java.util.List;

// 代码生成器
public class Generator {
	// 代码
	public static List<String> codeList = new ArrayList<>();
	// 生成代码
	public static void gencode(String code) {
		codeList.add(code);
	}
}
