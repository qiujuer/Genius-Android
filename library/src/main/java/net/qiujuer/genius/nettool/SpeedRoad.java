package net.qiujuer.genius.nettool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by QiuJu
 * on 2014/9/20.
 */
public class SpeedRoad extends NetModel {
    private String urlStr, proxy;
    private int size, proxyPort;
    private long downSize, totalSize, downTime, connectTime;
    private float speed;
    private HttpURLConnection httpUrlConn = null;


    public SpeedRoad(String urlAddress, int size) {
        this(urlAddress, size, null, 0);
    }

    public SpeedRoad(String urlAddress, int size, String proxy, int proxyPort) {
        this.urlStr = urlAddress;
        this.size = size;
        this.proxy = proxy;
        this.proxyPort = proxyPort;
    }

    public float getSpeed() {
        return speed;
    }

    public long getDownSize() {
        return downSize;
    }

    public long getDownTime() {
        return downTime;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public long getConnectTime() {
        return connectTime;
    }

    @Override
    public void start() {
        try {
            URL url = new URL(urlStr);

            //Proxy host
            if (proxy == null) {
                httpUrlConn = (HttpURLConnection) url.openConnection();
            } else {
                InetAddress inetAddress = InetAddress.getByAddress(convertIpToByte(proxy));
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(inetAddress, proxyPort));
                httpUrlConn = (HttpURLConnection) url.openConnection(proxy);
            }

            //set
            httpUrlConn.setConnectTimeout(15000);
            httpUrlConn.setReadTimeout(10000);
            httpUrlConn.setUseCaches(false);
            //get connectTime
            long beginTime = System.currentTimeMillis();
            httpUrlConn.connect();
            connectTime = System.currentTimeMillis() - beginTime;
        } catch (MalformedURLException e) {
            error = MALFORMED_URL_ERROR;
        } catch (UnknownHostException e) {
            error = UNKNOWN_HOST_ERROR;
        } catch (IOException e) {
            error = TCP_LINK_ERROR;
        } catch (Exception e) {
            error = UNKNOWN_ERROR;
        }
        //run
        if (error == SUCCEED)
            download(httpUrlConn);
        //close
        if (httpUrlConn != null) {
            close();
        }
    }

    @Override
    public void cancel() {
        if (httpUrlConn != null) {
            close();
        }
    }

    private void close() {
        try {
            OutputStream out = httpUrlConn.getOutputStream();
            if (out != null)
                out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            InputStream in = httpUrlConn.getInputStream();
            closeInputStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            InputStream in = httpUrlConn.getErrorStream();
            closeInputStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        httpUrlConn.disconnect();
        httpUrlConn = null;
    }

    private void download(HttpURLConnection httpConn) {
        long beginTime = System.currentTimeMillis();

        int code;
        try {
            code = httpConn.getResponseCode();
        } catch (IOException e) {
            error = HTTP_CODE_ERROR;
            return;
        }
        if (code < 200 && code > 299)
            return;
        //get size
        long fSize = httpConn.getContentLength();
        long dwRead, curSize = 0;
        InputStream cin;
        try {
            cin = httpConn.getInputStream();
        } catch (IOException e) {
            error = SERVICE_NOT_AVAILABLE;
            return;
        }
        //file down
        if (fSize > 0) {
            try {
                byte[] buffer = new byte[32];
                while ((dwRead = cin.read(buffer)) != -1) {
                    curSize += dwRead;
                    if (curSize >= size || curSize >= fSize)
                        break;
                    if (curSize + buffer.length > size) {
                        byte[] lastByte = new byte[(int) (size - curSize)];
                        curSize += cin.read(lastByte);
                        break;
                    }
                }
            } catch (IOException e) {
                if (curSize <= 0) {
                    error = SERVICE_NOT_AVAILABLE;
                    return;
                }
            }
            //get result
            totalSize = fSize;
            downSize = curSize;
            downTime = System.currentTimeMillis() - beginTime;
            if (downTime <= 0)
                downTime = 1;
            speed = ((float) curSize * 1000) / downTime;
        } else {
            //page down
            try {
                byte[] buffer = new byte[32];
                while ((dwRead = cin.read(buffer)) != -1) {
                    curSize += dwRead;
                    if (curSize > size)
                        break;
                }
            } catch (IOException e) {
                if (curSize <= 0) {
                    error = SERVICE_NOT_AVAILABLE;
                    return;
                }
            }

            //get result
            totalSize = curSize;
            downSize = size < curSize ? size : curSize;
            long time = (System.currentTimeMillis() - beginTime);
            if (time <= 0)
                time = 1;
            speed = ((float) curSize * 1000) / time;
            if (downSize == curSize)
                downTime = time;
            else
                downTime = (int) (time * ((float) downSize / curSize) + 1);
        }
        closeInputStream(cin);
    }
}
