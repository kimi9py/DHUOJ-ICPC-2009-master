package cn.edu.dhu.acm.oj.common.judge;

import cn.edu.dhu.acm.oj.common.bean.*;
import cn.edu.dhu.acm.oj.common.config.*;
import java.io.*;

public class Run extends Thread {

	public Run(RunBean rb, EnvironmentBean eb) {
		runbean = rb;
		String lan = Const.LANGUAGE[rb.getLanguage()];
		commandline = eb.getRunCmd(lan, Const.COMPILENAME);
	}

	private void InputData(OutputStream os) {
		try {
			os.write(runbean.getInput().getBytes());
			os.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	@Override
	public void destroy() {
		try {
			runbean.setResult(Const.TLE);
			tend = System.currentTimeMillis();
			runbean.setTimeUsed(tend - tbegin);
			pro.destroy();
			pin.close();
			pout.close();
			perr.close();
		} catch (IOException IOE) {
			System.out.println(IOE.toString());
		}
	}

	@Override
	public void run() {
		long tle = 1;
		try {
			tle = runbean.getTimeLimit();
			if (runbean.getLanguage() == Const.JAVA) {
				tle = tle * Const.JAVA_LIMIT;
			}
			tk = new TimeoutKill(tle);
			System.out.println("Run : " + commandline);
			pro = Runtime.getRuntime().exec(commandline);
			tbegin = System.currentTimeMillis();
			pin = pro.getOutputStream();
			pout = pro.getInputStream();
			perr = pro.getErrorStream();
			errris = new ReadInputStream(perr);
			outris = new ReadInputStream(pout);

			tk.setTestThread(this);
			errris.start();
			outris.start();
			tk.start();

			InputData(pin);
			pro.waitFor();
			outris.join();
			errris.join();

			tend = System.currentTimeMillis();
			runbean.setTimeUsed(tend - tbegin);
			if ((tend - tbegin) >= (tle * (long) 1000)) {
				runbean.setResult(Const.TLE);
			} else {
				int exitValue = pro.exitValue();
				if (exitValue == 0) {
					tk.stop();
					runbean.setOutput(outris.getMessage());
					if (outris.getMessage().length() >= Const.FILEMAXSIZE) {
						runbean.setResult(Const.OLE);
					}
				} else {
					tk.stop();
					runbean.setResult(Const.RE);
					runbean.setOutput(outris.getMessage());
				}
			}
			pin.close();
			pro.destroy();
			System.out.println("Run Done!");
		} catch (Exception E) {
			tk.stop();
			tend = System.currentTimeMillis();
			if ((tend - tbegin) >= (tle * (long) 1000)) {
				runbean.setResult(Const.TLE);
			} else {
				runbean.setResult(Const.RE);
			}
			E.printStackTrace();
		}
	}

	public RunBean getRunBean() {
		return runbean;
	}
	private RunBean runbean;
	private Process pro;
	private OutputStream pin;
	private InputStream perr;
	private InputStream pout;
	private ReadInputStream errris;
	private ReadInputStream outris;
	private String commandline;
	private TimeoutKill tk;
	private long tbegin;
	private long tend;
}
