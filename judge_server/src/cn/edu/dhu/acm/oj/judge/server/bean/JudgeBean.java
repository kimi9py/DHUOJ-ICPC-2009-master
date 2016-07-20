package cn.edu.dhu.acm.oj.judge.server.bean;

import java.util.HashSet;
import java.util.Set;

public class JudgeBean {

    private String ip;
    private int port;
    private long judges = 0;
    private int retry = 0;
    private boolean receiveMessage;

    public void setReceiveMessage(boolean receiveMessage) {
        this.receiveMessage = receiveMessage;
    }

    public boolean isReceiveMessage() {
        return receiveMessage;
    }
    private Set<Integer> blocked = new HashSet<Integer>();

    public boolean isBlocked(int problem) {
        return blocked.contains(problem);
    }
    
    public void addBlocked(int problem) {
        blocked.add(problem);
    }
    
    public void setBlocked(Set<Integer> blocked) {
        this.blocked = blocked;
    }

    public Set<Integer> getBlocked() {
        return blocked;
    }

    public void incRetry() {
        retry++;
    }

    public void incJudges() {
        judges++;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getJudges() {
        return judges;
    }

    public int getRetry() {
        return retry;
    }
}
