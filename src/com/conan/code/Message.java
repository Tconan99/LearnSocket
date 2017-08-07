package com.conan.code;

import java.net.InetAddress;

/**
 * Created by tconan on 2017/8/3.
 */
public class Message {
    public String version;
    public String mid;
    public String name;
    public String hostname;
    public long command;
    public String message;
    public String ext;

    public InetAddress address;
    public int port;

    public String msg;

    public Message() {

    }

    public Message(String msg, InetAddress address, int port) {
        this.address = address;
        this.port = port;
        this.msg = msg;
        // 1_lbt6_3#128#OF8CA8C030F8#0#0#0#4001#9:1501745625079:tconan:MacMini:6291457:

        StringBuilder builder = new StringBuilder();

        String[] splitedMsg = msg.split(":");

        version = N(splitedMsg[0]);
        mid = N(splitedMsg[1]);
        name = N(splitedMsg[2]);
        hostname = N(splitedMsg[3]);
        try {
            command = Long.valueOf(N(splitedMsg[4]));
        } catch (Exception e) {
            e.printStackTrace();
            command = Main.IPMSG_NOOPERATION;
        }

        if (splitedMsg.length > 5) {
            message = N(splitedMsg[5]);
        }

        if (splitedMsg.length > 6) {
            ext = N(splitedMsg[6]);
        }

    }

    public String print() {
        //return name + ":"  + hostname + ":" + address.getHostName();
        return address.getHostName() + ":" + port;
    }

    public String msg() {
        return Long.toHexString(command) + "-" + msg;
    }

    private String N(String str) {
        return str == null ? "" : str;
    }
}
