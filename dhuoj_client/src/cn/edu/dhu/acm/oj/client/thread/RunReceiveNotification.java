package cn.edu.dhu.acm.oj.client.thread;

import cn.edu.dhu.acm.oj.client.*;
import cn.edu.dhu.acm.oj.common.config.Const;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RunReceiveNotification implements Runnable {

    DatagramSocket receiveSocket = null;
    DatagramSocket sendSocket = null;

    public void run() {
        try {
            receiveSocket = new DatagramSocket(Const.CLIENT_RCV_SOCKET);
            System.out.println("Notification receiver started.");
            while (true) {
                System.out.print(">>");
                byte[] buf = new byte[10240];
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                receiveSocket.receive(dp);

                String ip = dp.getAddress().getHostAddress();
                String tmp = new String(dp.getData(), 0, dp.getLength(), "UTF-8");
                System.out.println(tmp);
                final String[] data = tmp.split("\\$\\|\\$");  // ID$|$Type$|$Time$|$Length$|$Content

                String replyText = data[0] + "$|$REPLY$|$" + System.currentTimeMillis() + "$|$2$|$";
                if (data.length == 5 && data[4].length() == Integer.parseInt(data[3])) {  // Simple verify
                    replyText += "AC";
                    if (!data[1].equals("TEST")) {
                        final int notificationid = Integer.parseInt(data[0]);
                        if (notificationid > Control.getNotificationID()) {  // Avoid duplication
                            Control.handleNotification(data);
                            Control.setNotificationID(notificationid);
                        } else {
                            System.out.println("Ignored due to duplication.");
                        }
                    }
                } else {
                    replyText += "WA";
                }
                
                // Send reply
                System.out.println(replyText);
                sendSocket = new DatagramSocket();
                buf = replyText.getBytes();
                dp = new DatagramPacket(buf, buf.length, InetAddress.getByName(ip), Const.SERVER_RCV_SOCKET);
                sendSocket.send(dp);
                sendSocket.close();
            }
        } catch (SocketException ex) {
            Logger.getLogger(RunReceiveNotification.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RunReceiveNotification.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NumberFormatException ex) {
            Logger.getLogger(RunReceiveNotification.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (receiveSocket != null) {
                receiveSocket.close();
            }
            if (sendSocket != null) {
                sendSocket.close();
            }
            RunReceiveNotification runReceiveNotification = new RunReceiveNotification();
            Thread thread = new Thread(runReceiveNotification);
            thread.start();
            System.out.println("Notification receiver stopped.");
        }
    }
}