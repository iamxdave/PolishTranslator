package zad1;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LangServer {
    private ServerSocket server;

    String language;
    Map<String, String> transMap = new HashMap<>();


    public LangServer(int port) throws IOException {
        server = new ServerSocket(port);
    }

    public ServerSocket getServer() {
        return server;
    }

    public String getLanguage() {
        return language;
    }

    public Map<String, String> getTransMap() {
        return transMap;
    }

    public int getLocalPort() {
        return server.getLocalPort();
    }

    public void setLanguage() throws IOException {
        Socket mainServer = new Socket();
        mainServer.connect(new InetSocketAddress("localhost", 6600));

        PrintWriter output = new PrintWriter(new OutputStreamWriter(mainServer.getOutputStream()), true);
        output.println("set," + server.getLocalPort());
    }


    public static void main(String[] args) throws Exception {
        LangServer tcp = new LangServer(0);
        tcp.setLanguage();

        ExecutorService threadPool = Executors.newFixedThreadPool(8);

        while (true) {
            threadPool.submit(new LServerHandler(tcp.getServer().accept(), tcp));
        }
    }
}
