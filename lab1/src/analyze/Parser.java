package analyze;


public class Parser {
	private String code;
	private int row = 0;
	
	public Parser(String fileName) {
		// ��ȡ�����ļ�
		this.code = Tool.readFile(fileName);
	}
	
	/**
	 * �ʷ�����
	 * */
	public void parser() {
		// ��������
		for (int i = 0; i < code.length(); i++) {
			char ch = code.charAt(i);
			if (ch == '\n') {
				this.row++;
				continue;
			} else if (ch == ' ' || ch == '\t') {
				continue;
			} else if (Character.isDigit(ch)) {
				// �������ֿ�ͷ��
				i = dealStartedDigit(i);
			} else if (Character.isLetter(ch) || ch == '_') {
				// ������ĸ���»��߿�ͷ��
				i = dealStartedLetter(i);
			} else {
				i = dealOtherChar(i);
			}
		}
	}
	
	/**
	 * �������ֿ�ͷ���ַ���
	 * */
	public int dealStartedDigit(int i) {
		String word = "";
		char ch = code.charAt(i);
		while (Character.isDigit(ch)) {
			word = word + ch;
			ch = code.charAt(++i);
		}
		// �ж��Ƿ���С��
		if (ch == '.') {
			word = word + ch;
			ch = code.charAt(++i);
			while (Character.isDigit(ch)) {
				word = word + ch;
				ch = code.charAt(++i);
			}
		}
		if (Character.isLetter(ch)) {
			// ���ֺ�������ĸ ����
			Tool.addErroInfo(this.row, ch, "���ֿ�ͷ�ı�ʶ��");
		}
		// ���뵽 token ��
		Tool.addToken(word, "CONST", word);
		return i-1;
	}
	
	/**
	 * ������ĸ���»��߿�ͷ���ַ���
	 * */
	public int dealStartedLetter(int i) {
		String word = "";
		char ch = code.charAt(i);
		while (Character.isLetterOrDigit(ch) || ch == '_') {
			word = word + ch;
			ch = code.charAt(++i);
		}
		// �ж��Ƿ��ǹؼ���
		if (Tool.getKEYKIND().containsKey(word)) {  // �ǹؼ���
			Tool.addToken(word, Tool.getKEYKIND().get(word), "-");
		} else {
			Tool.addToken(word, "IDN", word);
			// ���뵽���ű���
			Tool.addChar(word);
		}
		return i-1;
	}
	
	/**
	 * �������ĸ�»�������֮�⣬�����ַ���ͷ��
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
	 * ���� '+' ��ʼ���ַ���
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
	 * ���� '-' ��ʼ���ַ���
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
	 * ���� '*' ��ʼ���ַ���
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
	 * ���� '/' ��ʼ���ַ���
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
			//Tool.addToken(word, "ע��", "-");
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
			//Tool.addToken(word, "ע��", "-");
		}
		return i-1;
	}
	
	/**
	 * ���� '>' ��ʼ���ַ���
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
	 * ���� '<' ��ʼ���ַ���
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
	 * ���� '&' ��ʼ���ַ���
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
	 * ���� '|' ��ʼ���ַ���
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
	 * ���� '!' ��ʼ���ַ���
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
	 * ��ô�������
	 * */
	public String getCode() {
		return this.code;
	}

}
