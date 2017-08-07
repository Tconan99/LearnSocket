package com.conan.code;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Locale;

public class Main {

    public static long IPMSG_NOOPERATION =      0x00000000L;

    public static long IPMSG_BR_ENTRY =         0x000000001;
    public static long IPMSG_BR_EXIT =          0x000000002;
    public static long IPMSG_ANSENTRY =         0x000000003;
    public static long IPMSG_BR_ABSENCE =       0x000000004;

    public static long IPMSG_BR_ENTRYF =        0x00600001L;
    public static long IPMSG_BR_EXITF =         0x00600002L;
    public static long IPMSG_ANSENTRYF =        0x00600003L;
    public static long IPMSG_BR_ABSENCEF =      0x00600004L;

    public static long IPMSG_SENDMSG =          0x00000020L;
    public static long IPMSG_RECVMSG =          0x00000021L;

    public static long IPMSG_SENDCHECKOPT =     0x00000100L;

    public static long IPMSG_FILEATTACHOPT =    0x00200000L;

    public static long IPMSG_FEIQ_UNKNOW_121 = 121;
    public static long IPMSG_FEIQ_UNKNOW_472 = 472;
    // 121
    // 288


    private static int port = 2425;
    private static DatagramSocket server = null;

    private static boolean isClosed = false;

    private static void print(String str) {
        System.out.println(str);
    }

    private static void sendMsg(InetAddress address, long command) {
        sendMsg(address, -1, command, "");
    }

    private static void sendMsg(InetAddress address, int port, long command) {
        sendMsg(address, port, command, "");
    }

    private static void sendMsg(InetAddress address, long command, String msg) {
        sendMsg(address, -1, command, msg);
    }

    private static void sendMsg(InetAddress address, int port, long command, String msg) {
        if (port <= 0) {
            port = Main.port;
        }

        String msgTemplate = "1_lbt6_3#128#OF8CA8C030F8#0#0#0#4001#9:%d:tconan:MacMini:%d:%s";

        String message = String.format(Locale.US, msgTemplate, System.currentTimeMillis(), command, msg);
        try {
            DatagramPacket sendPacket= new DatagramPacket(message.getBytes(), message.getBytes().length, address, port);
            server.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void receiveMsg(Message message) {
        if (message.command == IPMSG_NOOPERATION) {
            print("空白消息:" + message.print());

        } else if (message.command == IPMSG_BR_ENTRY || message.command == IPMSG_BR_ENTRYF) {
            print("登录消息:" + message.print());
            sendMsg(message.address, message.port, IPMSG_ANSENTRY);

        } else if (message.command == IPMSG_BR_EXIT || message.command == IPMSG_BR_EXITF) {
            print("退出登录:" + message.print());

        } else if (message.command == IPMSG_ANSENTRY || message.command == IPMSG_ANSENTRYF) {
            print("在线好友:" + message.print());

        } else if (message.command == IPMSG_BR_ABSENCE || message.command == IPMSG_BR_ABSENCEF) {
            print("缺席好友:" + message.print());

        } else if (IPMSG_SENDMSG == (message.command & IPMSG_SENDMSG)) {
            message.message = message.message.substring(0, message.message.length() - 1);
            print("接收消息:" + message.print() + ":" + message.message);
            if (IPMSG_SENDCHECKOPT == (message.command & IPMSG_SENDCHECKOPT)) {
                sendMsg(message.address, message.port, IPMSG_RECVMSG, message.mid);
            }

            // 0:app-release.apk:08ae6ff:597ff2a2:1:
            // 1:jcmp:061643c8:597ef478:2:
            // 2:jcmp_syj_interface.sql:0425f:58db4c74:1:
            if (IPMSG_FILEATTACHOPT == (message.command & IPMSG_FILEATTACHOPT)) {
                print("信息包含文件:" + message.msg);
            }

        } else if (message.command == IPMSG_FEIQ_UNKNOW_472
                || message.command == IPMSG_FEIQ_UNKNOW_121
                ) {
            // print("未知命令472");
            // print(message.msg);

        } else {

            //print(message.msg());
            //print("未处理:" + message.command + ":" + message.print() + ":" + message.message);
        }
    }

    public static void main(String[] args) {
        try {
            InetAddress testHost = InetAddress.getByName("255.255.255.255");

            server = new DatagramSocket(port);
            System.out.println("Server Started (" + port + ")");

            sendMsg(testHost, IPMSG_BR_ENTRY);
            sendMsg(InetAddress.getByName("192.168.180.1"), IPMSG_BR_ENTRY);
            //sendMsg(testHost, IPMSG_BR_ENTRYF);

            while (!isClosed) {
                byte[] receiveBuffer = new byte[1024*4];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                server.receive(receivePacket);
                String receiveStr = new String(receivePacket.getData(), 0, receivePacket.getLength());
                receiveMsg(new Message(receiveStr, receivePacket.getAddress(), receivePacket.getPort()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                server.close();
            }
        }

    }
}
