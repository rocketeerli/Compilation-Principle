package syntactic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import codeGen.Generator;
import entity.Attribute;

// LR 规约类
public class LR {
	// 两个栈，一个队列
	public static Stack<Integer> stateStack = new Stack<Integer>();  // 状态栈
	public static Stack<String> symbolStack = new Stack<String>();  // 符号栈
	public static Queue<String> buffer = new LinkedList<String>();  // 缓冲区
	// 语义分析  新加一个 value 栈 （属性值栈）
	public static Stack<Attribute> valueStack = new Stack<Attribute>(); // 属性值栈
	public static Integer offset = 0;  // 地址偏移量
	public static List<String> newtemps = new ArrayList<>(); // 存储新增加的变量
	public static Integer tempIndex = 0;

	// 利用生成的 ACTION 和 GOTO 表对源码进行规约
	public static boolean analyzeLR(List<String> input) {
		// 将字符串加入到缓冲区
		for (String string : input) {
			buffer.offer(string);
		}
		buffer.offer("#");
		// 初始化状态栈
		stateStack.push(0);
		symbolStack.push("#");
		// 开始规约
		while (true) {
			int state = stateStack.peek();  // 当前栈顶状态
			int ip = Analysis.finalChar.indexOf(buffer.element());  // 缓冲区指针指向的字符
			String action = Table.ACTION[state][ip];
//			System.out.println("栈顶：" + buffer.element() + "\t当前状态：" + state);
			if (action != null) {
				if (action.equals("acc")) {
					return true;
				}
				String act = action.substring(0, 1);  // 'R' or 'S'  规约或是移入
				int trans_state = Integer.parseInt(action.substring(1));  // 转移到的下一个状态或是规约的产生式
				// 移入操作
				if (act.equals("S")) {
					// 把状态 i(trans_state) 压入到状态栈中，并将符号压入到符号栈中
					stateStack.push(trans_state);
					String buf = buffer.poll();
					symbolStack.push(buf);
//					语义分析新增  每压入一个符号，同时压入一个属性
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
				} else if (act.equals("R")) { // 规约操作
					// 按照第 k(trans_state) 个产生式进行规约
					String pro = Analysis.GRAMMER.get(trans_state);
					
//					语义分析  根据规约的产生式  执行相应的动作
					// 声明语句
					if (trans_state == 2) {  // D -> T L ;
						valueStack.pop();
						Tool.enter(valueStack.pop().getValue(), valueStack.peek().getValue(), offset);
						offset += valueStack.pop().getWidth();
						valueStack.push(new Attribute());
//						Generator.gencode("声明语句：");
					} 
					// 赋值语句
					assignment(trans_state);
					// 布尔表达式
					boolAnaly(trans_state);
					// 条件、循环语句
					jump(trans_state);
					
					// 求 beta
					List<String> symbols_beta = new ArrayList<>();
					String[] strList = pro.split("->")[1].split(" ");
					for (int i = 0; i < strList.length; i++) {
						if (strList[i].length() > 0) {
							symbols_beta.add(strList[i]);
						}
					}
					int num = symbols_beta.size();  // beta 的长度
					// 从栈顶弹出 beta 长度的 符号 和 状态  
					while(num > 0) {
						stateStack.pop();
						symbolStack.pop();
						num--;
					}
					// 把 A 和 Goto(A) 先后压入栈中
					String A = pro.split("->")[0].replaceAll(" ", "");
					symbolStack.push(A);
					int st = stateStack.peek();  // 当前栈顶状态
					int pos = Analysis.unfinalChar.indexOf(A);
					int state_tran = Integer.parseInt(Table.GOTO[st][pos]);
					stateStack.push(state_tran);
				}
			} else {
				System.out.println("LR 分析错误!!!");
				break;
			}
		}
		return false;
	}
	
	// 赋值语句， 语义分析
	public static void assignment(Integer trans_state) {
		if (trans_state == 7) { // S -> id = E ;
			valueStack.pop();  // ;
			String eAddr = valueStack.pop().getValue();  // E
			valueStack.pop();  // =
			String idValue = valueStack.pop().getValue(); // id
			if (Tool.symbolTable.keySet().contains(idValue)) {
				Generator.gencode(idValue + " = " + eAddr);
			} else {
				System.out.println("变量未声明!!!");
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

	// 布尔表达式， 语义分析
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

	// 条件、循环语句
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
	
	// 新建一个变量
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
