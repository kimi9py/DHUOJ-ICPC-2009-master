/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.dhu.acm.oj.notification.server.thread;

import cn.edu.dhu.acm.oj.common.config.Const;
import cn.edu.dhu.acm.oj.notification.server.ServerUI;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wujy
 */
public class RunReceive implements Runnable {

    DatagramSocket receiveSocket = null;
    ServerUI serverUI;

    public RunReceive(ServerUI serverUI) {
        this.serverUI = serverUI;
    }

    public void run() {
        try {
            receiveSocket = new DatagramSocket(Const.SERVER_RCV_SOCKET);
            System.out.println("Waiting for replies ...");
            while (true) {
                byte[] buf = new byte[1024];
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                receiveSocket.receive(dp);

                String ip = dp.getAddress().getHostAddress();
                String tmp = new String(dp.getData(), 0, dp.getLength(), "UTF-8");
                System.out.println(ip + " >> " + tmp);
                String[] data = tmp.split("\\$\\|\\$");  // ID$|$Type$|$Time$|$Length$|$Content

                if (data.length == 5 && data[4].length() == Integer.parseInt(data[3])) {  // Simple verify
                    int notificationID = Integer.parseInt(data[0]);
                    if (data[4].equals("AC")) {
                        if (serverUI.mapClientIP.get(ip) != null) {
                            serverUI.status.get(notificationID-1).set(serverUI.mapClientIP.get(ip), Boolean.TRUE);
                            System.out.println("Send notification #" + notificationID + " to " + ip + " succeeded.");
                        }
                    } else {
                        System.out.println("Send notification #" + notificationID + " to " + ip + " failed.");
                    }
                }
            }
        } catch (SocketException ex) {
            Logger.getLogger(RunReceive.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(RunReceive.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RunReceive.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (receiveSocket != null) {
                receiveSocket.close();
            }
            System.out.println("Reply-receiving thread aborted.");
        }
    }
    
}
