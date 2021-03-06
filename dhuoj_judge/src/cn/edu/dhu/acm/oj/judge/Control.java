package cn.edu.dhu.acm.oj.judge;

import cn.edu.dhu.acm.oj.common.config.*;
import cn.edu.dhu.acm.oj.common.judge.*;
import cn.edu.dhu.acm.oj.common.bean.RunBean;
import cn.edu.dhu.acm.oj.common.config.Const;
import cn.edu.dhu.acm.oj.common.paper.PaperBean;
import cn.edu.dhu.acm.oj.common.problem.SpecialJudgeBean;
import cn.edu.dhu.acm.oj.persistence.beans.SolutionBean;
import cn.edu.dhu.acm.oj.persistence.beans.MessageBean;
import cn.edu.dhu.acm.oj.persistence.beans.CompileinfoBean;
import java.io.*;
import java.net.*;
import java.util.*;

public class Control {

	//public:
	public Control() {
	}

	public static void init(MainFrame f) {

		java.io.File file = new java.io.File("./Environment.xml");
		if (file.exists()) {
			System.out.println("Exist Environment.xml");
			envbean = new EnvironmentBean("./Environment.xml");
		} else {
			System.out.println("No Environment.xml");
			envbean = new EnvironmentBean();
		}
		solutionbean = null;
		mainframe = f;
		isauto = false;
		isAcceptLocaljudge = true;
		codeframe = new CodeInfFrame();
		problemframe = new ProblemLookFrame();
		applyframe = new ApplyFrame();
		try {
			server = new ServerSocket(Const.CLIENT_RCV_SOCKET);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isIsAcceptLocaljudge() {
		return isAcceptLocaljudge;
	}

	public static void setInfo(String str) {
		codeframe.setInfo(str);
	}

	public static void setCode(String str) {
		codeframe.setCode(str);
		codeframe.setVisible(true);
	}

	public static void setApply(){
		applyframe.setVisible(true);
	}

	public static void setProblem() {
		problemframe.setProblem(paperbean, 0);
		problemframe.setVisible(true);
	}

	public static void setIsAcceptLocaljudge(boolean isAcceptLocaljudge) {
		Control.isAcceptLocaljudge = isAcceptLocaljudge;
	}

	public static void setIP(String SIP) {
		ServerIP = SIP;
	}

	public static void setIsauto(boolean t) {
		isauto = t;
	}

	//for Receiver
	public static void Receive() {
		ObjectInputStream ois = null;
		try {
			Socket socket = server.accept();
			ois = new ObjectInputStream(socket.getInputStream());
			Object obj = ois.readObject();
			if (obj instanceof SolutionBean) {
				synchronized (solutionqueue) {
					solutionqueue.add((SolutionBean) obj);
					mainframe.setSolutionQueue(solutionqueue.size());
					mainframe.setVisible(true);
					System.out.println("Received a SolutionBean "+((SolutionBean) obj).getSolutionId());
					socket.getOutputStream().write("OK\r\n".getBytes());
				}
			} else if (obj instanceof MessageBean) {
				synchronized (messagequeue) {
					messagequeue.add((MessageBean) obj);
					mainframe.setMessageQueue(messagequeue.size(), true);
					mainframe.setVisible(true);
					System.out.println("Received a MessageBean "+((MessageBean) obj).getMessageId());
					socket.getOutputStream().write("OK\r\n".getBytes());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ois.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	//Judge Step 1
	public static boolean GetSubmit() {
		boolean ans = false;
		synchronized (solutionqueue) {
			if (solutionqueue.isEmpty()) {
				solutionbean = null;
			} else {
				solutionbean = solutionqueue.removeFirst();
				Solution2Run();
				ans = true;
				mainframe.setSolutionGotten();
				mainframe.setSolutionQueue(solutionqueue.size());
			}
		}
		return ans;
	}

	//Judge Step 2
	public static void Judge() {
		if (solutionbean != null) {
			short localresult = solutionbean.getLocalJudgeResult();
			if (solutionbean.getProblemId() == 0 && localresult == Const.AC) {
				runbean.setResult(localresult);
				return;
			}
			if (isAcceptLocaljudge) {
				if (localresult != Const.AC && localresult != Const.CE) {
					runbean.setResult(localresult);
					return;
				}
			}
                        
                        System.out.println("Problem: " + solutionbean.getProblemId());
                        
                        // Add for contest 20141216-2
                        /*
                        if (solutionbean.getContestId() == 201412162) {
                            if ((solutionbean.getLanguage() != Const.C)) {
                                runbean.setResult(Const.CE);
                                runbean.setCompileInfo("C++/Java is forbiddened!");
                                System.out.println("C++/Java is forbiddened!");
                                return;
                            }
                        }
                        if (solutionbean.getProblemId() == 14) {  // problem D
                            if ((solutionbean.getSourceCode().getSource().contains("strlen"))) {
                                runbean.setResult(Const.CE);
                                runbean.setCompileInfo("strlen() is forbiddened!");
                                System.out.println("strlen() is forbiddened!");
                                return;
                            }
                        }
                        */
                        
			judger = new Judger(runbean, envbean);
			if (judger.Compile()) {
				judger.Run();
				judger.Check();
			}
			System.out.println("Result: " + runbean.getResult());
		}
	}

	//Judge Step 3
	public static void SendResult() {
		if (solutionbean != null) {
			Run2Solution();
			synchronized (sendqueue) {
				sendqueue.add(solutionbean);
			}
		}
	}

	//Message Step 1
	public static boolean GetMessage() {
		boolean ans = false;
		synchronized (messagequeue) {
			if (messagequeue.isEmpty()) {
				messagebean = null;
			} else {
				messagebean = messagequeue.removeFirst();
				ans = true;
				mainframe.setMessageQueue(messagequeue.size(), false);
			}
		}
		return ans;
	}

	//Message Step 2
	public static void SendMessage() {
		if (messagebean != null) {
			synchronized (sendqueue) {
				sendqueue.add(messagebean);
			}
		}
	}

	//for Sender
	public static void Send() {
		try {
			synchronized (sendqueue) {
				if (!sendqueue.isEmpty()) {
					if (sendBean(sendqueue.getFirst())) {
						sendqueue.removeFirst();
						System.out.println("send one");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.gc();
		}

	}

	public static int getSolutionQueueNum() {
		synchronized (solutionqueue) {
			return solutionqueue.size();
		}
	}

	public static RunBean getRunbean() {
		return runbean;
	}

	public static SolutionBean getSolutionbean() {
		return solutionbean;
	}

	public static MessageBean getMessagebean() {
		return messagebean;
	}

	public static PaperBean getPaperbean() {
		return paperbean;
	}

	public static boolean getIsauto() {
		return isauto;
	}

	public static MainFrame getMainFrame(){
		return mainframe;
	}
        public static ApplyFrame getApplyFrame(){
            return applyframe;
        }
	//private:
	private static void Solution2Run() {
		runbean = new RunBean();
		runbean.setLanguage(solutionbean.getLanguage());
		runbean.setSourceCode(solutionbean.getSourceCode().getSource());

		String name = solutionbean.getProblemId() + ".xml";
		if (!name.equals(paperName)) {
			setPaper(name);
		}
		runbean.setInput(tmpIn);
		runbean.setStdAns(tmpAns);
		runbean.setTimeLimit(tmpTimelim);
                runbean.setIsSpecial(tmpIsSpecial);
                runbean.setSpecialJudgeList(tmpSpecialJudgeList);
	}

	private static void Run2Solution() {
		//solutionbean.getCompileInfo().setError(runbean.getCompileInfo());
		solutionbean.setRuntime((int) runbean.getTimeUsed());
		solutionbean.setResult(runbean.getResult());
                solutionbean.setCompileInfo(new CompileinfoBean(solutionbean.getSolutionId(), runbean.getCompileInfo()));
	}

	private static void setPaper(String x) {
		try {
			paperName = x;
			paperbean = new PaperBean();
			paperbean.unmarshal("./paper/" + paperName);
			tmpIn = paperbean.getProblemAt(0).getTestData().getTestInput();
			tmpAns = paperbean.getProblemAt(0).getTestData().getTestOutput();
			tmpTimelim = paperbean.getProblemAt(0).getTestData().getTimeLimit();
                        
                        tmpIsSpecial = paperbean.getProblemAt(0).getJudgeType().equals("Special Judge");
                        System.out.println("SPJ ? : " + tmpIsSpecial);
                        if (tmpIsSpecial && paperbean.getProblemAt(0).getSpecialJudgeCount() > 0) {
                            tmpSpecialJudgeList = paperbean.getProblemAt(0).getSpecialJudgeList();
                        }
		} catch (Exception E) {
			E.printStackTrace();
		}
	}

	private static boolean sendBean(Object b) {
		Socket socket = null;
		try {
			socket = new Socket(ServerIP, Const.SERVER_RCV_SOCKET);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(b);
			Scanner scan = new Scanner(socket.getInputStream());

			if (scan.nextLine().equals("OK")) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				socket.close();
				socket = null;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	private static String ServerIP;
	private static Judger judger;
	private static EnvironmentBean envbean;
	private static ServerSocket server = null;
	private static final LinkedList<SolutionBean> solutionqueue = new LinkedList();
	private static final LinkedList<MessageBean> messagequeue = new LinkedList();
	private static final LinkedList<Object> sendqueue = new LinkedList();
	private static PaperBean paperbean;
	private static String paperName = Const.INITPAPER;
	private static String tmpIn,  tmpAns;
	private static long tmpTimelim;
        private static boolean tmpIsSpecial;
        private static List<SpecialJudgeBean> tmpSpecialJudgeList;
	private static boolean isauto;
	private static boolean isAcceptLocaljudge;
	private static MainFrame mainframe;
	private static SolutionBean solutionbean;
	private static MessageBean messagebean;
	private static RunBean runbean;
	private static CodeInfFrame codeframe;
	private static ProblemLookFrame problemframe;
	private static ApplyFrame applyframe;
}