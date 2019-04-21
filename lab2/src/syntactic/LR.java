package syntactic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class LR {
	// ����ջ��һ������
	public static Stack<Integer> stateStack = new Stack<Integer>();  // ״̬ջ
	public static Stack<String> symbolStack = new Stack<String>();  // ����ջ
	public static Queue<String> buffer = new LinkedList<String>();;  // ������

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
					symbolStack.push(buffer.poll());
				} else if (act.equals("R")) { // ��Լ����
					// ���յ� k(trans_state) ������ʽ���й�Լ
					String pro = Analysis.GRAMMER.get(trans_state);
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
					// �������ʽ
					System.out.println(pro);
				}
			} else {
				System.out.println("LR ��������!!!");
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
