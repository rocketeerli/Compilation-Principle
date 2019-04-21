package syntactic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class LR {
	// 两个栈，一个队列
	public static Stack<Integer> stateStack = new Stack<Integer>();  // 状态栈
	public static Stack<String> symbolStack = new Stack<String>();  // 符号栈
	public static Queue<String> buffer = new LinkedList<String>();;  // 缓冲区

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
					symbolStack.push(buffer.poll());
				} else if (act.equals("R")) { // 规约操作
					// 按照第 k(trans_state) 个产生式进行规约
					String pro = Analysis.GRAMMER.get(trans_state);
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
					// 输出产生式
					System.out.println(pro);
				}
			} else {
				System.out.println("LR 分析错误!!!");
				break;
			}
//			System.out.println(buffer.element());
//			System.out.println("1:  " + Table.ACTION[state]);
//			System.out.println("3:  state: " + state + "\t ip:" + ip);
//			System.out.println("2:  " + Table.ACTION[state][ip]);
		}
		return false;
	}
}
