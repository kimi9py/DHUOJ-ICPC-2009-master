/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test;
import cn.edu.dhu.acm.oj.persistence.dao.SolutionDAO;
import cn.edu.dhu.acm.oj.persistence.beans.SolutionBean;
import cn.edu.dhu.acm.oj.persistence.beans.SourceCodeBean;
import cn.edu.dhu.acm.oj.persistence.dao.SourceCodeDAO;
import cn.edu.dhu.acm.oj.common.config.Const;
import cn.edu.dhu.acm.oj.persistence.dao.UserDAO;
import cn.edu.dhu.acm.oj.persistence.dao.ContestProblemDAO;
import cn.edu.dhu.acm.oj.persistence.beans.ContestProblemBean;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class Main {

    /**
     * @param args the command line arguments
     */


    public static void main(String[] args) {
        // TODO code application logic here
        File rootDir = new File("./sourceCodes");
        int contestId;
        if (args.length != 1) {
            System.out.println("Usage: java -jar GenerateSourceCodes.jar {contestId}");
            return;
        } else {
            contestId = Integer.parseInt(args[0]);
        }
        
        rootDir.mkdir();

        SolutionDAO sd = new SolutionDAO();
        List<SolutionBean> list = new ArrayList<SolutionBean>();
        list = sd.findContestSolutionsInRange(contestId, 0, Integer.MAX_VALUE);
        
        UserDAO uDao = new UserDAO();
 
        int k=0;
        List tempList = new ArrayList();
        ContestProblemDAO contestProblemDAO = new ContestProblemDAO();
        List contestProblemList = contestProblemDAO.findProblemListByContest(contestId);
        Map titleMap = new HashMap();
        ContestProblemBean contestProblemBean;
        for(Iterator iter=contestProblemList.iterator();iter.hasNext();){
            contestProblemBean = (ContestProblemBean)iter.next();
            titleMap.put(contestProblemBean.getProblemId(),contestProblemBean.getTitle());
        }
//        ContestDAO contestDAO = new ContestDAO();
//
//        ContestBean contestBean = contestDAO.findContest(contestId);
//        contestProblemDAO.findProblemListByContest(k);
        for (SolutionBean s : list)
        {
            k++;
            String uid = s.getUserId();
            Short result = s.getResult();
            int solutionId = s.getSolutionId();
            String resultStr ="";
            int problemId = s.getProblemId();
            SourceCodeDAO scd  = new SourceCodeDAO();
            SourceCodeBean code = scd.findSourceCode(solutionId);
            if (code == null) continue;
            String source = code.getSource();
            byte[] b = source.getBytes();
            String userNick = uDao.findUser(uid).getNick();
            //String source = code.getSource();
//            System.out.println("problemBean:"+problemBean);
            byte la = s.getLanguage();
//            System.out.println(result + la + " ");
            String laStr = "";
            String title = "";
            switch(result){
                case Const.AC:resultStr="AC";
                break;
                case Const.CE:resultStr="CE";
                break;
                case Const.MLE:resultStr="MLE";
                break;
                case Const.OLE:resultStr="OLE";
                break;
                case Const.PE:resultStr="PE";
                break;
                case Const.TLE:resultStr="TLE";
                break;
                case Const.WA:resultStr="WA";
                break;
                case Const.RE:resultStr="RE";
                break;
            }

            switch(la){
                case Const.C: laStr = "C";
                break;
                case Const.CPP:laStr="CPP";
                break;
                case Const.JAVA:laStr="JAVA";
                break;
                case Const.PASCAL:laStr="PASCAL";
                break;
            }

            if(titleMap.get(problemId) != null){
                 title =titleMap.get(problemId).toString();
            }
            if (problemId == 0) {
                title = "A+B";
            }

            if(!tempList.contains(uid)){
                tempList.add(uid);
                File dir = new File("sourceCodes" +
                        "/" + uid  + "_"+userNick);
                dir.mkdir();
            }

            FileOutputStream fos = null;
            try {
                File file = new File("sourceCodes" +
                        "/"+ uid+"_"+userNick+"/"+ k + "_" + uid+"_"+userNick+"_"+title+"_"+resultStr + "."+laStr);
                fos = new FileOutputStream(file);
                fos.write(b);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            System.out.println(k + "_" + uid+"_"+userNick+"_"+title+"_"+resultStr + "."+laStr);
        }

    }

}
