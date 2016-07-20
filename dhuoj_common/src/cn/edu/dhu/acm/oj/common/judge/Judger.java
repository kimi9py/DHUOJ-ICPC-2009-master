package cn.edu.dhu.acm.oj.common.judge;

import cn.edu.dhu.acm.oj.common.bean.*;
import cn.edu.dhu.acm.oj.common.config.*;
import cn.edu.dhu.acm.oj.common.problem.SpecialJudgeBean;
import cn.edu.dhu.acm.oj.common.problem.TestCaseBean;
import java.util.Iterator;
import org.jdom.Element;

public class Judger {

	public Judger(RunBean rb, EnvironmentBean eb) {
		rbean = rb;
		envbean = eb;
	}

	public String getCompileinfo() {
		return compileinfo;
	}

	public boolean Compile() {
		CheckMaliciousCode cmc = new CheckMaliciousCode(rbean);
		compile = cmc.checkKeyWord();
		if (compile) {
			Compile cc = new Compile(rbean, envbean);
			compile = cc.doit();
		}
		compileinfo = rbean.getCompileInfo();
		return compile;
	}

	public void Run() {
		Run rr = new Run(rbean, envbean);
		rr.start();
		try {
			rr.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Check() {
            if (rbean.getResult() == Const.QUEUE) {
                if (!rbean.isIsSpecial()) {
                    CheckAnswer ca = new CheckAnswer();
                    ca.setAnswer(rbean.getOutput());
                    ca.setStandarAnswer(rbean.getStdAns());
                    ca.AnswerCheck();
                    rbean.setResult(ca.getVerdict());
                    rbean.setPercent(ca.getPercent());
                } else {  // Special Judge
                    short result = -1, id = 0;
                    Iterator it = rbean.getSpecialJudgeList().iterator();
                    while ( it.hasNext() ) {
                        SpecialJudgeBean specialJudgeBean = new SpecialJudgeBean( (Element)( it.next() ));
                        CheckAnswerSpecial cas = new CheckAnswerSpecial();
                        cas.setAnswer(rbean.getOutput());
                        cas.setStandardAnswer(rbean.getStdAns());
                        cas.setStandardInput(rbean.getInput());
                        cas.setSpecialCode(specialJudgeBean.getSourceCode());
                        cas.setSpecialLanguage(Const.getLanguageByte(specialJudgeBean.getLanguage()));
                        cas.setEnvbean(envbean);
                        cas.AnswerCheck();
                        System.out.println("SPJ #" + (++id) + " Verdict: " + cas.getVerdict());
                        if (result == -1) {
                            result = cas.getVerdict();
                        } else if (result != cas.getVerdict()) {
                            rbean.setResult(Const.QUEUE);
                            rbean.setPercent(50);
                            return;
                        }
                    }
                    rbean.setResult(result);
                    rbean.setPercent(100);  // TODO: change to real percent?
                }
            }
	}
	private RunBean rbean;
	private EnvironmentBean envbean;
	private boolean compile;
	private String compileinfo;
}
