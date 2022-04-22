package zad1;

import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;

public class MServerHandler implements Runnable {

    private final Socket socket;
    private final MainServer server;
    private final BufferedReader input;
    private final PrintWriter output;

    public MServerHandler(Socket socket, MainServer server) throws IOException {

        this.socket = socket;
        this.server = server;

        System.out.println("Connected: " + socket.getPort());

        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
    }

    @Override
    public void run() {

        try {
            String response = input.readLine();

            System.out.println("GOT MSG: " + response);

            String[] parts = response.split(",");

            String cmd = parts[0];
            String text;
            String language;
            String adress;


            switch (cmd) {
                case "set" -> {
                    adress = parts[1];
                    if (!server.getLangTransMap().isEmpty()) {
                        Map.Entry<String, Map<String, String>> entry = server.getLangTransMap().entrySet().iterator().next();

                        StringBuilder sb = new StringBuilder();

                        sb.append(entry.getKey()).append(",");

                        for(Map.Entry<String, String> trans : entry.getValue().entrySet()) {
                            sb.append(trans.getKey()).append("=").append(trans.getValue()).append(" ");
                        }

                        Socket langServer = new Socket();
                        langServer.connect(new InetSocketAddress("localhost", Integer.parseInt(adress)));
                        PrintWriter langServerOutput = new PrintWriter(new OutputStreamWriter(langServer.getOutputStream()), true);

                        System.out.println("SENDING: set," + sb);
                        langServerOutput.println("set," + sb);

                        langServer.close();
                        langServerOutput.close();

                    } else {
                        System.out.println("SENDING: err, no available languages to set");
                        output.println("err,no available languages to set");
                    }
                }

                case "setack" -> {
                    language = parts[1];
                    adress = parts[2];

                    server.getLangServerMap().put(language, Integer.parseInt(adress));

                    server.getLangTransMap().remove(language);
                }

                case "get" -> {
                    text = parts[1];
                    language = parts[2];
                    adress = parts[3];

                    boolean canTranslate = server.getLangServerMap().entrySet().stream().anyMatch(e -> e.getKey().equals(language));

                    if (canTranslate) {
                        Socket langServer = new Socket("localhost", server.getLangServerMap().get(language));
                        PrintWriter outputLangServer = new PrintWriter(new OutputStreamWriter(langServer.getOutputStream()), true);

                        System.out.println("SENDING: get," + text + ',' + language + ',' + adress);
                        outputLangServer.println("get," + text + ',' + language + ',' + adress);

                        System.out.println("SENDING: getack," + server.getServer().getLocalPort());
                        output.println("getack," + server.getServer().getLocalPort());

                        output.close();
                        langServer.close();
                        outputLangServer.close();
                    } else {
                        System.out.println("SENDING: err,No such language in the database");
                        output.println("err,No such language in the database");

                        output.close();
                    }
                }

                default -> output.println("err,Unknown cmd");
            }

            socket.close();
            input.close();
            output.close();

        } catch (ConnectException e) {
            System.out.println("SENDING: err,Current language server has disconnected");
            output.println("err,Current language server has disconnected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

