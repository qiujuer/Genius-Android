package net.qiujuer.genius.nettool;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by QiuJu
 * on 2014/9/20.
 */
public class DnsResolve extends NetModel {
    private static final byte[] ID = new byte[]{(byte) 2, (byte) 6};
    private static final String POINT = ".";
    private String host;
    private String server;
    private List<String> addresses;
    private long delay;

    /**
     * Domain name resolution test
     *
     * @param host Domain name address
     */
    public DnsResolve(String host) {
        this(host, null);
    }

    /**
     * Domain name resolution test
     *
     * @param host   Domain name address
     * @param server Specify the domain name server
     */
    public DnsResolve(String host, String server) {
        this.host = host;
        this.server = server;
    }

    private ArrayList<String> resolve(String domain, String dnsServer) {
        //pointer
        int pos;
        /**
         * init
         */
        byte[] sendBuffer = new byte[100];
        pos = 12;
        // message head
        sendBuffer[0] = ID[0];
        sendBuffer[1] = ID[1];
        sendBuffer[2] = 0x01;
        sendBuffer[3] = 0x00;
        sendBuffer[4] = 0x00;
        sendBuffer[5] = 0x01;
        sendBuffer[6] = 0x00;
        sendBuffer[7] = 0x00;
        sendBuffer[8] = 0x00;
        sendBuffer[9] = 0x00;
        sendBuffer[10] = 0x00;
        sendBuffer[11] = 0x00;
        // add domain
        String[] part = domain.split("\\.");
        for (String s : part) {
            if (s == null || s.length() <= 0)
                continue;
            int sLength = s.length();
            sendBuffer[pos++] = (byte) sLength;
            int i = 0;
            char[] val = s.toCharArray();
            while (i < sLength) {
                sendBuffer[pos++] = (byte) val[i++];
            }
        }
        // 0 end
        sendBuffer[pos++] = 0x00;
        sendBuffer[pos++] = 0x00;
        // 1 A record query
        sendBuffer[pos++] = 0x01;
        sendBuffer[pos++] = 0x00;
        // Internet record query
        sendBuffer[pos++] = 0x01;

        /**
         * UDP Send
         */
        DatagramSocket ds = null;
        byte[] receiveBuffer = null;
        try {
            ds = new DatagramSocket();
            ds.setSoTimeout(2000);
            DatagramPacket dp = new DatagramPacket(sendBuffer, pos,
                    InetAddress.getByName(dnsServer), 53);
            ds.send(dp);
            dp = new DatagramPacket(new byte[1024], 1024);
            ds.receive(dp);
            int len = dp.getLength();
            receiveBuffer = new byte[len];
            for (int i = 0; i < len; i++) {
                receiveBuffer[i] = dp.getData()[i];
            }
        } catch (UnknownHostException e) {
            error = UNKNOWN_HOST_ERROR;
        } catch (SocketException e) {
            error = NETWORK_SOCKET_ERROR;
            e.printStackTrace();
        } catch (IOException e) {
            error = NETWORK_IO_ERROR;
            e.printStackTrace();
        } finally {
            if (ds != null)
                ds.close();
        }

        /**
         * resolve data
         */
        if (error != SUCCEED || receiveBuffer == null)
            return null;

        int queryCount, answerCount;
        if (receiveBuffer[0] != ID[0] || receiveBuffer[1] != ID[1]
                || (receiveBuffer[2] & 0x80) != 0x80)
            return null;
        queryCount = (receiveBuffer[4] << 8) | receiveBuffer[5];
        if (queryCount == 0)
            return null;
        answerCount = (receiveBuffer[6] << 8) | receiveBuffer[7];
        if (answerCount == 0)
            return null;
        // pointer restore
        pos = 12;
        // Skip the query part head
        for (int i = 0; i < queryCount; i++) {
            while (receiveBuffer[pos] != 0x00) {
                pos += receiveBuffer[pos] + 1;
            }
            pos += 5;
        }
        // get ip form data
        ArrayList<String> iPs = new ArrayList<String>();
        for (int i = 0; i < answerCount; i++) {
            if (receiveBuffer[pos] == (byte) 0xC0) {
                pos += 2;
            } else {
                while (receiveBuffer[pos] != (byte) 0x00) {
                    pos += receiveBuffer[pos] + 1;
                }
                pos++;
            }
            byte queryType = (byte) (receiveBuffer[pos] << 8 | receiveBuffer[pos + 1]);
            pos += 8;
            int dataLength = (receiveBuffer[pos] << 8 | receiveBuffer[pos + 1]);
            pos += 2;
            // add ip
            if (queryType == (byte) 0x01) {
                int address[] = new int[4];
                for (int n = 0; n < 4; n++) {
                    address[n] = receiveBuffer[pos + n];
                    if (address[n] < 0)
                        address[n] += 256;
                }
                iPs.add(address[0] + POINT + address[1] + POINT + address[2] + POINT + address[3]);
            }
            pos += dataLength;
        }
        return iPs;
    }

    @Override
    public void start() {
        long sTime = System.currentTimeMillis();
        if (server == null) {
            try {
                InetAddress[] adds = InetAddress.getAllByName(host);
                addresses = new ArrayList<String>(adds.length);
                for (InetAddress add : adds)
                    addresses.add(add.getHostAddress());
            } catch (UnknownHostException e) {
                error = UNKNOWN_HOST_ERROR;
            } catch (Exception e) {
                error = UNKNOWN_ERROR;
            }
        } else {
            try {
                addresses = resolve(host, server);
            } catch (Exception e) {
                if (error == SUCCEED)
                    error = UNKNOWN_ERROR;
                e.printStackTrace();
            }
        }
        delay = System.currentTimeMillis() - sTime;
    }

    @Override
    public void cancel() {

    }

    public List<String> getAddresses() {
        return addresses;
    }

    public long getDelay() {
        return delay;
    }

    @Override
    public String toString() {
        return "Delay:" + delay +
                " IPs:" + (addresses == null ? "[]" : addresses.toString());
    }
}