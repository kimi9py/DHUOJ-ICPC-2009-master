package cn.edu.dhu.acm.oj.judge.server;

import java.io.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;


public class ServerController {

    ServerRcvImpl s_rcv = null;
    ServerSendImpl s_send = null;
    DBService dbs = null;

    public ServerController() {
        dbs = new DBService();
        s_rcv = new ServerRcvImpl(dbs);
        s_send = new ServerSendImpl(dbs);
        loadJudges();
        new Thread(s_rcv).start();
        new Thread(s_send).start();
        new Thread(dbs).start();
    }

    private void loadJudges() {
        try {
            Scanner scan = new Scanner(new FileInputStream(new File("judge.ini")));
            String line = null;
            while (scan.hasNextLine()) {
                line = scan.nextLine();
                if (!line.startsWith("#")) {
                    String arr[] = line.split(",");
                    if (arr.length >= 1) {
                        String server[] = arr[0].split(":");
                        boolean receiveMessage = true;
                        Set<Integer> blocked = new HashSet<Integer>();
                        if (arr.length >= 2) {
                            receiveMessage = Boolean.parseBoolean(arr[1]);
                            for (int i = 2; i < arr.length; i++) {
                                blocked.add(Integer.parseInt(arr[i]));
                            }
                        }
                        s_send.addJudge(server[0], Integer.parseInt(server[1]), receiveMessage, blocked);
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ServerController s = new ServerController();
    }
}
