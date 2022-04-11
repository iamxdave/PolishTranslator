package zad1;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainServer {
    private final ServerSocket server;
    private final Map<String, Integer> langServerMap = new HashMap<>();
    private final Map<String, Map<String, String>> langTransMap = new HashMap<>();


    public MainServer(int port) throws IOException {
        server = new ServerSocket(port);
    }

    public ServerSocket getServer() {
        return server;
    }

    public Map<String, Integer> getLangServerMap() {
        return langServerMap;
    }

    public Map<String, Map<String, String>> getLangTransMap() {
        return langTransMap;
    }

    public void putAvailableLanguages() {
        Map<String, String> eng = new HashMap<>();
        eng.put("dom", "house");
        eng.put("dach", "roof");
        eng.put("drzwi", "door");
        eng.put("okno", "window");
        eng.put("dzwonek", "bell");

        Map<String, String> de = new HashMap<>();
        de.put("dom", "haus");
        de.put("dach", "dach");
        de.put("drzwi", "tür");
        de.put("okno", "fenster");
        de.put("dzwonek", "glocke");

        langTransMap.put("EN", eng);
        langTransMap.put("DE", de);
    }


    public static void main(String[] args) throws Exception {

        MainServer tcp = new MainServer(6600);
        tcp.putAvailableLanguages();

        ExecutorService threadPool = Executors.newFixedThreadPool(8);

        while (true) {
            threadPool.submit(new MServerHandler(tcp.getServer().accept(), tcp));
        }
    }
}
