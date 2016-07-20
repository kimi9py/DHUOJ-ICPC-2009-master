/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.dhu.acm.oj.notification.server.bean;

/**
 *
 * @author wujy
 */
public class ClientBean {
    
    private String userID;
    private String password;
    private int contestID;
    private String IP;

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setContestID(int contestID) {
        this.contestID = contestID;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }
    
    public String getUserID() {
        return userID;
    }

    public String getPassword() {
        return password;
    }

    public int getContestID() {
        return contestID;
    }

    public String getIP() {
        return IP;
    }
    
}
