package analyze;

public class Token {
	private String word;
	private String code;
	private String value;
	
	
	public Token(String word, String code, String value) {
		this.word = word;
		this.code = code;
		this.value = value;
	}
	
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return this.word + '\t' + 
				"<" + this.code + "\t,  " + 
				this.value + "\t>";
	}
}
