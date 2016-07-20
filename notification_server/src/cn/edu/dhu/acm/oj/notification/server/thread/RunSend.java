/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.dhu.acm.oj.notification.server.thread;

import cn.edu.dhu.acm.oj.common.config.Const;
import cn.edu.dhu.acm.oj.notification.server.ServerUI;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wujy
 */
public class RunSend implements Runnable {
    
    String id, type, content, ip;
    DatagramSocket sendSocket;
    ServerUI serverUI;

    public RunSend(ServerUI serverUI, int id, String type, String content, String ip) {
        this.serverUI = serverUI;
        this.id = String.valueOf(id);
        this.type = type;
        this.content = content;
        this.ip = ip;
    }
    
    @Override
    public void run() {
        try {
            // ID$|$Type$|$Time$|$Length$|$Content
            content = id + "$|$" + type + "$|$" + System.currentTimeMillis() + "$|$" + content.length() + "$|$" + content;
            sendSocket = new DatagramSocket();
            byte[] buf = content.getBytes("UTF-8");
            DatagramPacket dp = new DatagramPacket(buf, buf.length, InetAddress.getByName(ip), Const.CLIENT_RCV_SOCKET);
            sendSocket.send(dp);
            System.out.println(content + " >> " + dp.getAddress().getHostAddress());
        } catch (SocketException ex) {
            Logger.getLogger(RunSend.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RunSend.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (sendSocket != null) {
                sendSocket.close();
            }
        }
    }

}
