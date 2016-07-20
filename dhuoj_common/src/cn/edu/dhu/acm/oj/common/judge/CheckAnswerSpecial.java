package cn.edu.dhu.acm.oj.common.judge;

import cn.edu.dhu.acm.oj.common.config.Const;
import cn.edu.dhu.acm.oj.common.config.EnvironmentBean;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 *  Author: Jiaye Wu
 */
public class CheckAnswerSpecial {

    public CheckAnswerSpecial(String out, String in, String ans, String specialCode, EnvironmentBean envbean) {
        this.out = out;
        this.in = in;
        this.ans = ans;
        this.specialCode = specialCode;
        this.envbean = envbean;
    }

    public CheckAnswerSpecial() {
        out = "";
        in = "";
        ans = "";
        specialCode = "";
    }

    public void setAnswer(String s) {
        out = s;
    }

    public void setStandardAnswer(String s) {
        ans = s;
    }

    public void setStandardInput(String s) {
        in = s;
    }
    
    public void setSpecialCode(String s) {
        specialCode = s;
    }
    
    public void setSpecialLanguage(byte s) {
        specialLanguage = s;
    }
    
    public void setEnvbean(EnvironmentBean env) {
        envbean = env;
    }

    public void AnswerCheck() {
        verdict = Const.WA;
        percent = -1;
        String lan = "cpp";
        
        // Prepare data
        try {
            Writer writer = new BufferedWriter(new FileWriter(new File(envbean.getSource() + Const.USEROUTPUTFILENAME)));
            writer.write(out);
            writer.close();
            writer = new BufferedWriter(new FileWriter(new File(envbean.getSource() + Const.INPUTDATAFILENAME)));
            writer.write(in);
            writer.close();
            writer = new BufferedWriter(new FileWriter(new File(envbean.getSource() + Const.OUTPUTDATAFILENAME)));
            writer.write(ans);
            writer.close();
            
            lan = Const.LANGUAGE[specialLanguage];
            String filename = Const.SPJCOMPILENAME + envbean.getFormerSuffix(lan);
            File file = new File(envbean.getSource() + filename);
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(specialCode);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(CheckAnswerSpecial.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Compile SPJ program
        boolean compileresult = false;
        try {
            String commandline = envbean.getCompileCmd(lan, Const.SPJCOMPILENAME, Const.SPJCOMPILENAME);
            System.out.println("Compile : " + commandline);

            Process pro = Runtime.getRuntime().exec(commandline);
            outstream = pro.getInputStream();
            errstream = pro.getErrorStream();
            errris = new ReadInputStream(errstream);
            outris = new ReadInputStream(outstream);
            errris.start();
            outris.start();
            pro.waitFor();
            errris.join();
            outris.join();

            try {
                if (pro.exitValue() != 0) {
                    throw new Exception();
                } else {
                    compileresult = true;
                }
            } catch (Exception e) {
                String info = errris.getMessage() + outris.getMessage();
                if (info.equals("")) {
                    info = "Compile no exitValue!";
                }
                Logger.getLogger(CheckAnswerSpecial.class.getName()).log(Level.SEVERE, info);
                compileresult = false;
            }
            pro.destroy();
        } catch (IOException e) {
            compileresult = false;
            Logger.getLogger(CheckAnswerSpecial.class.getName()).log(Level.SEVERE, "IOException!", e);
        } catch (InterruptedException e) {
            compileresult = false;
            Logger.getLogger(CheckAnswerSpecial.class.getName()).log(Level.SEVERE, "InterruptedException!", e);
        }
        
        // Run SPJ
        if (compileresult) {
            //long tle = 1;
            try {
                //tk = new TimeoutKill(tle);
                String commandline = envbean.getRunCmd(lan, Const.SPJCOMPILENAME) + " " + Const.INPUTDATAFILENAME + " " + Const.OUTPUTDATAFILENAME + " " + Const.USEROUTPUTFILENAME;
                System.out.println("Run : " + commandline);
                
                Process pro = Runtime.getRuntime().exec(commandline);
                outstream = pro.getInputStream();
                errstream = pro.getErrorStream();
                errris = new ReadInputStream(errstream);
                outris = new ReadInputStream(outstream);

                //tk.setTestThread(this);  // TODO: SPJ timeout
                errris.start();
                outris.start();
                //tk.start();
                pro.waitFor();
                outris.join();
                errris.join();

                int exitValue = pro.exitValue();
                if (exitValue == 0) {
                    verdict = Const.AC;
                } else if (exitValue == 1) {
                    verdict = Const.WA;
                } else if (exitValue == 2) {
                    verdict = Const.PE;
                }
                
                if (!outris.getMessage().isEmpty()) {
                    System.out.println("Info: " + outris.getMessage());
                }
                if (!errris.getMessage().isEmpty()) {
                    System.out.println("Warning: " + errris.getMessage());
                }
                pro.destroy();
            } catch (IOException e) {
                Logger.getLogger(CheckAnswerSpecial.class.getName()).log(Level.SEVERE, "IOException!", e);
            } catch (InterruptedException e) {
                Logger.getLogger(CheckAnswerSpecial.class.getName()).log(Level.SEVERE, "InterruptedException!", e);
            } finally {
                //tk.stop();
            }
        }
    }

    public boolean getCheckResult() {
        if (verdict == Const.AC) {
            return true;
        }
        return false;
    }

    public short getVerdict() {
        return verdict;
    }

    public int getPercent() {
        return percent;
    }
    
    private short verdict;
    private int percent;
    private String ans;
    private String out;
    private String in;
    private String specialCode;
    private byte specialLanguage;
    private EnvironmentBean envbean;
    private InputStream errstream;
    private InputStream outstream;
    private ReadInputStream errris;
    private ReadInputStream outris;
    private TimeoutKill tk;
}
