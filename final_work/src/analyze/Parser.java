package analyze;


public class Parser {
	private String code;
	private int row = 0;
	
	public Parser(String fileName) {
		// 读取代码文件
		this.code = Tool.readFile(fileName);
	}
	
	/**
	 * 词法分析
	 * */
	public void parser() {
		// 遍历代码
		for (int i = 0; i < code.length(); i++) {
			char ch = code.charAt(i);
			if (ch == '\n') {
				this.row++;
				continue;
			} else if (ch == ' ' || ch == '\t') {
				continue;
			} else if (Character.isDigit(ch)) {
				// 处理数字开头的
				i = dealStartedDigit(i);
			} else if (Character.isLetter(ch) || ch == '_') {
				// 处理字母或下划线开头的
				i = dealStartedLetter(i);
			} else {
				i = dealOtherChar(i);
			}
		}
	}
	
	/**
	 * 处理数字开头的字符串
	 * */
	public int dealStartedDigit(int i) {
		String word = "";
		char ch = code.charAt(i);
		while (Character.isDigit(ch)) {
			word = word + ch;
			ch = code.charAt(++i);
		}
		// 判断是否是小数
		if (ch == '.') {
			word = word + ch;
			ch = code.charAt(++i);
			while (Character.isDigit(ch)) {
				word = word + ch;
				ch = code.charAt(++i);
			}
		}
		if (Character.isLetter(ch)) {
			// 数字后面是字母 报错
			Tool.addErroInfo(this.row, ch, "数字开头的标识符");
		}
		// 加入到 token 中
		Tool.addToken(word, "CONST", word);
		return i-1;
	}
	
	/**
	 * 处理字母或下划线开头的字符串
	 * */
	public int dealStartedLetter(int i) {
		String word = "";
		char ch = code.charAt(i);
		while (Character.isLetterOrDigit(ch) || ch == '_') {
			word = word + ch;
			ch = code.charAt(++i);
		}
		// 判断是否是关键字
		if (Tool.getKEYKIND().containsKey(word)) {  // 是关键字
			Tool.addToken(word, Tool.getKEYKIND().get(word), "-");
		} else {
			Tool.addToken(word, "IDN", word);
			// 加入到符号表中
			Tool.addChar(word);
		}
		return i-1;
	}
	
	/**
	 * 处理除字母下划线数字之外，其他字符开头的
	 * */
	public int dealOtherChar(int i) {
		char ch = code.charAt(i);
		switch (ch) {
		case ';':
		case '(':
		case ')':
		case '{':
		case '}':
		case '=':
			Tool.addToken(String.valueOf(ch), Tool.getKEYKIND().get(String.valueOf(ch)), "-");
			return i;
		case '+':
			return dealStartedAdd(i);
		case '-':
			return dealStartedSub(i);
		case '*':
			return dealStartedMul(i);
		case '/':
			return dealStartedSlash(i);
		case '>': 
			return dealStartedGR(i);
		case '<':
			return dealStartedLE(i);
		case '!':
			return dealStartedNE(i);
		case '&':
			return dealStartedAND(i);
		case '|':
			return dealStartedOR(i);
		default:
			break;
		}
		return i;
	}
	
	/**
	 * 处理 '+' 开始的字符串
	 * */
	public int dealStartedAdd(int i) {
		String word = "" + code.charAt(i);
		char ch = code.charAt(i+1);
		if (ch == '=') {
			word = word + ch;
			++i;
		} else if (ch == '+') {
			word = word + ch;
			++i;
		}
		Tool.addToken(word, Tool.getKEYKIND().get(word), "-");
		return i;
	}
	
	/**
	 * 处理 '-' 开始的字符串
	 * */
	public int dealStartedSub(int i) {
		String word = "" + code.charAt(i);
		char ch = code.charAt(++i);
		if (ch == '=') {
			word = word + ch;
			ch = code.charAt(++i);
		} else if (ch == '-') {
			word = word + ch;
			ch = code.charAt(++i);
		}
		Tool.addToken(word, Tool.getKEYKIND().get(word), "-");
		return i-1;
	}
	
	/**
	 * 处理 '*' 开始的字符串
	 * */
	public int dealStartedMul(int i) {
		String word = "" + code.charAt(i);
		char ch = code.charAt(++i);
		if (ch == '=') {
			word = word + ch;
			ch = code.charAt(++i);
		}
		Tool.addToken(word, Tool.getKEYKIND().get(word), "-");
		return i-1;
	}
	
	/**
	 * 处理 '/' 开始的字符串
	 * */
	public int dealStartedSlash(int i) {
		String word = "" + code.charAt(i);
		char ch = code.charAt(++i);
		if (ch == '=') {
			word = word + ch;
			ch = code.charAt(++i);
			Tool.addToken(word, Tool.getKEYKIND().get(word), "-");
		} else if (ch == '/') {
			word = word + ch;
			ch = code.charAt(++i);
			while (ch != '\n') {
				word = word + ch;
				ch = code.charAt(++i);
		 	}
			//Tool.addToken(word, "注释", "-");
		} else if (ch == '*') {
			word = word + ch;
			ch = code.charAt(++i);
			while (true) {
				if (code.charAt(i) == '*' && code.charAt(i+1) == '/') {
					word = word + code.charAt(i) + code.charAt(++i);
					break;
				}
				word = word + ch;
				ch = code.charAt(++i);
			}
			//Tool.addToken(word, "注释", "-");
		}
		return i-1;
	}
	
	/**
	 * 处理 '>' 开始的字符串
	 * */
	public int dealStartedGR(int i) {
		String word = "" + code.charAt(i);
		char ch = code.charAt(++i);
		if (ch == '=') {
			word = word + ch;
			ch = code.charAt(++i);
		} else if (ch == '>') {
			word = word + ch;
			ch = code.charAt(++i);
		}
		Tool.addToken(word, Tool.getKEYKIND().get(word), "-");
		return i-1;
	}
	
	/**
	 * 处理 '<' 开始的字符串
	 * */
	public int dealStartedLE(int i) {
		String word = "" + code.charAt(i);
		char ch = code.charAt(++i);
		if (ch == '=') {
			word = word + ch;
			ch = code.charAt(++i);
		} else if (ch == '<') {
			word = word + ch;
			ch = code.charAt(++i);
		}
		Tool.addToken(word, Tool.getKEYKIND().get(word), "-");
		return i-1;
	}
	
	/**
	 * 处理 '&' 开始的字符串
	 * */
	public int dealStartedAND(int i) {
		String word = "" + code.charAt(i);
		char ch = code.charAt(++i);
		if (ch == '&') {
			word = word + ch;
			ch = code.charAt(++i);
		} 
		Tool.addToken(word, Tool.getKEYKIND().get(word), "-");
		return i-1;
	}
	
	/**
	 * 处理 '|' 开始的字符串
	 * */
	public int dealStartedOR(int i) {
		String word = "" + code.charAt(i);
		char ch = code.charAt(++i);
		if (ch == '|') {
			word = word + ch;
			ch = code.charAt(++i);
		} 
		Tool.addToken(word, Tool.getKEYKIND().get(word), "-");
		return i-1;
	}
	
	/**
	 * 处理 '!' 开始的字符串
	 * */
	public int dealStartedNE(int i) {
		String word = "" + code.charAt(i);
		char ch = code.charAt(++i);
		if (ch == '=') {
			word = word + ch;
			ch = code.charAt(++i);
		}
		return i-1;
	}
	
	/**
	 * 获得代码内容
	 * */
	public String getCode() {
		return this.code;
	}

}
