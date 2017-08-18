package org.moonforest.pleasewakeup.httpserver;

import android.net.Uri;
import android.text.TextUtils;

import org.moonforest.pleasewakeup.MyApplication;
import org.moonforest.pleasewakeup.RedirectActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by ljian on 17/8/18.
 */

public class SimpleHttpServer {
    public static final String URL_PATH = "wakeup";
    private static final String CRLF = "\r\n";
    private static final String HEADER_BODY_SEP = CRLF + CRLF;
    private static final String SERVER_NAME = "PleaseWakeUpSimpleHttpServer";

    private static final SimpleHttpServer sSimpleHttpServer = new SimpleHttpServer();

    private Thread mMainThread;

    public static SimpleHttpServer getInstance() {
        return sSimpleHttpServer;
    }

    public void stopHttpServer() {
        if (isServerAlive()) {
            mMainThread.interrupt();
            mMainThread = null;
        }
    }

    private boolean isServerAlive() {
        return mMainThread != null && mMainThread.isAlive();
    }

    public void startHttpServer() {
        if (isServerAlive()) {
            return;
        }

        for (int port : new int[]{61593, 41123, 43387, 39083, 24423, 16834, 9289, 8452, 6217, 5300, 4118, 3787, 2998, 0}) {
            try {
                ServerSocket localServer = new ServerSocket(port);
                int localPort = localServer.getLocalPort();
                if (localPort <= 0) {
                    continue;
                }
                mMainThread = new MainThread(localServer);
                mMainThread.start();
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class MainThread extends Thread {
        private boolean mIfStop = false;
        private ServerSocket mServerSocket;

        public MainThread(ServerSocket serverSocket) {
            mServerSocket = serverSocket;
        }

        public void run() {
            while (!mIfStop) {
                try {
                    Socket s = mServerSocket.accept();
                    handleConnection(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        @Override
        public void interrupt() {
            super.interrupt();
            try {
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mIfStop = true;
        }

        private void handleConnection(final Socket connection) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        doHandleConnection(connection);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            connection.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

        private void doHandleConnection(Socket connection) throws IOException {
            StringBuffer sb = new StringBuffer();
            int bytesRead = 0;
            byte[] bytes = new byte[8096];
            while ((bytesRead = connection.getInputStream().read(bytes)) != -1) {
                String msg = new String(bytes, 0, bytesRead);
                sb.append(msg);
                if (sb.toString().contains(HEADER_BODY_SEP)) {//读完header为止
                    break;
                }
                if (mIfStop) {
                    return;
                }
            }
            String requestStr = sb.toString();
            if (TextUtils.isEmpty(requestStr)) {
                return;
            }

            String path = requestStr.split(" ")[1];
            String queryString = path.substring(path.indexOf("?") + 1);
            String[] args = queryString.split("&");
            Uri uri = Uri.parse(path);
            String firstPath = uri.getPathSegments().get(0);
            if (firstPath.equalsIgnoreCase(URL_PATH)) {//唤醒用,这里最好对请求做一些验证,防止被误触发或者攻击
                String url = uri.getQueryParameter("r");
                response(connection, "wakeup success".getBytes());
                RedirectActivity.launch(MyApplication.sMyApplication, url);
            } else if (firstPath.equalsIgnoreCase("test")) {//这段只是测试在微信浏览器里面打开H5页面用的,实际项目里就是着陆页
                response(connection, getTestPageContent());
            } else {
                response(connection, "404".getBytes());
            }
        }

        private byte[] getTestPageContent() {
            InputStream is = null;
            try {
                is = MyApplication.sMyApplication.getAssets().open("test.html");
                byte[] bytes = new byte[8096];
                int count;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((count = is.read(bytes)) != -1) {
                    baos.write(bytes, 0, count);
                }
                return baos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return new byte[0];
        }

        private void response(Socket connection, byte[] body) throws IOException {
            connection.getOutputStream().write(("HTTP/1.1 " + 200 + " OK" + CRLF).getBytes());
            connection.getOutputStream().write(("Content-Length: " + body.length + CRLF).getBytes());
            connection.getOutputStream().write(("Content-Type: text/html;charset=UTF-8" + CRLF).getBytes());
            connection.getOutputStream().write(("Access-Control-Allow-Origin: *" + CRLF).getBytes());//注意这个参数需要跨域
            connection.getOutputStream().write(("Server: " + SERVER_NAME + HEADER_BODY_SEP).getBytes());
            connection.getOutputStream().write(body);
        }
    }
}
