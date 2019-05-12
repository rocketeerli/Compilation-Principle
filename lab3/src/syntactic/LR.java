package syntactic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import codeGen.Generator;
import entity.Attribute;

// LR ��Լ��
public class LR {
	// ����ջ��һ������
	public static Stack<Integer> stateStack = new Stack<Integer>();  // ״̬ջ
	public static Stack<String> symbolStack = new Stack<String>();  // ����ջ
	public static Queue<String> buffer = new LinkedList<String>();  // ������
	// �������  �¼�һ�� value ջ ������ֵջ��
	public static Stack<Attribute> valueStack = new Stack<Attribute>(); // ����ֵջ
	public static Integer offset = 0;  // ��ַƫ����
	public static List<String> newtemps = new ArrayList<>(); // �洢�����ӵı���
	public static Integer tempIndex = 0;

	// �������ɵ� ACTION �� GOTO ���Դ����й�Լ
	public static boolean analyzeLR(List<String> input) {
		// ���ַ������뵽������
		for (String string : input) {
			buffer.offer(string);
		}
		buffer.offer("#");
		// ��ʼ��״̬ջ
		stateStack.push(0);
		symbolStack.push("#");
		// ��ʼ��Լ
		while (true) {
			int state = stateStack.peek();  // ��ǰջ��״̬
			int ip = Analysis.finalChar.indexOf(buffer.element());  // ������ָ��ָ����ַ�
			String action = Table.ACTION[state][ip];
//			System.out.println("ջ����" + buffer.element() + "\t��ǰ״̬��" + state);
			if (action != null) {
				if (action.equals("acc")) {
					return true;
				}
				String act = action.substring(0, 1);  // 'R' or 'S'  ��Լ��������
				int trans_state = Integer.parseInt(action.substring(1));  // ת�Ƶ�����һ��״̬���ǹ�Լ�Ĳ���ʽ
				// �������
				if (act.equals("S")) {
					// ��״̬ i(trans_state) ѹ�뵽״̬ջ�У���������ѹ�뵽����ջ��
					stateStack.push(trans_state);
					String buf = buffer.poll();
					symbolStack.push(buf);
//					�����������  ÿѹ��һ�����ţ�ͬʱѹ��һ������
					Attribute attribute = new Attribute();
					if (buf.equals("int") || buf.equals("float")) {
						attribute.setValue(buf);
						attribute.setWidth(4);
					} else if (buf.equals("double")) {
						attribute.setValue(buf);
						attribute.setWidth(8);
					} else if (buf.equals("id")) {
						attribute.setValue(Tool.symbolName.poll());
					} else if (buf.equals("num")) {
						attribute.setValue(Tool.numberTable.poll());
					}
					valueStack.push(attribute);
				} else if (act.equals("R")) { // ��Լ����
					// ���յ� k(trans_state) ������ʽ���й�Լ
					String pro = Analysis.GRAMMER.get(trans_state);
					
//					�������  ���ݹ�Լ�Ĳ���ʽ  ִ����Ӧ�Ķ���
					// �������
					if (trans_state == 2) {  // D -> T L ;
						valueStack.pop();
						Tool.enter(valueStack.pop().getValue(), valueStack.peek().getValue(), offset);
						offset += valueStack.pop().getWidth();
						valueStack.push(new Attribute());
//						Generator.gencode("������䣺");
					} 
					// ��ֵ���
					assignment(trans_state);
					// �������ʽ
					boolAnaly(trans_state);
					// ������ѭ�����
					jump(trans_state);
					
					// �� beta
					List<String> symbols_beta = new ArrayList<>();
					String[] strList = pro.split("->")[1].split(" ");
					for (int i = 0; i < strList.length; i++) {
						if (strList[i].length() > 0) {
							symbols_beta.add(strList[i]);
						}
					}
					int num = symbols_beta.size();  // beta �ĳ���
					// ��ջ������ beta ���ȵ� ���� �� ״̬  
					while(num > 0) {
						stateStack.pop();
						symbolStack.pop();
						num--;
					}
					// �� A �� Goto(A) �Ⱥ�ѹ��ջ��
					String A = pro.split("->")[0].replaceAll(" ", "");
					symbolStack.push(A);
					int st = stateStack.peek();  // ��ǰջ��״̬
					int pos = Analysis.unfinalChar.indexOf(A);
					int state_tran = Integer.parseInt(Table.GOTO[st][pos]);
					stateStack.push(state_tran);
				}
			} else {
				System.out.println("LR ��������!!!");
				break;
			}
		}
		return false;
	}
	
	// ��ֵ��䣬 �������
	public static void assignment(Integer trans_state) {
		if (trans_state == 7) { // S -> id = E ;
			valueStack.pop();  // ;
			String eAddr = valueStack.pop().getValue();  // E
			valueStack.pop();  // =
			String idValue = valueStack.pop().getValue(); // id
			if (Tool.symbolTable.keySet().contains(idValue)) {
				Generator.gencode(idValue + " = " + eAddr);
			} else {
				System.out.println("����δ����!!!");
			}
			valueStack.push(new Attribute());
		} else if (trans_state == 8) { // E -> E + E
			String e2 = valueStack.pop().getValue();  // E
			valueStack.pop();  // +
			String e1 = valueStack.pop().getValue();  // E
			Attribute attribute = new Attribute();
			String temp_new = newtemp();
			attribute.setValue(temp_new);
			valueStack.push(attribute);
			Generator.gencode(temp_new + " = " + e1 + " + " + e2);
		} else if (trans_state == 9) { // E -> E * E
			String e2 = valueStack.pop().getValue();  // E
			valueStack.pop();  // *
			String e1 = valueStack.pop().getValue();  // E
			Attribute attribute = new Attribute();
			String temp_new = newtemp();
			attribute.setValue(temp_new);
			valueStack.push(attribute);
			Generator.gencode(temp_new + " = " + e1 + " * " + e2);
		} else if (trans_state == 10) { // E -> - E
			String e1 = valueStack.pop().getValue();  // E
			valueStack.pop();  // -
			Attribute attribute = new Attribute();
			String temp_new = newtemp();
			attribute.setValue(temp_new);
			valueStack.push(attribute);
			Generator.gencode(temp_new + " = - " + e1);
		} else if (trans_state == 11) { // E -> ( E )
			valueStack.pop();  // )
			Attribute e1 = valueStack.pop();  // E
			valueStack.pop();  // (
			valueStack.push(e1);
		}
	}

	// �������ʽ�� �������
	public static void boolAnaly(Integer trans_state) {
		if (trans_state == 14) { // B -> B COMP B
			Attribute b2 = valueStack.pop();  // B
			Attribute or = valueStack.pop();  // COMP
			Attribute b1 = valueStack.pop();  // B
			Tool.backpatch(b1.getFalselist(), or.getQuad());
			Attribute attribute = new Attribute();
			attribute.setTruelist(Tool.merge(b1.getTruelist(), b2.getTruelist()));
			attribute.setFalselist(b2.getFalselist());
			valueStack.push(attribute);
		} else if (trans_state == 15) { // B -> B COM B
			Attribute b2 = valueStack.pop();  // B
			Attribute and = valueStack.pop(); // COM
			Attribute b1 = valueStack.pop();  // B
			Tool.backpatch(b1.getTruelist(), and.getQuad());
			Attribute attribute = new Attribute();
			attribute.setTruelist(b2.getTruelist());
			attribute.setFalselist(Tool.merge(b1.getFalselist(), b2.getFalselist()));
			valueStack.push(attribute);
		} else if (trans_state == 16) { // B -> not B
			Attribute b1 = valueStack.pop();  // B
			valueStack.pop();  // not
			Attribute attribute = new Attribute();
			attribute.setTruelist(b1.getFalselist());
			attribute.setFalselist(b1.getTruelist());
			valueStack.push(attribute);
		} else if (trans_state == 17) { // B -> ( B )
			valueStack.pop();  // )
			Attribute b1 = valueStack.pop();  // B
			valueStack.pop();  // (
			valueStack.push(b1);
		} else if (trans_state == 18 || trans_state == 19 || trans_state == 20 || trans_state == 21) { 
			// B -> E > E | E < E | E <= E | E >= E
			Attribute e2 = valueStack.pop();  // E
			valueStack.pop(); // >  <  >=  <=
			Attribute e1 = valueStack.pop();  // E
			Attribute attribute = new Attribute();
			attribute.setTruelist(Tool.makelist(Generator.codeList.size()));
			attribute.setFalselist(Tool.makelist(Generator.codeList.size() + 1));
			valueStack.push(attribute);
			if (trans_state == 18) {
				Generator.gencode("if " + e1.getValue() + " > " + e2.getValue() + " goto -");
			} else if (trans_state == 19) {
				Generator.gencode("if " + e1.getValue() + " < " + e2.getValue() + " goto -");
			} else if (trans_state == 20) {
				Generator.gencode("if " + e1.getValue() + " >= " + e2.getValue() + " goto -");
			} else if (trans_state == 21) {
				Generator.gencode("if " + e1.getValue() + " <= " + e2.getValue() + " goto -");
			}
			Generator.gencode("goto -");
		} else if (trans_state == 22) { // B -> true
			Attribute b = valueStack.pop();
			b.setTruelist(Tool.makelist(Generator.codeList.size()));
			Generator.gencode("goto -");
			valueStack.push(b);
		} else if (trans_state == 23) { // B -> false
			Attribute b = valueStack.pop();
			b.setFalselist(Tool.makelist(Generator.codeList.size()));
			Generator.gencode("goto -");
			valueStack.push(b);
		} else if (trans_state == 24 || trans_state == 25) { // COMP -> or  COM -> and
			Attribute b = valueStack.pop();
			b.setQuad(Generator.codeList.size());
			valueStack.push(b);
		}
	}

	// ������ѭ�����
	public static void jump(Integer trans_state) {
		if (trans_state == 26 || trans_state == 27 || trans_state == 28) {  // TH -> then  WH -> while  DO -> do
			Attribute b = valueStack.pop();
			b.setQuad(Generator.codeList.size());
			valueStack.push(b);
		} else if (trans_state == 29) {  // S -> if B TH S
			Attribute s1 = valueStack.pop();
			Attribute m = valueStack.pop();
			Attribute b = valueStack.pop();
			valueStack.pop();  // if
			Tool.backpatch(b.getTruelist(), m.getQuad());
			Attribute s = new Attribute();
			s.setNextlist(Tool.merge(b.getFalselist(), s1.getNextlist()));
			valueStack.push(s);
		} else if (trans_state == 30) {  // S -> WH B DO S
			Attribute s1 = valueStack.pop();
			Attribute m2 = valueStack.pop();
			Attribute b = valueStack.pop();
			Attribute m1 = valueStack.pop();
			Tool.backpatch(s1.getNextlist(), m1.getQuad());
			Tool.backpatch(b.getTruelist(), m2.getQuad());
			Attribute s = new Attribute();
			s.setNextlist(b.getFalselist());
			Generator.gencode("goto " + m1.getQuad());
			valueStack.push(s);
		} else if (trans_state == 31) {  // S -> A
			Attribute s = new Attribute();
			s.setNextlist(new ArrayList<>());
			valueStack.push(s);
		}
	}
	
	// �½�һ������
	public static String newtemp() {
		tempIndex ++;
		String temp = "t" + tempIndex;
		while(Tool.symbolTable.containsKey(temp) || newtemps.contains(temp)) {
			tempIndex ++;
			temp = "t" + tempIndex;
		}
		newtemps.add(temp);
		return temp;
	}
}
