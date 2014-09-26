package net.qiujuer.genius.nettool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @author QiuJu
 */
public class Telnet extends NetModel {
    protected static final int TimeOutSize = 3000;
    private String host;
    private int port;
    private long delay;
    private boolean isConnected;

    public Telnet(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void start() {
        Socket socket = null;
        try {
            Long bTime = System.currentTimeMillis();
            socket = new Socket();
            try {
                socket.setSoTimeout(TimeOutSize);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            socket.connect(new InetSocketAddress(host, port), TimeOutSize);
            if (isConnected = socket.isConnected())
                delay = System.currentTimeMillis() - bTime;
            else
                error = TCP_LINK_ERROR;
        } catch (UnknownHostException e) {
            error = UNKNOWN_HOST_ERROR;
        } catch (IOException e) {
            error = TCP_LINK_ERROR;
        } finally {
            if (socket != null)
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    @Override
    public void cancel() {

    }

    public boolean isConnected() {
        return isConnected;
    }

    public long getDelay() {
        return delay;
    }

    @Override
    public String toString() {
        return "Port:" + port +
                " Delay:" + delay +
                " IsConnected" + isConnected;
    }
}
