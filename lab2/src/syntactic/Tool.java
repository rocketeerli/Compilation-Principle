package syntactic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Tool {
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
				} else if (code.equals("CONST")) {
					symbols.add("num");
				} else {
					symbols.add(orignal);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return symbols;
	}
}
